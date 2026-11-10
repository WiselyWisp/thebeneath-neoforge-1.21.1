package net.wiselywisp.thebeneath.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.fluid.ModFluids;
import org.joml.Matrix4f;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.AABB;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;


@EventBusSubscriber(modid = TheBeneath.MOD_ID, value = Dist.CLIENT)
public final class VoidFluidVeilRenderer {
    private static final ResourceLocation PROGRAM =
            ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "void_fluid");
    private static boolean loggedSuccess;
    private static boolean loggedFailure;
    private static boolean loggedStage;
    private static boolean warnedBridgeUnavailable;
    private static int loggedRenderDistance = -1;
    private static boolean loggedBridge;
    private static boolean loggedBridgeFailure;
    private static Object veilShaderShard;
    private static final Map<Long, SectionMesh> SECTION_MESHES = new HashMap<>();
    private static final Map<Long, List<Long>> SECTION_POSITIONS = new HashMap<>();
    private static ClientLevel cachedLevel;
    private static long lastIndexTick = Long.MIN_VALUE;
    private static int lastKnownCount = -1;
    private static int meshBuilds;
    private static int visibleDraws;
    private static final int MAX_BUILDS_PER_FRAME = 8;
    private static final int MESH_REFRESH_TICKS = 100;


    private static final class SectionMesh implements AutoCloseable {
        final VertexBuffer gpu;
        final int vertexCount;
        final long indexSignature;
        final long builtTick;
        SectionMesh(VertexBuffer gpu, int vertexCount, long indexSignature, long builtTick) {
            this.gpu = gpu;
            this.vertexCount = vertexCount;
            this.indexSignature = indexSignature;
            this.builtTick = builtTick;
        }
        @Override public void close() { gpu.close(); }
    }

    private static long signature(List<Long> positions) {

        long h = 0x9E3779B97F4A7C15L ^ positions.size();
        for (long p : positions) h += Long.rotateLeft(p * 0xBF58476D1CE4E5B9L, 19);
        return h;
    }

    private static void clearCache() {
        for (SectionMesh mesh : SECTION_MESHES.values()) mesh.close();
        SECTION_MESHES.clear();
        SECTION_POSITIONS.clear();
        lastIndexTick = Long.MIN_VALUE;
        lastKnownCount = -1;
    }

    private static void refreshIndex(ClientLevel level) {
        long tick = level.getGameTime();
        int count = VoidFluidClient.knownVoidPositions().size();
        if (lastIndexTick != Long.MIN_VALUE && count == lastKnownCount &&
                tick - lastIndexTick < 20) return;
        lastIndexTick = tick;
        lastKnownCount = count;
        SECTION_POSITIONS.clear();
        for (Long packed : VoidFluidClient.knownVoidPositions()) {
            BlockPos pos = BlockPos.of(packed);
            if (!level.hasChunkAt(pos)) continue;
            long section = SectionPos.of(pos).asLong();
            SECTION_POSITIONS.computeIfAbsent(section, key -> new ArrayList<>()).add(packed);
        }

        SECTION_MESHES.entrySet().removeIf(entry -> {
            if (SECTION_POSITIONS.containsKey(entry.getKey())) return false;
            entry.getValue().close();
            return true;
        });
    }

    private static SectionMesh buildSection(ClientLevel level, SectionPos section,
                                            List<Long> positions, long now) {
        BufferBuilder mesh = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
        OffsetConsumer local = new OffsetConsumer(mesh);
        local.offset(0, 0, 0);
        VoidFluidClient.beginVeilTessellation();
        try {
            for (long packed : positions) {
                BlockPos pos = BlockPos.of(packed);
                if (!level.hasChunkAt(pos)) continue;
                FluidState fluid = level.getFluidState(pos);
                if (!fluid.is(ModFluids.ESSENCE.get()) &&
                        !fluid.is(ModFluids.FLOWING_ESSENCE.get())) {
                    VoidFluidClient.knownVoidPositions().remove(packed);
                    continue;
                }
                BlockState state = level.getBlockState(pos);
                Minecraft.getInstance().getBlockRenderer()
                        .renderLiquid(pos, level, local, state, fluid);
            }
        } finally {
            VoidFluidClient.endVeilTessellation();
        }
        if (local.vertices == 0) return null;
        VertexBuffer buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        try {
            buffer.bind();
            buffer.upload(mesh.buildOrThrow());
            VertexBuffer.unbind();
            return new SectionMesh(buffer, local.vertices, signature(positions), now);
        } catch (RuntimeException e) {
            VertexBuffer.unbind();
            buffer.close();
            throw e;
        }
    }



    private static final RenderLevelStageEvent.Stage FLUID_STAGE =
            RenderLevelStageEvent.Stage.AFTER_PARTICLES;

    @SubscribeEvent
    public static void afterTranslucent(RenderLevelStageEvent event) {
        if (event.getStage() != FLUID_STAGE) return;
        if (!VoidFluidClient.shaderAvailable()) {

            restoreFallback(Minecraft.getInstance());
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (!loggedStage) {
            TheBeneath.LOGGER.info("[Void Fluid] Dedicated Veil renderer enabled at {}.", event.getStage());
            loggedStage = true;
        }

        if (cachedLevel != mc.level) {
            clearCache();
            cachedLevel = mc.level;
            veilShaderShard = null;
            loggedSuccess = false;
            loggedFailure = false;
        }

        double cx = event.getCamera().getPosition().x;
        double cy = event.getCamera().getPosition().y;
        double cz = event.getCamera().getPosition().z;
        BlockPos center = BlockPos.containing(cx, cy, cz);
        int chunkX = center.getX() >> 4;
        int chunkZ = center.getZ() >> 4;
        int renderDistance = mc.options.renderDistance().get();
        int radiusChunks = VoidFluidClient.radiusForDistance(renderDistance);
        Set<Long> changedSections = VoidFluidClient.updateCoverage(mc.level, chunkX, chunkZ, radiusChunks);

        for (long packedSection : changedSections) {
            SectionPos section = SectionPos.of(packedSection);
            mc.levelRenderer.setSectionDirty(section.x(), section.y(), section.z());
        }

        if (loggedRenderDistance != renderDistance) {
            TheBeneath.LOGGER.info(
                    "[Void Fluid] Shader radius {} chunks for view distance {}; " +
                    "{} Void-fluid sections scheduled for fallback boundary refresh.",
                    radiusChunks, renderDistance, changedSections.size());
            loggedRenderDistance = renderDistance;
        }

        if (TheBeneath.LOGGER.isDebugEnabled() && !changedSections.isEmpty()) {
            TheBeneath.LOGGER.debug(
                    "[Void Fluid] Camera chunk {},{}; updated {} Void-fluid sections.",
                    chunkX, chunkZ, changedSections.size());
        }
        if (VoidFluidClient.knownVoidPositions().isEmpty()) return;

        ShaderInstance original = RenderSystem.getShader();
        try {
            Class<?> api = Class.forName("foundry.veil.api.client.render.VeilRenderSystem");
            Object engine = api.getMethod("renderer").invoke(null);
            Object manager = engine.getClass().getMethod("getShaderManager").invoke(engine);
            Object shader = manager.getClass().getMethod("getShader", ResourceLocation.class)
                    .invoke(manager, PROGRAM);
            if (shader == null || !Boolean.TRUE.equals(shader.getClass().getMethod("isValid").invoke(shader))) {
                restoreFallback(mc);
                return;
            }


            refreshIndex(mc.level);
            long tick = mc.level.getGameTime();
            List<Long> visible = new ArrayList<>();
            for (Long id : SECTION_POSITIONS.keySet()) {
                SectionPos section = SectionPos.of(id);
                BlockPos origin = section.origin();
                if (!VoidFluidClient.withinShaderZone(origin)) continue;
                if (event.getFrustum() != null && !event.getFrustum().isVisible(
                        new AABB(origin.getX(), origin.getY(), origin.getZ(),
                                origin.getX() + 16, origin.getY() + 16, origin.getZ() + 16))) continue;
                visible.add(id);
            }
            if (visible.isEmpty()) return;

            visible.sort(java.util.Comparator.comparingDouble(id -> {
                BlockPos o = SectionPos.of(id).origin();
                return o.distToCenterSqr(cx, cy, cz);
            }));
            int rebuilt = 0;
            int vertexTotal = 0;
            for (Long id : visible) {
                List<Long> positions = SECTION_POSITIONS.get(id);
                if (positions == null || positions.isEmpty()) continue;
                SectionMesh existing = SECTION_MESHES.get(id);
                long fingerprint = signature(positions);
                boolean stale = existing == null || existing.indexSignature != fingerprint ||
                        tick - existing.builtTick >= MESH_REFRESH_TICKS;
                if (stale && rebuilt < MAX_BUILDS_PER_FRAME) {
                    SectionMesh updated = buildSection(mc.level, SectionPos.of(id), positions, tick);
                    SectionMesh old = SECTION_MESHES.remove(id);
                    if (old != null) old.close();
                    if (updated != null) SECTION_MESHES.put(id, updated);
                    rebuilt++;
                }
                SectionMesh cached = SECTION_MESHES.get(id);
                if (cached != null) vertexTotal += cached.vertexCount;
            }
            meshBuilds += rebuilt;
            if (vertexTotal == 0) return;


            Object bridge = shader.getClass().getMethod("toShaderInstance").invoke(shader);
            if (!(bridge instanceof ShaderInstance wrapper)) {
                if (!loggedFailure) {
                    TheBeneath.LOGGER.warn("[Void Fluid] Veil shader did not provide ShaderInstance bridge.");
                    loggedFailure = true;
                }
                restoreFallback(mc);
                return;
            }

            var phaseUniform = wrapper.getUniform("VoidPhase");
            if (phaseUniform == null) {
                if (!loggedFailure) {
                    TheBeneath.LOGGER.error("[Void Fluid] Shader is missing VoidPhase uniform; custom fluid pass skipped.");
                    loggedFailure = true;
                }
                restoreFallback(mc);
                return;
            }
            float phase = (float) ((System.nanoTime() % 180_000_000_000L)
                    / 180_000_000_000.0 * Math.PI * 2.0);
            phaseUniform.set(phase);
            Matrix4f previousModelView = new Matrix4f(RenderSystem.getModelViewMatrix());
            RenderSystem.getModelViewMatrix().set(event.getModelViewMatrix());

            {
                try {
                    Class<?> shardClass = Class.forName(
                            "foundry.veil.impl.client.render.pipeline.ShaderProgramShard");
                    if (veilShaderShard == null) {
                        var ctor = shardClass.getConstructor(ResourceLocation.class);
                        veilShaderShard = ctor.newInstance(PROGRAM);
                    }
                    shardClass.getMethod("setupRenderState").invoke(veilShaderShard);
                    ShaderInstance selected = RenderSystem.getShader();
                    if (!loggedBridge) {
                        TheBeneath.LOGGER.info(
                                "[Void Iris Bridge] Veil ShaderProgramShard.setupRenderState() called; " +
                                "selectedShader={}, sameAsDirectWrapper={}. " +
                                "The bridge may choose an Iris-compatible program.",
                                selected == null ? "null" : selected.getClass().getName(),
                                selected == wrapper);
                        loggedBridge = true;
                    }

                    if (selected == null) RenderSystem.setShader(() -> wrapper);
                } catch (ReflectiveOperationException | RuntimeException bridgeError) {
                    if (!loggedBridgeFailure) {
                        TheBeneath.LOGGER.warn(
                                "[Void Iris Bridge] ShaderProgramShard setup not usable; using original Veil program.",
                                bridgeError);
                        loggedBridgeFailure = true;
                    }
                    if (ModList.get().isLoaded("iris")) {
                        RenderSystem.getModelViewMatrix().set(previousModelView);
                        restoreFallback(mc);
                        return;
                    }
                    RenderSystem.setShader(() -> wrapper);
                }
            }
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(false);
            try {
                ShaderInstance selected = RenderSystem.getShader();
                if (selected == null) selected = wrapper;
                int drawnSections = 0;
                for (Long id : visible) {
                    SectionMesh cached = SECTION_MESHES.get(id);
                    if (cached == null) continue;
                    BlockPos origin = SectionPos.of(id).origin();
                    Matrix4f sectionView = new Matrix4f(event.getModelViewMatrix())
                            .translate((float) (origin.getX() - cx),
                                       (float) (origin.getY() - cy),
                                       (float) (origin.getZ() - cz));
                    cached.gpu.bind();
                    cached.gpu.drawWithShader(sectionView, event.getProjectionMatrix(), selected);
                    drawnSections++;
                }
                VertexBuffer.unbind();
                visibleDraws += drawnSections;

                if (!VoidFluidClient.isExclusiveReady()) {
                    VoidFluidClient.setExclusiveReady(true);
                    mc.levelRenderer.allChanged();
                    TheBeneath.LOGGER.info("[Void Fluid] Cached renderer activated; rebuilding duplicate vanilla fluid surfaces once.");
                }
            } finally {
                RenderSystem.depthMask(true);
                RenderSystem.disableBlend();
                RenderSystem.getModelViewMatrix().set(previousModelView);
            }
            if (!loggedSuccess) {
                TheBeneath.LOGGER.info("[Void Fluid] Drawing cached geometry from {} indexed sections ({} vertices); shader radius {} chunks (view distance {}).",
                        SECTION_POSITIONS.size(), vertexTotal, radiusChunks, renderDistance);
                loggedSuccess = true;
            }
        } catch (ReflectiveOperationException | RuntimeException err) {
            if (!loggedFailure) {
                TheBeneath.LOGGER.error("[Void Fluid] Dedicated draw failed; restoring native fallback.", err);
                loggedFailure = true;
            }
            restoreFallback(mc);
        } finally {
            if (veilShaderShard != null) {
                try {
                    veilShaderShard.getClass().getMethod("clearRenderState").invoke(veilShaderShard);
                } catch (ReflectiveOperationException | RuntimeException ignored) {

                }
            }
            RenderSystem.setShader(() -> original);
        }
    }


    private static void restoreFallback(Minecraft mc) {
        if (VoidFluidClient.isExclusiveReady()) {
            VoidFluidClient.resetForFallback();
            if (mc.level != null) mc.levelRenderer.allChanged();
            TheBeneath.LOGGER.warn("[Void Fluid] Restored vanilla fluid fallback after renderer became unavailable.");
        }
    }

    private static final class OffsetConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private float dx, dy, dz;
        private int vertices;
        OffsetConsumer(VertexConsumer delegate) { this.delegate = delegate; }
        void offset(double x, double y, double z) {
            dx = (float) x; dy = (float) y; dz = (float) z;
        }
        @Override public VertexConsumer addVertex(float x, float y, float z) {
            delegate.addVertex(x + dx, y + dy, z + dz);
            vertices++;
            return this;
        }
        @Override public VertexConsumer setColor(int r, int g, int b, int a) {
            delegate.setColor(r,g,b,a); return this;
        }
        @Override public VertexConsumer setUv(float u, float v) {
            delegate.setUv(u,v); return this;
        }
        @Override public VertexConsumer setUv1(int u, int v) {
            delegate.setUv1(u,v); return this;
        }
        @Override public VertexConsumer setUv2(int u, int v) {
            delegate.setUv2(u,v); return this;
        }
        @Override public VertexConsumer setNormal(float x, float y, float z) {
            delegate.setNormal(x,y,z); return this;
        }
    }
    private VoidFluidVeilRenderer() {}
}

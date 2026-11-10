package net.wiselywisp.thebeneath.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.neoforged.fml.ModList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import java.util.HashSet;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.fluid.ModFluids;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = TheBeneath.MOD_ID, value = Dist.CLIENT)
public final class VoidFluidClient {
    private static final ResourceLocation STILL = ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "block/essence_of_the_void_still");
    private static final ResourceLocation FLOW = ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "block/essence_of_the_void_flow");


    static boolean shaderAvailable() {
        return !"false".equalsIgnoreCase(System.getProperty("thebeneath.voidFluidRenderer", "true"))
                && ModList.get().isLoaded("veil")
                && (!ModList.get().isLoaded("iris") || ModList.get().isLoaded("irisveil"));
    }


    private static final ThreadLocal<Boolean> IN_VEIL_PASS = ThreadLocal.withInitial(() -> false);
    private static volatile boolean exclusiveReady;


    private static final Set<Long> KNOWN_VOID = ConcurrentHashMap.newKeySet();
    private static volatile int cameraChunkX, cameraChunkZ, activeRadiusChunks = -1;
    private static volatile ClientLevel trackedLevel;

    static Set<Long> knownVoidPositions() { return KNOWN_VOID; }


    static int radiusForDistance(int configuredChunks) {
        return Math.max(1, Math.min(32, configuredChunks - 1));
    }


    static Set<Long> updateCoverage(ClientLevel level, int chunkX, int chunkZ, int radius) {
        if (trackedLevel != level) {
            trackedLevel = level;
            KNOWN_VOID.clear();
            exclusiveReady = false;
            cameraChunkX = chunkX;
            cameraChunkZ = chunkZ;
            activeRadiusChunks = radius;
            return Set.of();
        }
        int previousX = cameraChunkX;
        int previousZ = cameraChunkZ;
        int previousRadius = activeRadiusChunks;
        if (previousX == chunkX && previousZ == chunkZ && previousRadius == radius)
            return Set.of();

        cameraChunkX = chunkX;
        cameraChunkZ = chunkZ;
        activeRadiusChunks = radius;

        Set<Long> affectedSections = new HashSet<>();
        boolean trackTransitions = exclusiveReady && shaderAvailable();
        for (long packed : KNOWN_VOID) {
            BlockPos pos = BlockPos.of(packed);
            int fluidChunkX = pos.getX() >> 4;
            int fluidChunkZ = pos.getZ() >> 4;
            boolean previouslyShader = previousRadius >= 0 &&
                    Math.abs(fluidChunkX - previousX) <= previousRadius &&
                    Math.abs(fluidChunkZ - previousZ) <= previousRadius;
            boolean nowShader = Math.abs(fluidChunkX - chunkX) <= radius &&
                    Math.abs(fluidChunkZ - chunkZ) <= radius;
            if (trackTransitions && previouslyShader != nowShader) {
                affectedSections.add(SectionPos.of(pos).asLong());
            }
        }

        KNOWN_VOID.removeIf(packed -> {
            BlockPos pos = BlockPos.of(packed);
            return Math.abs((pos.getX() >> 4) - chunkX) > radius + 3 ||
                   Math.abs((pos.getZ() >> 4) - chunkZ) > radius + 3;
        });
        return affectedSections;
    }

    static boolean withinShaderZone(BlockPos pos) {
        int r = activeRadiusChunks;
        return r >= 0 &&
               Math.abs((pos.getX() >> 4) - cameraChunkX) <= r &&
               Math.abs((pos.getZ() >> 4) - cameraChunkZ) <= r;
    }

    static void beginVeilTessellation() { IN_VEIL_PASS.set(true); }
    static void endVeilTessellation() { IN_VEIL_PASS.remove(); }
    static boolean isExclusiveReady() { return exclusiveReady; }
    static void setExclusiveReady(boolean ready) { exclusiveReady = ready; }
    static void resetForFallback() {
        exclusiveReady = false;
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override public ResourceLocation getStillTexture() { return STILL; }
            @Override public ResourceLocation getFlowingTexture() { return FLOW; }
            @Override public int getTintColor() { return 0xFFFFFFFF; }
            @Override
            public boolean renderFluid(FluidState fluidState, BlockAndTintGetter getter,
                                       BlockPos pos, VertexConsumer consumer, BlockState blockState) {
                if (IN_VEIL_PASS.get()) return false;

                KNOWN_VOID.add(pos.asLong());
                return shaderAvailable() && exclusiveReady && withinShaderZone(pos);
            }
        }, ModFluids.ESSENCE_TYPE.get());
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(ModFluids.ESSENCE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(ModFluids.FLOWING_ESSENCE.get(), RenderType.translucent());
        });
    }
    private VoidFluidClient() {}
}

package net.wiselywisp.thebeneath.client.entity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.impl.client.render.pipeline.ShaderProgramShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.wiselywisp.thebeneath.TheBeneath;

public final class VoidRiftRenderType extends RenderType {
    private static final RenderType ORB = createOrb();
    private VoidRiftRenderType() {
        super("thebeneath:rift", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS,
                512, true, false, () -> {}, () -> {});
    }
    private static RenderType createOrb() {
        CompositeState state = CompositeState.builder()
                .setShaderState(new ShaderProgramShard(ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "void_rift")))
                .setTextureState(NO_TEXTURE)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setWriteMaskState(COLOR_WRITE)
                .createCompositeState(false);
        return create("thebeneath:void_rift_orb", DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS, 512, true, false, state);
    }
    public static RenderType orb() { return ORB; }
}

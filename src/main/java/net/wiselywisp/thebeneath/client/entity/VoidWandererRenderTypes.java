package net.wiselywisp.thebeneath.client.entity;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.impl.client.render.pipeline.ShaderProgramShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.wiselywisp.thebeneath.TheBeneath;

public final class VoidWandererRenderTypes extends RenderType {
    private static final ResourceLocation BODY_PROGRAM = ResourceLocation.fromNamespaceAndPath(
            TheBeneath.MOD_ID, "void_wanderer");
    private static final ResourceLocation ECHO_PROGRAM = ResourceLocation.fromNamespaceAndPath(
            TheBeneath.MOD_ID, "void_wanderer_echo");
    private static final RenderType VOID_BODY = createVoidPart("body", BODY_PROGRAM);
    private static final RenderType VOID_ECHO = createVoidPart("echo", ECHO_PROGRAM);

    private VoidWandererRenderTypes() {
        super("thebeneath:void_wanderer", DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS, 256, true, false, () -> {}, () -> {});
    }

    private static RenderType createVoidPart(String name, ResourceLocation program) {
        CompositeState state = CompositeState.builder()
                .setShaderState(new ShaderProgramShard(program))
                .setTextureState(NO_TEXTURE)
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false);
        return create("thebeneath:void_wanderer_" + name, DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS, 256, true, true, state);
    }

    public static RenderType body() { return VOID_BODY; }

    public static RenderType echo() { return VOID_ECHO; }
}

package net.wiselywisp.thebeneath.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.wiselywisp.thebeneath.entity.VoidWanderer;


final class VoidWandererEchoLayer extends RenderLayer<VoidWanderer, PlayerModel<VoidWanderer>> {
    private static final float[][] OFFSETS = {
            {-0.22F, 0.00F, 0.08F},
            { 0.20F,-0.025F,0.13F},
            {-0.39F, 0.02F, 0.17F}
    };


    private static final float SWAY_AMPLITUDE = 0.075F;
    private static final float JITTER_AMPLITUDE = 0.085F;

    VoidWandererEchoLayer(RenderLayerParent<VoidWanderer, PlayerModel<VoidWanderer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight,
                       VoidWanderer entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) return;

        for (int i = 0; i < OFFSETS.length; i++) {
            float[] shift = OFFSETS[i];
            float sway = (float) Math.sin(ageInTicks * (1.9F + i * 0.27F)
                    + i * 2.2F) * SWAY_AMPLITUDE;
            double signalFrame = Math.floor(ageInTicks * (1.10F + i * 0.13F));
            float jitter = (float) Math.sin(signalFrame * 12.9898D
                    + i * 78.233D) * JITTER_AMPLITUDE;

            poseStack.pushPose();
            poseStack.translate(shift[0] + sway + jitter, shift[1], shift[2]);
            this.getParentModel().renderToBuffer(poseStack,
                    buffers.getBuffer(VoidWandererRenderTypes.echo()),
                    0xF000F0, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}

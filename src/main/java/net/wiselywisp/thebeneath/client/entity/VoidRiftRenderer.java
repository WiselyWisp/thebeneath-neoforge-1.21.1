package net.wiselywisp.thebeneath.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.entity.VoidRift;


public final class VoidRiftRenderer extends EntityRenderer<VoidRift> {
    private static final ResourceLocation LENS_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            TheBeneath.MOD_ID, "textures/entity/void_rift_lens.png");
    private static final float WIDTH = 5.5F;
    private static final float HEIGHT = 3.6F;

    public VoidRiftRenderer(EntityRendererProvider.Context context) {
        super(context);
        shadowRadius = 0.0F;
    }

    @Override
    public void render(VoidRift rift, float yaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffers, int packedLight) {
        VoidRiftShaderClock.update();
        pose.pushPose();
        pose.translate(0.0D, 1.25D, 0.0D);

        pose.mulPose(this.entityRenderDispatcher.cameraOrientation());
        pose.scale(WIDTH, HEIGHT, 1.0F);


        drawQuad(buffers.getBuffer(RenderType.entityTranslucent(LENS_TEXTURE)), pose.last());

        pose.translate(0.0F, 0.0F, 0.002F);
        drawQuad(buffers.getBuffer(VoidRiftRenderType.orb()), pose.last());
        pose.popPose();
        super.render(rift, yaw, partialTick, pose, buffers, packedLight);
    }

    private static void drawQuad(VertexConsumer out, PoseStack.Pose matrices) {
        vertex(out, matrices, -0.5F, -0.5F, 0.0F, 1.0F);
        vertex(out, matrices,  0.5F, -0.5F, 1.0F, 1.0F);
        vertex(out, matrices,  0.5F,  0.5F, 1.0F, 0.0F);
        vertex(out, matrices, -0.5F,  0.5F, 0.0F, 0.0F);
    }

    private static void vertex(VertexConsumer out, PoseStack.Pose pose,
                               float x, float y, float u, float v) {
        out.addVertex(pose.pose(), x, y, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0x00F000F0)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(VoidRift entity) {
        return LENS_TEXTURE;
    }
}

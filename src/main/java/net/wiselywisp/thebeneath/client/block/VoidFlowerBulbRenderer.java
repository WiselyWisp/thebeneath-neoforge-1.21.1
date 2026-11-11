
package net.wiselywisp.thebeneath.client.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import net.wiselywisp.thebeneath.block.entity.custom.VoidFlowerBulbBlockEntity;

public class VoidFlowerBulbRenderer
        implements BlockEntityRenderer<VoidFlowerBulbBlockEntity> {

    private static final ResourceLocation EMISSIVE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "thebeneath",
                    "textures/block/void_flower_bulb_emissive.png"
            );
    private static final ResourceLocation GLOW_RENDER_TYPE =
            ResourceLocation.fromNamespaceAndPath(
                    "thebeneath",
                    "void_flower_bulb_glow"
            );
    private static final float MIN = -0.002F;
    private static final float MAX = 1.002F;

    public VoidFlowerBulbRenderer(
            BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(

            VoidFlowerBulbBlockEntity bulb,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            int packedOverlay) {

        RenderType renderType = VeilRenderType.get(
                GLOW_RENDER_TYPE,
                EMISSIVE_TEXTURE.toString()
        );


        if (renderType == null) {
            renderType = RenderType.entityTranslucent(
                    EMISSIVE_TEXTURE
            );
        }

        VertexConsumer consumer = buffer.getBuffer(renderType);

        PoseStack.Pose pose = poseStack.last();

        // Front
        quad(consumer, pose,
                MIN, MIN, MAX,
                MAX, MIN, MAX,
                MAX, MAX, MAX,
                MIN, MAX, MAX,
                0, 0, 1);

        // Back
        quad(consumer, pose,
                MAX, MIN, MIN,
                MIN, MIN, MIN,
                MIN, MAX, MIN,
                MAX, MAX, MIN,
                0, 0, -1);

        // Left
        quad(consumer, pose,
                MIN, MIN, MIN,
                MIN, MIN, MAX,
                MIN, MAX, MAX,
                MIN, MAX, MIN,
                -1, 0, 0);

        // Right
        quad(consumer, pose,
                MAX, MIN, MAX,
                MAX, MIN, MIN,
                MAX, MAX, MIN,
                MAX, MAX, MAX,
                1, 0, 0);

        // Top
        quad(consumer, pose,
                MIN, MAX, MAX,
                MAX, MAX, MAX,
                MAX, MAX, MIN,
                MIN, MAX, MIN,
                0, 1, 0);

        // Bottom
        quad(consumer, pose,
                MIN, MIN, MIN,
                MAX, MIN, MIN,
                MAX, MIN, MAX,
                MIN, MIN, MAX,
                0, -1, 0);
    }

    private static void quad(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            float nx, float ny, float nz) {

        vertex(consumer, pose, x1, y1, z1, 0, 1, nx, ny, nz);
        vertex(consumer, pose, x2, y2, z2, 1, 1, nx, ny, nz);
        vertex(consumer, pose, x3, y3, z3, 1, 0, nx, ny, nz);
        vertex(consumer, pose, x4, y4, z4, 0, 0, nx, ny, nz);
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x, float y, float z,
            float u, float v,
            float nx, float ny, float nz) {

        consumer.addVertex(pose.pose(), x, y, z)
                .setColor(255, 255, 255, 100)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, nx, ny, nz);
    }
}

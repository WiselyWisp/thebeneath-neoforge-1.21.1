package net.wiselywisp.thebeneath.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.entity.VoidWanderer;

public final class VoidWandererRenderer extends HumanoidMobRenderer<VoidWanderer, PlayerModel<VoidWanderer>> {
    private static final ResourceLocation SKIN = ResourceLocation.fromNamespaceAndPath(
            TheBeneath.MOD_ID, "textures/entity/void_wanderer.png");

    public VoidWandererRenderer(EntityRendererProvider.Context context) {
        super(context, new VoidWandererModel(context.bakeLayer(VoidWandererModel.LAYER)), 0.0F);
        this.addLayer(new VoidWandererEchoLayer(this));
    }

    @Override
    protected RenderType getRenderType(VoidWanderer entity, boolean bodyVisible,
                                       boolean translucent, boolean glowing) {
        return bodyVisible ? VoidWandererRenderTypes.body()
                : super.getRenderType(entity, bodyVisible, translucent, glowing);
    }

    @Override
    public void render(VoidWanderer entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        VoidWandererShaderClock.update();
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(VoidWanderer entity) {
        return SKIN;
    }
}

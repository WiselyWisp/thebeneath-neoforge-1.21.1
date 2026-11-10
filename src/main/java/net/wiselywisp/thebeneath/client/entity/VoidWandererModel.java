package net.wiselywisp.thebeneath.client.entity;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.entity.VoidWanderer;


public final class VoidWandererModel extends PlayerModel<VoidWanderer> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "void_wanderer"), "main");

    public VoidWandererModel(ModelPart root) {
        super(root, true);

        this.hat.visible = false;
        this.jacket.visible = false;
        this.leftSleeve.visible = false;
        this.rightSleeve.visible = false;
        this.leftPants.visible = false;
        this.rightPants.visible = false;
    }

    private static final float SLOW_WALK_CYCLE_RATE = 0.31F;
    private static final float SLOW_WALK_SWING = 0.30F;

    @Override
    public void setupAnim(VoidWanderer entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {

        float walkBlend = Mth.clamp(limbSwingAmount * 10.0F, 0.0F, 1.0F);
        super.setupAnim(entity, ageInTicks * SLOW_WALK_CYCLE_RATE,
                SLOW_WALK_SWING * walkBlend, ageInTicks, netHeadYaw, headPitch);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = PlayerModel.createMesh(CubeDeformation.NONE, true);
        return LayerDefinition.create(mesh, 64, 64);
    }
}

package net.wiselywisp.thebeneath.client.entity;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.entity.ModEntities;
import net.wiselywisp.thebeneath.block.entity.ModBlockEntities;
import net.wiselywisp.thebeneath.client.block.VoidPlushieRenderer;

@EventBusSubscriber(modid = TheBeneath.MOD_ID, value = Dist.CLIENT)
public final class ModEntityRenderers {
    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.VOID_WANDERER.get(), VoidWandererRenderer::new);
        event.registerEntityRenderer(ModEntities.VOID_RIFT.get(), VoidRiftRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MYSTERIOUS_PLUSHIE.get(), VoidPlushieRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(VoidWandererModel.LAYER, VoidWandererModel::createBodyLayer);
    }
}

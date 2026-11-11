
package net.wiselywisp.thebeneath.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.block.entity.ModBlockEntities;
import net.wiselywisp.thebeneath.client.block.VoidFlowerBulbRenderer;

@EventBusSubscriber(
        modid = TheBeneath.MOD_ID,
        value = Dist.CLIENT
)
public final class VoidFlowerBulbClient {

    private VoidFlowerBulbClient() {
    }

    @SubscribeEvent
    public static void registerRenderers(
            EntityRenderersEvent.RegisterRenderers event) {

        event.registerBlockEntityRenderer(
                ModBlockEntities.VOID_FLOWER_BULB.get(),
                VoidFlowerBulbRenderer::new
        );
    }
}

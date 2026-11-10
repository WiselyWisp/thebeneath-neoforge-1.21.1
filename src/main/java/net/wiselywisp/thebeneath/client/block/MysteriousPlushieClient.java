package net.wiselywisp.thebeneath.client.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.item.ModItems;


@EventBusSubscriber(modid = TheBeneath.MOD_ID, value = Dist.CLIENT)
public final class MysteriousPlushieClient {
    private MysteriousPlushieClient() {}

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private MysteriousPlushieItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    Minecraft minecraft = Minecraft.getInstance();
                    renderer = new MysteriousPlushieItemRenderer(
                            minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
                }
                return renderer;
            }
        }, ModItems.MYSTERIOUS_PLUSHIE.get());
    }
}

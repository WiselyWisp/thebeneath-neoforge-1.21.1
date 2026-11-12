package net.wiselywisp.thebeneath.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.block.ModBlocks;

@EventBusSubscriber(
        modid = TheBeneath.MOD_ID,
        value = Dist.CLIENT
)
public final class ShrublandClient {

    private ShrublandClient() {
    }

    @SubscribeEvent
    public static void registerBlockColors(
            RegisterColorHandlersEvent.Block event) {

        event.register(
                (state, level, pos, tintIndex) -> {

                    // Tint grass surfaces green.
                    if (tintIndex == 0) {
                        return 0x7EB358;
                    }

                    // Do not tint other texture layers.
                    return -1;
                },

                ModBlocks.SHRUBLAND_GRASS.get()
        );
    }
}
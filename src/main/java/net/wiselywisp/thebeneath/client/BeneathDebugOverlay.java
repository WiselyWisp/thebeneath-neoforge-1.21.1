package net.wiselywisp.thebeneath.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.world.dimension.ModDimensions;


@EventBusSubscriber(modid = TheBeneath.MOD_ID, value = Dist.CLIENT)
public final class BeneathDebugOverlay {
    private BeneathDebugOverlay() {}

    @SubscribeEvent
    public static void onDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null
                || client.level.dimension() != ModDimensions.THE_BENEATH_LEVEL_KEY) {
            return;
        }

        event.getLeft().clear();
        event.getRight().clear();
        event.getLeft().add("Error 404");
    }
}

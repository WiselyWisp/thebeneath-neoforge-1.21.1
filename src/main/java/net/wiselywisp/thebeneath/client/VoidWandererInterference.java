package net.wiselywisp.thebeneath.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.entity.VoidWanderer;


@EventBusSubscriber(modid = TheBeneath.MOD_ID, value = Dist.CLIENT)
public final class VoidWandererInterference {
    private static final double RANGE = 12.0D;
    private static final double RANGE_SQR = RANGE * RANGE;

    private VoidWandererInterference() {}

    @SubscribeEvent
    public static void onGui(RenderGuiLayerEvent.Post event) {

        if (!VanillaGuiLayers.CROSSHAIR.equals(event.getName())) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui || mc.player.isSpectator()) return;

        double nearestSqr = RANGE_SQR;
        AABB searchBox = mc.player.getBoundingBox().inflate(RANGE);
        for (VoidWanderer wanderer : mc.level.getEntitiesOfClass(VoidWanderer.class, searchBox)) {
            if (wanderer.isAlive()) {
                nearestSqr = Math.min(nearestSqr, wanderer.distanceToSqr(mc.player));
            }
        }
        if (nearestSqr >= RANGE_SQR) return;

        float strength = 1.0F - (float) Math.sqrt(nearestSqr) / (float) RANGE;
        GuiGraphics gui = event.getGuiGraphics();
        int w = gui.guiWidth();
        int h = gui.guiHeight();
        long frame = mc.level.getGameTime();

        int lines = 2 + (int) (strength * 10.0F);
        for (int i = 0; i < lines; i++) {
            long seed = frame * 1140071481932319849L + i * 6364136223846793005L;
            seed ^= seed >>> 29;
            int y = (int) Math.floorMod(seed, Math.max(h, 1));
            int x = (int) Math.floorMod(seed >>> 17, Math.max(w, 1));
            int length = 12 + (int) Math.floorMod(seed >>> 33, Math.max(20, w / 3));
            int alpha = 7 + (int) (strength * 25.0F);
            int shade = (seed & 1L) == 0L ? 0xFFFFFF : 0x131313;
            gui.fill(x, y, Math.min(w, x + length), y + 1, (alpha << 24) | shade);
        }

        if (strength > 0.55F && Math.floorMod(frame * 37L, 43L) == 0L) {
            int y = (int) Math.floorMod(frame * 67L, Math.max(h, 1));
            gui.fill(0, y, w, Math.min(h, y + 2), 0x1CFFFFFF);
        }
    }
}

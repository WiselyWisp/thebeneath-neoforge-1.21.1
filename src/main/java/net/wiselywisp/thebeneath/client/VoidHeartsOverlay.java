package net.wiselywisp.thebeneath.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.effect.ModEffects;
import net.wiselywisp.thebeneath.fluid.ModFluids;
import net.wiselywisp.thebeneath.fluid.VoidExposureHandler;

@EventBusSubscriber(modid = TheBeneath.MOD_ID, value = Dist.CLIENT)
public final class VoidHeartsOverlay {
    private static final ResourceLocation VOID_HEART =
            ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "textures/gui/void_heart.png");
    private static int exposureTicks = 0;
    private static net.minecraft.client.player.LocalPlayer lastPlayer = null;
    private static int recoveryElapsed = 0;
    private static int recoveryFrom = 0;

    private VoidHeartsOverlay() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            reset();
            lastPlayer = null;
            return;
        }

        if (lastPlayer != mc.player) {
            reset();
            lastPlayer = mc.player;
        }
        if (!mc.player.isAlive() || mc.player.isDeadOrDying()) {
            reset();
            return;
        }

        if (mc.player.isCreative() || mc.player.isSpectator()) {
            reset();
            return;
        }
        Vec3 pos = mc.player.position();
        boolean submerged = isEssence(mc, BlockPos.containing(pos.x, pos.y + 0.15, pos.z))
                || isEssence(mc, mc.player.blockPosition());
        if (submerged) {
            exposureTicks = Math.min(VoidExposureHandler.TOTAL_TICKS, exposureTicks + 1);
            recoveryElapsed = 0;
            recoveryFrom = 0;
        } else if (exposureTicks > 0) {
            if (recoveryFrom == 0) recoveryFrom = exposureTicks;
            recoveryElapsed++;
            exposureTicks = Math.max(0, recoveryFrom - (int) ((long) recoveryFrom * recoveryElapsed / VoidExposureHandler.RECOVERY_TICKS));
        }
    }

    private static void reset() {
        exposureTicks = 0;
        recoveryElapsed = 0;
        recoveryFrom = 0;
    }

    private static boolean isEssence(Minecraft mc, BlockPos pos) {
        Fluid fluid = mc.level.getFluidState(pos).getType();
        return fluid.isSame(ModFluids.ESSENCE.get()) || fluid.isSame(ModFluids.FLOWING_ESSENCE.get());
    }

    @SubscribeEvent
    public static void onHealthLayer(RenderGuiLayerEvent.Post event) {
        if (!VanillaGuiLayers.PLAYER_HEALTH.equals(event.getName())) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.player.isAlive() || mc.player.isDeadOrDying()
                || mc.options.hideGui || mc.player.isSpectator() || mc.player.isCreative()) return;

        int predicted = (exposureTicks * 10 + VoidExposureHandler.TOTAL_TICKS - 1)
                / VoidExposureHandler.TOTAL_TICKS;
        var status = mc.player.getEffect(ModEffects.VOID_CORRUPTION);
        int synced = status == null ? 0 : Math.min(10, Math.max(0, status.getAmplifier()));
        int blackHearts = Math.min(10, Math.max(predicted, synced));
        if (blackHearts == 0) return;

        int x = event.getGuiGraphics().guiWidth() / 2 - 91;
        int y = event.getGuiGraphics().guiHeight() - 39;

        for (int i = 0; i < blackHearts; i++) {
            event.getGuiGraphics().blit(VOID_HEART, x + i * 8, y, 0, 0, 9, 9, 9, 9);
        }
    }
}

package net.wiselywisp.thebeneath.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.minecraft.world.phys.Vec3;
import net.wiselywisp.thebeneath.effect.ModEffects;

public final class VoidExposureHandler {
    private static final String TICKS_KEY = "thebeneath_void_exposure_ticks";
    public static final int TOTAL_TICKS = 400;
    public static final int RECOVERY_TICKS = 600;
    private static final String RECOVERY_KEY = "thebeneath_void_recovery_elapsed";
    private static final String RECOVERY_FROM_KEY = "thebeneath_void_recovery_from";

    public static void tick(ServerPlayer player) {
        if (!player.isAlive() || player.isDeadOrDying()
                || player.isCreative() || player.isSpectator()) {
            clearCorruption(player);
            return;
        }
        Vec3 pos = player.position();
        boolean inFluid = player.serverLevel().getFluidState(BlockPos.containing(pos.x, pos.y + 0.15, pos.z)).getType().isSame(ModFluids.ESSENCE.get())
                || player.serverLevel().getFluidState(player.blockPosition()).getType().isSame(ModFluids.ESSENCE.get())
                || player.serverLevel().getFluidState(player.blockPosition()).getType().isSame(ModFluids.FLOWING_ESSENCE.get());
        int ticks = player.getPersistentData().getInt(TICKS_KEY);
        if (inFluid) {
            ticks = Math.min(TOTAL_TICKS, ticks + 1);
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 80, 0, true, false));
            if (ticks >= TOTAL_TICKS) {
                clearCorruption(player);
                player.kill();
                return;
            }
        } else if (ticks > 0) {

            int recoveryElapsed = player.getPersistentData().getInt(RECOVERY_KEY) + 1;
            player.getPersistentData().putInt(RECOVERY_KEY, recoveryElapsed);
            int recoveryFrom = player.getPersistentData().getInt(RECOVERY_FROM_KEY);
            if (recoveryFrom <= 0) {
                recoveryFrom = ticks;
                player.getPersistentData().putInt(RECOVERY_FROM_KEY, recoveryFrom);
            }
            ticks = Math.max(0, recoveryFrom - (int) ((long) recoveryFrom * recoveryElapsed / RECOVERY_TICKS));
        }
        if (inFluid) {
            player.getPersistentData().remove(RECOVERY_KEY);
            player.getPersistentData().remove(RECOVERY_FROM_KEY);
        }
        if (ticks <= 0) {
            player.getPersistentData().remove(TICKS_KEY);
            player.getPersistentData().remove(RECOVERY_KEY);
            player.getPersistentData().remove(RECOVERY_FROM_KEY);
            player.removeEffect(ModEffects.VOID_CORRUPTION);
            return;
        }
        player.getPersistentData().putInt(TICKS_KEY, ticks);
        int blackHearts = Math.min(10, (ticks * 10 + TOTAL_TICKS - 1) / TOTAL_TICKS);
        player.addEffect(new MobEffectInstance(ModEffects.VOID_CORRUPTION, 40, blackHearts, true, false, false));
    }

    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            clearCorruption(player);
        }
    }

    private static void clearCorruption(ServerPlayer player) {
        player.getPersistentData().remove(TICKS_KEY);
        player.getPersistentData().remove(RECOVERY_KEY);
        player.getPersistentData().remove(RECOVERY_FROM_KEY);
        player.removeEffect(ModEffects.VOID_CORRUPTION);
    }

    private VoidExposureHandler() {}
}

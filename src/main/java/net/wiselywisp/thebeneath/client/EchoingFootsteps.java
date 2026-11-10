package net.wiselywisp.thebeneath.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.sound.ModSounds;
import net.wiselywisp.thebeneath.world.dimension.ModDimensions;


@EventBusSubscriber(modid = TheBeneath.MOD_ID, value = Dist.CLIENT)
public final class EchoingFootsteps {
    private static final RandomSource RANDOM = RandomSource.create();
    private static LocalPlayer lastPlayer;
    private static Vec3 previousPosition;
    private static int nextCheckTicks = 320;
    private static int pendingTicks = -1;
    private static int secondStepTicks = -1;
    private static Vec3 soundPosition;
    private static float pitch;

    private EchoingFootsteps() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (mc.isPaused() || player == null || mc.level == null
                || !player.isAlive() || player.isSpectator()
                || !mc.level.dimension().equals(ModDimensions.THE_BENEATH_LEVEL_KEY)) {
            reset();
            return;
        }
        if (lastPlayer != player) {
            reset();
            lastPlayer = player;
        }

        Vec3 currentPosition = player.position();
        boolean walking = previousPosition != null
                && currentPosition.distanceToSqr(previousPosition) > 0.0004D
                && player.onGround() && !player.isPassenger()
                && !player.isSwimming() && !player.isFallFlying();
        Vec3 direction = previousPosition == null ? Vec3.ZERO
                : currentPosition.subtract(previousPosition).multiply(1, 0, 1);
        previousPosition = currentPosition;

        if (pendingTicks >= 0 && --pendingTicks < 0 && soundPosition != null) {
            play(mc, soundPosition, 0.46F, pitch);
            secondStepTicks = 7 + RANDOM.nextInt(5);
        }
        if (secondStepTicks >= 0 && --secondStepTicks < 0 && soundPosition != null) {
            play(mc, soundPosition.add((RANDOM.nextFloat() - 0.5F) * 0.6D, 0,
                    (RANDOM.nextFloat() - 0.5F) * 0.6D), 0.32F, pitch * 0.94F);
            soundPosition = null;
        }

        if (--nextCheckTicks > 0) return;
        nextCheckTicks = 220 + RANDOM.nextInt(281); // 11–25 seconds between checks.
        if (!walking || pendingTicks >= 0 || secondStepTicks >= 0 || RANDOM.nextFloat() >= 0.48F) return;

        Vec3 backward = direction.lengthSqr() > 0.00001D
                ? direction.normalize().scale(-1.0D)
                : player.getLookAngle().multiply(-1, 0, -1).normalize();

        double distance = 3.5D + RANDOM.nextDouble() * 2.5D;
        double lateral = (RANDOM.nextDouble() - 0.5D) * 2.5D;
        soundPosition = currentPosition.add(backward.x * distance - backward.z * lateral,
                0, backward.z * distance + backward.x * lateral);
        pitch = 0.84F + RANDOM.nextFloat() * 0.22F;
        pendingTicks = 9 + RANDOM.nextInt(13); // Delayed echo after the player moves.
    }

    private static void play(Minecraft mc, Vec3 pos, float volume, float playbackPitch) {

        mc.level.playLocalSound(pos.x, pos.y, pos.z, ModSounds.ECHO_FOOTSTEP.get(),
                SoundSource.AMBIENT, volume, playbackPitch, false);
    }

    private static void reset() {
        lastPlayer = null;
        previousPosition = null;
        nextCheckTicks = 320;
        pendingTicks = -1;
        secondStepTicks = -1;
        soundPosition = null;
    }
}

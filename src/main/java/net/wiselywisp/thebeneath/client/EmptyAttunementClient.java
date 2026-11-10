package net.wiselywisp.thebeneath.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.enchantment.EmptyAttunementNetwork;
import net.wiselywisp.thebeneath.enchantment.ModEnchantments;


@EventBusSubscriber(modid = TheBeneath.MOD_ID, value = Dist.CLIENT)
public final class EmptyAttunementClient {
    private static final double SWIM_SPEED = 0.19;
    private static boolean wasJumpHeld;
    private static boolean airSwimming;
    private static int activeSyncTicks;
    private static boolean releasedJumpWhileAirborne;
    private static LocalPlayer trackedPlayer;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;
        if (trackedPlayer != player) {
            trackedPlayer = player;
            wasJumpHeld = false;
            airSwimming = false;
            activeSyncTicks = 0;
            releasedJumpWhileAirborne = false;
        }

        boolean held = player.input != null && player.input.jumping;
        boolean eligible = player.input != null && ModEnchantments.canAirSwim(player);


        if (!eligible) {
            releasedJumpWhileAirborne = false;
            if (airSwimming) {
                airSwimming = false;
                activeSyncTicks = 0;
                PacketDistributor.sendToServer(new EmptyAttunementNetwork.Toggle(false));
            }
            wasJumpHeld = held;
            return;
        }

        if (!held) releasedJumpWhileAirborne = true;
        if (!airSwimming && releasedJumpWhileAirborne && held && !wasJumpHeld) {
            airSwimming = true;
            activeSyncTicks = 0;
            PacketDistributor.sendToServer(new EmptyAttunementNetwork.Toggle(true));
        }
        wasJumpHeld = held;
        if (!airSwimming) return;

        if (++activeSyncTicks >= 20) {
            activeSyncTicks = 0;
            PacketDistributor.sendToServer(new EmptyAttunementNetwork.Toggle(true));
        }

        Vec3 look = player.getLookAngle();
        Vec3 right = new Vec3(-look.z, 0.0, look.x);
        if (right.lengthSqr() > 1.0E-8) right = right.normalize();

        Vec3 direction = look.scale(player.input.forwardImpulse)
                .add(right.scale(-player.input.leftImpulse))
                .add(0.0, (held ? 1.0 : 0.0)
                        - (player.input.shiftKeyDown ? 1.0 : 0.0), 0.0);

        Vec3 velocity = direction.lengthSqr() > 1.0E-8
                ? direction.normalize().scale(SWIM_SPEED) : Vec3.ZERO;
        player.setDeltaMovement(velocity);
        player.setSwimming(true);
        player.setPose(Pose.SWIMMING);
        player.resetFallDistance();
    }

    private EmptyAttunementClient() {}
}

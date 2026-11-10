package net.wiselywisp.thebeneath.enchantment;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;


public final class EmptyAttunementServer {
    private static final String GRANTED_FLIGHT = "thebeneath:empty_attunement_granted_flight";
    private static final String ACTIVE = "thebeneath:empty_attunement_active";
    private static final String ACTIVE_TICKS = "thebeneath:empty_attunement_active_ticks";
    private static final int DAMAGE_INTERVAL_TICKS = 35; // One durability damage attempt every 35 active ticks.


    static void setActive(ServerPlayer player, boolean requested) {

        if (requested && ModEnchantments.canAirSwim(player)) {
            if (!player.getPersistentData().getBoolean(ACTIVE)) {
                player.getPersistentData().putInt(ACTIVE_TICKS, 0);
            }
            player.getPersistentData().putBoolean(ACTIVE, true);
        } else if (!requested) {
            deactivate(player);
        }
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        boolean wearing = ModEnchantments.equipped(player);
        boolean survival = !player.isCreative() && !player.isSpectator();
        boolean eligible = wearing && survival && ModEnchantments.canAirSwim(player);

        boolean granted = player.getPersistentData().getBoolean(GRANTED_FLIGHT);
        if (eligible && !player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.getPersistentData().putBoolean(GRANTED_FLIGHT, true);
            player.onUpdateAbilities();
            granted = true;
        }
        if (!eligible) {
            deactivate(player);
        }
        if ((!wearing || !survival || player.onGround()) && granted) {
            player.getPersistentData().remove(GRANTED_FLIGHT);
            if (survival) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
        }

        if (eligible && player.getAbilities().flying) {
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }

        if (eligible && player.getPersistentData().getBoolean(ACTIVE)) {
            player.setSwimming(true);
            player.resetFallDistance();

            int ticks = player.getPersistentData().getInt(ACTIVE_TICKS) + 1;
            if (ticks >= DAMAGE_INTERVAL_TICKS) {
                ticks = 0;
                ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

                if (boots.isDamageableItem()) {
                    boots.hurtAndBreak(1, player, EquipmentSlot.FEET);
                }
                if (!ModEnchantments.canAirSwim(player)) {
                    deactivate(player);
                    return;
                }
            }
            player.getPersistentData().putInt(ACTIVE_TICKS, ticks);
        }

    }

    private static void deactivate(ServerPlayer player) {
        player.getPersistentData().remove(ACTIVE);
        player.getPersistentData().remove(ACTIVE_TICKS);
    }

    private EmptyAttunementServer() {}
}

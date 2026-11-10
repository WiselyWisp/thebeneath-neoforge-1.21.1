package net.wiselywisp.thebeneath.enchantment;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.wiselywisp.thebeneath.TheBeneath;

public final class ModEnchantments {
    public static final ResourceKey<Enchantment> EMPTY_ATTUNEMENT = ResourceKey.create(
            Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "empty_attunement"));

    public static boolean equipped(Player player) {
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty()) return false;
        return player.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                .getHolder(EMPTY_ATTUNEMENT)
                .map(holder -> EnchantmentHelper.getItemEnchantmentLevel(holder, boots) > 0)
                .orElse(false);
    }

    public static boolean canAirSwim(Player player) {
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        return equipped(player) && boots.isDamageableItem()
                && player.isAlive() && !player.isSpectator()
                && !player.isPassenger() && !player.onGround()
                && !player.isCreative() && !player.isFallFlying() && !player.isInWater() && !player.isInLava();
    }

    private ModEnchantments() {}
}

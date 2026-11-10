package net.wiselywisp.thebeneath.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.wiselywisp.thebeneath.TheBeneath;

import java.util.List;

public class DummyLootItem extends Item {
    private static final TagKey<Structure> DIG_SITE = TagKey.create(
            Registries.STRUCTURE, ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "map"));

    public DummyLootItem(Properties properties, ResourceLocation unusedLootTable) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean selected) {
        if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof Player player)) return;
        if (slotId < 0 || slotId >= player.getInventory().getContainerSize()) return;
        if (player.getInventory().getItem(slotId) != stack) return;

        if (serverLevel.getGameTime() % 200 != 0) return;

        ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        BlockPos found = overworld.findNearestMapStructure(DIG_SITE, player.blockPosition(), 100, false);
        if (found == null) {
            TheBeneath.LOGGER.warn("Mysterious Map: no Ancient Dig Site found within 100 chunks of {}", player.blockPosition());
            return;
        }

        ItemStack map = MapItem.create(overworld, found.getX(), found.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(overworld, map);
        MapItemSavedData.addTargetDecoration(map, found, "+", MapDecorationTypes.TARGET_X);
        map.set(DataComponents.CUSTOM_NAME, Component.literal("Mysterious Map").withColor(0xFCFCFC));
        map.set(DataComponents.LORE, new ItemLore(List.of(
                Component.literal("//Footnote Y -60 ").withColor(0x8E8585))));
        player.getInventory().setItem(slotId, map);
        TheBeneath.LOGGER.info("Mysterious Map: located Ancient Dig Site at {} and generated filled map", found);
    }
}

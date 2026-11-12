package net.wiselywisp.thebeneath.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.wiselywisp.thebeneath.entity.ModEntities;
import net.wiselywisp.thebeneath.item.custom.FragileVoidBucket;
import net.wiselywisp.thebeneath.fluid.ModFluids;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.block.ModBlocks;
import net.wiselywisp.thebeneath.item.custom.DummyLootItem;
import net.wiselywisp.thebeneath.item.custom.MysteriousPlushieItem;
import net.wiselywisp.thebeneath.sound.ModSounds;
import net.wiselywisp.thebeneath.item.custom.OldNoteItem;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TheBeneath.MOD_ID);

    public static final DeferredItem<FragileVoidBucket> ESSENCE_BUCKET = ITEMS.register(
            "essence_of_the_void_bucket", () -> new FragileVoidBucket(ModFluids.ESSENCE.get(),
                    new Item.Properties().stacksTo(1)));

    public static final DeferredItem<MysteriousPlushieItem> MYSTERIOUS_PLUSHIE = ITEMS.register(
            "mysterious_plushie", () -> new MysteriousPlushieItem(ModBlocks.MYSTERIOUS_PLUSHIE.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> VOID_FLOWER_BULB = ITEMS.registerSimpleBlockItem(ModBlocks.VOID_FLOWER_BULB);
    public static final DeferredItem<BlockItem> VOID_FLOWER_STALK = ITEMS.registerSimpleBlockItem(ModBlocks.VOID_FLOWER_STALK);
    public static final DeferredItem<BlockItem> CRACKED_BEDROCK = ITEMS.registerSimpleBlockItem(ModBlocks.CRACKED_BEDROCK);
    public static final DeferredItem<BlockItem> ANCIENT_LIGHT_ORB = ITEMS.registerSimpleBlockItem(ModBlocks.ANCIENT_LIGHT_ORB);
    public static final DeferredItem<BlockItem> STABLE_VOID_CORE = ITEMS.registerSimpleBlockItem(ModBlocks.STABLE_VOID_CORE);
    public static final DeferredItem<BlockItem> BEDROCK_REMOVER = ITEMS.registerSimpleBlockItem(ModBlocks.BEDROCK_REMOVER);
    public static final DeferredItem<BlockItem> BRITTLE_BEDROCK = ITEMS.registerSimpleBlockItem(ModBlocks.BRITTLE_BEDROCK);

    public static final DeferredItem<Item> WOZZY_DISC = ITEMS.register("wozzy_disc", () ->
            new Item(new Item.Properties().jukeboxPlayable(ModSounds.WOZZY_KEY).stacksTo(1)));

    public static final DeferredItem<DummyLootItem> EXPLORER_MAP = ITEMS.register("explorer_map", () ->
            new DummyLootItem(new Item.Properties().stacksTo(1),
                    ResourceLocation.fromNamespaceAndPath(TheBeneath.MOD_ID, "map")));

    public static final DeferredItem<SpawnEggItem> VOID_WANDERER_SPAWN_EGG = ITEMS.registerItem(
            "void_wanderer_spawn_egg", properties -> new SpawnEggItem(ModEntities.VOID_WANDERER.get(), 0x21152F, 0xB88AFF, properties));

    public static final DeferredItem<OldNoteItem> OLD_NOTE = ITEMS.register("old_note", () ->
            new OldNoteItem(new Item.Properties().stacksTo(1)));
    private ModItems() {}
}

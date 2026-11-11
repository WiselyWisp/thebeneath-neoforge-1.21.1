package net.wiselywisp.thebeneath.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.block.ModBlocks;

public final class ModItemGroup {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, TheBeneath.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> THE_BENEATH_GROUP =
            TABS.register("thebeneath", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemgroup.thebeneath"))
                    .icon(() -> new ItemStack(ModBlocks.CRACKED_BEDROCK.get()))
                    .displayItems((parameters, entries) -> {
                        entries.accept(ModItems.EXPLORER_MAP.get());
                        entries.accept(ModItems.MYSTERIOUS_PLUSHIE.get());
                        entries.accept(ModItems.VOID_WANDERER_SPAWN_EGG.get());
                        entries.accept(ModItems.ESSENCE_BUCKET.get());
                        entries.accept(ModBlocks.ANCIENT_LIGHT_ORB.get());
                        entries.accept(ModBlocks.VOID_FLOWER_BULB.get());
                        entries.accept(ModBlocks.VOID_FLOWER_STALK.get());
                        entries.accept(ModBlocks.CRACKED_BEDROCK.get());
                        entries.accept(ModItems.OLD_NOTE.get());
                    }).build());
    private ModItemGroup() {}
}

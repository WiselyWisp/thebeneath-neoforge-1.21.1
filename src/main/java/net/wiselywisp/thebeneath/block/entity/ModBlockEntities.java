package net.wiselywisp.thebeneath.block.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.block.ModBlocks;
import net.wiselywisp.thebeneath.block.entity.custom.SnackerBlockEntity;
import net.wiselywisp.thebeneath.block.entity.custom.VoidFlowerBulbBlockEntity;
import net.wiselywisp.thebeneath.block.entity.custom.VoidPlushieBlockEntity;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TheBeneath.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SnackerBlockEntity>> SNACKER_BE =
            BLOCK_ENTITIES.register("snacker_be", () -> BlockEntityType.Builder.of(
                    SnackerBlockEntity::new, ModBlocks.BEDROCK_REMOVER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VoidPlushieBlockEntity>> MYSTERIOUS_PLUSHIE =
            BLOCK_ENTITIES.register("mysterious_plushie", () -> BlockEntityType.Builder.of(
                    VoidPlushieBlockEntity::new, ModBlocks.MYSTERIOUS_PLUSHIE.get()).build(null));

    public static final DeferredHolder<
            BlockEntityType<?>,
            BlockEntityType<VoidFlowerBulbBlockEntity>>
            VOID_FLOWER_BULB = BLOCK_ENTITIES.register(
            "void_flower_bulb",
            () -> BlockEntityType.Builder.of(
                    VoidFlowerBulbBlockEntity::new,
                    ModBlocks.VOID_FLOWER_BULB.get()
            ).build(null)
    );
    private ModBlockEntities() {}
}

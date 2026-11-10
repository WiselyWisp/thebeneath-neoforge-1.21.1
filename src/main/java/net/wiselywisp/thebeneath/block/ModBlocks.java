package net.wiselywisp.thebeneath.block;

import net.minecraft.world.level.block.Block;
import net.wiselywisp.thebeneath.block.custom.VoidPlushieBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.wiselywisp.thebeneath.fluid.ModFluids;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.wiselywisp.thebeneath.TheBeneath;
import net.wiselywisp.thebeneath.block.custom.Ancient_Light_Orb;
import net.wiselywisp.thebeneath.block.custom.SnackerBlock;
import net.wiselywisp.thebeneath.block.custom.VoidFlower;
import net.wiselywisp.thebeneath.block.custom.VoidFlowerBulb;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TheBeneath.MOD_ID);

    public static final DeferredBlock<VoidPlushieBlock> MYSTERIOUS_PLUSHIE = BLOCKS.registerBlock(
            "mysterious_plushie", VoidPlushieBlock::new,
            BlockBehaviour.Properties.of().strength(0.4F).noOcclusion().noCollission());

    public static final DeferredBlock<LiquidBlock> ESSENCE_OF_THE_VOID = BLOCKS.registerBlock(
            "essence_of_the_void", properties -> new LiquidBlock(ModFluids.ESSENCE.get(), properties),
            BlockBehaviour.Properties.of().liquid().replaceable().noCollission().noLootTable().lightLevel(s -> 5));

    public static final DeferredBlock<VoidFlowerBulb> VOID_FLOWER_BULB = BLOCKS.registerBlock(
            "void_flower_bulb", VoidFlowerBulb::new,
            BlockBehaviour.Properties.of().instabreak().lightLevel(s -> 8).ignitedByLava().requiresCorrectToolForDrops().randomTicks());

    public static final DeferredBlock<VoidFlower> VOID_FLOWER_STALK = BLOCKS.registerBlock(
            "void_flower_stalk", VoidFlower::new,
            BlockBehaviour.Properties.of().instabreak().noOcclusion().ignitedByLava());

    public static final DeferredBlock<Block> CRACKED_BEDROCK = BLOCKS.registerSimpleBlock(
            "cracked_bedrock", BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F).noLootTable());

    public static final DeferredBlock<Ancient_Light_Orb> ANCIENT_LIGHT_ORB = BLOCKS.registerBlock(
            "ancient_light_orb", Ancient_Light_Orb::new,
            BlockBehaviour.Properties.of().strength(5.0F).lightLevel(s -> 10).requiresCorrectToolForDrops());

    public static final DeferredBlock<SnackerBlock> BEDROCK_REMOVER = BLOCKS.registerBlock(
            "bedrock_remover", SnackerBlock::new,
            BlockBehaviour.Properties.of().strength(-1.0F, 3600000.0F));

    public static final DeferredBlock<Ancient_Light_Orb> STABLE_VOID_CORE = BLOCKS.registerBlock(
            "stable_void_core", Ancient_Light_Orb::new,
            BlockBehaviour.Properties.of().strength(2, 3600000.0F).lightLevel(s -> 8));
}

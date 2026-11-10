package net.wiselywisp.thebeneath.world.gen;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class ReplaceBedrockProcessor extends StructureProcessor {
    public static final ReplaceBedrockProcessor INSTANCE = new ReplaceBedrockProcessor();
    public static final MapCodec<ReplaceBedrockProcessor> CODEC = MapCodec.unit(INSTANCE);
    private ReplaceBedrockProcessor() {}

    @Override public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader world, BlockPos origin, BlockPos pivot,
            StructureTemplate.StructureBlockInfo original,
            StructureTemplate.StructureBlockInfo current, StructurePlaceSettings settings) {
        if (world.getBlockState(current.pos()).is(Blocks.BEDROCK)) {
            return new StructureTemplate.StructureBlockInfo(current.pos(), original.state(), original.nbt());
        }
        return current;
    }
    @Override protected StructureProcessorType<?> getType() {
        return MyStructureProcessors.REPLACE_BEDROCK.get();
    }
}

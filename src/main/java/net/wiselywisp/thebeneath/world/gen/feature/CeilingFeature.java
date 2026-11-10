package net.wiselywisp.thebeneath.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class CeilingFeature extends Feature<NoneFeatureConfiguration> {
    public CeilingFeature(Codec<NoneFeatureConfiguration> codec) { super(codec); }
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        BlockPos origin = ctx.origin();
        int startX = origin.getX() & ~15;
        int startZ = origin.getZ() & ~15;
        int y = level.getMaxBuildHeight() - 1;
        for (int dx = 0; dx < 16; dx++) for (int dz = 0; dz < 16; dz++) {
            BlockPos ceilingPos = new BlockPos(startX + dx, y, startZ + dz);
            if (level.isEmptyBlock(ceilingPos)) {
                level.setBlock(ceilingPos, Blocks.BEDROCK.defaultBlockState(), 2);
            }
        }
        return true;
    }
}

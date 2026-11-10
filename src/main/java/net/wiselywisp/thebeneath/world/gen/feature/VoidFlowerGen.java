package net.wiselywisp.thebeneath.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.wiselywisp.thebeneath.block.custom.VoidFlowerBulb;

public class VoidFlowerGen extends Feature<NoneFeatureConfiguration> {
    public VoidFlowerGen(Codec<NoneFeatureConfiguration> codec) { super(codec); }
    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel world = ctx.level();
        BlockPos pos = ctx.origin();
        if (world.isEmptyBlock(pos) && world.getBlockState(pos.below()).is(Blocks.BEDROCK)) {
            VoidFlowerBulb.generate(world, pos, ctx.random(), 16);
            return true;
        }
        return false;
    }
}

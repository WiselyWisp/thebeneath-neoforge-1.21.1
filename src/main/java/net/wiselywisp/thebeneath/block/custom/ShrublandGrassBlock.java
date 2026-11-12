package net.wiselywisp.thebeneath.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShrublandGrassBlock extends GrassBlock {

    public static final MapCodec<GrassBlock> SHRUBLAND_CODEC =
            simpleCodec(ShrublandGrassBlock::new);

    public ShrublandGrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<GrassBlock> codec() {
        return SHRUBLAND_CODEC;
    }

    @Override
    public void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random) {


    }
}

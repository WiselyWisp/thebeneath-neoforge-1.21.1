package net.wiselywisp.thebeneath.block.custom;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.wiselywisp.thebeneath.block.ModBlocks;

public class VoidFlower extends ChorusPlantBlock {
    public static final MapCodec<ChorusPlantBlock> CODEC = simpleCodec(VoidFlower::new);

    public VoidFlower(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<ChorusPlantBlock> codec() {
        return CODEC;
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return withConnectionProperties(ctx.getLevel(), ctx.getClickedPos(), defaultBlockState());
    }

    public static BlockState withConnectionProperties(BlockGetter level, BlockPos pos, BlockState state) {
        Block plant = state.getBlock();
        Block bulb = ModBlocks.VOID_FLOWER_BULB.get();
        return state
                .setValue(DOWN, joins(level.getBlockState(pos.below()), plant, bulb, true))
                .setValue(UP, joins(level.getBlockState(pos.above()), plant, bulb, false))
                .setValue(NORTH, joins(level.getBlockState(pos.north()), plant, bulb, false))
                .setValue(EAST, joins(level.getBlockState(pos.east()), plant, bulb, false))
                .setValue(SOUTH, joins(level.getBlockState(pos.south()), plant, bulb, false))
                .setValue(WEST, joins(level.getBlockState(pos.west()), plant, bulb, false));
    }

    private static boolean joins(BlockState state, Block plant, Block bulb, boolean downward) {
        return state.is(plant) || state.is(bulb) || (downward && state.is(Blocks.BEDROCK));
    }

    @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
                                               LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
        return state.setValue(PROPERTY_BY_DIRECTION.get(direction),
                joins(neighbor, this, ModBlocks.VOID_FLOWER_BULB.get(), direction == Direction.DOWN));
    }

    @Override protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        boolean between = !level.getBlockState(pos.above()).isAir() && !below.isAir();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos next = pos.relative(dir);
            if (level.getBlockState(next).is(this)) {
                if (between) return false;
                BlockState support = level.getBlockState(next.below());
                if (support.is(this) || support.is(Blocks.BEDROCK)) return true;
            }
        }
        return below.is(this) || below.is(Blocks.BEDROCK);
    }
}

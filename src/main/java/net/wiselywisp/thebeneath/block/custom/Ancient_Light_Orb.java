package net.wiselywisp.thebeneath.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HeavyCoreBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Ancient_Light_Orb extends HeavyCoreBlock {
    private static final VoxelShape SHAPE = box(4.0, 4.0, 4.0, 12.0, 12.0, 12.0);
    public Ancient_Light_Orb(Properties properties) { super(properties); }
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
}

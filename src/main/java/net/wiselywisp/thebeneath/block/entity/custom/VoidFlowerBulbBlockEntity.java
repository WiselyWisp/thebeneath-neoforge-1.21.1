package net.wiselywisp.thebeneath.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.wiselywisp.thebeneath.block.entity.ModBlockEntities;

public class VoidFlowerBulbBlockEntity extends BlockEntity {

    public VoidFlowerBulbBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOID_FLOWER_BULB.get(), pos, state);
    }
}
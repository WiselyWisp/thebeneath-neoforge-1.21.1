package net.wiselywisp.thebeneath.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.wiselywisp.thebeneath.block.entity.ModBlockEntities;

public final class VoidPlushieBlockEntity extends BlockEntity {
    public VoidPlushieBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MYSTERIOUS_PLUSHIE.get(), pos, state);
    }
}

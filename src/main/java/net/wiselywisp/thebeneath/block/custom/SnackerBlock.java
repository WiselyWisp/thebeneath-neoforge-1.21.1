package net.wiselywisp.thebeneath.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.wiselywisp.thebeneath.block.entity.ModBlockEntities;
import net.wiselywisp.thebeneath.block.entity.custom.SnackerBlockEntity;
import org.jetbrains.annotations.Nullable;

public class SnackerBlock extends BaseEntityBlock {
    public static final MapCodec<SnackerBlock> CODEC = simpleCodec(SnackerBlock::new);
    public SnackerBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SnackerBlockEntity(pos, state);
    }
    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.SNACKER_BE.get(), SnackerBlockEntity::tick);
    }
}

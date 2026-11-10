package net.wiselywisp.thebeneath.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.wiselywisp.thebeneath.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class SnackerBlockEntity extends BlockEntity {
    private int ticksElapsed;
    public SnackerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SNACKER_BE.get(), pos, state);
    }
    public static void tick(Level world, BlockPos pos, BlockState state, SnackerBlockEntity be) {
        if (world.isClientSide) return;
        if (++be.ticksElapsed == 10) {
            world.updateNeighborsAt(pos, state.getBlock());
        } else if (be.ticksElapsed == 20) {
            if (!world.isEmptyBlock(pos.below())) world.destroyBlock(pos.below(), true);
            world.destroyBlock(pos, true);
        }
        be.setChanged();
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("TicksElapsed", ticksElapsed);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ticksElapsed = tag.getInt("TicksElapsed");
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
    @Nullable @Override public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

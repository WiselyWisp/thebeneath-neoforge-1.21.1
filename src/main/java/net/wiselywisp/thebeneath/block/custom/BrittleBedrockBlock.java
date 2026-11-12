package net.wiselywisp.thebeneath.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BrittleBedrockBlock extends Block {

    public static final MapCodec<BrittleBedrockBlock> CODEC =
            simpleCodec(BrittleBedrockBlock::new);

    public BrittleBedrockBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public void stepOn(Level level, BlockPos pos,
                       BlockState state, Entity entity) {


        if (!level.isClientSide && entity instanceof LivingEntity) {


            level.destroyBlock(pos, false);
        }

        super.stepOn(level, pos, state, entity);
    }
}
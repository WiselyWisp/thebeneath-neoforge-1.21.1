package net.wiselywisp.thebeneath.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.wiselywisp.thebeneath.block.ModBlocks;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.wiselywisp.thebeneath.block.entity.custom.VoidFlowerBulbBlockEntity;


public class VoidFlowerBulb extends Block implements EntityBlock {
    public static final MapCodec<VoidFlowerBulb> CODEC = simpleCodec(VoidFlowerBulb::new);
    public static final int MAX_AGE = 5;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_5;

    public VoidFlowerBulb(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }
    @Override protected MapCodec<? extends Block> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
    @Override protected boolean isRandomlyTicking(BlockState state) { return state.getValue(AGE) < MAX_AGE; }

    @Override protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (!level.getBlockState(pos.relative(dir)).isAir()) return true;
        }
        return false;
    }

    @Override protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!state.canSurvive(level, pos)) level.destroyBlock(pos, true);
    }

    @Override protected BlockState updateShape(BlockState state, Direction side, BlockState neighbor,
                                               LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) level.scheduleTick(pos, this, 1);
        return super.updateShape(state, side, neighbor, level, pos, neighborPos);
    }

    @Override protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        BlockPos above = pos.above();
        if (!level.isEmptyBlock(above) || above.getY() >= level.getMaxBuildHeight()) return;
        int age = state.getValue(AGE);
        if (age >= MAX_AGE) return;

        boolean growUp = false;
        boolean rooted = false;
        BlockState below = level.getBlockState(pos.below());
        if (below.is(Blocks.BEDROCK)) {
            growUp = true;
        } else if (below.is(ModBlocks.VOID_FLOWER_STALK.get())) {
            int length = 1;
            for (int i = 0; i < 4; i++) {
                BlockState down = level.getBlockState(pos.below(length + 1));
                if (!down.is(ModBlocks.VOID_FLOWER_STALK.get())) {
                    if (down.is(Blocks.BEDROCK)) rooted = true;
                    break;
                }
                length++;
            }
            growUp = length < 2 || length <= rand.nextInt(rooted ? 5 : 4);
        } else if (below.isAir()) {
            growUp = true;
        }

        if (growUp && surroundedByAir(level, above, null) && level.isEmptyBlock(pos.above(2))) {
            level.setBlock(pos, VoidFlower.withConnectionProperties(level, pos,
                    ModBlocks.VOID_FLOWER_STALK.get().defaultBlockState()), Block.UPDATE_CLIENTS);
            grow(level, above, age);
        } else if (age < 4) {
            int branches = rand.nextInt(4) + (rooted ? 1 : 0);
            boolean grown = false;
            for (int i = 0; i < branches; i++) {
                Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
                BlockPos branch = pos.relative(dir);
                if (level.isEmptyBlock(branch) && level.isEmptyBlock(branch.below())
                        && surroundedByAir(level, branch, dir.getOpposite())) {
                    grow(level, branch, age + 1);
                    grown = true;
                }
            }
            if (grown) {
                level.setBlock(pos, VoidFlower.withConnectionProperties(level, pos,
                        ModBlocks.VOID_FLOWER_STALK.get().defaultBlockState()), Block.UPDATE_CLIENTS);
            } else die(level, pos);
        } else die(level, pos);
    }

    private void grow(Level level, BlockPos pos, int age) {
        level.setBlock(pos, defaultBlockState().setValue(AGE, age), Block.UPDATE_CLIENTS);
        level.levelEvent(1033, pos, 0);
    }
    private void die(Level level, BlockPos pos) {
        level.setBlock(pos, defaultBlockState().setValue(AGE, 5), Block.UPDATE_CLIENTS);
        level.levelEvent(1034, pos, 0);
    }
    private static boolean surroundedByAir(LevelReader level, BlockPos pos, @Nullable Direction excluded) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (dir != excluded && !level.isEmptyBlock(pos.relative(dir))) return false;
        }
        return true;
    }


    public static void generate(WorldGenLevel level, BlockPos pos, RandomSource random, int size) {
        level.setBlock(pos, VoidFlower.withConnectionProperties(level, pos,
                ModBlocks.VOID_FLOWER_STALK.get().defaultBlockState()), Block.UPDATE_CLIENTS);
        growGenerated(level, pos, random, pos, size, 0);
    }
    private static void growGenerated(WorldGenLevel level, BlockPos pos, RandomSource random,
                                      BlockPos origin, int maxSize, int layer) {
        Block stalk = ModBlocks.VOID_FLOWER_STALK.get();
        int height = random.nextInt(4) + 1 + (layer == 0 ? 1 : 0);
        for (int j = 0; j < height; j++) {
            BlockPos current = pos.above(j + 1);
            if (current.getY() >= level.getMaxBuildHeight() || !surroundedByAir(level, current, null)) return;
            level.setBlock(current, VoidFlower.withConnectionProperties(level, current,
                    stalk.defaultBlockState()), Block.UPDATE_CLIENTS);
            level.setBlock(current.below(), VoidFlower.withConnectionProperties(level, current.below(),
                    stalk.defaultBlockState()), Block.UPDATE_CLIENTS);
        }
        boolean branched = false;
        if (layer < 4) {
            int amount = random.nextInt(4) + (layer == 0 ? 1 : 0);
            for (int n = 0; n < amount; n++) {
                Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                BlockPos branch = pos.above(height).relative(dir);
                if (Math.abs(branch.getX() - origin.getX()) < maxSize
                        && Math.abs(branch.getZ() - origin.getZ()) < maxSize
                        && level.isEmptyBlock(branch) && level.isEmptyBlock(branch.below())
                        && surroundedByAir(level, branch, dir.getOpposite())) {
                    branched = true;
                    level.setBlock(branch, VoidFlower.withConnectionProperties(level, branch,
                            stalk.defaultBlockState()), Block.UPDATE_CLIENTS);
                    BlockPos connection = branch.relative(dir.getOpposite());
                    level.setBlock(connection, VoidFlower.withConnectionProperties(level, connection,
                            stalk.defaultBlockState()), Block.UPDATE_CLIENTS);
                    growGenerated(level, branch, random, origin, maxSize, layer + 1);
                }
            }
        }
        if (!branched) level.setBlock(pos.above(height),
                ModBlocks.VOID_FLOWER_BULB.get().defaultBlockState().setValue(AGE, 5), Block.UPDATE_CLIENTS);
    }
    @Override protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide) level.destroyBlock(hit.getBlockPos(), true, projectile);
    }
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VoidFlowerBulbBlockEntity(pos, state);
    }
}

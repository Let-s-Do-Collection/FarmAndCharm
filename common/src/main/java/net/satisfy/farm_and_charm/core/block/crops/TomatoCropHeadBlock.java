package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class TomatoCropHeadBlock extends TomatoCropBlock implements BonemealableBlock {
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public TomatoCropHeadBlock(Properties properties) {
        super(properties, SHAPE);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(AGE, 0)
                .setValue(SUPPORTED, false));
    }

    public static int getMaxHeight(LevelAccessor level, BlockPos pos) {
        return isRopeAbove(level, pos) ? 4 : 2;
    }

    public static boolean canGrowInto(ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).isAir()
                && (isRopeAbove(level, pos) || getHeight(pos.below(), level) < getMaxHeight(level, pos));
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (getHeight(pos, level) > getMaxHeight(level, pos) && !isRopeAbove(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (level.getRawBrightness(pos, 0) >= 9
                && random.nextFloat() < 0.2F
                && canGrowInto(level, pos.above())) {
            boolean supported = isRopeAbove(level, pos.above());
            level.setBlockAndUpdate(pos.above(),
                    this.defaultBlockState().setValue(SUPPORTED, supported));
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction dir, BlockState neighbor,
                                           LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (dir == Direction.UP) {
            state = state.setValue(SUPPORTED, isRopeAbove(level, pos));
        }
        if ((dir == Direction.DOWN && !state.canSurvive(level, pos))
                || (getHeight(pos, level) > 2 && !isRopeAbove(level, pos))) {
            level.scheduleTick(pos, this, 1);
        }
        if (dir == Direction.UP && (neighbor.is(this) || neighbor.is(getBodyBlock()))) {
            return getBodyBlock().getStateForAge(state.getValue(AGE))
                    .setValue(SUPPORTED, isRopeAbove(level, pos));
        }
        return state;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (random.nextBoolean() && canGrowInto(level, pos.above())) {
            boolean supported = isRopeAbove(level, pos.above());
            level.setBlockAndUpdate(pos.above(),
                    this.defaultBlockState().setValue(SUPPORTED, supported));
            return;
        }
        if (canGrow(state)) {
            level.setBlockAndUpdate(pos,
                    getStateForAge(state.getValue(AGE) + 1).setValue(SUPPORTED, isRopeAbove(level, pos)));
        } else {
            dropTomatoes(level, pos, state);
        }
    }
}

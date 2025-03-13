package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.core.BlockPos;
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
        this.registerDefaultState(defaultBlockState().setValue(AGE, 0));
    }

    public static boolean canGrowInto(ServerLevel serverLevel, BlockPos blockPos) {
        return serverLevel.getBlockState(blockPos).isAir() && (isRopeAbove(serverLevel, blockPos) || getHeight(blockPos.below(), serverLevel) < getMaxHeight(serverLevel, blockPos));
    }

    public static int getMaxHeight(LevelAccessor levelAccessor, BlockPos blockPos) {
        return isRopeAbove(levelAccessor, blockPos) ? 4 : 2;
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        super.tick(blockState, serverLevel, blockPos, randomSource);
        if (getHeight(blockPos, serverLevel) > getMaxHeight(serverLevel, blockPos) && !isRopeAbove(serverLevel, blockPos)) {
            serverLevel.destroyBlock(blockPos, true);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return true;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        super.randomTick(blockState, serverLevel, blockPos, randomSource);
        if (serverLevel.getRawBrightness(blockPos, 0) >= 9) {
            if (randomSource.nextFloat() < 0.2F && canGrowInto(serverLevel, blockPos.above())) {
                serverLevel.setBlockAndUpdate(blockPos.above(), this.defaultBlockState());
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState blockState, net.minecraft.core.Direction direction, BlockState blockState2, net.minecraft.world.level.LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if ((direction == net.minecraft.core.Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos)) || (getHeight(blockPos, levelAccessor) > getMaxHeight(levelAccessor, blockPos) && !isRopeAbove(levelAccessor, blockPos))) {
            levelAccessor.scheduleTick(blockPos, this, 1);
        }
        if (direction != net.minecraft.core.Direction.UP || !blockState2.is(this) && !blockState2.is(getBodyBlock())) {
            return blockState;
        } else {
            return getBodyBlock().getStateForAge(blockState.getValue(AGE));
        }
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        if (randomSource.nextBoolean() && canGrowInto(serverLevel, blockPos.above())) {
            serverLevel.setBlockAndUpdate(blockPos.above(), this.defaultBlockState());
            return;
        }
        if (this.canGrow(blockState)) {
            serverLevel.setBlockAndUpdate(blockPos, getStateForAge(blockState.getValue(AGE) + 1));
        } else {
            dropTomatoes(serverLevel, blockPos, blockState);
        }
    }
}

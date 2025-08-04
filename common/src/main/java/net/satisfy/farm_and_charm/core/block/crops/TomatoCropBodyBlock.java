package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@SuppressWarnings("deprecation")
public class TomatoCropBodyBlock extends TomatoCropBlock implements BonemealableBlock {
    public static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public TomatoCropBodyBlock(BlockBehaviour.Properties arg) {
        super(arg, SHAPE);
    }

    private Optional<BlockPos> getHeadPos(BlockGetter blockGetter, BlockPos blockPos, Block block) {
        return BlockUtil.getTopConnectedBlock(blockGetter, blockPos, block, Direction.UP, ObjectRegistry.TOMATO_CROP.get());
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return new ItemStack(getHeadBlock());
    }

    @Override
    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        boolean bl = super.canBeReplaced(blockState, blockPlaceContext);
        return (!bl || !blockPlaceContext.getItemInHand().is(getHeadBlock().asItem())) && bl;
    }

    public @NotNull BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (direction == Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos)) {
            levelAccessor.scheduleTick(blockPos, this, 1);
        }
        TomatoCropHeadBlock hopsCropHeadBlock = getHeadBlock();
        if (direction == Direction.UP && !blockState2.is(this) && !blockState2.is(hopsCropHeadBlock)) {
            if (getHeight(blockPos, levelAccessor) > TomatoCropHeadBlock.getMaxHeight(levelAccessor, blockPos) - 1 && !isRopeAbove(levelAccessor, blockPos)) {
                levelAccessor.scheduleTick(blockPos, hopsCropHeadBlock, 1);
            }
            return hopsCropHeadBlock.getStateForAge(blockState.getValue(AGE));
        } else {
            return blockState;
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
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        Optional<BlockPos> optional = this.getHeadPos(serverLevel, blockPos, blockState.getBlock());
        if (optional.isPresent()) {
            BlockPos pos = optional.get();
            if (TomatoCropHeadBlock.canGrowInto(serverLevel, pos.above())) {
                serverLevel.setBlockAndUpdate(pos.above(), ObjectRegistry.TOMATO_CROP.get().defaultBlockState());
                return;
            }
        }
        if (this.canGrow(blockState)) {
            serverLevel.setBlockAndUpdate(blockPos, getStateForAge(blockState.getValue(AGE) + 1));
        } else {
            dropTomatoes(serverLevel, blockPos, blockState);
        }
    }
}
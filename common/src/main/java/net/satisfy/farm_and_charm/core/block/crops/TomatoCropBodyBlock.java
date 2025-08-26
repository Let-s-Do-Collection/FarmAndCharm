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

public class TomatoCropBodyBlock extends TomatoCropBlock implements BonemealableBlock {
    public static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public TomatoCropBodyBlock(BlockBehaviour.Properties properties) {
        super(properties, SHAPE);
    }

    private Optional<BlockPos> getHeadPos(BlockGetter level, BlockPos pos, Block block) {
        return BlockUtil.getTopConnectedBlock(level, pos, block, Direction.UP, ObjectRegistry.TOMATO_CROP.get());
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(getHeadBlock());
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
        boolean base = super.canBeReplaced(state, ctx);
        return (!base || !ctx.getItemInHand().is(getHeadBlock().asItem())) && base;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (dir == Direction.UP) {
            state = state.setValue(SUPPORTED, isRopeAbove(level, pos));
        }
        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
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
        Optional<BlockPos> head = getHeadPos(level, pos, state.getBlock());
        if (head.isPresent() && TomatoCropHeadBlock.canGrowInto(level, head.get().above())) {
            level.setBlockAndUpdate(head.get().above(), ObjectRegistry.TOMATO_CROP.get().defaultBlockState());
            return;
        }
        if (canGrow(state)) {
            level.setBlockAndUpdate(pos, getStateForAge(state.getValue(AGE) + 1));
        } else {
            dropTomatoes(level, pos, state);
        }
    }
}

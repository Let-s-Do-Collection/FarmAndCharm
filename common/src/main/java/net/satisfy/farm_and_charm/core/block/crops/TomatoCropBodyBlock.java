package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
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

public class TomatoCropBodyBlock extends ClimbingCropBlock implements BonemealableBlock {
    public static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public TomatoCropBodyBlock(BlockBehaviour.Properties properties) {
        super(properties, SHAPE);
    }

    @Override
    protected ItemLike getRipeItem() {
        return ObjectRegistry.TOMATO.get();
    }

    @Override
    protected ItemLike getRottenItem() {
        return ObjectRegistry.ROTTEN_TOMATO.get();
    }

    @Override
    protected int getHarvestResetAge(Level level, BlockPos pos, BlockState state) {
        return level.getBlockState(pos.above()).is(ObjectRegistry.TOMATO_CROP.get()) ? 2 : 1;
    }

    private Optional<BlockPos> getHeadPos(LevelReader level, BlockPos pos, BlockState state) {
        return BlockUtil.getTopConnectedBlock(level, pos, state.getBlock(), Direction.UP, ObjectRegistry.TOMATO_CROP.get());
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(ObjectRegistry.TOMATO_CROP.get());
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
        boolean base = super.canBeReplaced(state, ctx);
        return (!base || !ctx.getItemInHand().is(ObjectRegistry.TOMATO_CROP.get().asItem())) && base;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (dir == Direction.UP) {
            boolean inherit = neighbor.getBlock() instanceof ClimbingCropBlock && neighbor.hasProperty(SUPPORTED) && neighbor.getValue(SUPPORTED);
            state = state.setValue(SUPPORTED, state.getValue(SUPPORTED) || inherit);
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
        Optional<BlockPos> head = getHeadPos(level, pos, state);
        if (head.isPresent()) {
            BlockPos hp = head.get();
            if (TomatoCropHeadBlock.getHeight(hp, level) < TomatoCropHeadBlock.getMaxHeight(level, hp) && level.getBlockState(hp.above()).isAir()) {
                BlockState headState = level.getBlockState(hp);
                boolean supported = headState.hasProperty(SUPPORTED) && headState.getValue(SUPPORTED);
                BlockState body = ObjectRegistry.TOMATO_CROP_BODY.get().defaultBlockState()
                        .setValue(AGE, headState.getValue(AGE))
                        .setValue(SUPPORTED, supported);
                level.setBlock(hp, body, 2);
                level.setBlockAndUpdate(hp.above(), ObjectRegistry.TOMATO_CROP.get().defaultBlockState().setValue(SUPPORTED, supported));
                return;
            }
        }
        if (state.getValue(AGE) < 4) {
            boolean supported = state.getValue(SUPPORTED);
            level.setBlockAndUpdate(pos, state.setValue(AGE, state.getValue(AGE) + 1).setValue(SUPPORTED, supported));
        } else {
            dropFruits(level, pos, state);
        }
    }
}

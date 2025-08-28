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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.RopeBlock;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class TomatoCropBodyBlock extends ClimbingCropBlock implements BonemealableBlock {
    public static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;

    public TomatoCropBodyBlock(BlockBehaviour.Properties properties) {
        super(properties, SHAPE);
        this.registerDefaultState(this.defaultBlockState().setValue(AGE, 0).setValue(SUPPORTED, false));
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected int getMaxAge() {
        return 4;
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
        if (dir == Direction.DOWN && !state.canSurvive(level, pos)) {
            level.scheduleTick(pos, this, 1);
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
        Optional<BlockPos> headOpt = BlockUtil.getTopConnectedBlock(level, pos, state.getBlock(), Direction.UP, ObjectRegistry.TOMATO_CROP.get());
        if (headOpt.isPresent()) {
            BlockPos headPos = headOpt.get();
            BlockState headState = level.getBlockState(headPos);
            BlockPos up = headPos.above();
            BlockState rope = level.getBlockState(up);

            boolean ropeOk =
                    rope.getBlock() instanceof RopeBlock
                            && (!rope.hasProperty(RopeBlock.DOWN) || rope.getValue(RopeBlock.DOWN))
                            && (!rope.hasProperty(RopeBlock.CENTER_PIECE) || rope.getValue(RopeBlock.CENTER_PIECE))
                            && (!rope.hasProperty(RopeBlock.ROPE_KNOT) || !rope.getValue(RopeBlock.ROPE_KNOT))
                            && (!rope.hasProperty(RopeBlock.SUPPORTING_ROPE_KNOT) || !rope.getValue(RopeBlock.SUPPORTING_ROPE_KNOT));

            if (headState.hasProperty(SUPPORTED)
                    && headState.getValue(SUPPORTED)
                    && ropeOk
                    && TomatoCropHeadBlock.getHeight(headPos, level) < TomatoCropHeadBlock.getMaxHeight(level, headPos)) {
                boolean supported = headState.getValue(SUPPORTED);
                BlockState bodyAtHead = ObjectRegistry.TOMATO_CROP_BODY.get()
                        .defaultBlockState()
                        .setValue(AGE, headState.getValue(TomatoCropHeadBlock.AGE))
                        .setValue(SUPPORTED, supported);
                level.setBlock(headPos, bodyAtHead, 2);
                level.setBlockAndUpdate(up, ObjectRegistry.TOMATO_CROP.get()
                        .defaultBlockState()
                        .setValue(TomatoCropHeadBlock.AGE, 0)
                        .setValue(SUPPORTED, supported));
                return;
            }
        }

        if (state.getValue(AGE) < getMaxAge()) {
            boolean supported = state.getValue(SUPPORTED);
            level.setBlockAndUpdate(pos, state.setValue(AGE, state.getValue(AGE) + 1).setValue(SUPPORTED, supported));
        } else {
            dropFruits(level, pos, state);
        }
    }
}

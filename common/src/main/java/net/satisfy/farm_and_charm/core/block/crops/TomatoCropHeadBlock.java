package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.RopeBlock;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class TomatoCropHeadBlock extends ClimbingCropBlock implements BonemealableBlock {
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;

    public TomatoCropHeadBlock(Properties properties) {
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
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (below.getBlock() instanceof TomatoCropHeadBlock || below.getBlock() instanceof TomatoCropBodyBlock) {
            return false;
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    protected ItemLike getRipeItem() {
        return ObjectRegistry.TOMATO.get();
    }

    @Override
    protected ItemLike getRottenItem() {
        return ObjectRegistry.ROTTEN_TOMATO.get();
    }

    public static int getMaxHeight(LevelAccessor level, BlockPos pos) {
        BlockState self = level.getBlockState(pos);
        boolean supportedFlag = self.hasProperty(SUPPORTED) && self.getValue(SUPPORTED);
        boolean ropeAbove = level.getBlockState(pos.above()).getBlock() instanceof RopeBlock
                || level.getBlockState(pos.above(2)).getBlock() instanceof RopeBlock;
        return (supportedFlag || ropeAbove) ? 4 : 2;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (getHeight(pos, level) > getMaxHeight(level, pos)) {
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
        if (level.getRawBrightness(pos, 0) >= 9 && random.nextFloat() < 0.2F) {
            BlockPos up = pos.above();
            BlockState rope = level.getBlockState(up);

            boolean ropeOk =
                    rope.getBlock() instanceof RopeBlock
                            && (!rope.hasProperty(RopeBlock.DOWN) || rope.getValue(RopeBlock.DOWN))
                            && (!rope.hasProperty(RopeBlock.CENTER_PIECE) || rope.getValue(RopeBlock.CENTER_PIECE))
                            && (!rope.hasProperty(RopeBlock.ROPE_KNOT) || !rope.getValue(RopeBlock.ROPE_KNOT))
                            && (!rope.hasProperty(RopeBlock.SUPPORTING_ROPE_KNOT) || !rope.getValue(RopeBlock.SUPPORTING_ROPE_KNOT));

            if (state.getValue(SUPPORTED) && ropeOk && getHeight(pos, level) < getMaxHeight(level, pos)) {
                boolean supported = state.getValue(SUPPORTED);
                BlockState body = ObjectRegistry.TOMATO_CROP_BODY.get()
                        .defaultBlockState()
                        .setValue(TomatoCropBodyBlock.AGE, state.getValue(AGE))
                        .setValue(SUPPORTED, supported);
                level.setBlock(pos, body, 2);
                level.setBlockAndUpdate(up, this.defaultBlockState().setValue(AGE, 0).setValue(SUPPORTED, supported));
                return;
            }

            if (state.getValue(AGE) < getMaxAge()) {
                level.setBlockAndUpdate(pos, state.setValue(AGE, state.getValue(AGE) + 1));
            }
        }
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos up = pos.above();
        BlockState rope = level.getBlockState(up);

        boolean ropeOk =
                rope.getBlock() instanceof RopeBlock
                        && (!rope.hasProperty(RopeBlock.DOWN) || rope.getValue(RopeBlock.DOWN))
                        && (!rope.hasProperty(RopeBlock.CENTER_PIECE) || rope.getValue(RopeBlock.CENTER_PIECE))
                        && (!rope.hasProperty(RopeBlock.ROPE_KNOT) || !rope.getValue(RopeBlock.ROPE_KNOT))
                        && (!rope.hasProperty(RopeBlock.SUPPORTING_ROPE_KNOT) || !rope.getValue(RopeBlock.SUPPORTING_ROPE_KNOT));

        if (state.getValue(SUPPORTED) && ropeOk && getHeight(pos, level) < getMaxHeight(level, pos)) {
            boolean supported = state.getValue(SUPPORTED);
            BlockState body = ObjectRegistry.TOMATO_CROP_BODY.get()
                    .defaultBlockState()
                    .setValue(TomatoCropBodyBlock.AGE, state.getValue(AGE))
                    .setValue(SUPPORTED, supported);
            level.setBlock(pos, body, 2);
            level.setBlockAndUpdate(up, this.defaultBlockState().setValue(AGE, 0).setValue(SUPPORTED, supported));
            return;
        }

        if (state.getValue(AGE) < getMaxAge()) {
            level.setBlockAndUpdate(pos, state.setValue(AGE, state.getValue(AGE) + 1));
        } else {
            dropFruits(level, pos, state);
        }
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
}

package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.RopeBlock;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class ClimbingCropBlock extends Block {
    public static final BooleanProperty SUPPORTED = BooleanProperty.create("supported");
    protected final VoxelShape shape;

    protected ClimbingCropBlock(BlockBehaviour.Properties properties, VoxelShape shape) {
        super(properties);
        this.shape = shape;
    }

    protected abstract IntegerProperty getAgeProperty();
    protected abstract int getMaxAge();
    protected abstract ItemLike getRipeItem();
    protected abstract ItemLike getRottenItem();

    protected float getRottenChance() { return 0.05F; }
    protected int getHarvestResetAge(Level level, BlockPos pos, BlockState state) { return 1; }

    protected static boolean isRopeAbove(LevelAccessor level, BlockPos pos) {
        BlockPos up = pos.above();
        BlockState s = level.getBlockState(up);
        if (s.getBlock() instanceof RopeBlock) return true;
        return s.isFaceSturdy(level, up, Direction.DOWN);
    }

    protected static int getHeight(BlockPos pos, LevelAccessor level) {
        int h = 0;
        while (level.getBlockState(pos.below(h)).getBlock() instanceof ClimbingCropBlock) {
            h++;
        }
        return h;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(getAgeProperty(), 0).setValue(SUPPORTED, false);
    }

    public @NotNull BlockState getStateForAge(int age) {
        return this.defaultBlockState().setValue(getAgeProperty(), Math.min(age, getMaxAge())).setValue(SUPPORTED, false);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return mayPlaceOn(belowState) || belowState.getBlock() instanceof ClimbingCropBlock;
    }

    protected boolean mayPlaceOn(BlockState state) {
        return state.is(Blocks.FARMLAND) || state.is(ObjectRegistry.FERTILIZED_FARM_BLOCK.get());
    }

    protected boolean canGrow(BlockState state) {
        return state.getValue(getAgeProperty()) < getMaxAge();
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionHand hand = player.getUsedItemHand();
        if (player.getItemInHand(hand).is(Items.BONE_MEAL)) return InteractionResult.PASS;
        int age = state.getValue(getAgeProperty());
        if (age == getMaxAge()) {
            dropFruits(level, pos, state);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(ObjectRegistry.ROPE.get().asItem()) && !state.getValue(SUPPORTED)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(SUPPORTED, true), 2);
                if (!player.getAbilities().instabuild) stack.shrink(1);
                level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8F, 1.0F);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (!level.isClientSide) {
                BlockPos p = pos.above();
                BlockState s = level.getBlockState(p);
                while (s.getBlock() instanceof ClimbingCropBlock) {
                    level.destroyBlock(p, true);
                    p = p.above();
                    s = level.getBlockState(p);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    protected void dropFruits(Level level, BlockPos blockPos, BlockState blockState) {
        int age = blockState.getValue(getAgeProperty());
        int amount = level.getRandom().nextInt(2) + (age >= getMaxAge() ? 1 : 0);
        ItemStack drop = level.getRandom().nextFloat() < getRottenChance()
                ? new ItemStack(getRottenItem(), 1)
                : new ItemStack(getRipeItem(), amount);
        popResource(level, blockPos, drop);
        level.playSound(null, blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        int resetAge = getHarvestResetAge(level, blockPos, blockState);
        level.setBlock(blockPos, blockState.setValue(getAgeProperty(), resetAge), 2);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) level.destroyBlock(pos, true);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return canGrow(state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getRawBrightness(pos, 0) >= 9) {
            int age = state.getValue(getAgeProperty());
            if (age < getMaxAge() && random.nextFloat() < 0.2f) {
                boolean supported = state.getValue(SUPPORTED);
                level.setBlock(pos, state.setValue(getAgeProperty(), age + 1).setValue(SUPPORTED, supported), 2);
            }
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(getAgeProperty(), SUPPORTED);
    }
}

package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.RopeBlock;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public abstract class TomatoCropBlock extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    public static final BooleanProperty SUPPORTED = BooleanProperty.create("supported");
    private static final int MAX_AGE = 4;
    protected final VoxelShape shape;

    protected TomatoCropBlock(BlockBehaviour.Properties properties, VoxelShape shape) {
        super(properties);
        this.shape = shape;
    }

    public static TomatoCropHeadBlock getHeadBlock() {
        return (TomatoCropHeadBlock) ObjectRegistry.TOMATO_CROP.get();
    }

    public static TomatoCropBodyBlock getBodyBlock() {
        return (TomatoCropBodyBlock) ObjectRegistry.TOMATO_CROP_BODY.get();
    }

    protected static boolean isRopeAbove(LevelAccessor level, BlockPos pos) {
        return level.getBlockState(pos.above()).getBlock() instanceof RopeBlock;
    }

    protected static int getHeight(BlockPos pos, LevelAccessor level) {
        int height = 0;
        while (level.getBlockState(pos.below(height)).getBlock() instanceof TomatoCropBlock) {
            height++;
        }
        return height;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState above = level.getBlockState(pos.above());

        boolean supported = isRopeAbove(level, pos);
        BlockState base = !above.is(getHeadBlock()) && !above.is(getBodyBlock())
                ? this.defaultBlockState()
                : getBodyBlock().defaultBlockState();

        return base.setValue(AGE, 0).setValue(SUPPORTED, supported);
    }

    public @NotNull BlockState getStateForAge(int age) {
        return this.defaultBlockState().setValue(AGE, Math.min(age, MAX_AGE)).setValue(SUPPORTED, false);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return mayPlaceOn(belowState) || belowState.is(getHeadBlock()) || belowState.is(getBodyBlock());
    }

    protected boolean mayPlaceOn(BlockState state) {
        return state.is(Blocks.FARMLAND) || state.is(ObjectRegistry.FERTILIZED_FARM_BLOCK.get());
    }

    protected boolean canGrow(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionHand hand = player.getUsedItemHand();
        if (player.getItemInHand(hand).is(Items.BONE_MEAL)) return InteractionResult.PASS;
        int age = state.getValue(AGE);
        if (age == MAX_AGE) {
            dropTomatoes(level, pos, state);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }

    protected void dropTomatoes(Level level, BlockPos blockPos, BlockState blockState) {
        int age = blockState.getValue(AGE);
        int amount = level.getRandom().nextInt(2) + (age >= MAX_AGE ? 1 : 0);
        ItemStack drop = new ItemStack(ObjectRegistry.TOMATO.get(), amount);
        if (level.getRandom().nextFloat() < 0.05f) {
            drop = new ItemStack(ObjectRegistry.ROTTEN_TOMATO.get(), 1);
        }
        popResource(level, blockPos, drop);
        level.playSound(null, blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);

        int resetAge = 1;
        if (this instanceof TomatoCropBodyBlock && level.getBlockState(blockPos.above()).is(getHeadBlock())) {
            resetAge = 2;
        }
        level.setBlock(blockPos, blockState.setValue(AGE, resetAge), 2);
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
            int age = state.getValue(AGE);
            if (age < MAX_AGE && random.nextFloat() < 0.2f) {
                boolean supported = isRopeAbove(level, pos);
                level.setBlock(pos, getStateForAge(age + 1).setValue(SUPPORTED, supported), 2);
            }
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, SUPPORTED);
    }
}

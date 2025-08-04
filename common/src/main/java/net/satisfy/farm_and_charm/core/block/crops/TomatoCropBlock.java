package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.FERTILIZED_FARM_BLOCK;

@SuppressWarnings("deprecation")
public abstract class TomatoCropBlock extends Block {
    public static final IntegerProperty AGE;
    private static final int MAX_AGE = 4;

    static {
        AGE = BlockStateProperties.AGE_4;
    }

    protected final VoxelShape shape;

    protected TomatoCropBlock(Properties arg, VoxelShape shape) {
        super(arg);
        this.shape = shape;
    }

    public static TomatoCropHeadBlock getHeadBlock() {
        return (TomatoCropHeadBlock) ObjectRegistry.TOMATO_CROP.get();
    }

    public static TomatoCropBodyBlock getBodyBlock() {
        return (TomatoCropBodyBlock) ObjectRegistry.TOMATO_CROP_BODY.get();
    }

    @SuppressWarnings("unused")
    protected static boolean isRopeAbove(LevelAccessor levelAccessor, BlockPos blockPos) {
        return false;
    }

    protected static int getHeight(BlockPos blockPos, LevelAccessor levelAccessor) {
        int height = 0;
        while (levelAccessor.getBlockState(blockPos.below(height)).getBlock() instanceof TomatoCropBlock) {
            height++;
        }
        return height;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return this.shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = context.getLevel().getBlockState(context.getClickedPos().above());
        return !blockState.is(getHeadBlock()) && !blockState.is(getBodyBlock()) ? this.defaultBlockState() : getBodyBlock().defaultBlockState();
    }

    public @NotNull BlockState getStateForAge(int age) {
        return this.defaultBlockState().setValue(AGE, Math.min(age, MAX_AGE));
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        BlockPos belowPos = blockPos.relative(Direction.DOWN);
        BlockState belowState = levelReader.getBlockState(belowPos);
        return mayPlaceOn(belowState) || belowState.is(getHeadBlock()) || belowState.is(getBodyBlock());
    }

    protected boolean mayPlaceOn(BlockState blockState) {
        return blockState.is(Blocks.FARMLAND) || blockState.is(FERTILIZED_FARM_BLOCK.get());
    }

    protected boolean canGrow(BlockState blockState) {
        return blockState.getValue(AGE) < MAX_AGE;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        InteractionHand interactionHand = player.getUsedItemHand();
        if (player.getItemInHand(interactionHand).is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        }
        int age = blockState.getValue(AGE);
        if (age > 1) {
            dropTomatoes(level, blockPos, blockState);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return super.useWithoutItem(blockState, level, blockPos, player, blockHitResult);
        }
    }

    protected void dropTomatoes(Level level, BlockPos blockPos, BlockState blockState) {
        int age = blockState.getValue(AGE);
        int amount = level.getRandom().nextInt(2) + (age >= MAX_AGE ? 1 : 0);
        ItemStack drop = new ItemStack(ObjectRegistry.TOMATO.get(), amount);
        if (level.getRandom().nextFloat() < 0.05) {
            drop = new ItemStack(ObjectRegistry.ROTTEN_TOMATO.get(), 1);
        }
        popResource(level, blockPos, drop);
        level.playSound(null, blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
        level.setBlock(blockPos, blockState.setValue(AGE, 1), 2);
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!blockState.canSurvive(serverLevel, blockPos)) {
            serverLevel.destroyBlock(blockPos, true);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return canGrow(blockState);
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (serverLevel.getRawBrightness(blockPos, 0) >= 9) {
            int age = blockState.getValue(AGE);
            if (age < MAX_AGE) {
                if (randomSource.nextFloat() < 0.2) {
                    serverLevel.setBlock(blockPos, this.getStateForAge(age + 1), 2);
                }
            }
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return blockState.getFluidState().isEmpty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }
}

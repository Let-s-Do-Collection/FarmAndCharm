package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PileBlock extends Block {
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 0, 3);

    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 0.5, 16.0);

    private final Supplier<Item> stackedItem;

    public PileBlock(Properties properties, Supplier<Item> stackedItem) {
        super(properties);
        this.stackedItem = stackedItem;
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 0));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state;
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(LAYERS, 0);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int layers = state.getValue(LAYERS);

        if (stack.isEmpty() && player.isShiftKeyDown()) {
            if (!world.isClientSide) {
                ItemStack droppedStack = new ItemStack(stackedItem.get(), layers + 1);
                if (!player.getInventory().add(droppedStack)) {
                    popResource(world, pos, droppedStack);
                }
                world.removeBlock(pos, false);
            }
            return ItemInteractionResult.sidedSuccess(world.isClientSide);
        }

        if (stack.getItem() == stackedItem.get()) {
            if (layers < 3) {
                world.setBlock(pos, state.setValue(LAYERS, layers + 1), 3);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        VoxelShape shape = world.getBlockState(pos.below()).getShape(world, pos.below());
        return Block.isFaceFull(shape, Direction.UP);
    }

    public boolean tryAddLayer(Level world, BlockPos pos, BlockState state, Player player, ItemStack heldStack) {
        int layers = state.getValue(LAYERS);
        if (layers >= 3) return false;

        if (!player.getAbilities().instabuild) {
            if (heldStack.isEmpty()) return false;
            heldStack.shrink(1);
        }

        world.setBlock(pos, state.setValue(LAYERS, layers + 1), 3);
        return true;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(world, pos)) {
            if (world instanceof Level level && !level.isClientSide) {
                int layers = state.getValue(LAYERS);
                popResource(level, pos, new ItemStack(stackedItem.get(), layers + 1));
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }
}
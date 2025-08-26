package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.RopeKnotBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RopeKnotBlock extends BaseEntityBlock {
    public static final MapCodec<RopeKnotBlock> CODEC = simpleCodec(RopeKnotBlock::new);
    private static final VoxelShape POST = Block.box(6, 0, 6, 10, 16, 10);
    private static final VoxelShape KNOT = Block.box(5, 7, 5, 11, 15, 11);
    private static final VoxelShape SHAPE = Shapes.or(POST, KNOT);
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    public RopeKnotBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false).setValue(WEST, false));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RopeKnotBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof RopeKnotBlockEntity be) {
            BlockState held = be.getHeldBlock();
            if (held != null && !held.isAir()) {
                drops.add(new ItemStack(held.getBlock().asItem()));
            }
        }
        return drops;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof ShearsItem || player.isShiftKeyDown()) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof RopeKnotBlockEntity be) {
                popResource(level, pos, new ItemStack(ObjectRegistry.ROPE.get()));
                BlockState held = be.getHeldBlock();
                if (held != null && !held.isAir()) {
                    level.setBlock(pos, held, 11);
                } else {
                    level.removeBlock(pos, false);
                }
                level.playSound(player, pos, SoundEvents.LEASH_KNOT_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

                if (stack.getItem() instanceof ShearsItem) {
                    stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
                updateNeighbors(level, pos);
            }
            return level.isClientSide ? ItemInteractionResult.CONSUME : ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }


    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction dir, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (dir.getAxis().isHorizontal()) {
            boolean connected = connectsTo(neighborState, dir);
            state = state.setValue(getPropertyForDirection(dir), connected);
        }

        if (level.getBlockEntity(pos) instanceof RopeKnotBlockEntity be) {
            BlockState held = be.getHeldBlock();
            if (held != null) {
                BlockState updated = held.updateShape(dir, neighborState, level, pos, neighborPos);
                be.setHeldBlock(updated);
            }
        }

        return state;
    }

    private BooleanProperty getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST  -> EAST;
            case WEST  -> WEST;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    private boolean connectsTo(BlockState neighbor, Direction dirTowardNeighbor) {
        if (neighbor.getBlock() instanceof RopeBlock) {
            Direction needOnNeighbor = dirTowardNeighbor.getOpposite();
            return neighbor.getValue(ropeProp(needOnNeighbor));
        }
        return false;
    }

    private static BooleanProperty ropeProp(Direction dir) {
        return switch (dir) {
            case NORTH -> RopeBlock.NORTH;
            case SOUTH -> RopeBlock.SOUTH;
            case EAST  -> RopeBlock.EAST;
            case WEST  -> RopeBlock.WEST;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState state = this.defaultBlockState();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            state = state.setValue(getPropertyForDirection(dir), connectsTo(level.getBlockState(pos.relative(dir)), dir));
        }
        return state;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        if (!level.isClientSide && !state.is(oldState.getBlock())) {
            updateNeighbors(level, pos);
        }
        super.onPlace(state, level, pos, oldState, moved);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!level.isClientSide && !state.is(newState.getBlock())) {
            updateNeighbors(level, pos);
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    private void updateNeighbors(Level level, BlockPos pos) {
        level.updateNeighborsAt(pos, this);
        for (Direction d : Direction.Plane.HORIZONTAL) {
            BlockPos n = pos.relative(d);
            level.updateNeighborsAt(n, level.getBlockState(n).getBlock());
        }
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = SHAPE;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, Block.box(7, 11, 0, 9, 13, 5));
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, Block.box(7, 11, 11, 9, 13, 16));
        if (state.getValue(WEST))  shape = Shapes.or(shape, Block.box(0, 11, 7, 5, 13, 9));
        if (state.getValue(EAST))  shape = Shapes.or(shape, Block.box(11, 11, 7, 16, 13, 9));
        return shape;
    }
}

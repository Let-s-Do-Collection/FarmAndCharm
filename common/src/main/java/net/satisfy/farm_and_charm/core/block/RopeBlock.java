package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class RopeBlock extends Block {
    public static final MapCodec<RopeBlock> CODEC = simpleCodec(RopeBlock::new);

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty CENTER_PIECE = BooleanProperty.create("center_piece");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty SUPPORTING_ROPE_KNOT = BooleanProperty.create("supporting_rope_knot");
    public static final BooleanProperty ROPE_KNOT = BooleanProperty.create("rope_knot");

    private static final VoxelShape CONNECTION_NORTH = Block.box(7, 11, 0, 9, 13, 7);
    private static final VoxelShape CONNECTION_SOUTH = Block.box(7, 11, 9, 9, 13, 16);
    private static final VoxelShape CONNECTION_WEST = Block.box(0, 11, 7, 7, 13, 9);
    private static final VoxelShape CONNECTION_EAST = Block.box(9, 11, 7, 16, 13, 9);
    private static final VoxelShape CONNECTION_KNOT = Block.box(6, 10, 6, 10, 14, 10);
    private static final VoxelShape CONNECTION_CENTER = Block.box(7, 11, 7, 9, 13, 9);
    private static final VoxelShape CONNECTION_DOWN = Block.box(7, 0, 7, 9, 11, 9);
    private static final VoxelShape CONNECTION_UP = Block.box(7, 13, 7, 9, 16, 9);
    private static final VoxelShape CONNECTION_SUPPORTING_KNOT = Block.box(5, 8, 5, 11, 16, 11);
    private static final VoxelShape BOTTOM_SUPPORT = Block.box(7, 0, 7, 9, 1, 9);

    public RopeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST, false)
                .setValue(WEST, false)
                .setValue(UP, false)
                .setValue(DOWN, false)
                .setValue(CENTER_PIECE, false)
                .setValue(SUPPORTING_ROPE_KNOT, false)
                .setValue(ROPE_KNOT, false));
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, CENTER_PIECE, SUPPORTING_ROPE_KNOT, ROPE_KNOT);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();

        boolean north = canConnectTo(level, pos.north(), Direction.SOUTH);
        boolean south = canConnectTo(level, pos.south(), Direction.NORTH);
        boolean east = canConnectTo(level, pos.east(), Direction.WEST);
        boolean west = canConnectTo(level, pos.west(), Direction.EAST);
        boolean up = canConnectTo(level, pos.above(), Direction.DOWN);
        boolean down = canConnectTo(level, pos.below(), Direction.UP);

        boolean belowClimbingCrop = level.getBlockState(pos.below()).getBlock() instanceof ClimbingCropBlock;
        if (belowClimbingCrop) {
            down = true;
            up = true;
        }

        boolean hasHorizontal = north || south || east || west;
        boolean isCorner = (north || south) && (east || west);
        boolean hasVertical = up || down;
        boolean ropeKnot = hasHorizontal && (isCorner || hasVertical);
        boolean topSupport = level.getBlockState(pos.above()).isFaceSturdy(level, pos.above(), Direction.DOWN);

        BlockState state = this.defaultBlockState()
                .setValue(NORTH, north)
                .setValue(SOUTH, south)
                .setValue(EAST, east)
                .setValue(WEST, west)
                .setValue(UP, up)
                .setValue(DOWN, down)
                .setValue(CENTER_PIECE, true)
                .setValue(ROPE_KNOT, ropeKnot)
                .setValue(SUPPORTING_ROPE_KNOT, topSupport);

        if (hasNoConnection(state)) return null;
        if (!hasFixedAnchor(ctx.getLevel(), pos)) return null;
        return state;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction.getAxis().isHorizontal() || direction.getAxis().isVertical()) {
            boolean conn = canConnectTo(level, neighborPos, direction.getOpposite());
            state = state.setValue(prop(direction), conn);
            if (direction == Direction.UP) {
                boolean topSupport = neighbor.isFaceSturdy(level, neighborPos, Direction.DOWN);
                state = state.setValue(SUPPORTING_ROPE_KNOT, topSupport);
            }
            if (direction == Direction.DOWN && neighbor.getBlock() instanceof ClimbingCropBlock) {
                state = state.setValue(DOWN, true).setValue(UP, true);
            }

            boolean north = state.getValue(NORTH);
            boolean south = state.getValue(SOUTH);
            boolean east = state.getValue(EAST);
            boolean west = state.getValue(WEST);
            boolean up = state.getValue(UP);
            boolean down = state.getValue(DOWN);

            boolean hasHorizontal = north || south || east || west;
            boolean isCorner = (north || south) && (east || west);
            boolean hasVertical = up || down;
            boolean ropeKnot = hasHorizontal && (isCorner || hasVertical);

            state = state.setValue(ROPE_KNOT, ropeKnot);
            state = state.setValue(CENTER_PIECE, true);

            if (hasNoConnection(state) || !canSurvive(state, level, pos)) {
                level.scheduleTick(pos, this, 1);
            }
        }
        return state;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (hasNoConnection(state) || !canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return hasFixedAnchor(level, pos);
    }

    private boolean hasFixedAnchor(LevelReader level, BlockPos start) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> seen = new HashSet<>();
        queue.add(start);
        int steps = 0;
        while (!queue.isEmpty() && steps < 256) {
            BlockPos p = queue.poll();
            if (!seen.add(p)) continue;
            for (Direction d : Direction.values()) {
                BlockPos n = p.relative(d);
                BlockState s = level.getBlockState(n);
                if (s.getBlock() instanceof RopeKnotBlock) return true;
                if (s.getBlock() instanceof RopeBlock) {
                    if (!seen.contains(n)) queue.add(n);
                    continue;
                }
                if (d == Direction.UP) {
                    if (s.isFaceSturdy(level, n, Direction.DOWN)) return true;
                } else if (d != Direction.DOWN) {
                    if (s.isFaceSturdy(level, n, d.getOpposite())) return true;
                }
            }
            steps++;
        }
        return false;
    }

    private boolean hasNoConnection(BlockState state) {
        return !(state.getValue(NORTH) || state.getValue(SOUTH) || state.getValue(EAST) || state.getValue(WEST) || state.getValue(UP) || state.getValue(DOWN));
    }

    private BooleanProperty prop(Direction direction) {
        return switch (direction) {
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
            default -> NORTH;
        };
    }

    private boolean canConnectTo(LevelAccessor level, BlockPos neighborPos, Direction dirTowardNeighbor) {
        BlockState blockState = level.getBlockState(neighborPos);
        if (blockState.getBlock() instanceof RopeBlock) return true;
        if (blockState.getBlock() instanceof RopeKnotBlock) return true;
        if (blockState.getBlock() instanceof ClimbingCropBlock) {
            return dirTowardNeighbor == Direction.DOWN || dirTowardNeighbor == Direction.UP;
        }
        if (dirTowardNeighbor == Direction.UP) {
            if (blockState.getBlock() instanceof LanternBlock) return true;
            if (blockState.getBlock() instanceof BellBlock) return true;
            return blockState.isFaceSturdy(level, neighborPos, Direction.DOWN);
        }
        return blockState.isFaceSturdy(level, neighborPos, dirTowardNeighbor);
    }


    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        VoxelShape shape = CONNECTION_CENTER;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, CONNECTION_NORTH);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, CONNECTION_SOUTH);
        if (state.getValue(WEST)) shape = Shapes.or(shape, CONNECTION_WEST);
        if (state.getValue(EAST)) shape = Shapes.or(shape, CONNECTION_EAST);
        if (state.getValue(UP)) shape = Shapes.or(shape, CONNECTION_UP);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, CONNECTION_DOWN);
        if (state.getValue(ROPE_KNOT)) shape = Shapes.or(shape, CONNECTION_KNOT);
        if (state.getValue(SUPPORTING_ROPE_KNOT)) shape = Shapes.or(shape, CONNECTION_SUPPORTING_KNOT);
        return shape;
    }

    @Override
    protected @NotNull VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return Shapes.or(getShape(state, level, pos, CollisionContext.empty()), BOTTOM_SUPPORT);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext ctx) {
        VoxelShape shape = CONNECTION_CENTER;
        if (state.getValue(NORTH)) shape = Shapes.or(shape, CONNECTION_NORTH);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, CONNECTION_SOUTH);
        if (state.getValue(WEST)) shape = Shapes.or(shape, CONNECTION_WEST);
        if (state.getValue(EAST)) shape = Shapes.or(shape, CONNECTION_EAST);
        if (state.getValue(UP)) shape = Shapes.or(shape, CONNECTION_UP);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, CONNECTION_DOWN);
        if (state.getValue(ROPE_KNOT)) shape = Shapes.or(shape, CONNECTION_KNOT);
        if (state.getValue(SUPPORTING_ROPE_KNOT)) shape = Shapes.or(shape, CONNECTION_SUPPORTING_KNOT);
        return shape;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hit) {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}

package net.satisfy.farm_and_charm.core.block;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;

public class WaterTroughBlock extends LineConnectingBlock {
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);

    private static final VoxelShape SHAPE_MIDDLE_NORTH = createShape(false, false);
    private static final VoxelShape SHAPE_LEFT_NORTH = createShape(true, false);
    private static final VoxelShape SHAPE_RIGHT_NORTH = createShape(false, true);
    private static final VoxelShape SHAPE_SINGLE_NORTH = createShape(true, true);

    private static final EnumMap<Direction, VoxelShape> SHAPE_MIDDLE = createRotations(SHAPE_MIDDLE_NORTH);
    private static final EnumMap<Direction, VoxelShape> SHAPE_LEFT = createRotations(SHAPE_LEFT_NORTH);
    private static final EnumMap<Direction, VoxelShape> SHAPE_RIGHT = createRotations(SHAPE_RIGHT_NORTH);
    private static final EnumMap<Direction, VoxelShape> SHAPE_SINGLE = createRotations(SHAPE_SINGLE_NORTH);

    public WaterTroughBlock(Properties properties) {
        super(properties.randomTicks());
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LEVEL);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.WATER_BUCKET)) {
            if (level.isClientSide) return ItemInteractionResult.SUCCESS;

            List<BlockPos> component = getComponent(level, pos, state.getValue(FACING));
            int baseFill = getFillLevelForComponentSize(component.size());

            boolean hasAnyZero = false;
            for (BlockPos componentPos : component) {
                BlockState componentState = level.getBlockState(componentPos);
                if (componentState.getBlock() != this) continue;
                if (componentState.getValue(LEVEL) == 0) {
                    hasAnyZero = true;
                    break;
                }
            }

            boolean changed = false;

            if (hasAnyZero) {
                for (BlockPos componentPos : component) {
                    BlockState componentState = level.getBlockState(componentPos);
                    if (componentState.getBlock() != this) continue;
                    if (componentState.getValue(LEVEL) == baseFill) continue;
                    level.setBlock(componentPos, componentState.setValue(LEVEL, baseFill), 3);
                    changed = true;
                }
            } else {
                for (BlockPos componentPos : component) {
                    BlockState componentState = level.getBlockState(componentPos);
                    if (componentState.getBlock() != this) continue;
                    int currentLevel = componentState.getValue(LEVEL);
                    if (currentLevel >= 3) continue;
                    level.setBlock(componentPos, componentState.setValue(LEVEL, currentLevel + 1), 3);
                    changed = true;
                }
            }

            if (!player.getAbilities().instabuild && changed) {
                player.setItemInHand(hand, new ItemStack(Items.BUCKET));
            }

            return ItemInteractionResult.SUCCESS;
        }

        if (stack.is(Items.BUCKET)) {
            int currentLevel = state.getValue(LEVEL);
            if (currentLevel <= 0) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (level.isClientSide) return ItemInteractionResult.SUCCESS;

            boolean infinite = isInfiniteSource(level, pos, state.getValue(FACING));

            if (!player.getAbilities().instabuild) {
                player.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
            }

            if (!infinite) {
                level.setBlock(pos, state.setValue(LEVEL, Math.max(0, currentLevel - 1)), 3);
            }

            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int currentLevel = state.getValue(LEVEL);
        if (currentLevel >= 3) return;
        if (!level.isRainingAt(pos.above())) return;
        if (random.nextInt(18) != 0) return;
        level.setBlock(pos, state.setValue(LEVEL, currentLevel + 1), 3);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();

        boolean hasLeft = isSameTrough(world, pos.relative(left), facing);
        boolean hasRight = isSameTrough(world, pos.relative(right), facing);

        if (hasLeft && hasRight) return SHAPE_MIDDLE.get(facing);
        if (!hasLeft && !hasRight) return SHAPE_SINGLE.get(facing);
        if (!hasLeft) return SHAPE_LEFT.get(facing);
        return SHAPE_RIGHT.get(facing);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
        if (level instanceof Level realLevel && !realLevel.isClientSide) {
            realLevel.scheduleTick(pos, this, 1);
        }
        return updated;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        normalizeConnectedLevels(level, pos, state);
    }

    private void normalizeConnectedLevels(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() != this) return;

        Direction facing = state.getValue(FACING);
        List<BlockPos> component = getComponent(level, pos, facing);
        if (component.size() < 2) return;

        int minLevel = 3;
        int maxLevel = 0;
        boolean hasZero = false;

        for (BlockPos componentPos : component) {
            BlockState componentState = level.getBlockState(componentPos);
            if (componentState.getBlock() != this) continue;
            int current = componentState.getValue(LEVEL);
            if (current == 0) hasZero = true;
            if (current < minLevel) minLevel = current;
            if (current > maxLevel) maxLevel = current;
        }

        int target;
        if (maxLevel > 0 && hasZero) {
            target = Math.max(0, maxLevel - 1);
        } else {
            target = minLevel;
        }

        for (BlockPos componentPos : component) {
            BlockState componentState = level.getBlockState(componentPos);
            if (componentState.getBlock() != this) continue;
            if (componentState.getValue(LEVEL) == target) continue;
            level.setBlock(componentPos, componentState.setValue(LEVEL, target), 3);
        }
    }

    private static VoxelShape createShape(boolean leftLeg, boolean rightLeg) {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.or(shape, Shapes.box(0.0, 4.0 / 16.0, 0.0, 1.0, 12.0 / 16.0, 1.0));
        if (leftLeg) shape = Shapes.or(shape, Shapes.box(1.0 / 16.0, 0.0, 0.0, 5.0 / 16.0, 4.0 / 16.0, 1.0));
        if (rightLeg) shape = Shapes.or(shape, Shapes.box(11.0 / 16.0, 0.0, 0.0, 15.0 / 16.0, 4.0 / 16.0, 1.0));
        return shape;
    }

    private static EnumMap<Direction, VoxelShape> createRotations(VoxelShape northShape) {
        EnumMap<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, northShape));
        }
        return map;
    }

    private int getFillLevelForComponentSize(int size) {
        if (size <= 1) return 3;
        if (size == 2) return 2;
        return 1;
    }

    private boolean isInfiniteSource(Level level, BlockPos pos, Direction facing) {
        List<BlockPos> component = getComponent(level, pos, facing);
        if (component.size() < 3) return false;

        boolean usesX = facing.getAxis() == Direction.Axis.Z;

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (BlockPos componentPos : component) {
            int value = usesX ? componentPos.getX() : componentPos.getZ();
            if (value < min) min = value;
            if (value > max) max = value;
        }

        int self = usesX ? pos.getX() : pos.getZ();
        return self != min && self != max;
    }

    private List<BlockPos> getComponent(Level level, BlockPos start, Direction facing) {
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();

        HashSet<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        List<BlockPos> result = new ArrayList<>();

        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            result.add(current);

            for (Direction step : new Direction[]{left, right}) {
                BlockPos next = current.relative(step);
                if (!visited.add(next)) continue;

                BlockState nextState = level.getBlockState(next);
                if (nextState.getBlock() != this) continue;
                if (nextState.getValue(FACING) != facing) continue;

                queue.add(next);
            }
        }

        return result;
    }

    private boolean isSameTrough(BlockGetter world, BlockPos pos, Direction facing) {
        BlockState otherState = world.getBlockState(pos);
        if (otherState.getBlock() != this) return false;
        return otherState.getValue(FACING) == facing;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        int earthy = 0xFFD966;
        int gold = 0xFFD700;

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.farm_and_charm.water_trough.info_0")
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
        } else {
            tooltip.add(Component.translatable(
                    "tooltip.farm_and_charm.tooltip_information.hold",
                    Component.literal("[SHIFT]").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(gold)))
            ).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
        }
    }
}
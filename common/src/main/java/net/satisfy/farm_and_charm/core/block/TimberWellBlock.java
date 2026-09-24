package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.TimberWellBlockEntity;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.Util;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.List;

import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimberWellBlock extends FacingBlock implements EntityBlock {
    public static final MapCodec<TimberWellBlock> CODEC = simpleCodec(TimberWellBlock::new);
    public static final EnumProperty<TimberWellPart> PART = EnumProperty.create("part", TimberWellPart.class);
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);
    public static final int GROUNDWATER_DEPTH = 6;
    private static final int RAIN_FILL_CHANCE = 6;

    public TimberWellBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, TimberWellPart.FOOT).setValue(LEVEL, 0));
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos originPos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite().getClockWise();

        BlockPos headPos = originPos.relative(facing);
        BlockPos topPos = headPos.above();

        if (!level.getWorldBorder().isWithinBounds(headPos) || !level.getWorldBorder().isWithinBounds(topPos)) {
            return null;
        }
        if (topPos.getY() >= level.getMaxBuildHeight()) {
            return null;
        }
        if (!level.getBlockState(headPos).canBeReplaced(context)) {
            return null;
        }
        if (!level.getBlockState(topPos).canBeReplaced(context)) {
            return null;
        }

        return this.defaultBlockState().setValue(FACING, facing).setValue(PART, TimberWellPart.FOOT);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) {
            return;
        }

        Direction facing = state.getValue(FACING);
        BlockPos headPos = pos.relative(facing);
        BlockPos topPos = headPos.above();

        level.setBlock(topPos, state.setValue(PART, TimberWellPart.TOP), 3);
        level.setBlock(headPos, state.setValue(PART, TimberWellPart.HEAD), 3);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(Items.BUCKET)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (state.getValue(LEVEL) <= 0) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.farm_and_charm.timber_well.empty"), true);
            }
            return ItemInteractionResult.CONSUME;
        }

        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            ItemStack filledStack = ItemUtils.createFilledResult(stack, player, new ItemStack(Items.WATER_BUCKET));
            player.setItemInHand(hand, filledStack);
            setWaterLevel(level, pos, state, state.getValue(LEVEL) - 1);
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int currentLevel = state.getValue(LEVEL);
        BlockPos topPos = getFootPos(pos, state).relative(state.getValue(FACING)).above();
        if (currentLevel >= 3 || !(level.getBlockEntity(topPos) instanceof TimberWellBlockEntity well) || well.isPumping(level.getGameTime())) {
            return InteractionResult.PASS;
        }
        if (!hasGroundwater(level, pos, state)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.farm_and_charm.timber_well.no_groundwater"), true);
            }
            return InteractionResult.CONSUME;
        }
        if (!level.isClientSide) {
            level.blockEvent(topPos, this, TimberWellBlockEntity.PUMP_EVENT, 0);
            setWaterLevel(level, pos, state, currentLevel + 1);
            level.playSound(null, topPos, SoundEventRegistry.WELL_PUMP.get(), SoundSource.BLOCKS, 0.5F, 0.9F + level.random.nextFloat() * 0.2F);
            level.playSound(null, getFootPos(pos, state).relative(state.getValue(FACING)), SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.5F, 0.9F + level.random.nextFloat() * 0.2F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SPLASH, topPos.getX() + 0.5D, topPos.getY(), topPos.getZ() + 0.5D, 10, 0.15D, 0.05D, 0.15D, 0.02D);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static boolean hasWater(BlockState state) {
        return state.getValue(LEVEL) > 0;
    }

    public static boolean drink(Level level, BlockPos pos, BlockState state) {
        int currentLevel = state.getValue(LEVEL);
        if (currentLevel <= 0) {
            return false;
        }
        setWaterLevel(level, pos, state, currentLevel - 1);
        return true;
    }

    private static BlockPos getFootPos(BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        return switch (state.getValue(PART)) {
            case FOOT -> pos;
            case HEAD -> pos.relative(facing.getOpposite());
            case TOP -> pos.below().relative(facing.getOpposite());
        };
    }

    private static void setWaterLevel(Level level, BlockPos pos, BlockState state, int waterLevel) {
        BlockPos footPos = getFootPos(pos, state);
        BlockPos headPos = footPos.relative(state.getValue(FACING));
        for (BlockPos partPos : new BlockPos[]{footPos, headPos, headPos.above()}) {
            BlockState partState = level.getBlockState(partPos);
            if (partState.getBlock() instanceof TimberWellBlock && partState.getValue(LEVEL) != waterLevel) {
                level.setBlock(partPos, partState.setValue(LEVEL, waterLevel), 3);
            }
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == TimberWellPart.TOP ? new TimberWellBlockEntity(pos, state) : null;
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(id, param);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(PART) != TimberWellPart.FOOT) {
            return;
        }
        int currentLevel = state.getValue(LEVEL);
        BlockPos topPos = pos.relative(state.getValue(FACING)).above();
        if (currentLevel < 3 && level.isRainingAt(topPos.above()) && random.nextInt(RAIN_FILL_CHANCE) == 0) {
            setWaterLevel(level, pos, state, currentLevel + 1);
        }
    }

    public static boolean hasGroundwater(LevelReader level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(FACING);
        BlockPos footPos = getFootPos(pos, state);
        BlockPos headPos = footPos.relative(facing);
        int minX = Math.min(footPos.getX(), headPos.getX()) - 1;
        int maxX = Math.max(footPos.getX(), headPos.getX()) + 1;
        int minZ = Math.min(footPos.getZ(), headPos.getZ()) - 1;
        int maxZ = Math.max(footPos.getZ(), headPos.getZ()) + 1;

        for (BlockPos checkPos : BlockPos.betweenClosed(minX, footPos.getY() - GROUNDWATER_DEPTH, minZ, maxX, footPos.getY() - 1, maxZ)) {
            FluidState fluidState = level.getFluidState(checkPos);
            if (fluidState.is(FluidTags.WATER) && fluidState.isSource()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            removeOtherParts(level, pos, state);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!this.canSurvive(state, level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        TimberWellPart part = state.getValue(PART);

        if (part == TimberWellPart.FOOT) {
            BlockPos headPos = pos.relative(facing);
            BlockPos topPos = headPos.above();
            return isPartOrAir(level, headPos, facing, TimberWellPart.HEAD) && isPartOrAir(level, topPos, facing, TimberWellPart.TOP);
        }

        if (part == TimberWellPart.HEAD) {
            BlockPos footPos = pos.relative(facing.getOpposite());
            BlockPos topPos = pos.above();
            return isPartOrAir(level, footPos, facing, TimberWellPart.FOOT) && isPartOrAir(level, topPos, facing, TimberWellPart.TOP);
        }

        BlockPos headPos = pos.below();
        return isPart(level, headPos, facing);
    }

    private boolean isPartOrAir(BlockGetter level, BlockPos pos, Direction facing, TimberWellPart expectedPart) {
        BlockState otherState = level.getBlockState(pos);
        if (otherState.isAir()) {
            return true;
        }
        return otherState.getBlock() == this && otherState.getValue(FACING) == facing && otherState.getValue(PART) == expectedPart;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART, LEVEL);
    }

    private void removeOtherParts(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) {
            return;
        }

        Direction facing = state.getValue(FACING);
        TimberWellPart part = state.getValue(PART);

        if (part == TimberWellPart.FOOT) {
            BlockPos headPos = pos.relative(facing);
            BlockPos topPos = headPos.above();
            clearIfMatches(level, headPos, facing, TimberWellPart.HEAD);
            clearIfMatches(level, topPos, facing, TimberWellPart.TOP);
            return;
        }

        if (part == TimberWellPart.HEAD) {
            BlockPos footPos = pos.relative(facing.getOpposite());
            BlockPos topPos = pos.above();
            clearIfMatches(level, footPos, facing, TimberWellPart.FOOT);
            clearIfMatches(level, topPos, facing, TimberWellPart.TOP);
            return;
        }

        BlockPos headPos = pos.below();
        BlockPos footPos = headPos.relative(facing.getOpposite());
        clearIfMatches(level, headPos, facing, TimberWellPart.HEAD);
        clearIfMatches(level, footPos, facing, TimberWellPart.FOOT);
    }

    private void clearIfMatches(Level level, BlockPos pos, Direction facing, TimberWellPart expectedPart) {
        BlockState otherState = level.getBlockState(pos);
        if (otherState.getBlock() != this) {
            return;
        }
        if (otherState.getValue(FACING) != facing) {
            return;
        }
        if (otherState.getValue(PART) != expectedPart) {
            return;
        }
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
    }

    private boolean isPart(BlockGetter level, BlockPos pos, Direction facing) {
        BlockState state = level.getBlockState(pos);
        return state.getBlock() == this && state.getValue(FACING) == facing && state.getValue(PART) == TimberWellPart.HEAD;
    }

    public enum TimberWellPart implements StringRepresentable {
        FOOT("foot"),
        HEAD("head"),
        TOP("top");

        private final String name;

        TimberWellPart(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide) {
            return;
        }
        if (state.getValue(PART) != TimberWellPart.TOP || !hasGroundwater(level, pos, state)) {
            return;
        }

        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.075D;
        double z = pos.getZ() + 0.5D;

        level.addParticle(ParticleTypes.DRIPPING_WATER, x, y, z, 0.0D, 0.0D, 0.0D);

        if (random.nextInt(12) == 0) {
            level.addParticle(ParticleTypes.DRIPPING_WATER, x, y, z, 0.0D, -0.01D, 0.0D);
        }

        if (random.nextInt(40) == 0) {
            level.playLocalSound(x, pos.getY() + 0.5D, z, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 0.2F, 0.9F + random.nextFloat() * 0.2F, false
            );
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int earthy = 0xFFD966;
        int gold = 0xFFD700;

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.farm_and_charm.timber_well.info_0", GROUNDWATER_DEPTH)
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
        } else {
            tooltip.add(Component.translatable(
                    "tooltip.farm_and_charm.tooltip_information.hold",
                    Component.literal("[SHIFT]").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(gold)))
            ).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
        }
    }

    private static final Supplier<VoxelShape> FOOT_SHAPE_SUPPLIER = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Block.box(1.0D, 0.0D, 0.0D, 5.0D, 4.0D, 16.0D), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Block.box(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D), BooleanOp.OR);
        return GeneralUtil.rotateShape(Direction.NORTH, Direction.WEST, shape);
    };

    private static final Supplier<VoxelShape> HEAD_SHAPE_SUPPLIER = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Block.box(11.0D, 0.0D, 0.0D, 15.0D, 4.0D, 16.0D), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Block.box(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Block.box(12.0D, 12.0D, 6.0D, 16.0D, 16.0D, 10.0D), BooleanOp.OR);
        return GeneralUtil.rotateShape(Direction.NORTH, Direction.WEST, shape);
    };

    private static final Supplier<VoxelShape> TOP_SHAPE_SUPPLIER = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Block.box(12.0D, 0.0D, 6.0D, 16.0D, 8.0D, 10.0D), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Block.box(6.0D, 3.0D, 7.0D, 12.0D, 5.0D, 9.0D), BooleanOp.OR);
        return GeneralUtil.rotateShape(Direction.NORTH, Direction.WEST, shape);
    };

    private static final Map<Direction, VoxelShape> FOOT_SHAPES = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, FOOT_SHAPE_SUPPLIER.get()));
        }
    });

    private static final Map<Direction, VoxelShape> HEAD_SHAPES = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, HEAD_SHAPE_SUPPLIER.get()));
        }
    });

    private static final Map<Direction, VoxelShape> TOP_SHAPES = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, TOP_SHAPE_SUPPLIER.get()));
        }
    });

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        TimberWellPart part = state.getValue(PART);

        return switch (part) {
            case FOOT -> FOOT_SHAPES.get(facing);
            case HEAD -> HEAD_SHAPES.get(facing);
            case TOP -> TOP_SHAPES.get(facing);
        };
    }
}
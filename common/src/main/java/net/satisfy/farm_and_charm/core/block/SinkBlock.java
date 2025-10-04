package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "deprecation"})
public class SinkBlock extends Block {
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");
    public static final EnumProperty<DoubleBlockHalf> HALF = EnumProperty.create("half", DoubleBlockHalf.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Map<Direction, VoxelShape> TOP_SHAPES = new HashMap<>();
    public static final Map<Direction, VoxelShape> BOTTOM_SHAPES = new HashMap<>();

    static {
        Supplier<VoxelShape> topShapeSupplier = SinkBlock::makeTopShape;
        Supplier<VoxelShape> bottomShapeSupplier = SinkBlock::makeBottomShape;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            TOP_SHAPES.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, topShapeSupplier.get()));
            BOTTOM_SHAPES.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, bottomShapeSupplier.get()));
        }
    }

    public SinkBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(FILLED, false).setValue(FACING, Direction.NORTH));
    }

    private static VoxelShape makeTopShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.375, 0, 0.75, 0.625, 0.0625, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0.125, 0.75, 0.375, 0.3125, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.3125, 0.5, 0.5625, 0.5, 0.625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.25, 0.4375, 0.625, 0.3125, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.0625, 0.8125, 0.5625, 0.375, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.4375, 0.375, 0.625, 0.5625, 0.5, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.1875, 0.8125, 0.4375, 0.25, 0.875), BooleanOp.OR);
        return shape;
    }

    private static VoxelShape makeBottomShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.125, 1, 0.75, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.75, 0.1875, 0.1875, 1, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.75, 0.75, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.75, 0, 1, 1, 0.1875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.8125, 0.75, 0.1875, 1, 1, 0.75), BooleanOp.OR);
        return shape;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return TOP_SHAPES.get(facing);
        } else {
            return BOTTOM_SHAPES.get(facing);
        }
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (world.isClientSide || state.getValue(HALF) != DoubleBlockHalf.LOWER) return ItemInteractionResult.SUCCESS;
        Item item = itemStack.getItem();
        if (itemStack.isEmpty() && !state.getValue(FILLED)) {
            world.setBlock(pos, state.setValue(FILLED, true), Block.UPDATE_ALL);
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
            return ItemInteractionResult.SUCCESS;
        } else if ((item == Items.WATER_BUCKET || item == Items.GLASS_BOTTLE) && !state.getValue(FILLED)) {
            world.setBlock(pos, state.setValue(FILLED, true), Block.UPDATE_ALL);
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (!player.isCreative()) {
                if (item == Items.WATER_BUCKET) {
                    itemStack.shrink(1);
                    player.addItem(new ItemStack(Items.BUCKET));
                } else {
                    itemStack.shrink(1);
                    player.addItem(new ItemStack(Items.GLASS_BOTTLE));
                }
            }
            return ItemInteractionResult.SUCCESS;
        } else if ((item == Items.BUCKET || item == Items.GLASS_BOTTLE) && state.getValue(FILLED)) {
            world.setBlock(pos, state.setValue(FILLED, false), Block.UPDATE_ALL);
            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (!player.isCreative()) {
                if (item == Items.BUCKET) {
                    itemStack.shrink(1);
                    player.addItem(new ItemStack(Items.WATER_BUCKET));
                } else {
                    itemStack.shrink(1);
                    PotionContents potionContents = new PotionContents(Potions.WATER);
                    ItemStack potionStack = new ItemStack(Items.POTION);
                    potionStack.set(DataComponents.POTION_CONTENTS, potionContents);
                    player.addItem(potionStack);
                }
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FILLED, FACING);
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        return context.getLevel().getBlockState(blockPos.above()).canBeReplaced(context) ? this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(FACING, context.getHorizontalDirection().getOpposite()) : null;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.below();
            BlockState blockState = world.getBlockState(blockPos);
            return blockState.is(this) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
        } else {
            return super.canSurvive(state, world, pos);
        }
    }

    public static void placeAt(LevelAccessor levelAccessor, BlockState blockState, BlockPos blockPos, int i) {
        BlockPos blockPos2 = blockPos.above();
        levelAccessor.setBlock(blockPos, copyWaterloggedFrom(levelAccessor, blockPos, blockState.setValue(HALF, DoubleBlockHalf.LOWER)), i);
        levelAccessor.setBlock(blockPos2, copyWaterloggedFrom(levelAccessor, blockPos2, blockState.setValue(HALF, DoubleBlockHalf.UPPER)), i);
    }


    private static BlockState copyWaterloggedFrom(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return blockState.hasProperty(BlockStateProperties.WATERLOGGED) ? blockState.setValue(BlockStateProperties.WATERLOGGED, levelReader.isWaterAt(blockPos)) : blockState;
    }

    protected static void preventCreativeDropFromBottomPart(Level level, BlockPos blockPos, BlockState blockState, Player player) {
        DoubleBlockHalf doubleBlockHalf = blockState.getValue(HALF);
        if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
            BlockPos blockPos2 = blockPos.below();
            BlockState blockState2 = level.getBlockState(blockPos2);
            if (blockState2.is(blockState.getBlock()) && blockState2.getValue(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockState3 = blockState2.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(blockPos2, blockState3, 35);
                level.levelEvent(player, 2001, blockPos2, Block.getId(blockState2));
            }
        }

    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos blockPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.getBlock() == this && blockState.getValue(HALF) != half) {
            world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 35);
            world.levelEvent(player, 2001, blockPos, Block.getId(blockState));
            if (!world.isClientSide && !player.isCreative()) {
                dropResources(state, world, pos, null, player, player.getMainHandItem());
                dropResources(blockState, world, blockPos, null, player, player.getMainHandItem());
            }
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        super.playerDestroy(level, player, blockPos, Blocks.AIR.defaultBlockState(), blockEntity, itemStack);
        if (blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockPos blockPos2 = blockPos.above();
            BlockState blockState2 = level.getBlockState(blockPos2);

            if (blockState2.is(this) && blockState2.getValue(HALF) == DoubleBlockHalf.UPPER) {
                level.destroyBlock(blockPos2, true);
            }
        } else {
            BlockPos blockPos1 = blockPos.below();
            BlockState blockState1 = level.getBlockState(blockPos1);

            if (blockState1.is(this) && blockState1.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.destroyBlock(blockPos1, true);
            }
        }
    }

    @Override
    public long getSeed(BlockState blockState, BlockPos blockPos) {
        return Mth.getSeed(blockPos.getX(), blockPos.below(blockState.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), blockPos.getZ());
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource randomSource) {
        float chance = randomSource.nextFloat();
        if (chance < 0.1F) {
            spawnDripParticle(world, pos, state);
        }
    }

    public static void spawnDripParticle(Level level, BlockPos blockPos, BlockState blockState) {
        Vec3 vec3 = blockState.getOffset(level, blockPos);
        double d = 0.0625;
        double e = (double) blockPos.getX() + 0.5 + vec3.x;
        double f = (double) ((float) (blockPos.getY() + 0.9) - 0.6875F) - d;
        double g = (double) blockPos.getZ() + 0.5 + vec3.z;
        ParticleOptions particleOptions = ParticleTypes.DRIPPING_WATER;
        level.addParticle(particleOptions, e, f, g, 0.0, 0.0, 0.0);
    }
}
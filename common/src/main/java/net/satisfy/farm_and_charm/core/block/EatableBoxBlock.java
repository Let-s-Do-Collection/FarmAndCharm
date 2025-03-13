package net.satisfy.farm_and_charm.core.block;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class EatableBoxBlock extends FacingBlock {
    public static final IntegerProperty CUTS = IntegerProperty.create("cuts", 0, 5);

    public EatableBoxBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(CUTS, 0));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        Level world = player.getCommandSenderWorld();
        if (world.isClientSide) {
            if (tryEat(world, blockPos, blockState, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if (itemStack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return tryEat(world, blockPos, blockState, player);
    }

    private InteractionResult tryEat(Level world, BlockPos pos, BlockState state, Player player) {
        world.playSound(null, pos, SoundEvents.FOX_EAT, SoundSource.PLAYERS, 0.5f, world.getRandom().nextFloat() * 0.1f + 0.9f);
        player.getFoodData().eat(1, 0.4f);
        int cuts = state.getValue(CUTS);
        world.gameEvent(player, GameEvent.EAT, pos);
        if (cuts < 5) {
            world.setBlock(pos, state.setValue(CUTS, cuts + 1), Block.UPDATE_ALL);
        } else {
            world.destroyBlock(pos, false);
            ItemStack bowlStack = new ItemStack(Items.CHEST);
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, bowlStack);
            world.addFreshEntity(itemEntity);
        }
        return InteractionResult.SUCCESS;
    }


    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN && !state.canSurvive(world, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).isSolid();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CUTS);

    }

    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0, 0.25, 0.8125, 0.125, 0.74375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.75, 0.125, 0.25, 0.8125, 0.1875, 0.74375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0.125, 0.25, 0.25, 0.1875, 0.74375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.125, 0.25, 0.75, 0.1875, 0.30625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.125, 0.68125, 0.75, 0.1875, 0.74375), BooleanOp.OR);
        return shape;
    };

    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
        }
    });

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }
}
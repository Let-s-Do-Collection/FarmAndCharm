package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("all")
public class StackableBlock extends Block {
    private static final IntegerProperty STACK_PROPERTY = IntegerProperty.create("stack", 1, 8);
    private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final int maxStack;
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 10, 14);

    public StackableBlock(Properties settings, int maxStack) {
        super(settings);
        this.maxStack = maxStack;
        this.registerDefaultState(this.stateDefinition.any().setValue(STACK_PROPERTY, 1).setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STACK_PROPERTY, FACING);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        final ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (player.isShiftKeyDown() && stack.isEmpty()) {
            if (!world.isClientSide) {
                if (state.getValue(STACK_PROPERTY) > 1) {
                    world.setBlock(pos, state.setValue(STACK_PROPERTY, state.getValue(STACK_PROPERTY) - 1), 3);
                } else {
                    world.removeBlock(pos, false);
                }
                ItemStack droppedStack = new ItemStack(this.asItem());
                if (!player.getInventory().add(droppedStack)) {
                    GeneralUtil.spawnSlice(world, droppedStack, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0.1, 0);
                }
                world.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0F, 1.0F);
                if (world instanceof ServerLevel) {
                    ServerLevel serverWorld = (ServerLevel) world;
                    for (int count = 0; count < 10; ++count) {
                        double d0 = world.random.nextGaussian() * 0.02;
                        double d1 = world.random.nextGaussian() * 0.0;
                        double d2 = world.random.nextGaussian() * 0.02;
                        serverWorld.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 1, d0, d1, d2, 0.1);
                    }
                }
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        } else if (stack.getItem() == this.asItem()) {
            if (state.getValue(STACK_PROPERTY) < this.maxStack) {
                world.setBlock(pos, state.setValue(STACK_PROPERTY, state.getValue(STACK_PROPERTY) + 1), 3);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        } else if (stack.isEmpty()) {
            if (state.getValue(STACK_PROPERTY) > 1) {
                world.setBlock(pos, state.setValue(STACK_PROPERTY, state.getValue(STACK_PROPERTY) - 1), 3);
            } else if (state.getValue(STACK_PROPERTY) == 1) {
                world.destroyBlock(pos, false);
            }
            Direction direction = player.getDirection().getOpposite();
            double xMotion = (double) direction.getStepX() * 0.13;
            double yMotion = 0.35;
            double zMotion = (double) direction.getStepZ() * 0.13;
            GeneralUtil.spawnSlice(world, new ItemStack(this), (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, xMotion, yMotion, zMotion);
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, world, pos, player, blockHitResult);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        VoxelShape shape = world.getBlockState(pos.below()).getShape(world, pos.below());
        Direction direction = Direction.UP;
        return Block.isFaceFull(shape, direction);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(world, pos)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(world, pos)) {
            world.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }
}

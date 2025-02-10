package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import org.joml.Vector3d;

@SuppressWarnings("all")
public class StackableEatableBlock extends Block {
    private static final IntegerProperty STACK_PROPERTY = IntegerProperty.create("stack", 1, 8);
    private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final int maxStack;
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 10, 14);

    public StackableEatableBlock(Properties settings, int maxStack) {
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
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        final ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown() && stack.isEmpty()) {
            if (!world.isClientSide) {
                if (state.getValue(STACK_PROPERTY) > 1) {
                    world.setBlock(pos, state.setValue(STACK_PROPERTY, state.getValue(STACK_PROPERTY) - 1), Block.UPDATE_ALL);
                } else {
                    world.removeBlock(pos, false);
                }
                player.getFoodData().eat(3, 0.6f);
                world.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            if (world.isClientSide) {
                for (int i = 0; i < 10; i++) {
                    double rx = world.random.nextDouble() - 0.5;
                    double ry = world.random.nextDouble();
                    double rz = world.random.nextDouble() - 0.5;
                    Vector3d velocity = new Vector3d(rx, ry, rz).mul(0.1);
                    world.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(this.asItem())),
                            pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, velocity.x, velocity.y, velocity.z);
                }
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else if (stack.getItem() == this.asItem()) {
            if (state.getValue(STACK_PROPERTY) < this.maxStack) {
                world.setBlock(pos, state.setValue(STACK_PROPERTY, state.getValue(STACK_PROPERTY) + 1), Block.UPDATE_ALL);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                for (int i = 0; i < 8; i++) {
                    double angle = world.random.nextDouble() * Math.PI * 2;
                    double speed = 0.1 + world.random.nextDouble() * 0.1;
                    double dx = Math.cos(angle) * speed;
                    double dy = 0.05;
                    double dz = Math.sin(angle) * speed;
                    world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dx, dy, dz);
                }
                return InteractionResult.SUCCESS;
            }
        } else if (stack.isEmpty()) {
            if (state.getValue(STACK_PROPERTY) > 1) {
                world.setBlock(pos, state.setValue(STACK_PROPERTY, state.getValue(STACK_PROPERTY) - 1), Block.UPDATE_ALL);
            } else if (state.getValue(STACK_PROPERTY) == 1) {
                world.destroyBlock(pos, false);
            }
            Direction direction = player.getDirection().getOpposite();
            double xMotion = direction.getStepX() * 0.13;
            double yMotion = 0.35;
            double zMotion = direction.getStepZ() * 0.13;
            GeneralUtil.spawnSlice(world, new ItemStack(this), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, xMotion, yMotion, zMotion);
            return InteractionResult.SUCCESS;
        }
        return super.use(state, world, pos, player, hand, hit);
    }
}

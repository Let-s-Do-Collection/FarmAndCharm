package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.StoveBlockEntity;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StoveBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public StoveBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        final BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof MenuProvider factory) {
            player.openMenu(factory);
            return InteractionResult.sidedSuccess(world.isClientSide());
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof StoveBlockEntity stoveBlockEntity)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (isIgnitionItem(itemStack)) {
            if (!stoveBlockEntity.canIgnite()) {
                return ItemInteractionResult.CONSUME;
            }

            if (!world.isClientSide) {
                if (stoveBlockEntity.ignite()) {
                    consumeIgnitionItem(player, hand, itemStack);
                    world.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, world.random.nextFloat() * 0.4f + 0.8f);
                }
            }

            return ItemInteractionResult.SUCCESS;
        }

        if (isExtinguishItem(itemStack)) {
            if (!stoveBlockEntity.canExtinguish()) {
                return ItemInteractionResult.CONSUME;
            }

            if (!world.isClientSide) {
                if (stoveBlockEntity.extinguish()) {
                    consumeExtinguishItem(player, hand, itemStack);
                    world.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0f, 1.0f);
                    if (world instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.9, pos.getZ() + 0.5, 10, 0.2, 0.2, 0.2, 0.01);
                    }
                }
            }

            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.is(newState.getBlock())) {
            return;
        }
        final BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StoveBlockEntity entity) {
            if (world instanceof ServerLevel) {
                Containers.dropContents(world, pos, entity);
                entity.dropExperience((ServerLevel) world, Vec3.atCenterOf(pos));
            }
            world.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(LIT, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClientSide) {
            return (level, blockPos, blockState, tickerBlockEntity) -> {
                if (tickerBlockEntity instanceof StoveBlockEntity stoveBlockEntity) {
                    stoveBlockEntity.tick(level, blockPos, blockState, stoveBlockEntity);
                }
            };
        }
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StoveBlockEntity(pos, state);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT) || !world.isEmptyBlock(pos.above()))
            return;

        double centerX = (double) pos.getX() + 0.5;
        double centerY = pos.getY() + 0.24;
        double centerZ = (double) pos.getZ() + 0.5;

        Direction direction = state.getValue(FACING);
        double horizontalOffset = random.nextDouble() * 0.6 - 0.3;
        double particleX = direction.getAxis() == Direction.Axis.X ? (double) direction.getStepX() * 0.52 : horizontalOffset;
        double particleY = random.nextDouble() * 6.0 / 16.0;
        double particleZ = direction.getAxis() == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52 : horizontalOffset;

        world.playLocalSound(centerX, centerY, centerZ, SoundEventRegistry.STOVE_CRACKLING.get(), SoundSource.BLOCKS, 0.05f, 1.0f, false);

        for (int index = 0; index < 2; ++index) {
            world.addParticle(ParticleTypes.SMOKE, centerX + particleX, centerY + particleY, centerZ + particleZ, 0.0, 0.0, 0.0);
            world.addParticle(ParticleTypes.FLAME, centerX + particleX, centerY + particleY, centerZ + particleZ, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (!world.isClientSide) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StoveBlockEntity stoveBlockEntity && placer instanceof Player player) {
                stoveBlockEntity.setOwner(player.getUUID());
            }
        }
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (state.getValue(LIT) && entity instanceof Player) {
            entity.hurt(world.damageSources().hotFloor(), 1.0F);
        }
    }

    private static boolean isIgnitionItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item instanceof FlintAndSteelItem || item instanceof FireChargeItem;
    }

    private static boolean isExtinguishItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item == Items.WATER_BUCKET || item instanceof ShovelItem;
    }

    private static void consumeIgnitionItem(Player player, InteractionHand hand, ItemStack itemStack) {
        if (player.isCreative()) {
            return;
        }

        if (itemStack.getItem() instanceof FlintAndSteelItem) {
            itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            return;
        }

        if (itemStack.getItem() instanceof FireChargeItem) {
            itemStack.shrink(1);
        }
    }

    private static void consumeExtinguishItem(Player player, InteractionHand hand, ItemStack itemStack) {
        if (player.isCreative()) {
            return;
        }

        if (itemStack.is(Items.WATER_BUCKET)) {
            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
            return;
        }

        if (itemStack.getItem() instanceof ShovelItem) {
            itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
    }
}
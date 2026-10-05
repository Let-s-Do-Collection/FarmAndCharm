package net.satisfy.farm_and_charm.core.block;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.satisfy.foundation.overlay.BlockNotice;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
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
        if (entity instanceof StoveBlockEntity stove && isGrillHit(stove, hit)) {
            int slot = StoveBlockEntity.grillSlotAt(hit.getLocation(), pos);
            if (!stove.getGrillItem(slot).isEmpty()) {
                if (!world.isClientSide) {
                    useGrillSlot(world, pos, player, stove, slot);
                }
                return InteractionResult.sidedSuccess(world.isClientSide());
            }
        }
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
                if (!stoveBlockEntity.isLit() && player instanceof ServerPlayer serverPlayer) {
                    world.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 0.6f, 1.6f);
                    BlockNotice.send(serverPlayer, pos, Component.translatable("hud.farm_and_charm.stove_no_fuel"));
                }
                return ItemInteractionResult.sidedSuccess(world.isClientSide);
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

        if (isGrillHit(stoveBlockEntity, hit)) {
            int slot = StoveBlockEntity.grillSlotAt(hit.getLocation(), pos);
            if (!stoveBlockEntity.getGrillItem(slot).isEmpty()) {
                if (!world.isClientSide) {
                    useGrillSlot(world, pos, player, stoveBlockEntity, slot);
                }
                return ItemInteractionResult.sidedSuccess(world.isClientSide);
            }
            if (StoveBlockEntity.findGrillRecipe(world, itemStack).isPresent()) {
                if (!world.isClientSide && stoveBlockEntity.placeOnGrill(slot, itemStack)) {
                    itemStack.consume(1, player);
                    world.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.6F, 1.2F);
                    if (stoveBlockEntity.isLit()) {
                        world.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.25F, 1.8F + world.random.nextFloat() * 0.2F);
                    }
                }
                return ItemInteractionResult.sidedSuccess(world.isClientSide);
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static boolean isGrillHit(StoveBlockEntity stove, BlockHitResult hit) {
        return hit.getDirection() == Direction.UP && stove.isGrillFree();
    }

    private static void useGrillSlot(Level world, BlockPos pos, Player player, StoveBlockEntity stove, int slot) {
        if (player.isShiftKeyDown() || stove.isGrillDone(slot)) {
            ItemStack taken = stove.takeFromGrill(slot);
            if (!player.addItem(taken)) {
                player.drop(taken, false);
            }
            world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.4F, 1.2F);
        } else if (stove.flipGrillItem(slot)) {
            world.playSound(null, pos, SoundEvents.WOOL_HIT, SoundSource.BLOCKS, 0.5F, 1.4F + world.random.nextFloat() * 0.2F);
        }
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
                entity.dropGrillItems((ServerLevel) world);
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

        if (world.getBlockEntity(pos) instanceof StoveBlockEntity stove) {
            for (int slot = 0; slot < StoveBlockEntity.GRILL_SLOTS; slot++) {
                if (!stove.getGrillItem(slot).isEmpty() && random.nextFloat() < 0.35F) {
                    Vec3 center = StoveBlockEntity.grillSlotCenter(slot);
                    world.addParticle(ParticleTypes.SMOKE, pos.getX() + center.x + (random.nextDouble() - 0.5) * 0.15, pos.getY() + 1.05, pos.getZ() + center.z + (random.nextDouble() - 0.5) * 0.15, 0.0, 0.02, 0.0);
                }
            }
        }

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
        return item == Items.WATER_BUCKET || item instanceof ShovelItem || isWaterBottle(itemStack);
    }

    private static boolean isWaterBottle(ItemStack itemStack) {
        PotionContents contents = itemStack.get(DataComponents.POTION_CONTENTS);
        return itemStack.is(Items.POTION) && contents != null && contents.is(Potions.WATER);
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

        if (isWaterBottle(itemStack)) {
            player.setItemInHand(hand, ItemUtils.createFilledResult(itemStack, player, new ItemStack(Items.GLASS_BOTTLE)));
            return;
        }

        if (itemStack.getItem() instanceof ShovelItem) {
            itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
    }
}
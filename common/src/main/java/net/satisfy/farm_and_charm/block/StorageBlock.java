package net.satisfy.farm_and_charm.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.block.entity.StorageBlockEntity;
import net.satisfy.farm_and_charm.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class StorageBlock extends FacingBlock implements EntityBlock {
    public static final SoundEvent event;

    public StorageBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof StorageBlockEntity shelfBlockEntity) {
            Optional optional = Util.getRelativeHitCoordinatesForBlockFace(hit, (Direction)state.getValue(FACING), this.unAllowedDirections());
            if (optional.isEmpty()) {
                return InteractionResult.PASS;
            } else {
                Tuple<Float, Float> ff = (Tuple)optional.get();
                int i = this.getSection((Float)ff.getA(), (Float)ff.getB());
                if (i == Integer.MIN_VALUE) {
                    return InteractionResult.PASS;
                } else if (!((ItemStack)shelfBlockEntity.getInventory().get(i)).isEmpty()) {
                    this.remove(world, pos, player, shelfBlockEntity, i);
                    return InteractionResult.sidedSuccess(world.isClientSide);
                } else {
                    ItemStack stack = player.getItemInHand(hand);
                    if (!stack.isEmpty() && this.canInsertStack(stack)) {
                        this.add(world, pos, player, shelfBlockEntity, stack, i);
                        return InteractionResult.sidedSuccess(world.isClientSide);
                    } else {
                        return InteractionResult.CONSUME;
                    }
                }
            }
        } else {
            return InteractionResult.PASS;
        }
    }

    public void add(Level level, BlockPos blockPos, Player player, StorageBlockEntity shelfBlockEntity, ItemStack itemStack, int i) {
        if (!level.isClientSide) {
            SoundEvent soundEvent = this.getAddSound(level, blockPos, player, i);
            shelfBlockEntity.setStack(i, itemStack.split(1));
            level.playSound((Player)null, blockPos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (player.isCreative()) {
                itemStack.grow(1);
            }

            level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
        }

    }

    public void remove(Level level, BlockPos blockPos, Player player, StorageBlockEntity shelfBlockEntity, int i) {
        if (!level.isClientSide) {
            ItemStack itemStack = shelfBlockEntity.removeStack(i);
            SoundEvent soundEvent = this.getRemoveSound(level, blockPos, player, i);
            level.playSound((Player)null, blockPos, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getInventory().add(itemStack)) {
                player.drop(itemStack, false);
            }

            level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
        }

    }

    public SoundEvent getRemoveSound(Level level, BlockPos blockPos, Player player, int i) {
        return event;
    }

    public SoundEvent getAddSound(Level level, BlockPos blockPos, Player player, int i) {
        return event;
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof StorageBlockEntity) {
                StorageBlockEntity shelf = (StorageBlockEntity)blockEntity;
                if (world instanceof ServerLevel) {
                    Containers.dropContents(world, pos, shelf.getInventory());
                }

                world.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, world, pos, newState, moved);
        }

    }

    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public abstract int size();

    public abstract ResourceLocation type();

    public abstract Direction[] unAllowedDirections();

    public abstract boolean canInsertStack(ItemStack var1);

    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StorageBlockEntity(pos, state, this.size());
    }

    public abstract int getSection(Float var1, Float var2);

    static {
        event = SoundEvents.WOOD_PLACE;
    }
}
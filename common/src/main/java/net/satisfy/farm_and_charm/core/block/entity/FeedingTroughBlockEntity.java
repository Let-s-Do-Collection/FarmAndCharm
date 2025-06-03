package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.FeedingTroughBlock;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.farm_and_charm.core.util.SaturationTracker;
import org.jetbrains.annotations.NotNull;

public class FeedingTroughBlockEntity extends BlockEntity implements WorldlyContainer {
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private boolean updatingState = false;

    public FeedingTroughBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.FEEDING_TROUGH_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.is(TagRegistry.FEEDING_TROUGH_FODDER) && getItem(slot).getCount() < 4;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.get(0).isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
        setChanged();
        return stack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = ContainerHelper.takeItem(items, slot);
        setChanged();
        return stack;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            items.set(slot, ItemStack.EMPTY);
        } else {
            ItemStack current = items.get(slot);
            if (current.isEmpty()) {
                items.set(slot, stack.copy());
            } else if (ItemStack.isSameItemSameTags(current, stack)) {
                int newCount = Math.min(4, current.getCount() + stack.getCount());
                current.setCount(newCount);
                items.set(slot, current);
            }
        }
        setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (!updatingState) {
            updatingState = true;
            updateBlockState();
            updatingState = false;
        }
    }

    private void updateBlockState() {
        if (level != null && !level.isClientSide) {
            BlockState state = level.getBlockState(worldPosition);
            if (state.getBlock() instanceof FeedingTroughBlock) {
                int newSize = items.get(0).getCount();
                if (state.getValue(FeedingTroughBlock.SIZE) != newSize) {
                    BlockState newState = state.setValue(FeedingTroughBlock.SIZE, newSize);
                    level.setBlock(worldPosition, newState, 3);
                    level.sendBlockUpdated(worldPosition, state, newState, 3);
                }
            }
        }
    }

    public void onAnimalFed(Animal animal) {
        if (!(animal instanceof SaturationTracker.SaturatedAnimal saturated)) return;
        saturated.farm_and_charm$getSaturationTracker().feedDirectly(animal, animal.tickCount, 5);
    }
}

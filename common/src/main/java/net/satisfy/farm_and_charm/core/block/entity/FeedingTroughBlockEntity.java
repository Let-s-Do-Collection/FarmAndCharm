package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.FeedingTroughBlock;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.farm_and_charm.core.network.packet.SyncSaturationPacket;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.util.SaturationTracker;
import org.jetbrains.annotations.NotNull;

public class FeedingTroughBlockEntity extends BlockEntity implements WorldlyContainer {
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    public FeedingTroughBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.FEEDING_TROUGH_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean addOne(Item item) {
        if (!new ItemStack(item).is(ItemTags.VILLAGER_PLANTABLE_SEEDS)) return false;
        ItemStack slot = items.get(0);
        if (slot.isEmpty()) {
            items.set(0, new ItemStack(item, 1));
            pushSizeToState();
            return true;
        }
        if (slot.is(item) && slot.getCount() < 4) {
            slot.grow(1);
            items.set(0, slot);
            pushSizeToState();
            return true;
        }
        return false;
    }

    public boolean consumeOne() {
        ItemStack slot = items.get(0);
        if (slot.isEmpty()) return false;
        slot.shrink(1);
        if (slot.getCount() <= 0) items.set(0, ItemStack.EMPTY);
        pushSizeToState();
        return true;
    }

    private void pushSizeToState() {
        setChanged();
        if (level == null || level.isClientSide) return;
        BlockState state = level.getBlockState(worldPosition);
        if (!(state.getBlock() instanceof FeedingTroughBlock)) return;
        int size = items.get(0).isEmpty() ? 0 : items.get(0).getCount();
        if (state.getValue(FeedingTroughBlock.SIZE) != size) {
            level.setBlockAndUpdate(worldPosition, state.setValue(FeedingTroughBlock.SIZE, size));
        } else {
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        return new int[]{0};
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        ItemStack cur = getItem(slot);
        return stack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS) && (cur.isEmpty() || cur.is(stack.getItem())) && cur.getCount() < 4;
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
        if (items.get(0).getCount() <= 0) items.set(0, ItemStack.EMPTY);
        pushSizeToState();
        return stack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = ContainerHelper.takeItem(items, slot);
        if (items.get(0).getCount() <= 0) items.set(0, ItemStack.EMPTY);
        pushSizeToState();
        return stack;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            items.set(slot, ItemStack.EMPTY);
        } else {
            int capped = Math.min(4, stack.getCount());
            ItemStack copy = stack.copy();
            copy.setCount(capped);
            items.set(slot, copy);
        }
        if (items.get(0).getCount() <= 0) items.set(0, ItemStack.EMPTY);
        pushSizeToState();
    }

    @Override
    public int getMaxStackSize() {
        return 4;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.set(0, ItemStack.EMPTY);
        pushSizeToState();
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        ContainerHelper.loadAllItems(compoundTag, items, provider);
        if (items.get(0).getCount() <= 0) items.set(0, ItemStack.EMPTY);
        pushSizeToState();
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        ContainerHelper.saveAllItems(compoundTag, items, provider);
    }

    @Override
    public void setChanged() {
        super.setChanged();
    }

    public void onAnimalFed(Animal animal) {
        if (!(animal instanceof SaturationTracker.SaturatedAnimal saturated)) return;
        SaturationTracker tracker = saturated.farm_and_charm$getSaturationTracker();
        tracker.feedDirectly(animal, 5);
        if (!animal.level().isClientSide) {
            PacketHandler.sendSaturationSync(new SyncSaturationPacket(animal.getId(), tracker.level(), tracker.foodCounter()), animal);
        }
        consumeOne();
    }
}

package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.foundation.storage.StorageBlock;
import net.satisfy.foundation.storage.StorageBlockEntity;

public class FarmAndCharmStorageBlockEntity extends StorageBlockEntity {
    public FarmAndCharmStorageBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.STORAGE_ENTITY.get(), pos, state);
    }

    public FarmAndCharmStorageBlockEntity(BlockPos pos, BlockState state, int size) {
        super(EntityTypeRegistry.STORAGE_ENTITY.get(), pos, state, size);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot >= 0 && slot < getItems().size() && getItems().get(slot).isEmpty() && getBlockState().getBlock() instanceof StorageBlock storage && storage.canInsertStack(stack);
    }
}

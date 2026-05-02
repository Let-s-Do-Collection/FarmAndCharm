package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Clearable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

public class StorageBlockEntity extends BlockEntity implements Clearable {
    private int size;
    private NonNullList<ItemStack> inventory;

    public StorageBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.STORAGE_ENTITY.get(), pos, state);
    }

    public StorageBlockEntity(BlockPos pos, BlockState state, int size) {
        super(EntityTypeRegistry.STORAGE_ENTITY.get(), pos, state);
        this.size = size;
        this.inventory = NonNullList.withSize(this.size, ItemStack.EMPTY);
    }

    public ItemStack removeStack(int slot) {
        ItemStack stack = (ItemStack)this.inventory.set(slot, ItemStack.EMPTY);
        this.setChanged();
        return stack;
    }

    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        this.setChanged();
    }

    public void setChanged() {
        Level var2 = this.level;
        if (var2 instanceof ServerLevel serverLevel) {
            if (!this.level.isClientSide()) {
                Packet<ClientGamePacketListener> updatePacket = this.getUpdatePacket();

                for (ServerPlayer player : GeneralUtil.tracking(serverLevel, this.getBlockPos())) {
                    player.connection.send(updatePacket);
                }
            }
        }

        super.setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.size = compoundTag.getInt("size");
        this.inventory = NonNullList.withSize(this.size, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, this.inventory, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        ContainerHelper.saveAllItems(nbt, this.inventory, provider);
        nbt.putInt("size", this.size);
        super.saveAdditional(nbt, provider);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    public void setInventory(NonNullList<ItemStack> inventory) {
        for(int i = 0; i < inventory.size(); ++i) {
            this.inventory.set(i, inventory.get(i));
        }

    }

    public NonNullList<ItemStack> getInventory() {
        return this.inventory;
    }

    @Override
    public void clearContent() {
        inventory.clear();
    }
}
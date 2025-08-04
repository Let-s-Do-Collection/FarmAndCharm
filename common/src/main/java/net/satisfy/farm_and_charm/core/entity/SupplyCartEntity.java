package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.joml.Vector3f;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;

public class SupplyCartEntity extends AbstractTowableEntity implements MenuProvider, Container {
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(27, ItemStack.EMPTY);
    private int openCount;

    public SupplyCartEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        ContainerHelper.loadAllItems(compoundTag, this.inventory, this.level().registryAccess());
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        ContainerHelper.saveAllItems(compoundTag, this.inventory, this.level().registryAccess());
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(ObjectRegistry.SUPPLY_CART.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int i, net.minecraft.world.entity.player.Inventory playerInventory, Player player) {
        return ChestMenu.threeRows(i, playerInventory, this);
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        if (!this.level().isClientSide && removalReason.shouldDestroy()) {
            Vector3f pos = new Vector3f((float) getX(), (float) getY(), (float) getZ());
            for (ItemStack stack : inventory) {
                if (!stack.isEmpty()) {
                    net.minecraft.world.entity.item.ItemEntity itementity = new net.minecraft.world.entity.item.ItemEntity(this.level(), pos.x, pos.y, pos.z, stack);
                    this.level().addFreshEntity(itementity);
                }
            }
        }
        super.remove(removalReason);
    }

    @Override
    public int getContainerSize() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.inventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (this.hasDriver()) {
            this.removeDriver();
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        } else if (!player.isShiftKeyDown()) {
            player.openMenu(this);
            this.startOpen(player);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            boolean added = this.addDriver(player);
            if (added) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WOOD_FALL, SoundSource.PLAYERS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    @Override
    public @NotNull ItemStack getItem(int i) {
        return this.inventory.get(i);
    }

    @Override
    public @NotNull ItemStack removeItem(int i, int count) {
        return ContainerHelper.removeItem(this.inventory, i, count);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int i) {
        return net.minecraft.world.ContainerHelper.takeItem(this.inventory, i);
    }

    @Override
    public void setItem(int i, ItemStack itemStack) {
        this.inventory.set(i, itemStack);
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        return !this.isRemoved() && player.distanceToSqr(this) <= 64.0;
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    @Override
    protected ItemStack getDropItem() {
        return new ItemStack(ObjectRegistry.SUPPLY_CART.get());
    }

    @Override
    public void startOpen(Player player) {
        if (!this.level().isClientSide) {
            ++this.openCount;
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.level().isClientSide) {
            --this.openCount;
        }
    }

    public boolean isOpen() {
        return this.openCount > 0;
    }
}
package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Containers;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class SupplyCartEntity extends AbstractCartEntity implements MenuProvider {
    private static final EntityDataAccessor<Boolean> DATA_OPEN = SynchedEntityData.defineId(SupplyCartEntity.class, EntityDataSerializers.BOOLEAN);

    private final SimpleContainer inventory;
    private int openTicks;

    public SupplyCartEntity(EntityType<? extends SupplyCartEntity> entityType, Level level) {
        super(entityType, level);
        this.inventory = new SimpleContainer(27);
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isSecondaryUseActive()) {
            return this.onSecondaryUse(player, hand);
        }
        return this.onPrimaryUse(player, hand);
    }

    @Override
    protected InteractionResult onPrimaryUse(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).isEmpty()) {
            return super.onPrimaryUse(player, hand);
        }

        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        player.openMenu(this);
        this.entityData.set(DATA_OPEN, true);
        this.openTicks = 10;
        return InteractionResult.CONSUME;
    }

    @Override
    protected InteractionResult onSecondaryUse(Player player, InteractionHand hand) {
        InteractionResult pickupResult = super.onSecondaryUse(player, hand);
        if (pickupResult.consumesAction()) {
            return pickupResult;
        }

        if (!player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }

        player.openMenu(this);
        this.entityData.set(DATA_OPEN, true);
        this.openTicks = 10;
        return InteractionResult.CONSUME;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OPEN, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        NonNullList<ItemStack> items = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, items, this.registryAccess());
        for (int slotIndex = 0; slotIndex < items.size(); slotIndex++) {
            this.inventory.setItem(slotIndex, items.get(slotIndex));
        }
        this.openTicks = compoundTag.getInt("OpenTicks");
        this.entityData.set(DATA_OPEN, this.openTicks > 0);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        NonNullList<ItemStack> items = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);
        for (int slotIndex = 0; slotIndex < items.size(); slotIndex++) {
            items.set(slotIndex, this.inventory.getItem(slotIndex));
        }
        ContainerHelper.saveAllItems(compoundTag, items, this.registryAccess());
        compoundTag.putInt("OpenTicks", this.openTicks);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.openTicks > 0) {
            this.openTicks--;
            if (this.openTicks == 0) {
                this.entityData.set(DATA_OPEN, false);
            }
        }
    }

    public boolean isOpen() {
        return this.entityData.get(DATA_OPEN);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ObjectRegistry.SUPPLY_CART.get().getDefaultInstance().getHoverName();
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return ChestMenu.threeRows(syncId, playerInventory, this.inventory);
    }

    @Override
    public void remove(RemovalReason removalReason) {
        if (!this.level().isClientSide && removalReason == RemovalReason.KILLED) {
            Containers.dropContents(this.level(), this.blockPosition(), this.inventory);
        }
        super.remove(removalReason);
    }

    @Override
    protected ItemStack getCartItemStack() {
        return new ItemStack(ObjectRegistry.SUPPLY_CART.get());
    }
}
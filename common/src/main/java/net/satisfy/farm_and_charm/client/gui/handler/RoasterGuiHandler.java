package net.satisfy.farm_and_charm.client.gui.handler;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.client.gui.handler.slot.ExtendedSlot;
import net.satisfy.farm_and_charm.core.block.entity.RoasterBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import org.jetbrains.annotations.NotNull;

public class RoasterGuiHandler extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerData propertyDelegate;

    public RoasterGuiHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(8), new SimpleContainerData(2));
    }

    public RoasterGuiHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate) {
        super(ScreenhandlerTypeRegistry.ROASTER_SCREEN_HANDLER.get(), syncId);

        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addDataSlots(propertyDelegate);

        this.buildBlockEntityContainer();
        this.buildPlayerContainer(playerInventory);
    }

    private void buildBlockEntityContainer() {
        this.addSlot(new ExtendedSlot(inventory, 6, 95, 55, stack -> stack.is(TagRegistry.CONTAINER)));

        for (int row = 0; row < 2; row++) {
            for (int slot = 0; slot < 3; slot++) {
                this.addSlot(new Slot(inventory, slot + row * 3, 30 + (slot * 18), 17 + (row * 18)));
            }
        }

        this.addSlot(new Slot(inventory, 7, 124, 28) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
    }

    private void buildPlayerContainer(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public boolean isBeingBurned() {
        return this.propertyDelegate.get(1) != 0;
    }

    public int getScaledProgress(int arrowWidth) {
        final int progress = this.propertyDelegate.get(0);
        final int totalProgress = RoasterBlockEntity.getMaxRoastingTime();
        if (progress == 0) {
            return 0;
        }
        return progress * arrowWidth / totalProgress + 1;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            ItemStack copy = stack.copy();

            if (index >= 0 && index <= 5) {
                if (!this.moveItemStackTo(stack, 8, 44, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 6) {
                if (!this.moveItemStackTo(stack, 8, 44, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 7) {
                if (!this.moveItemStackTo(stack, 8, 44, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stack.is(TagRegistry.CONTAINER)) {
                    if (!this.moveItemStackTo(stack, 6, 7, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, 0, 6, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            slot.onTake(player, stack);
            return copy;
        }
        return ItemStack.EMPTY;
    }


    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }
}
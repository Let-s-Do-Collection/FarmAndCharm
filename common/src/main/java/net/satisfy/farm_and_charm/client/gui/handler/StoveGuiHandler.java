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
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.satisfy.farm_and_charm.client.gui.handler.slot.ExtendedSlot;
import net.satisfy.farm_and_charm.client.gui.handler.slot.StoveOutputSlot;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class StoveGuiHandler extends AbstractContainerMenu {
    private final Container inventory;
    public final ContainerData propertyDelegate;

    public StoveGuiHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(5), new SimpleContainerData(4));
    }

    public StoveGuiHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate) {
        super(ScreenhandlerTypeRegistry.STOVE_SCREEN_HANDLER.get(), syncId);

        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.addDataSlots(propertyDelegate);

        buildBlockEntityContainer(playerInventory);
        buildPlayerContainer(playerInventory);
    }

    private static boolean isFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.isFuel(stack);
    }

    private void buildBlockEntityContainer(Inventory playerInventory) {
        this.addSlot(new StoveOutputSlot(playerInventory.player, inventory, 0, 126, 42));

        this.addSlot(new ExtendedSlot(inventory, 1, 29, 18));
        this.addSlot(new ExtendedSlot(inventory, 2, 47, 18));
        this.addSlot(new ExtendedSlot(inventory, 3, 65, 18));

        this.addSlot(new ExtendedSlot(inventory, 4, 42, 48, StoveGuiHandler::isFuel));

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

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            ItemStack copy = stack.copy();

            if (index == 0) {
                if (!this.moveItemStackTo(stack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, copy);
            } else if (index >= 1 && index <= 3) {
                if (!this.moveItemStackTo(stack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 4) {
                if (!this.moveItemStackTo(stack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (isFuel(stack)) {
                    if (!this.moveItemStackTo(stack, 4, 5, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(stack, 1, 4, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == copy.getCount()) {
                return ItemStack.EMPTY;
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

    public int getScaledProgress(int arrowWidth) {
        final int progress = this.propertyDelegate.get(2);
        final int totalProgress = this.propertyDelegate.get(3);
        if (progress > 0 && totalProgress > 0) {
            return progress * arrowWidth / totalProgress;
        }
        return 0;
    }

    public boolean isBeingBurned() {
        return propertyDelegate.get(0) > 0;
    }
}


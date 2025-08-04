package net.satisfy.farm_and_charm.client.gui.handler;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.satisfy.farm_and_charm.block.entity.RoasterBlockEntity;
import net.satisfy.farm_and_charm.client.gui.handler.slot.ExtendedSlot;
import net.satisfy.farm_and_charm.recipe.RoasterRecipe;
import net.satisfy.farm_and_charm.registry.ScreenhandlerTypeRegistry;
import net.satisfy.farm_and_charm.registry.TagRegistry;

import java.util.List;

public class RoasterGuiHandler extends AbstractRecipeBookGUIScreenHandler {
    private final ContainerData propertyDelegate;

    public RoasterGuiHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(8), new SimpleContainerData(2));
    }

    public RoasterGuiHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate) {
        super(ScreenhandlerTypeRegistry.ROASTER_SCREEN_HANDLER.get(), syncId, 7, playerInventory, inventory, propertyDelegate);

        this.buildBlockEntityContainer(inventory);
        this.buildPlayerContainer(playerInventory);

        this.propertyDelegate = propertyDelegate;
        this.addDataSlots(propertyDelegate);
    }

    private void buildBlockEntityContainer(Container inventory) {
        this.addSlot(new ExtendedSlot(inventory, 6, 95, 55, stack -> stack.is(TagRegistry.CONTAINER)));

        for (int row = 0; row < 2; row++) {
            for (int slot = 0; slot < 3; slot++) {
                this.addSlot(new Slot(inventory, slot + row + (row * 2), 30 + (slot * 18), 17 + (row * 18)));
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
        int i;
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
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
    public boolean hasIngredient(Recipe<?> recipe) {
        if (recipe instanceof RoasterRecipe roasterRecipe) {
            for (Ingredient ingredient : roasterRecipe.getIngredients()) {
                boolean found = false;
                for (Slot slot : this.slots) {
                    if (ingredient.test(slot.getItem())) {
                        found = true;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            ItemStack container = roasterRecipe.getContainer();
            for (Slot slot : this.slots) {
                if (container.getItem() == slot.getItem().getItem()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getCraftingSlotCount() {
        return 7;
    }
}

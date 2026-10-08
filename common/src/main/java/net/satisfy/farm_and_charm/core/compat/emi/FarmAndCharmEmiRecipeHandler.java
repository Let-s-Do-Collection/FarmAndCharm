package net.satisfy.farm_and_charm.core.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class FarmAndCharmEmiRecipeHandler<T extends AbstractContainerMenu> implements StandardRecipeHandler<T> {
    private final EmiRecipeCategory category;
    private final int firstInput;
    private final int inputCount;
    private final int containerSlot;
    private final int firstInventory;

    public FarmAndCharmEmiRecipeHandler(EmiRecipeCategory category, int firstInput, int inputCount, int containerSlot, int firstInventory) {
        this.category = category;
        this.firstInput = firstInput;
        this.inputCount = inputCount;
        this.containerSlot = containerSlot;
        this.firstInventory = firstInventory;
    }

    @Override
    public List<Slot> getInputSources(T handler) {
        return new ArrayList<>(handler.slots.subList(this.firstInventory, this.firstInventory + 36));
    }

    @Override
    public List<Slot> getCraftingSlots(T handler) {
        List<Slot> slots = new ArrayList<>(handler.slots.subList(this.firstInput, this.firstInput + this.inputCount));
        if (this.containerSlot >= 0) {
            slots.add(handler.getSlot(this.containerSlot));
        }
        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(EmiRecipe recipe, T handler) {
        int ingredients = recipe instanceof FarmAndCharmEmiRecipe emiRecipe ? emiRecipe.getIngredientCount() : recipe.getInputs().size();
        List<Slot> slots = new ArrayList<>();
        for (int i = 0; i < Math.min(ingredients, this.inputCount); i++) {
            slots.add(handler.getSlot(this.firstInput + i));
        }
        if (this.containerSlot >= 0 && recipe.getInputs().size() > ingredients) {
            slots.add(handler.getSlot(this.containerSlot));
        }
        return slots;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory() == this.category && recipe.supportsRecipeTree();
    }
}

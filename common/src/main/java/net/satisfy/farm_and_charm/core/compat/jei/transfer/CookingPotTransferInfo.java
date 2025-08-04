package net.satisfy.farm_and_charm.core.compat.jei.transfer;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.satisfy.farm_and_charm.client.gui.handler.CookingPotGuiHandler;
import net.satisfy.farm_and_charm.core.compat.jei.category.CookingPotCategory;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CookingPotTransferInfo implements IRecipeTransferInfo<CookingPotGuiHandler, CookingPotRecipe> {
    @Override
    public @NotNull Class<? extends CookingPotGuiHandler> getContainerClass() {
        return CookingPotGuiHandler.class;
    }

    @Override
    public @NotNull Optional<MenuType<CookingPotGuiHandler>> getMenuType() {
        return Optional.of(ScreenhandlerTypeRegistry.COOKING_POT_SCREEN_HANDLER.get());
    }

    @Override
    public @NotNull RecipeType<CookingPotRecipe> getRecipeType() {
        return CookingPotCategory.COOKING_POT;
    }

    @Override
    public boolean canHandle(CookingPotGuiHandler container, CookingPotRecipe recipe) {
        return true;
    }

    @Override
    public @NotNull List<Slot> getRecipeSlots(CookingPotGuiHandler container, CookingPotRecipe recipe) {
        List<Slot> slots = new ArrayList<>();
        slots.add(container.getSlot(7));
        for (int i = 1; i <= recipe.getIngredients().size() && i < 7; i++) {
            slots.add(container.getSlot(i));
        }
        return slots;
    }

    @Override
    public @NotNull List<Slot> getInventorySlots(CookingPotGuiHandler container, CookingPotRecipe recipe) {
        List<Slot> slots = new ArrayList<>();
        for (int i = 8; i < 8 + 36; i++) {
            Slot slot = container.getSlot(i);
            slots.add(slot);
        }
        return slots;
    }
}

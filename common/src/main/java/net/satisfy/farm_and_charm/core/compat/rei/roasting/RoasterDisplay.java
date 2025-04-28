package net.satisfy.farm_and_charm.core.compat.rei.roasting;


import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.compat.rei.FarmAndCharmREIClientPlugin;
import net.satisfy.farm_and_charm.core.recipe.RoasterRecipe;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RoasterDisplay extends BasicDisplay {

    public static final CategoryIdentifier<RoasterDisplay> ROASTER_DISPLAY = CategoryIdentifier.of(FarmAndCharm.MOD_ID, "roaster_display");


    public RoasterDisplay(RoasterRecipe recipe) {
        this(EntryIngredients.ofIngredients(
                FarmAndCharmREIClientPlugin.ingredients(recipe, getContainer(recipe))
        ), Collections.singletonList(
                EntryIngredients.of(recipe.getResultItem(registryAccess()))
        ), Optional.empty());
    }

    public RoasterDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> id) {
        super(inputs, outputs, id);
    }

    public static ItemStack getContainer(Recipe<RecipeInput> recipe) {
        if (recipe instanceof RoasterRecipe c) {
            return c.getContainer();
        } else return ItemStack.EMPTY;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ROASTER_DISPLAY;
    }

}
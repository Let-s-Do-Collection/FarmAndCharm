package net.satisfy.farm_and_charm.core.compat.rei.roasting;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.recipe.RoasterRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoasterDisplay extends BasicDisplay {
    public static final CategoryIdentifier<RoasterDisplay> ROASTER_DISPLAY = CategoryIdentifier.of(FarmAndCharm.MOD_ID, "roaster_display");

    public RoasterDisplay(RoasterRecipe recipe) {
        super(createInputs(recipe), List.of(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess()))), Optional.empty());
    }

    private static List<EntryIngredient> createInputs(RoasterRecipe recipe) {
        List<EntryIngredient> inputs = new ArrayList<>();
        ItemStack container = recipe.getContainer();
        inputs.add(EntryIngredients.of(container.isEmpty() ? ItemStack.EMPTY : container));
        int max = Math.min(6, recipe.getIngredients().size());
        for (int i = 0; i < max; i++) {
            inputs.add(EntryIngredients.ofIngredient(recipe.getIngredients().get(i)));
        }
        return inputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ROASTER_DISPLAY;
    }
}

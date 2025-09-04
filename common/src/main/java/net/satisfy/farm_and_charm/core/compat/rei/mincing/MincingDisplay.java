package net.satisfy.farm_and_charm.core.compat.rei.mincing;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.recipe.MincerRecipe;

import java.util.List;
import java.util.Optional;

public class MincingDisplay extends BasicDisplay {

    public static final CategoryIdentifier<MincingDisplay> MINCING_DISPLAY =
            CategoryIdentifier.of(FarmAndCharm.MOD_ID, "mincing_display");

    public MincingDisplay(MincerRecipe recipe) {
        super(
                List.of(EntryIngredients.ofIngredient(recipe.getInput())),
                List.of(EntryIngredients.of(recipe.getOutput())),
                Optional.empty()
        );
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return MINCING_DISPLAY;
    }
}

package net.satisfy.farm_and_charm.core.compat.rei.cutting;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardAssemblyRecipe;

import java.util.List;
import java.util.Optional;

public class AssemblyDisplay extends BasicDisplay {
    public static final CategoryIdentifier<AssemblyDisplay> ID = CategoryIdentifier.of(FarmAndCharm.MOD_ID, "cutting_board_assembly");

    public AssemblyDisplay(CuttingBoardAssemblyRecipe recipe) {
        super(recipe.getIngredients().stream().map(EntryIngredients::ofIngredient).toList(), List.of(EntryIngredients.of(recipe.getResult())), Optional.empty());
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }
}

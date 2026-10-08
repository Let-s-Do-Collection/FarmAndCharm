package net.satisfy.farm_and_charm.core.compat.emi;

import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BiConsumer;

public class FarmAndCharmEmiRecipe extends BasicEmiRecipe {
    private final BiConsumer<FarmAndCharmEmiRecipe, WidgetHolder> widgets;
    private final int ingredientCount;

    public FarmAndCharmEmiRecipe(EmiRecipeCategory category, ResourceLocation id, int width, int height, List<EmiIngredient> inputs, List<EmiStack> outputs, BiConsumer<FarmAndCharmEmiRecipe, WidgetHolder> widgets) {
        this(category, id, width, height, inputs, inputs.size(), outputs, widgets);
    }

    public FarmAndCharmEmiRecipe(EmiRecipeCategory category, ResourceLocation id, int width, int height, List<EmiIngredient> inputs, int ingredientCount, List<EmiStack> outputs, BiConsumer<FarmAndCharmEmiRecipe, WidgetHolder> widgets) {
        super(category, id, width, height);
        this.ingredientCount = ingredientCount;
        this.inputs.addAll(inputs);
        this.outputs.addAll(outputs);
        this.widgets = widgets;
    }

    public int getIngredientCount() {
        return this.ingredientCount;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        this.widgets.accept(this, widgets);
    }
}

package net.satisfy.farm_and_charm.core.compat.rei.cooking;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CookingPotDisplay extends BasicDisplay {

    public static final CategoryIdentifier<CookingPotDisplay> COOKING_POT_DISPLAY =
            CategoryIdentifier.of(FarmAndCharm.MOD_ID, "cooking_pot_display");

    public CookingPotDisplay(CookingPotRecipe recipe) {
        super(createInputs(recipe), createOutputs(recipe), Optional.of(CookingPotRecipe.getIdStatic()));
    }

    private static List<EntryIngredient> createInputs(CookingPotRecipe recipe) {
        List<EntryIngredient> inputs = new ArrayList<>();
        int ingredientIndex = 0;

        for (Ingredient ingredient : recipe.getIngredients()) {
            for (ItemStack stack : ingredient.getItems()) {
                if (ingredientIndex < 6) {
                    inputs.add(EntryIngredients.of(stack));
                    ingredientIndex++;
                }
            }
        }

        while (inputs.size() < 6) {
            inputs.add(EntryIngredients.of(ItemStack.EMPTY));
        }

        if (recipe.isContainerRequired() && !recipe.getContainerItem().isEmpty()) {
            inputs.add(EntryIngredients.of(recipe.getContainerItem()));
        } else {
            inputs.add(EntryIngredients.of(new ItemStack(Items.AIR)));
        }

        return inputs;
    }

    private static List<EntryIngredient> createOutputs(CookingPotRecipe recipe) {
        RegistryAccess access = Minecraft.getInstance().level != null
                ? Minecraft.getInstance().level.registryAccess()
                : RegistryAccess.EMPTY;
        return List.of(EntryIngredients.of(recipe.getResultItem(access)));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return COOKING_POT_DISPLAY;
    }
}

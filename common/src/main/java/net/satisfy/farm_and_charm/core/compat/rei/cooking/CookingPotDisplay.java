package net.satisfy.farm_and_charm.core.compat.rei.cooking;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CookingPotDisplay extends BasicDisplay {

    public static final CategoryIdentifier<CookingPotDisplay> COOKING_POT_DISPLAY = CategoryIdentifier.of(FarmAndCharm.MOD_ID, "cooking_pot_display");

    public CookingPotDisplay(Recipe<Container> recipe) {
        this(createInputs(recipe), createOutputs(recipe), getLocation(recipe));
    }

    private static List<EntryIngredient> createInputs(Recipe<Container> recipe) {
        List<EntryIngredient> inputs = new ArrayList<>(recipe.getIngredients().stream()
                .map(EntryIngredients::ofIngredient)
                .collect(Collectors.toList()));
        while (inputs.size() < 6) {
            inputs.add(EntryIngredients.of(new ItemStack(Items.AIR)));
        }
        if (recipe instanceof CookingPotRecipe c && c.isContainerRequired() && !c.getContainerItem().isEmpty()) {
            inputs.add(EntryIngredients.of(c.getContainerItem()));
        } else {
            inputs.add(EntryIngredients.of(new ItemStack(Items.AIR)));
        }
        return inputs;
    }

    private static List<EntryIngredient> createOutputs(Recipe<Container> recipe) {
        RegistryAccess registryAccess = Minecraft.getInstance().level != null ? Minecraft.getInstance().level.registryAccess() : RegistryAccess.EMPTY;
        ItemStack result = recipe.getResultItem(registryAccess);
        return List.of(EntryIngredients.of(result));
    }

    private static Optional<ResourceLocation> getLocation(Recipe<Container> recipe) {
        return Optional.of(recipe.getId());
    }

    public CookingPotDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<ResourceLocation> location) {
        super(inputs, outputs, location);
    }

    public static ItemStack getContainer(Recipe<Container> recipe) {
        if (recipe instanceof CookingPotRecipe c) {
            return c.getContainerItem();
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return COOKING_POT_DISPLAY;
    }

}

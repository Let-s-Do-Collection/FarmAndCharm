package net.satisfy.farm_and_charm.core.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.satisfy.farm_and_charm.core.compat.rei.cooking.CookingPotCategory;
import net.satisfy.farm_and_charm.core.compat.rei.cooking.CookingPotDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.doughing.CraftingBowlCategory;
import net.satisfy.farm_and_charm.core.compat.rei.doughing.CraftingBowlDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.mincing.MincingCategory;
import net.satisfy.farm_and_charm.core.compat.rei.mincing.MincingDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.roasting.RoasterCategory;
import net.satisfy.farm_and_charm.core.compat.rei.roasting.RoasterDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.silo.SiloCategory;
import net.satisfy.farm_and_charm.core.compat.rei.silo.SiloDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.stove.StoveCategory;
import net.satisfy.farm_and_charm.core.compat.rei.stove.StoveDisplay;
import net.satisfy.farm_and_charm.core.recipe.*;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;

import java.util.ArrayList;
import java.util.List;

public class Farm_And_CharmREIClientPlugin implements REIClientPlugin {
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new CookingPotCategory());
        registry.add(new StoveCategory());
        registry.add(new CraftingBowlCategory());
        registry.add(new RoasterCategory());
        registry.add(new SiloCategory());
        registry.add(new MincingCategory());
        registry.addWorkstations(MincingCategory.MINCING_DISPLAY, EntryStacks.of(ObjectRegistry.MINCER.get()));
        registry.addWorkstations(CraftingBowlCategory.CRAFTING_BOWL_DISPLAY, EntryStacks.of(ObjectRegistry.CRAFTING_BOWL.get()));
        registry.addWorkstations(CookingPotDisplay.COOKING_POT_DISPLAY, EntryStacks.of(ObjectRegistry.COOKING_POT.get()));
        registry.addWorkstations(StoveDisplay.STOVE_DISPLAY, EntryStacks.of(ObjectRegistry.STOVE.get()));
        registry.addWorkstations(RoasterDisplay.ROASTER_DISPLAY, EntryStacks.of(ObjectRegistry.ROASTER.get()));
        registry.addWorkstations(SiloCategory.SILO_DISPLAY, EntryStacks.of(ObjectRegistry.SILO_WOOD.get()), EntryStacks.of(ObjectRegistry.SILO_COPPER.get()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(
                CookingPotRecipe.class,
                RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get(),
                holder -> new CookingPotDisplay(holder.value())
        );
        registry.registerRecipeFiller(
                MincerRecipe.class,
                RecipeTypeRegistry.MINCER_RECIPE_TYPE.get(),
                holder -> new MincingDisplay(holder.value())
        );
        registry.registerRecipeFiller(
                StoveRecipe.class,
                RecipeTypeRegistry.STOVE_RECIPE_TYPE.get(),
                holder -> new StoveDisplay(holder.value())
        );
        registry.registerRecipeFiller(
                CraftingBowlRecipe.class,
                RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.get(),
                holder -> new CraftingBowlDisplay(holder.value())
        );
        registry.registerRecipeFiller(
                RoasterRecipe.class,
                RecipeTypeRegistry.ROASTER_RECIPE_TYPE.get(),
                holder -> new RoasterDisplay(holder.value())
        );
        registry.registerRecipeFiller(
                SiloRecipe.class,
                RecipeTypeRegistry.SILO_RECIPE_TYPE.get(),
                holder -> new SiloDisplay(holder.value())
        );
    }


    public static List<Ingredient> ingredients(Recipe<RecipeInput> recipe, ItemStack stack) {
        List<Ingredient> l = new ArrayList<>(recipe.getIngredients());
        l.add(0, Ingredient.of(stack.getItem()));
        return l;
    }
}

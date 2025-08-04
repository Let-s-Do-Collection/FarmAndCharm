package net.satisfy.farm_and_charm.core.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.satisfy.farm_and_charm.client.gui.handler.RoasterGuiHandler;
import net.satisfy.farm_and_charm.client.gui.handler.StoveGuiHandler;
import net.satisfy.farm_and_charm.compat.jei.category.*;
import net.satisfy.farm_and_charm.core.compat.jei.category.CookingPotCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.CraftingBowlCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.MincerCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.RoasterCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.SiloCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.StoveCategory;
import net.satisfy.farm_and_charm.core.compat.jei.transfer.CookingPotTransferInfo;
import net.satisfy.farm_and_charm.core.compat.jei.transfer.RoasterTransferInfo;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.recipe.CraftingBowlRecipe;
import net.satisfy.farm_and_charm.core.recipe.MincerRecipe;
import net.satisfy.farm_and_charm.core.recipe.RoasterRecipe;
import net.satisfy.farm_and_charm.core.recipe.SiloRecipe;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@JeiPlugin
public class Farm_And_Charm_JEIPlugin implements IModPlugin {

    public static void addSlot(IRecipeLayoutBuilder builder, int x, int y, Ingredient ingredient) {
        builder.addSlot(RecipeIngredientRole.INPUT, x, y).addIngredients(ingredient);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CookingPotCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new StoveCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CraftingBowlCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new RoasterCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SiloCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new MincerCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<RecipeHolder<CookingPotRecipe>> cookingHoldersRecipes = rm.getAllRecipesFor(RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get());
        List<CookingPotRecipe> cookingPotRecipes = new ArrayList<>();
        cookingHoldersRecipes.iterator().forEachRemaining(recipeHolder -> {
            cookingPotRecipes.add(recipeHolder.value());
        });
        registration.addRecipes(CookingPotCategory.COOKING_POT, cookingPotRecipes);

        List<RecipeHolder<StoveRecipe>> stoveHolderRecipes = rm.getAllRecipesFor(RecipeTypeRegistry.STOVE_RECIPE_TYPE.get());
        List<StoveRecipe> stoveRecipes = new ArrayList<>();
        stoveHolderRecipes.iterator().forEachRemaining(recipeHolder -> {
            stoveRecipes.add(recipeHolder.value());
        });
        registration.addRecipes(StoveCategory.STOVE, stoveRecipes);

        List<RecipeHolder<CraftingBowlRecipe>> doughingHolderRecipes = rm.getAllRecipesFor(RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.get());
        List<CraftingBowlRecipe> doughingRecipes = new ArrayList<>();
        doughingHolderRecipes.iterator().forEachRemaining(recipeHolder -> {
            doughingRecipes.add(recipeHolder.value());
        });
        registration.addRecipes(CraftingBowlCategory.DOUGHING, doughingRecipes);

        List<RecipeHolder<RoasterRecipe>> roasterHolderRecipes = rm.getAllRecipesFor(RecipeTypeRegistry.ROASTER_RECIPE_TYPE.get());
        List<RoasterRecipe> roasterRecipes = new ArrayList<>();
        roasterHolderRecipes.iterator().forEachRemaining(recipeHolder -> {
            roasterRecipes.add(recipeHolder.value());
        });
        registration.addRecipes(RoasterCategory.ROASTER, roasterRecipes);

        List<RecipeHolder<SiloRecipe>> siloHolderRecipes = rm.getAllRecipesFor(RecipeTypeRegistry.SILO_RECIPE_TYPE.get());
        List<SiloRecipe> siloRecipes = new ArrayList<>();
        siloHolderRecipes.iterator().forEachRemaining(recipeHolder -> {
            siloRecipes.add(recipeHolder.value());
        });
        registration.addRecipes(SiloCategory.DRYING_TYPE, siloRecipes);

        List<RecipeHolder<MincerRecipe>> mincerHolderRecipes = rm.getAllRecipesFor(RecipeTypeRegistry.MINCER_RECIPE_TYPE.get());
        List<MincerRecipe> mincerRecipes = new ArrayList<>();
        mincerHolderRecipes.iterator().forEachRemaining(recipeHolder -> {
            mincerRecipes.add(recipeHolder.value());
        });
        registration.addRecipes(MincerCategory.MINCING_TYPE, mincerRecipes);
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return FarmAndCharmIdentifier.of("jei_plugin");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new RoasterTransferInfo());
        registration.addRecipeTransferHandler(RoasterGuiHandler.class, ScreenhandlerTypeRegistry.ROASTER_SCREEN_HANDLER.get(), RoasterCategory.ROASTER, 1, 3, 5, 36);
        registration.addRecipeTransferHandler(new CookingPotTransferInfo());
        registration.addRecipeTransferHandler(StoveGuiHandler.class, ScreenhandlerTypeRegistry.STOVE_SCREEN_HANDLER.get(), StoveCategory.STOVE, 1, 3, 5, 36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ObjectRegistry.COOKING_POT.get().asItem().getDefaultInstance(), CookingPotCategory.COOKING_POT);
        registration.addRecipeCatalyst(ObjectRegistry.CRAFTING_BOWL.get().asItem().getDefaultInstance(), CraftingBowlCategory.DOUGHING);
        registration.addRecipeCatalyst(ObjectRegistry.STOVE.get().asItem().getDefaultInstance(), StoveCategory.STOVE);
        registration.addRecipeCatalyst(ObjectRegistry.ROASTER.get().asItem().getDefaultInstance(), RoasterCategory.ROASTER);
        registration.addRecipeCatalyst(ObjectRegistry.MINCER.get().asItem().getDefaultInstance(), MincerCategory.MINCING_TYPE);
        registration.addRecipeCatalyst(ObjectRegistry.SILO_WOOD.get().asItem().getDefaultInstance(), SiloCategory.DRYING_TYPE);
        registration.addRecipeCatalyst(ObjectRegistry.SILO_COPPER.get().asItem().getDefaultInstance(), SiloCategory.DRYING_TYPE);

    }
}

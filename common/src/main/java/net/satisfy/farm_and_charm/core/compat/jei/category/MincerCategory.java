package net.satisfy.farm_and_charm.core.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.recipe.MincerRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MincerCategory implements IRecipeCategory<MincerRecipe> {
    public static final RecipeType<MincerRecipe> MINCING_TYPE = RecipeType.create(FarmAndCharm.MOD_ID, "mincer", MincerRecipe.class);
    private static final int WIDTH = 150;
    private static final int HEIGHT = 50;

    private final IDrawable icon;
    private final IDrawable slot;

    public MincerCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ObjectRegistry.MINCER.get()));
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType<MincerRecipe> getRecipeType() {
        return MINCING_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("rei.farm_and_charm.mincer_category");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MincerRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> ingredients = recipe.getIngredients();
        if (!ingredients.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 30, 15)
                    .setBackground(this.slot, -1, -1)
                    .addIngredients(ingredients.get(0));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 15)
                .setBackground(this.slot, -1, -1)
                .addItemStack(recipe.getOutput());
    }
}

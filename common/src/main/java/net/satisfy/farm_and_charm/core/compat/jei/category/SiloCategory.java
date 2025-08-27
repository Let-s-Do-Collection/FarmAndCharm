package net.satisfy.farm_and_charm.core.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.recipe.SiloRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class SiloCategory implements IRecipeCategory<SiloRecipe> {
    public static final RecipeType<SiloRecipe> DRYING_TYPE = RecipeType.create(FarmAndCharm.MOD_ID, "drying", SiloRecipe.class);
    private static final int WIDTH = 150;
    private static final int HEIGHT = 50;

    private final IDrawable icon;

    public SiloCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ObjectRegistry.SILO_WOOD.get()));
    }

    @Override
    public @NotNull RecipeType<SiloRecipe> getRecipeType() {
        return DRYING_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("rei.farm_and_charm.silo_category");
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
    public void setRecipe(IRecipeLayoutBuilder builder, SiloRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 15).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 15).addItemStack(recipe.getResultItem(null));
    }
}

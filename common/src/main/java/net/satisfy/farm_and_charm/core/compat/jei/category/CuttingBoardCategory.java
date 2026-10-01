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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.foundation.compat.RecipeViewerLayout;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class CuttingBoardCategory implements IRecipeCategory<CuttingBoardRecipe> {
    public static final RecipeType<CuttingBoardRecipe> TYPE = RecipeType.create(FarmAndCharm.MOD_ID, "cutting_board", CuttingBoardRecipe.class);
    private static final int INPUT_X = 10;
    private static final int ARROW_X = 34;
    private static final int OUTPUT_X = 64;
    private static final int ROW_Y = 6;

    private final IDrawable icon;
    private final IDrawable slot;

    public CuttingBoardCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ObjectRegistry.CUTTING_BOARD.get()));
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType<CuttingBoardRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("category.farm_and_charm.cutting_board");
    }

    @Override
    public int getWidth() {
        return RecipeViewerLayout.ROW_WIDTH;
    }

    @Override
    public int getHeight() {
        return RecipeViewerLayout.ROW_HEIGHT;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CuttingBoardRecipe recipe, IFocusGroup focuses) {
        int offset = offset(recipe);
        builder.addSlot(RecipeIngredientRole.INPUT, offset + INPUT_X + 1, ROW_Y + 1).setBackground(this.slot, -1, -1).addIngredients(recipe.getIngredient());
        builder.addSlot(RecipeIngredientRole.OUTPUT, offset + OUTPUT_X + 1, ROW_Y + 1).setBackground(this.slot, -1, -1).addItemStack(recipe.getResult());
        int x = offset + OUTPUT_X + RecipeViewerLayout.SLOT;
        for (ItemStack byproduct : recipe.getByproducts()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, x + 1, ROW_Y + 1).setBackground(this.slot, -1, -1).addItemStack(byproduct);
            x += RecipeViewerLayout.SLOT;
        }
    }

    @Override
    public void draw(CuttingBoardRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        RecipeViewerLayout.drawRightArrow(graphics, offset(recipe) + ARROW_X, ROW_Y + 1);
    }

    private static int offset(CuttingBoardRecipe recipe) {
        int width = OUTPUT_X - INPUT_X + (1 + recipe.getByproducts().size()) * RecipeViewerLayout.SLOT;
        return (RecipeViewerLayout.ROW_WIDTH - width) / 2 - INPUT_X;
    }
}

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
import net.minecraft.world.item.crafting.Ingredient;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.compat.RecipeViewerLayout;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardAssemblyRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AssemblyCategory implements IRecipeCategory<CuttingBoardAssemblyRecipe> {
    public static final RecipeType<CuttingBoardAssemblyRecipe> TYPE = RecipeType.create(FarmAndCharm.MOD_ID, "cutting_board_assembly", CuttingBoardAssemblyRecipe.class);
    private static final int SLOT_X = (RecipeViewerLayout.ASSEMBLY_WIDTH - RecipeViewerLayout.SLOT) / 2;
    private static final int ARROW_X = (RecipeViewerLayout.ASSEMBLY_WIDTH - RecipeViewerLayout.ARROW_THICKNESS) / 2;

    private final IDrawable icon;
    private final IDrawable slot;

    public AssemblyCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ObjectRegistry.CUTTING_BOARD.get()));
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType<CuttingBoardAssemblyRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("category.farm_and_charm.assembly");
    }

    @Override
    public int getWidth() {
        return RecipeViewerLayout.ASSEMBLY_WIDTH;
    }

    @Override
    public int getHeight() {
        return RecipeViewerLayout.ASSEMBLY_HEIGHT;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CuttingBoardAssemblyRecipe recipe, IFocusGroup focuses) {
        List<Ingredient> ingredients = recipe.getIngredients();
        int y = RecipeViewerLayout.assemblyTop(ingredients.size());
        for (Ingredient ingredient : ingredients) {
            builder.addSlot(RecipeIngredientRole.INPUT, SLOT_X + 1, y + 1).setBackground(this.slot, -1, -1).addIngredients(ingredient);
            y += RecipeViewerLayout.ASSEMBLY_STEP;
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, SLOT_X + 1, y + 1).setBackground(this.slot, -1, -1).addItemStack(recipe.getResult());
    }

    @Override
    public void draw(CuttingBoardAssemblyRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        int y = RecipeViewerLayout.assemblyTop(recipe.getIngredients().size()) + RecipeViewerLayout.SLOT + 1;
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            RecipeViewerLayout.drawDownArrow(graphics, ARROW_X, y);
            y += RecipeViewerLayout.ASSEMBLY_STEP;
        }
    }
}

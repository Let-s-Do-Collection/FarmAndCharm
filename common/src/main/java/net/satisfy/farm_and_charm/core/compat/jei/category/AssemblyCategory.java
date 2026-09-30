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
        RecipeViewerLayout.AssemblyLayout layout = RecipeViewerLayout.assembly(ingredients.size(), recipe.isOrdered());
        for (int i = 0; i < ingredients.size(); i++) {
            RecipeViewerLayout.Pos pos = layout.slots().get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, pos.x() + 1, pos.y() + 1).setBackground(this.slot, -1, -1).addIngredients(ingredients.get(i));
        }
        RecipeViewerLayout.Pos output = layout.output();
        builder.addSlot(RecipeIngredientRole.OUTPUT, output.x() + 1, output.y() + 1).setBackground(this.slot, -1, -1).addItemStack(recipe.getResult());
    }

    @Override
    public void draw(CuttingBoardAssemblyRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        for (RecipeViewerLayout.Pos pos : RecipeViewerLayout.assembly(recipe.getIngredients().size(), recipe.isOrdered()).arrows()) {
            RecipeViewerLayout.drawRightArrow(graphics, pos.x(), pos.y());
        }
    }
}

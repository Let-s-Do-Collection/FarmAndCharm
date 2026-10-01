package net.satisfy.farm_and_charm.core.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import java.util.List;
import net.satisfy.foundation.compat.RecipeViewerLayout;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.client.gui.GuiGraphics;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
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
    private static final int INPUT_X = (WIDTH - 72) / 2;
    private static final int ARROW_X = INPUT_X + 24;
    private static final int OUTPUT_X = INPUT_X + 54;
    private static final int ROW_Y = (HEIGHT - RecipeViewerLayout.SLOT) / 2;

    private final IDrawable icon;
    private final IDrawable slot;

    public SiloCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ObjectRegistry.SILO_WOOD.get()));
        this.slot = helper.getSlotDrawable();
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
        List<Ingredient> ingredients = recipe.getIngredients();
        IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X + 1, ROW_Y + 1).setBackground(this.slot, -1, -1);
        if (!ingredients.isEmpty()) {
            input.addIngredients(ingredients.get(0));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + 1, ROW_Y + 1)
                .setBackground(this.slot, -1, -1)
                .addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(SiloRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        RecipeViewerLayout.drawRightArrow(graphics, ARROW_X, ROW_Y + 1);
    }
}

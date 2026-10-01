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
import net.minecraft.world.item.Items;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.foundation.compat.RecipeViewerLayout;
import net.satisfy.farm_and_charm.core.util.Strippables;
import org.jetbrains.annotations.NotNull;

public class StrippingCategory implements IRecipeCategory<Strippables.Entry> {
    public static final RecipeType<Strippables.Entry> TYPE = RecipeType.create(FarmAndCharm.MOD_ID, "log_stripping", Strippables.Entry.class);
    private static final int INPUT_X = 10;
    private static final int ARROW_X = 34;
    private static final int OUTPUT_X = 64;
    private static final int ROW_Y = 6;

    private final IDrawable icon;
    private final IDrawable slot;

    public StrippingCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.IRON_AXE));
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType<Strippables.Entry> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("category.farm_and_charm.log_stripping");
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
    public void setRecipe(IRecipeLayoutBuilder builder, Strippables.Entry recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X + 1, ROW_Y + 1).setBackground(this.slot, -1, -1).addItemStack(recipe.log());
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + 1, ROW_Y + 1).setBackground(this.slot, -1, -1).addItemStack(recipe.stripped());
    }

    @Override
    public void draw(Strippables.Entry recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        RecipeViewerLayout.drawRightArrow(graphics, ARROW_X, ROW_Y + 1);
    }
}

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
import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.recipe.CraftingBowlRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class CraftingBowlCategory implements IRecipeCategory<CraftingBowlRecipe> {
    public static final RecipeType<CraftingBowlRecipe> DOUGHING = RecipeType.create(FarmAndCharm.MOD_ID, "doughing", CraftingBowlRecipe.class);
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "textures/gui/crafting_bowl.png");

    private final IDrawable background;
    private final IDrawable icon;
    private static final int WIDTH = 176;
    private static final int HEIGHT = 85;

    public CraftingBowlCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ObjectRegistry.CRAFTING_BOWL.get().asItem().getDefaultInstance());
    }

    @Override
    public @NotNull RecipeType<CraftingBowlRecipe> getRecipeType() {
        return DOUGHING;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("rei.farm_and_charm.bowl_category");
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
    public void draw(CraftingBowlRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CraftingBowlRecipe recipe, IFocusGroup focuses) {
        if (!recipe.getIngredients().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 50, 25).addIngredients(recipe.getIngredients().get(0));
        }
        if (recipe.getIngredients().size() > 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, 50, 43).addIngredients(recipe.getIngredients().get(1));
        }
        if (recipe.getIngredients().size() > 2) {
            builder.addSlot(RecipeIngredientRole.INPUT, 32, 25).addIngredients(recipe.getIngredients().get(2));
        }
        if (recipe.getIngredients().size() > 3) {
            builder.addSlot(RecipeIngredientRole.INPUT, 32, 43).addIngredients(recipe.getIngredients().get(3));
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 35).addItemStack(recipe.getResultItem(null));
    }
}

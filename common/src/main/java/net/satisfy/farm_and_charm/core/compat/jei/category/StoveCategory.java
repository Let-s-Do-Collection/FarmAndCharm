package net.satisfy.farm_and_charm.core.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.block.entity.StoveBlockEntity;
import net.satisfy.farm_and_charm.client.gui.StoveGui;
import net.satisfy.farm_and_charm.core.compat.jei.Farm_And_Charm_JEIPlugin;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class StoveCategory implements IRecipeCategory<StoveRecipe> {
    public static final RecipeType<StoveRecipe> STOVE = RecipeType.create(FarmAndCharm.MOD_ID, "stove", StoveRecipe.class);
    public static final int WIDTH = 124;
    public static final int HEIGHT = 60;
    public static final int WIDTH_OF = 26;
    public static final int HEIGHT_OF = 13;
    private final IDrawable background;
    private final IDrawable icon;

    private final IDrawable burnIcon;
    private final IDrawableAnimated arrow;
    private final Component localizedName;

    public StoveCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(StoveGui.BACKGROUND, WIDTH_OF, HEIGHT_OF, WIDTH, HEIGHT);
        this.arrow = helper.drawableBuilder(StoveGui.BACKGROUND, 178, 20, 18, 25)
                .buildAnimated(StoveBlockEntity.TOTAL_COOKING_TIME, IDrawableAnimated.StartDirection.LEFT, false);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ObjectRegistry.STOVE.get().asItem().getDefaultInstance());
        this.burnIcon = helper.createDrawable(StoveGui.BACKGROUND, 176, 0, 17, 15);
        this.localizedName = Component.translatable("rei.farm_and_charm.stove_category");
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, StoveRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        Farm_And_Charm_JEIPlugin.addSlot(builder, 29 - WIDTH_OF, 18 - HEIGHT_OF, ingredients.get(0));

        if (ingredients.size() > 1) {
            Farm_And_Charm_JEIPlugin.addSlot(builder, 47 - WIDTH_OF, 18 - HEIGHT_OF, ingredients.get(1));
        }

        if (ingredients.size() > 2) {
            Farm_And_Charm_JEIPlugin.addSlot(builder, 65 - WIDTH_OF, 18 - HEIGHT_OF, ingredients.get(2));
        }

        assert Minecraft.getInstance().level != null;
        builder.addSlot(RecipeIngredientRole.OUTPUT, 126 - WIDTH_OF, 42 - HEIGHT_OF)
                .addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
    }


    @Override
    public void draw(StoveRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, StoveGui.ARROW_X - WIDTH_OF, StoveGui.ARROW_Y - HEIGHT_OF);
        burnIcon.draw(guiGraphics, 62 - WIDTH_OF, 49 - HEIGHT_OF);

        drawExperience(recipe, guiGraphics);
    }

    protected void drawExperience(StoveRecipe recipe, GuiGraphics guiGraphics) {
        float experience = recipe.getExperience();
        if (experience > 0) {
            Component experienceString = Component.translatable("gui.jei.category.smelting.experience", experience);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(experienceString);
            guiGraphics.drawString(fontRenderer, experienceString, getWidth() - stringWidth, 0, 0xFF808080, false);
        }
    }

    @Override
    public @NotNull RecipeType<StoveRecipe> getRecipeType() {
        return STOVE;
    }

    @Override
    public @NotNull Component getTitle() {
        return this.localizedName;
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }
}

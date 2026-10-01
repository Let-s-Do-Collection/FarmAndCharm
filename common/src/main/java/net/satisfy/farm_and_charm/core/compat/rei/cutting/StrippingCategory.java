package net.satisfy.farm_and_charm.core.compat.rei.cutting;

import net.satisfy.foundation.compat.rei.ReiWidgets;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.satisfy.foundation.compat.RecipeViewerLayout;
import net.satisfy.farm_and_charm.core.util.Strippables;

import java.util.ArrayList;
import java.util.List;

public class StrippingCategory implements DisplayCategory<StrippingDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends StrippingDisplay> getCategoryIdentifier() {
        return StrippingDisplay.ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.farm_and_charm.log_stripping");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(Items.IRON_AXE);
    }

    @Override
    public int getDisplayWidth(StrippingDisplay display) {
        return RecipeViewerLayout.ROW_WIDTH + PADDING * 2;
    }

    @Override
    public int getDisplayHeight() {
        return RecipeViewerLayout.ROW_HEIGHT + PADDING * 2;
    }

    @Override
    public List<Widget> setupDisplay(StrippingDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        int x = bounds.x + PADDING;
        int y = bounds.y + PADDING + 6;
        ReiWidgets.inputSlot(widgets, x + 10, y, display.getInputEntries().getFirst());
        ReiWidgets.rightArrow(widgets, x + 34, y + 1);
        ReiWidgets.outputSlot(widgets, x + 64, y, display.getOutputEntries().getFirst());
        return widgets;
    }
}

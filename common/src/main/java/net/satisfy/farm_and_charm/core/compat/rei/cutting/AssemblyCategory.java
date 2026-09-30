package net.satisfy.farm_and_charm.core.compat.rei.cutting;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.satisfy.farm_and_charm.core.compat.RecipeViewerLayout;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.ArrayList;
import java.util.List;

public class AssemblyCategory implements DisplayCategory<AssemblyDisplay> {
    private static final int PADDING = 5;

    @Override
    public CategoryIdentifier<? extends AssemblyDisplay> getCategoryIdentifier() {
        return AssemblyDisplay.ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("category.farm_and_charm.assembly");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ObjectRegistry.CUTTING_BOARD.get());
    }

    @Override
    public int getDisplayWidth(AssemblyDisplay display) {
        return RecipeViewerLayout.ASSEMBLY_WIDTH + PADDING * 2;
    }

    @Override
    public int getDisplayHeight() {
        return RecipeViewerLayout.ASSEMBLY_HEIGHT + PADDING * 2;
    }

    @Override
    public List<Widget> setupDisplay(AssemblyDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));
        int slotX = bounds.x + PADDING + (RecipeViewerLayout.ASSEMBLY_WIDTH - RecipeViewerLayout.SLOT) / 2;
        int arrowX = bounds.x + PADDING + (RecipeViewerLayout.ASSEMBLY_WIDTH - RecipeViewerLayout.ARROW_THICKNESS) / 2;
        List<EntryIngredient> inputs = display.getInputEntries();
        int y = bounds.y + PADDING + RecipeViewerLayout.assemblyTop(inputs.size());
        for (EntryIngredient input : inputs) {
            ReiWidgets.inputSlot(widgets, slotX, y, input);
            ReiWidgets.downArrow(widgets, arrowX, y + RecipeViewerLayout.SLOT + 1);
            y += RecipeViewerLayout.ASSEMBLY_STEP;
        }
        ReiWidgets.outputSlot(widgets, slotX, y, display.getOutputEntries().getFirst());
        return widgets;
    }
}

package net.satisfy.farm_and_charm.core.compat.rei.mincing;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.List;

public class MincingCategory implements DisplayCategory<MincingDisplay> {
    public static final CategoryIdentifier<MincingDisplay> MINCING_DISPLAY = CategoryIdentifier.of(FarmAndCharm.MOD_ID, "mincing_display");

    @Override
    public CategoryIdentifier<MincingDisplay> getCategoryIdentifier() {
        return MINCING_DISPLAY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("rei.farm_and_charm.mincer_category");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ObjectRegistry.MINCER.get());
    }

    @Override
    public int getDisplayWidth(MincingDisplay display) {
        return 64;
    }

    @Override
    public int getDisplayHeight() {
        return 96;
    }

    @Override
    public List<Widget> setupDisplay(MincingDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX(), bounds.getCenterY());
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createDrawableWidget((drawableHelper, matrices, mouseX, mouseY) -> {
            int arrowX = startPoint.x - 5;
            int arrowY = startPoint.y - 12;
        }));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x - 8, startPoint.y + 12)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x - 8, startPoint.y + 12))
                .entries(display.getOutputEntries().get(0)).disableBackground().markOutput());

        if (display.getInputEntries().isEmpty())
            widgets.add(Widgets.createSlotBackground(new Point(startPoint.x - 8, startPoint.y - 32)));
        else
            widgets.add(Widgets.createSlot(new Point(startPoint.x - 8, startPoint.y - 32))
                    .entries(display.getInputEntries().get(0)).markInput());

        return widgets;
    }

}
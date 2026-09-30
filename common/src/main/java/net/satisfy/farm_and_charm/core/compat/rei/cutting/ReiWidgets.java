package net.satisfy.farm_and_charm.core.compat.rei.cutting;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.satisfy.farm_and_charm.core.compat.RecipeViewerLayout;

import java.util.List;

final class ReiWidgets {
    private ReiWidgets() {
    }

    static void inputSlot(List<Widget> widgets, int x, int y, EntryIngredient entries) {
        widgets.add(Widgets.createSlot(new Point(x + 1, y + 1)).entries(entries).markInput());
    }

    static void outputSlot(List<Widget> widgets, int x, int y, EntryIngredient entries) {
        widgets.add(Widgets.createSlot(new Point(x + 1, y + 1)).entries(entries).markOutput());
    }

    static void rightArrow(List<Widget> widgets, int x, int y) {
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> RecipeViewerLayout.drawRightArrow(graphics, x, y)));
    }

    static void rightArrow(List<Widget> widgets, int x, int y, Component label) {
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
            RecipeViewerLayout.drawRightArrow(graphics, x, y);
            int width = Minecraft.getInstance().font.width(label);
            graphics.drawString(Minecraft.getInstance().font, label, x + (RecipeViewerLayout.ARROW_LENGTH - width) / 2, y + 19, 0x808080, false);
        }));
    }

    static void downArrow(List<Widget> widgets, int x, int y) {
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> RecipeViewerLayout.drawDownArrow(graphics, x, y)));
    }
}

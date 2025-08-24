package net.satisfy.farm_and_charm.core.compat.rei.cooking;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.satisfy.farm_and_charm.core.block.entity.CookingPotBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.List;

public class CookingPotCategory implements DisplayCategory<CookingPotDisplay> {
    

    @Override
    public CategoryIdentifier<CookingPotDisplay> getCategoryIdentifier() {
        return CookingPotDisplay.COOKING_POT_DISPLAY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("rei.farm_and_charm.cooking_pot_category");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ObjectRegistry.COOKING_POT.get());
    }

    @Override
    public List<Widget> setupDisplay(CookingPotDisplay display, Rectangle bounds) {
        var startPoint = new org.joml.Vector2i(bounds.getCenterX() - 55, bounds.getCenterY() - 13);
        var widgets = new java.util.ArrayList<Widget>();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 54, startPoint.y - 1))
                .animationDurationTicks(CookingPotBlockEntity.getMaxCookingTime()));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 90, startPoint.y)));
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 90, startPoint.y))
                .entries(display.getOutputEntries().get(0))
                .disableBackground()
                .markOutput());

        var containerEntry = display.getInputEntries().get(6);
        var containerStack = containerEntry.isEmpty() ? ItemStack.EMPTY : (ItemStack) containerEntry.get(0).getValue();
        if (!containerStack.isEmpty() && containerStack.getItem() != Items.AIR) {
            widgets.add(Widgets.createSlot(new Point(startPoint.x + 56, startPoint.y + 23))
                    .entries(containerEntry)
                    .markInput());
        }

        for (int i = 0; i < 6; i++) {
            int x = (i % 3) * 18 - 8;
            int y = (i / 3) * 18 - 4;
            widgets.add(Widgets.createSlot(new Point(startPoint.x + x, startPoint.y + y))
                    .entries(display.getInputEntries().get(i))
                    .markInput());
        }

        return widgets;
    }
}

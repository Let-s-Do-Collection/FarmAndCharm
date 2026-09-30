package net.satisfy.farm_and_charm.core.compat;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardAssemblyRecipe;

import java.util.ArrayList;
import java.util.List;

public final class RecipeViewerLayout {
    public static final int SLOT = 18;
    public static final int ARROW_LENGTH = 24;
    public static final int ARROW_THICKNESS = 16;
    public static final int GRID = 3 * SLOT;
    public static final int ARROW_GAP = 3;
    public static final int ORDERED_STEP = SLOT + ARROW_GAP + ARROW_LENGTH + ARROW_GAP;
    public static final int ASSEMBLY_WIDTH = CuttingBoardAssemblyRecipe.MAX_ORDERED_ITEMS * ORDERED_STEP + SLOT;
    public static final int ASSEMBLY_HEIGHT = GRID;
    public static final int ROW_WIDTH = 130;
    public static final int ROW_HEIGHT = 36;

    private static final ResourceLocation ARROW = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");

    private RecipeViewerLayout() {
    }

    public static void drawRightArrow(GuiGraphics graphics, int x, int y) {
        graphics.blitSprite(ARROW, x, y, ARROW_LENGTH, ARROW_THICKNESS);
    }

    public static AssemblyLayout assembly(int inputs, boolean ordered) {
        List<Pos> slots = new ArrayList<>();
        List<Pos> arrows = new ArrayList<>();
        int arrowY = (GRID - ARROW_THICKNESS) / 2;
        int rowY = (GRID - SLOT) / 2;
        if (ordered) {
            int x = (ASSEMBLY_WIDTH - (inputs * ORDERED_STEP + SLOT)) / 2;
            for (int i = 0; i < inputs; i++) {
                slots.add(new Pos(x, rowY));
                arrows.add(new Pos(x + SLOT + ARROW_GAP, arrowY + 1));
                x += ORDERED_STEP;
            }
            return new AssemblyLayout(slots, arrows, new Pos(x, rowY));
        }
        int gridWidth = GRID + ARROW_GAP * 2 + ARROW_LENGTH + ARROW_GAP * 2 + SLOT;
        int x = (ASSEMBLY_WIDTH - gridWidth) / 2;
        int top = (GRID - ((inputs + 2) / 3) * SLOT) / 2;
        for (int i = 0; i < inputs; i++) {
            slots.add(new Pos(x + (i % 3) * SLOT, top + (i / 3) * SLOT));
        }
        int arrowX = x + GRID + ARROW_GAP * 2;
        arrows.add(new Pos(arrowX, arrowY + 1));
        return new AssemblyLayout(slots, arrows, new Pos(arrowX + ARROW_LENGTH + ARROW_GAP * 2, rowY));
    }

    public record Pos(int x, int y) {
    }

    public record AssemblyLayout(List<Pos> slots, List<Pos> arrows, Pos output) {
    }
}

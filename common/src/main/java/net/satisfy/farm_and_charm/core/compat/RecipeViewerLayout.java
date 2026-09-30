package net.satisfy.farm_and_charm.core.compat;

import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class RecipeViewerLayout {
    public static final int SLOT = 18;
    public static final int ARROW_LENGTH = 24;
    public static final int ARROW_THICKNESS = 16;
    public static final int ASSEMBLY_WIDTH = 60;
    public static final int ASSEMBLY_STEP = SLOT + ARROW_LENGTH + 2;
    public static final int ASSEMBLY_MAX_INGREDIENTS = 4;
    public static final int ASSEMBLY_HEIGHT = ASSEMBLY_MAX_INGREDIENTS * ASSEMBLY_STEP + SLOT;
    public static final int ROW_WIDTH = 130;
    public static final int ROW_HEIGHT = 36;

    private static final ResourceLocation ARROW = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");

    private RecipeViewerLayout() {
    }

    public static void drawRightArrow(GuiGraphics graphics, int x, int y) {
        graphics.blitSprite(ARROW, x, y, ARROW_LENGTH, ARROW_THICKNESS);
    }

    public static void drawDownArrow(GuiGraphics graphics, int x, int y) {
        graphics.pose().pushPose();
        graphics.pose().translate(x + ARROW_THICKNESS, y, 0);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(90.0F));
        graphics.blitSprite(ARROW, 0, 0, ARROW_LENGTH, ARROW_THICKNESS);
        graphics.pose().popPose();
    }

    public static int assemblyTop(int ingredients) {
        return (ASSEMBLY_HEIGHT - (ingredients * ASSEMBLY_STEP + SLOT)) / 2;
    }
}

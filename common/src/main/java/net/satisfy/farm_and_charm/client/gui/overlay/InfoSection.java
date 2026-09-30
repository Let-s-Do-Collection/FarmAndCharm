package net.satisfy.farm_and_charm.client.gui.overlay;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record InfoSection(Component title, List<ItemStack> icons, int columns, @Nullable ResourceLocation image, int imageSize) {
    public static final int ROW_COLUMNS = 6;
    public static final int GRID_COLUMNS = 3;
    private static final int ICON_SIZE = 16;
    private static final int ICON_STEP = 19;
    private static final int TITLE_HEIGHT = 13;
    private static final int TEXT_HEIGHT = 8;
    private static final int COLOR_TITLE = 0xFFE8C97A;
    private static final int COLOR_SLOT = 0x28000000;

    public static InfoSection title(Component title) {
        return new InfoSection(title, List.of(), ROW_COLUMNS, null, 0);
    }

    public static InfoSection icons(Component title, List<ItemStack> icons, int columns) {
        return new InfoSection(title, icons, columns, null, 0);
    }

    public static InfoSection image(Component title, ResourceLocation image, int size) {
        return new InfoSection(title, List.of(), ROW_COLUMNS, image, size);
    }

    int rows() {
        return (this.icons.size() + this.columns - 1) / this.columns;
    }

    int width(Font font) {
        int iconWidth = Math.min(this.icons.size(), this.columns) * ICON_STEP - (this.icons.isEmpty() ? 0 : ICON_STEP - ICON_SIZE - 2);
        return Math.max(Math.max(font.width(this.title), iconWidth), this.imageSize);
    }

    int height() {
        if (this.icons.isEmpty() && this.image == null) {
            return TEXT_HEIGHT;
        }
        return TITLE_HEIGHT + this.rows() * ICON_STEP + (this.image != null ? this.imageSize + 2 : 0);
    }

    void draw(GuiGraphics graphics, Font font, int y, int width) {
        graphics.drawString(font, this.title, (width - font.width(this.title)) / 2, y, COLOR_TITLE);
        int cursor = y + TITLE_HEIGHT;
        for (int row = 0; row < this.rows(); row++) {
            int start = row * this.columns;
            int inRow = Math.min(this.columns, this.icons.size() - start);
            int rowX = (width - (inRow * ICON_STEP - (ICON_STEP - ICON_SIZE))) / 2;
            for (int i = 0; i < inRow; i++) {
                int iconX = rowX + i * ICON_STEP;
                int iconY = cursor + row * ICON_STEP;
                graphics.fill(iconX - 1, iconY - 1, iconX + ICON_SIZE + 1, iconY + ICON_SIZE + 1, COLOR_SLOT);
                graphics.renderItem(this.icons.get(start + i), iconX, iconY);
            }
        }
        cursor += this.rows() * ICON_STEP;
        if (this.image != null) {
            graphics.blit(this.image, (width - this.imageSize) / 2, cursor, 0, 0, this.imageSize, this.imageSize, this.imageSize, this.imageSize);
        }
    }
}

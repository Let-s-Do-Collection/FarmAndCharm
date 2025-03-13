package net.satisfy.farm_and_charm.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.satisfy.farm_and_charm.client.gui.handler.StoveGuiHandler;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

@Environment(EnvType.CLIENT)
public class StoveGui extends AbstractContainerScreen<StoveGuiHandler> {
    public static final ResourceLocation BACKGROUND;

    public static final int ARROW_X = 93;
    public static final int ARROW_Y = 32;

    public StoveGui(StoveGuiHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
    }

    static {
        BACKGROUND = FarmAndCharmIdentifier.of("textures/gui/pot_gui.png");
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics, (int) delta, mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public void renderProgressArrow(GuiGraphics guiGraphics) {
        int progress = this.menu.getScaledProgress(18);
        guiGraphics.blit(BACKGROUND, leftPos + 93, topPos + 32, 178, 20, progress, 25);
    }

    public void renderBurnIcon(GuiGraphics guiGraphics, int posX, int posY) {
        if (this.menu.isBeingBurned()) {
            guiGraphics.blit(BACKGROUND, posX + 62, posY + 49, 176, 0, 17, 15);
        }
    }

    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, StoveGui.BACKGROUND);
        int posX = this.leftPos;
        int posY = this.topPos;
        guiGraphics.blit(StoveGui.BACKGROUND, posX, posY, 0, 0, this.imageWidth - 1, this.imageHeight);
        this.renderProgressArrow(guiGraphics);
        this.renderBurnIcon(guiGraphics, posX, posY);
    }
}
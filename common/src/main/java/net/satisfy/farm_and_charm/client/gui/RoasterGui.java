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
import net.satisfy.farm_and_charm.client.gui.handler.RoasterGuiHandler;
import net.satisfy.farm_and_charm.util.FarmAndCharmIdentifier;

@Environment(EnvType.CLIENT)
public class RoasterGui extends AbstractContainerScreen<RoasterGuiHandler> {
    public static final ResourceLocation BACKGROUND;

    public static final int ARROW_X = 95;
    public static final int ARROW_Y = 14;

    static {
        BACKGROUND = FarmAndCharmIdentifier.of("textures/gui/roaster_gui.png");
    }

    public RoasterGui(RoasterGuiHandler handler, Inventory playerInventory, Component title) {
        super(handler, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX += 20;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, RoasterGui.BACKGROUND);
        int posX = this.leftPos;
        int posY = this.topPos;
        guiGraphics.blit(RoasterGui.BACKGROUND, posX, posY, 0, 0, this.imageWidth - 1, this.imageHeight);
        this.renderProgressArrow(guiGraphics);
        this.renderBurnIcon(guiGraphics, posX, posY);
    }

    public void renderProgressArrow(GuiGraphics guiGraphics) {
        int progress = this.menu.getScaledProgress(23);
        guiGraphics.blit(BACKGROUND, this.leftPos + 95, this.topPos + 14, 178, 15, progress, 30);
    }

    public void renderBurnIcon(GuiGraphics guiGraphics, int posX, int posY) {
        if (this.menu.isBeingBurned()) {
            guiGraphics.blit(BACKGROUND, posX + 124, posY + 56, 176, 0, 17, 15);
        }
    }
}

package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.satisfy.farm_and_charm.core.block.PetBowlBlock;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;

public class PetBowlBlockRenderer implements BlockEntityRenderer<PetBowlBlockEntity> {

    @Override
    public void render(PetBowlBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.165, 0.5);
        Direction facing = entity.getBlockState().getValue(PetBowlBlock.FACING);
        float rotation = switch (facing) {
            case SOUTH -> 180f;
            case WEST -> -90f;
            case EAST -> 90f;
            default -> 0f;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.translate(0.0, 0.0, 0.2505);
        poseStack.scale(0.01f, -0.01f, 0.01f);

        Font font = Minecraft.getInstance().font;
        Component text = entity.getText();
        String str = text.getString();
        if (str.length() > 6) str = str.substring(0, 6);
        if (!str.isEmpty()) {
            font.drawInBatch(Component.literal(str).getVisualOrderText(), -font.width(str) / 2f, 0, 0xD8C4A0, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
        }

        poseStack.popPose();
    }
}
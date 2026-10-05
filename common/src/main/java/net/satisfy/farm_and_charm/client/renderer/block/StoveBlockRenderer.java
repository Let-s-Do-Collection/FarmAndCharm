package net.satisfy.farm_and_charm.client.renderer.block;

import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.satisfy.foundation.render.ClientUtil;
import net.satisfy.farm_and_charm.core.block.StoveBlock;
import net.satisfy.farm_and_charm.core.block.entity.StoveBlockEntity;

import java.util.Arrays;

@SuppressWarnings("unused")
public class StoveBlockRenderer implements BlockEntityRenderer<StoveBlockEntity> {
    private static final float FLIP_TICKS = 10.0F;

    public StoveBlockRenderer(Context context) {
    }

    @Override
    public void render(StoveBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction direction = blockEntity.getBlockState().getValue(StoveBlock.FACING);
        Vec3 baseOffset = new Vec3(0.5, 1.0, 0.5);
        Vec3 directionOffset = Vec3.atLowerCornerOf(direction.getNormal()).scale(0.3);
        double downOffset = -0.575;
        Vec3 inputSlotOffset = new Vec3(
                direction == Direction.NORTH || direction == Direction.WEST ? 0.15 : -0.15,
                downOffset,
                direction == Direction.NORTH || direction == Direction.EAST ? 0.15 : -0.15
        );

        renderSlots(blockEntity, poseStack, baseOffset, directionOffset, inputSlotOffset, bufferSource);

        renderOutput(blockEntity, poseStack, baseOffset, directionOffset, bufferSource);

        renderGrill(blockEntity, partialTicks, poseStack, bufferSource);
    }

    private void renderGrill(StoveBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource) {
        if (blockEntity.getLevel() == null || !blockEntity.isGrillFree()) {
            return;
        }
        float time = blockEntity.getLevel().getGameTime() + partialTicks;
        for (int slot = 0; slot < StoveBlockEntity.GRILL_SLOTS; slot++) {
            ItemStack stack = blockEntity.getGrillItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            Vec3 center = StoveBlockEntity.grillSlotCenter(slot);
            float flip = Mth.clamp((time - blockEntity.getGrillFlipStart(slot)) / FLIP_TICKS, 0.0F, 1.0F);
            float lift = Mth.sin(flip * Mth.PI) * 0.3F;
            poseStack.pushPose();
            poseStack.translate(center.x, center.y + 0.02 + lift, center.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(slot * 67.0F + 20.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F + flip * 360.0F));
            poseStack.scale(0.35F, 0.35F, 0.35F);
            ClientUtil.renderGuiItemForEntity(stack, poseStack, bufferSource, blockEntity.getLevel(), blockEntity.getBlockPos().above());
            poseStack.popPose();
        }
    }

    private void renderSlots(StoveBlockEntity blockEntity, PoseStack poseStack, Vec3 baseOffset, Vec3 directionOffset, Vec3 slotOffset, MultiBufferSource bufferSource) {
        int[] slots = blockEntity.getIngredientSlots();
        int nonEmptyCount = (int) Arrays.stream(slots).filter(slot -> !blockEntity.getItem(slot).isEmpty()).count();
        double ySpacing = 1.0 / 50;

        double yOffset = 0.1;
        for (int slot : slots) {
            ItemStack stack = blockEntity.getItem(slot);
            if (!stack.isEmpty()) {
                poseStack.pushPose();
                Vec3 position = baseOffset.add(directionOffset).add(slotOffset).add(0, yOffset, 0);
                poseStack.translate(position.x, position.y, position.z);
                poseStack.mulPose(Axis.YP.rotationDegrees(45f * (nonEmptyCount - 1)));
                poseStack.mulPose(Axis.XP.rotationDegrees(90f));
                poseStack.scale(0.3f, 0.3f, 0.3f);
                ClientUtil.renderItem(stack, poseStack, bufferSource, blockEntity);
                poseStack.popPose();
                yOffset += ySpacing;
            }
        }
    }

    private void renderOutput(StoveBlockEntity blockEntity, PoseStack poseStack, Vec3 baseOffset, Vec3 directionOffset, MultiBufferSource bufferSource) {
        ItemStack outputStack = blockEntity.getItem(blockEntity.getOutputSlot());
        if (!outputStack.isEmpty()) {
            poseStack.pushPose();
            Vec3 position = baseOffset.add(directionOffset).add(0, -0.49, 0);
            poseStack.translate(position.x, position.y, position.z);
            poseStack.mulPose(Axis.XP.rotationDegrees(90f));
            poseStack.scale(0.3f, 0.3f, 0.3f);
            ClientUtil.renderItem(outputStack, poseStack, bufferSource, blockEntity);
            poseStack.popPose();
        }
    }

}
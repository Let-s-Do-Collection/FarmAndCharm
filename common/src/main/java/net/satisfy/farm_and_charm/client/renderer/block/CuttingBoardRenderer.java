package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.block.CuttingBoardBlock;
import net.satisfy.farm_and_charm.core.block.entity.CuttingBoardBlockEntity;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CuttingBoardRenderer implements BlockEntityRenderer<CuttingBoardBlockEntity> {
    private static final double BOARD_TOP = 0.07;
    private static final double STACK_STEP = 0.03;
    private static final float ITEM_SCALE = 0.4F;
    private static final float KNIFE_SCALE = 0.5F;
    private static final double KNIFE_REST_OFFSET = 0.12;
    private static final double CHOP_HEIGHT = 0.18;
    private static final float CHOP_TILT = 35.0F;
    private static final double TOOL_FORWARD = 2.0 / 16.0;
    private static final double STUCK_DEPTH = 0.03;
    private static final float STUCK_YAW = 25.0F;
    private static final float STUCK_TILT = -12.0F;
    private static final float BLADE_DOWN = -135.0F;
    private static final double UPRIGHT_HEIGHT = 0.16;
    private static final double ITEM_OFFSET = 2.0 / 16.0;

    public CuttingBoardRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CuttingBoardBlockEntity board, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        Direction facing = board.getBlockState().getValue(CuttingBoardBlock.FACING);
        float yaw = 180.0F - facing.toYRot();
        int seed = (int) board.getBlockPos().asLong();
        Direction right = facing.getCounterClockWise();
        double itemX = 0.5 + right.getStepX() * ITEM_OFFSET;
        double itemZ = 0.5 + right.getStepZ() * ITEM_OFFSET;

        List<ItemStack> items = board.getItems();
        for (int i = 0; i < items.size(); i++) {
            renderFlat(items.get(i), poseStack, buffers, light, overlay, board.getLevel(),
                    itemX, BOARD_TOP + i * STACK_STEP, itemZ, yaw + i * 25.0F, ITEM_SCALE, seed + i);
        }

        if (board.isCutting()) {
            renderFlat(board.getPending(), poseStack, buffers, light, overlay, board.getLevel(),
                    itemX, BOARD_TOP, itemZ, yaw, ITEM_SCALE, seed);
        }

        if (board.hasKnife()) {
            this.renderKnife(board, facing, yaw, partialTick, poseStack, buffers, light, overlay, seed);
        }
    }

    private static void renderFlat(ItemStack stack, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, @Nullable Level level, double x, double y, double z, float yaw, float scale, int seed) {
        if (stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(scale, scale, scale);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, poseStack, buffers, level, seed);
        poseStack.popPose();
    }

    private void renderKnife(CuttingBoardBlockEntity board, Direction facing, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay, int seed) {
        Direction side = facing.getOpposite();
        Direction right = facing.getCounterClockWise();
        double lift = 0.0;
        float tilt = 0.0F;
        double offset = 0.0;
        if (board.isCutting() && board.getLevel() != null) {
            offset = KNIFE_REST_OFFSET - TOOL_FORWARD;
            float sinceChop = board.getLevel().getGameTime() - board.getCutStart() + partialTick;
            if (sinceChop >= 0.0F && sinceChop < CuttingBoardBlockEntity.CHOP_ANIMATION_TICKS) {
                double chop = Math.sin(Math.PI * sinceChop / CuttingBoardBlockEntity.CHOP_ANIMATION_TICKS);
                lift = chop * CHOP_HEIGHT;
                tilt = (float) chop * CHOP_TILT;
            }
        }
        poseStack.pushPose();
        if (board.isCutting()) {
            poseStack.translate(0.5 + right.getStepX() * ITEM_OFFSET + side.getStepX() * offset, BOARD_TOP + UPRIGHT_HEIGHT + lift, 0.5 + right.getStepZ() * ITEM_OFFSET + side.getStepZ() * offset);
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
            poseStack.mulPose(Axis.ZP.rotationDegrees(BLADE_DOWN + tilt));
        } else {
            poseStack.translate(0.5 + right.getStepX() * ITEM_OFFSET, BOARD_TOP + UPRIGHT_HEIGHT - STUCK_DEPTH, 0.5 + right.getStepZ() * ITEM_OFFSET);
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw + STUCK_YAW));
            poseStack.mulPose(Axis.ZP.rotationDegrees(BLADE_DOWN + STUCK_TILT));
        }
        poseStack.scale(KNIFE_SCALE, KNIFE_SCALE, KNIFE_SCALE);
        Minecraft.getInstance().getItemRenderer().renderStatic(board.getKnife(), ItemDisplayContext.FIXED, light, overlay, poseStack, buffers, board.getLevel(), seed + 7);
        poseStack.popPose();
    }
}

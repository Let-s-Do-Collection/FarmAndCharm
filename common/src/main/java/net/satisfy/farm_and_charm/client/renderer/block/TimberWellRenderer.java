package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.model.WellPumpModel;
import net.satisfy.farm_and_charm.core.block.TimberWellBlock;
import net.satisfy.farm_and_charm.core.block.entity.TimberWellBlockEntity;

public class TimberWellRenderer implements BlockEntityRenderer<TimberWellBlockEntity> {
    private static final ResourceLocation TEXTURE = FarmAndCharm.identifier("textures/entity/well_pump.png");

    // Pivot of the lever in block model space (pixels, well facing east). Blockbench pivot [8, 7, 0] = block space [16, 7, 8].
    private static final float PIVOT_X = 16.0F / 16.0F;
    private static final float PIVOT_Y = 7.0F / 16.0F;
    private static final float PIVOT_Z = 8.0F / 16.0F;
    private static final float REST_ANGLE = 0.0F;
    private static final float PRESSED_ANGLE = 35.0F;
    private static final float PRESS_PORTION = 0.3F;

    private final ModelPart lever;

    public TimberWellRenderer(BlockEntityRendererProvider.Context context) {
        this.lever = context.bakeLayer(WellPumpModel.LAYER_LOCATION).getChild("bone");
    }

    @Override
    public void render(TimberWellBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof TimberWellBlock) || be.getLevel() == null) {
            return;
        }

        float angle = getLeverAngle(be.getLevel().getGameTime() - be.getPumpStartTick() + partialTick);
        float blockstateYRot = (state.getValue(TimberWellBlock.FACING).toYRot() + 90.0F) % 360.0F;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-blockstateYRot));
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        poseStack.translate(PIVOT_X, PIVOT_Y, PIVOT_Z);
        poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
        poseStack.translate(-PIVOT_X, -PIVOT_Y, -PIVOT_Z);
        // Blockbench entity space -> block model space
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        this.lever.render(poseStack, buffers.getBuffer(RenderType.entityCutout(TEXTURE)), light, overlay);
        poseStack.popPose();
    }

    /**
     * Pushes the lever quickly, then lets it spring back with a small overshoot.
     */
    private static float getLeverAngle(float ticksSincePump) {
        float progress = ticksSincePump / TimberWellBlockEntity.PUMP_DURATION_TICKS;
        if (progress < 0.0F || progress >= 1.0F) {
            return REST_ANGLE;
        }
        float pressed;
        if (progress < PRESS_PORTION) {
            float t = progress / PRESS_PORTION;
            pressed = t * t * (3.0F - 2.0F * t);
        } else {
            float t = (progress - PRESS_PORTION) / (1.0F - PRESS_PORTION);
            float c1 = 1.70158F;
            float c3 = c1 + 1.0F;
            float easeOutBack = 1.0F + c3 * (float) Math.pow(t - 1.0F, 3) + c1 * (float) Math.pow(t - 1.0F, 2);
            pressed = 1.0F - easeOutBack;
        }
        return Mth.lerp(pressed, REST_ANGLE, PRESSED_ANGLE);
    }
}

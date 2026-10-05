package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.model.WaterSprinklerModel;
import net.satisfy.farm_and_charm.core.block.entity.WaterSprinklerBlockEntity;

public class WaterSprinklerRenderer implements BlockEntityRenderer<WaterSprinklerBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "textures/entity/water_sprinkler.png");
    private static final float WOBBLE_ANGLE = 1.5F;
    private static final float JOLT_ANGLE = 2.5F;
    private static final float RATTLE_ANGLE = 2.0F;
    private static final float PIVOT_Y = 0.5F;
    private final ModelPart rotating;
    private final ModelPart basin;

    public WaterSprinklerRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(WaterSprinklerModel.LAYER_LOCATION);
        this.rotating = root.getChild("rotating");
        this.basin = root.getChild("basin");
    }

    @Override
    public void render(WaterSprinklerBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) return;
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(TEXTURE));
        boolean animated = PlatformHelper.animationsEnabled();
        float angle = animated ? blockEntity.getRotationAngle(partialTicks) : 0.0F;
        float speed = animated ? Math.min(1.0F, blockEntity.getSpeed()) : 0.0F;
        double time = level.getGameTime() + partialTicks;
        float tiltX = 0.0F;
        float tiltZ = 0.0F;
        switch (blockEntity.getPressure()) {
            case PULSING -> {
                float jolt = blockEntity.getJolt(partialTicks);
                tiltX = (float) Math.sin(time * 0.21) * WOBBLE_ANGLE * speed;
                tiltZ = (float) Math.cos(time * 0.17) * WOBBLE_ANGLE * speed + jolt * jolt * JOLT_ANGLE * speed;
            }
            case HIGH -> {
                tiltX = (float) Math.sin(time * 1.9) * RATTLE_ANGLE * speed;
                tiltZ = (float) Math.cos(time * 2.3) * RATTLE_ANGLE * speed;
            }
            case STEADY -> {
            }
        }

        matrixStack.pushPose();
        matrixStack.translate(0.5, PIVOT_Y, 0.5);
        matrixStack.mulPose(Axis.XP.rotationDegrees(tiltX));
        matrixStack.mulPose(Axis.ZP.rotationDegrees(tiltZ));
        matrixStack.mulPose(Axis.YP.rotationDegrees(-angle));
        matrixStack.translate(-0.5, -PIVOT_Y, -0.5);
        rotating.render(matrixStack, vertexConsumer, combinedLight, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
        basin.render(matrixStack, vertexConsumer, combinedLight, OverlayTexture.NO_OVERLAY);
    }
}

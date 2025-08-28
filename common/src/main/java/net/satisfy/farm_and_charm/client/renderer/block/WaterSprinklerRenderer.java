package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.model.WaterSprinklerModel;
import net.satisfy.farm_and_charm.core.block.entity.WaterSprinklerBlockEntity;
import org.joml.Quaternionf;

public class WaterSprinklerRenderer implements BlockEntityRenderer<WaterSprinklerBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "textures/entity/water_sprinkler.png");
    private final ModelPart rotating;
    private final ModelPart basin;
    private long lastRenderTime = 0;
    private float rotationAngle = 0.0F;

    public WaterSprinklerRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(WaterSprinklerModel.LAYER_LOCATION);
        this.rotating = root.getChild("rotating");
        this.basin = root.getChild("basin");
    }

    private float updateRotationAngle(WaterSprinklerBlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        assert level != null;
        boolean isRaining = level.isRaining();
        boolean isThundering = level.isThundering();
        float rotationSpeed = isRaining || isThundering ? 2.0F : 1.0F;

        long currentTime = System.currentTimeMillis();
        if (lastRenderTime == 0) {
            lastRenderTime = currentTime;
        }
        float deltaTime = (currentTime - lastRenderTime) / 1000.0F;
        lastRenderTime = currentTime;

        rotationAngle += rotationSpeed * deltaTime * 40.0F;
        rotationAngle %= 360;

        blockEntity.setRotationAngle(rotationAngle);
        return rotationAngle;
    }

    @Override
    public void render(WaterSprinklerBlockEntity blockEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(TEXTURE));
        matrixStack.pushPose();
        final var angle = updateRotationAngle(blockEntity);
        matrixStack.translate(0.5, 0, 0.5);
        matrixStack.mulPose(new Quaternionf().rotationY((float) Math.toRadians(-angle)));
        matrixStack.translate(-0.5, 0, -0.5);
        rotating.render(matrixStack, vertexConsumer, combinedLight, OverlayTexture.NO_OVERLAY);
        matrixStack.popPose();
        basin.render(matrixStack, vertexConsumer, combinedLight, OverlayTexture.NO_OVERLAY);
    }
}

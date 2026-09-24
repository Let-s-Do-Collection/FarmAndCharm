package net.satisfy.farm_and_charm.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.model.SeederCartModel;
import net.satisfy.farm_and_charm.core.entity.SeederCartEntity;
import org.jetbrains.annotations.NotNull;

public class SeederCartRenderer extends EntityRenderer<SeederCartEntity> {
    public static final ResourceLocation CART_TEXTURE = FarmAndCharm.identifier("textures/entity/supply_cart.png");
    private final SeederCartModel<SeederCartEntity> model;

    public SeederCartRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SeederCartModel<>(context.bakeLayer(SeederCartModel.LAYER_LOCATION));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(SeederCartEntity entity) {
        return CART_TEXTURE;
    }

    @Override
    public void render(SeederCartEntity cart, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light) {
        super.render(cart, yaw, partialTick, poseStack, buffer, light);

        poseStack.pushPose();

        poseStack.translate(0.0D, 1.4D, 0.0D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));

        boolean isPulled = cart.getPulling() != null;
        float basePitch = isPulled ? 3.0F : 0.0F;
        poseStack.mulPose(Axis.XP.rotationDegrees(basePitch));

        double deltaX = cart.getX() - cart.xOld;
        double deltaZ = cart.getZ() - cart.zOld;
        boolean isMoving = isPulled && (deltaX * deltaX + deltaZ * deltaZ) > 1.0E-4D;

        if (isMoving) {
            float speed = Mth.sqrt((float) (deltaX * deltaX + deltaZ * deltaZ));
            float wobbleStrength = Mth.clamp(speed * 18.0F, 0.0F, 1.0F);

            float transformBoost = Mth.clamp(cart.getSowEffectTicks() / 6.0F, 0.0F, 1.0F);
            wobbleStrength = Mth.clamp(wobbleStrength + transformBoost * 0.9F, 0.0F, 1.0F);

            float time = cart.tickCount + partialTick;

            float roll = Mth.sin(time * 0.9F) * (1.2F + transformBoost * 1.8F) * wobbleStrength;
            float pitch = Mth.sin(time * 1.6F + 1.4F) * (0.9F + transformBoost * 2.2F) * wobbleStrength;

            poseStack.mulPose(Axis.ZP.rotationDegrees(roll));
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        }

        this.model.setupAnim(cart, cart.tickCount + partialTick, 0.0F, cart.tickCount + partialTick, yaw, cart.getXRot());
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(CART_TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}

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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.model.ScarecrowModel;
import net.satisfy.farm_and_charm.core.block.ScarecrowBlock;
import net.satisfy.farm_and_charm.core.block.entity.ScarecrowBlockEntity;

public class ScarecrowRenderer implements BlockEntityRenderer<ScarecrowBlockEntity> {

    private static final ResourceLocation TEX_WITH = FarmAndCharm.identifier("textures/entity/scarecrow.png");
    private static final ResourceLocation TEX_NO  = FarmAndCharm.identifier("textures/entity/scarecrow_no_dungarees.png");
    private static final float POST_TOP = 12.0F / 16.0F;
    private static final float SWAY_SIDE = 2.5F;
    private static final float SWAY_FORWARD = 1.2F;
    private static final float ARM_FLAP = 0.12F;
    private static final float HEAD_TILT = 0.08F;
    private static final float CALM_SWAY = 1.5F;
    private static final float ARM_RAISE = 0.7F;
    private final ModelPart scarecrow;
    private final ModelPart post;
    private final ModelPart head;
    private final ModelPart armLeft;
    private final ModelPart armRight;

    public ScarecrowRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(ScarecrowModel.LAYER_LOCATION);
        this.scarecrow = root.getChild("scarecrow");
        this.post = root.getChild("post");
        this.head = this.scarecrow.getChild("head_r1");
        this.armLeft = this.scarecrow.getChild("arm_left");
        this.armRight = this.scarecrow.getChild("arm_right");
    }

    @Override
    public void render(ScarecrowBlockEntity be, float pt, PoseStack ms, MultiBufferSource buf, int light, int overlay) {
        Level level = be.getLevel();
        if (level == null) return;
        Direction dir = be.getBlockState().getValue(ScarecrowBlock.FACING);
        boolean has = be.getBlockState().getValue(ScarecrowBlock.HAS_DUNGAREES);
        ResourceLocation tex = has ? TEX_WITH : TEX_NO;
        VertexConsumer vc = buf.getBuffer(RenderType.entityCutoutNoCull(tex));

        double time = level.getGameTime() + pt + (Mth.murmurHash3Mixer((int) be.getBlockPos().asLong()) & 1023);
        float side = 0.0F;
        float forward = 0.0F;

        this.head.resetPose();
        this.armLeft.resetPose();
        this.armRight.resetPose();

        switch (be.getBlockState().getValue(ScarecrowBlock.MODE)) {
            case CALM -> forward = (float) Math.sin(time * 0.05) * CALM_SWAY;
            case WINDY -> {
                float wind = 1.0F + level.getRainLevel(pt) * 0.8F + level.getThunderLevel(pt) * 0.8F;
                float gust = (float) Math.pow(Math.max(0.0F, (float) Math.sin(time * 0.013)), 6.0F);
                float strength = wind * (1.0F + gust * 1.5F);
                side = ((float) Math.sin(time * 0.045) + (float) Math.sin(time * 0.13) * 0.25F) * SWAY_SIDE * strength;
                forward = (float) Math.sin(time * 0.031 + 1.0) * SWAY_FORWARD * strength;
                this.head.zRot += (float) Math.sin(time * 0.045 - 0.8) * HEAD_TILT * strength;
                this.head.xRot += (float) Math.sin(time * 0.037) * HEAD_TILT * 0.5F * strength;
                this.armRight.zRot += (float) Math.sin(time * 0.09) * ARM_FLAP * strength;
                this.armLeft.zRot -= (float) Math.sin(time * 0.09 + 0.7) * ARM_FLAP * strength;
            }
            case WATCHFUL -> {
                forward = (float) Math.sin(time * 0.05) * CALM_SWAY * 0.5F;
                this.head.yRot -= be.getHeadYaw(pt) * Mth.DEG_TO_RAD;
                float raise = be.getArmRaise(pt);
                float twitch = raise * (float) Math.sin(time * 1.3) * 0.15F;
                this.armRight.zRot += raise * ARM_RAISE + twitch;
                this.armLeft.zRot -= raise * ARM_RAISE + twitch;
            }
        }

        ms.pushPose();
        ms.translate(0.5, 0, 0.5);
        ms.mulPose(Axis.YP.rotationDegrees(-dir.toYRot() + 180));
        ms.translate(-0.5, 0, -0.5);

        this.post.render(ms, vc, light, OverlayTexture.NO_OVERLAY);

        ms.translate(0.5, POST_TOP, 0.5);
        ms.mulPose(Axis.ZP.rotationDegrees(side));
        ms.mulPose(Axis.XP.rotationDegrees(forward));
        ms.translate(-0.5, -POST_TOP, -0.5);

        this.scarecrow.render(ms, vc, light, OverlayTexture.NO_OVERLAY);
        ms.popPose();
    }

    @SuppressWarnings("unused")
    public AABB getRenderBoundingBox(BlockEntity be) {
        BlockPos pos = be.getBlockPos();
        return new AABB(pos.getX() - 0.25, pos.getY(), pos.getZ() - 0.25, pos.getX() + 1.25, pos.getY() + 2.0, pos.getZ() + 1.25);
    }
}

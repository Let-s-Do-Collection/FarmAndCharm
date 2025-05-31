package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.model.ScarecrowModel;
import net.satisfy.farm_and_charm.core.block.ScarecrowBlock;
import net.satisfy.farm_and_charm.core.block.entity.ScarecrowBlockEntity;
import org.joml.Quaternionf;

import java.util.Objects;

public class ScarecrowRenderer implements BlockEntityRenderer<ScarecrowBlockEntity> {
    private static final ResourceLocation TEX_WITH = new ResourceLocation(FarmAndCharm.MOD_ID, "textures/entity/scarecrow.png");
    private static final ResourceLocation TEX_NO  = new ResourceLocation(FarmAndCharm.MOD_ID, "textures/entity/scarecrow_no_dungarees.png");

    private final ModelPart scarecrow;
    private final ModelPart post;

    public ScarecrowRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart root = ctx.bakeLayer(ScarecrowModel.LAYER_LOCATION);
        this.scarecrow = root.getChild("scarecrow");
        this.post      = root.getChild("post");
    }

    @Override
    public void render(ScarecrowBlockEntity be, float pt, PoseStack ms, MultiBufferSource buf, int light, int overlay) {
        Direction dir = be.getBlockState().getValue(ScarecrowBlock.FACING);
        boolean has  = be.getBlockState().getValue(ScarecrowBlock.HAS_DUNGAREES);
        ResourceLocation tex = has ? TEX_WITH : TEX_NO;
        VertexConsumer vc = buf.getBuffer(RenderType.entityCutoutNoCull(tex));

        float rotY = -dir.toYRot() + 180;
        long t = Objects.requireNonNull(be.getLevel()).getGameTime();
        float angle = (float) Math.sin(t * 0.05) * 1.5f;

        ms.pushPose();
        ms.translate(0.5, 0, 0.5);
        ms.mulPose(new Quaternionf().rotateY((float)Math.toRadians(rotY)));
        ms.mulPose(new Quaternionf().rotateX((float)Math.toRadians(angle)));
        ms.translate(-0.5, 0, -0.5);

        scarecrow.render(ms, vc, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        post.render(ms, vc, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        ms.popPose();
    }
}

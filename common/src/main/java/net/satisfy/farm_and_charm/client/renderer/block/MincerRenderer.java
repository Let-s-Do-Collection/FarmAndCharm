package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.block.MincerBlock;
import net.satisfy.farm_and_charm.core.block.entity.MincerBlockEntity;
import net.satisfy.farm_and_charm.client.model.MincerModel;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MincerRenderer implements BlockEntityRenderer<MincerBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "textures/entity/mincer.png");
    private final ModelPart mincer;
    private final ModelPart crank;

    public MincerRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(MincerModel.LAYER_LOCATION);

        this.mincer = root.getChild("mincer");
        this.crank = root.getChild("crank");
    }

    @Override
    public void render(MincerBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {

        Level level = blockEntity.getLevel();
        assert level != null;
        BlockState blockState = level.getBlockState(blockEntity.getBlockPos());
        if (!(blockState.getBlock() instanceof MincerBlock)) return;

        poseStack.pushPose();

        Direction facing = blockState.getValue(MincerBlock.FACING);
        Vector3f offset = new Vector3f();
        float rotationDegrees = 0;

        switch (facing) {
            case NORTH:
                offset.set(1f, 0f, 1f);
                rotationDegrees = 180;
                break;
            case EAST:
                offset.set(0f, 0f, 1f);
                rotationDegrees = 90;
                break;
            case SOUTH:
                offset.set(0.0f, 0f, 0f);
                break;
            case WEST:
                offset.set(1f, 0f, 0f);
                rotationDegrees = 270;
                break;
        }

        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotationDegrees));

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        mincer.render(poseStack, vertexConsumer, light, overlay);

        if (blockState.getValue(MincerBlock.CRANK) > 0) {
            long time = System.currentTimeMillis();
            float angle = (time % 3600) / 5f;
            Quaternionf rotation = new Quaternionf().rotateX((float) Math.toRadians(-angle));
            Matrix4f matrix = new Matrix4f().translate(0.5f, 0.625f, 0.5f).mul(rotation.get(new Matrix4f())).translate(-0.5f, -0.625f, -0.5f);
            poseStack.last().pose().mul(matrix);
        }
        crank.render(poseStack, vertexConsumer, light, overlay);

        poseStack.popPose();
    }
}

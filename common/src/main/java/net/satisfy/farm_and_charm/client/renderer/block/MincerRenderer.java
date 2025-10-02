package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import net.satisfy.farm_and_charm.client.model.MincerModel;
import net.satisfy.farm_and_charm.core.block.MincerBlock;
import net.satisfy.farm_and_charm.core.block.entity.MincerBlockEntity;
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
        if (level == null) return;
        BlockState blockState = level.getBlockState(blockEntity.getBlockPos());
        if (!(blockState.getBlock() instanceof MincerBlock)) return;

        poseStack.pushPose();

        Direction facing = blockState.getValue(MincerBlock.FACING);
        Vector3f offset = new Vector3f();
        float rotationDegrees = 0F;

        switch (facing) {
            case NORTH -> { offset.set(1F, 0F, 1F); rotationDegrees = 180F; }
            case EAST  -> { offset.set(0F, 0F, 1F); rotationDegrees = 90F; }
            case SOUTH -> offset.set(0F, 0F, 0F);
            case WEST  -> { offset.set(1F, 0F, 0F); rotationDegrees = 270F; }
        }

        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationDegrees));

        VertexConsumer vc = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        mincer.render(poseStack, vc, light, overlay);

        poseStack.translate(0.5F, 0.625F, 0.5F);
        poseStack.mulPose(Axis.XP.rotation(blockEntity.getInterpolatedCrankAngle(partialTicks)));
        poseStack.translate(-0.5F, -0.625F, -0.5F);

        crank.render(poseStack, vc, light, overlay);

        poseStack.popPose();
    }
}

package net.satisfy.farm_and_charm.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.satisfy.farm_and_charm.FarmAndCharm;

public class ScarecrowModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "scarecrow"), "main");
    private final ModelPart scarecrow;
    private final ModelPart post;

    public ScarecrowModel(ModelPart root) {
        this.scarecrow = root.getChild("scarecrow");
        this.post = root.getChild("post");

    }

    @SuppressWarnings("unused")
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition scarecrow = partdefinition.addOrReplaceChild("scarecrow", CubeListBuilder.create(), PartPose.offset(6.9592F, 28.0F, 9.0F));

        PartDefinition head_r1 = scarecrow.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(26, 0).addBox(-4.0F, -4.0F, -3.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 0.0F, -1.0F, 0.0873F, -0.1745F, 0.0F));

        PartDefinition arm_right = scarecrow.addOrReplaceChild("arm_right", CubeListBuilder.create(), PartPose.offset(0.0817F, -2.8478F, 1.0154F));

        PartDefinition arm_left_straw_right_r1 = arm_right.addOrReplaceChild("arm_left_straw_right_r1", CubeListBuilder.create().texOffs(26, 22).mirror().addBox(3.8692F, -12.1478F, -3.0154F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.025F)).mirror(false), PartPose.offsetAndRotation(1.0F, -2.3045F, -1.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition arm_left_straw_back_r1 = arm_right.addOrReplaceChild("arm_left_straw_back_r1", CubeListBuilder.create().texOffs(26, 27).mirror().addBox(2.9965F, -9.3478F, 2.1346F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.025F)).mirror(false), PartPose.offsetAndRotation(1.0F, -2.3045F, -1.0F, 0.7854F, 0.0F, 0.2618F));

        PartDefinition arm_left_straw_front_r1 = arm_right.addOrReplaceChild("arm_left_straw_front_r1", CubeListBuilder.create().texOffs(26, 27).mirror().addBox(2.9965F, -8.7478F, -2.8654F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.025F)).mirror(false), PartPose.offsetAndRotation(1.0F, -2.3045F, -1.0F, -0.7854F, 0.0F, 0.2618F));

        PartDefinition arm_left_r1 = arm_right.addOrReplaceChild("arm_left_r1", CubeListBuilder.create().texOffs(26, 16).mirror().addBox(2.9965F, -6.0528F, -3.0154F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.025F)).mirror(false), PartPose.offsetAndRotation(1.0F, -2.3045F, -1.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition arm_left = scarecrow.addOrReplaceChild("arm_left", CubeListBuilder.create(), PartPose.offset(0.0F, -2.8478F, 1.0154F));

        PartDefinition arm_left_straw_right_r2 = arm_left.addOrReplaceChild("arm_left_straw_right_r2", CubeListBuilder.create().texOffs(26, 22).addBox(-3.8692F, -12.1478F, -3.0154F, 0.0F, 3.0F, 5.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(1.0F, -2.3045F, -1.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition arm_left_straw_back_r2 = arm_left.addOrReplaceChild("arm_left_straw_back_r2", CubeListBuilder.create().texOffs(26, 27).addBox(-7.9965F, -9.3478F, 2.1346F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(1.0F, -2.3045F, -1.0F, 0.7854F, 0.0F, -0.2618F));

        PartDefinition arm_left_straw_front_r2 = arm_left.addOrReplaceChild("arm_left_straw_front_r2", CubeListBuilder.create().texOffs(26, 27).addBox(-7.9965F, -8.7478F, -2.8654F, 5.0F, 3.0F, 0.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(1.0F, -2.3045F, -1.0F, -0.7854F, 0.0F, -0.2618F));

        PartDefinition arm_left_r2 = arm_left.addOrReplaceChild("arm_left_r2", CubeListBuilder.create().texOffs(26, 16).addBox(-7.9965F, -6.0528F, -3.0154F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(1.0F, -2.3045F, -1.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition body = scarecrow.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -17.0F, -3.0F, 8.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition straw_right_r1 = body.addOrReplaceChild("straw_right_r1", CubeListBuilder.create().texOffs(8, 17).addBox(-2.2592F, -13.5F, -2.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -8.0F, -1.0F, 0.0F, 0.0F, 0.6545F));

        PartDefinition straw_left_r1 = body.addOrReplaceChild("straw_left_r1", CubeListBuilder.create().texOffs(8, 17).addBox(2.2908F, -13.5F, -2.0F, 0.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -8.0F, -1.0F, 0.0F, 0.0F, -0.6545F));

        PartDefinition straw_back_r1 = body.addOrReplaceChild("straw_back_r1", CubeListBuilder.create().texOffs(8, 18).addBox(-3.9592F, -12.9F, -3.1F, 8.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -8.0F, -1.0F, -0.6545F, 0.0F, 0.0F));

        PartDefinition straw_front_r1 = body.addOrReplaceChild("straw_front_r1", CubeListBuilder.create().texOffs(8, 18).addBox(-3.9592F, -12.3F, 3.9F, 8.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -8.0F, -1.0F, 0.6545F, 0.0F, 0.0F));

        PartDefinition post = partdefinition.addOrReplaceChild("post", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(0.0F, -38.0F, -3.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(6.9592F, 38.0F, 10.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
        scarecrow.render(poseStack, vertexConsumer, i, j);
        post.render(poseStack, vertexConsumer, i, j);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}
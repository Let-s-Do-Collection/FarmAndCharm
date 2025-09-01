package net.satisfy.farm_and_charm.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.entity.PlowCartEntity;

public class PlowCartModel<T extends PlowCartEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(FarmAndCharm.identifier("plow"), "main");

    private final ModelPart cart;
    private final ModelPart right_wheel;
    private final ModelPart left_wheel;

    public PlowCartModel(ModelPart root) {
        this.cart = root.getChild("cart");
        this.right_wheel = root.getChild("right_wheel");
        this.left_wheel = root.getChild("left_wheel");
    }

    @SuppressWarnings("unused")
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition cart = partdefinition.addOrReplaceChild("cart", CubeListBuilder.create()
                        .texOffs(153, 44).addBox(-11.0F, 0.5F, 4.9167F, 22.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(159, 8).addBox(-2.0F, 0.5F, -27.0833F, 4.0F, 4.0F, 32.0F, new CubeDeformation(0.0F))
                        .texOffs(88, 0).addBox(5.0F, 2.5F, 10.9167F, 3.0F, 3.0F, 18.0F, new CubeDeformation(0.0F))
                        .texOffs(88, 0).addBox(-8.0F, 2.5F, 10.9167F, 3.0F, 3.0F, 18.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 12.5F, 10.0833F));

        PartDefinition plow_r1 = cart.addOrReplaceChild("plow_r1", CubeListBuilder.create()
                        .texOffs(173, 61).addBox(-12.0F, -4.0F, 4.0F, 16.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.4142F, 8.0F, -28.9706F, 0.0F, -0.7854F, 0.0F));

        PartDefinition plow_r2 = cart.addOrReplaceChild("plow_r2", CubeListBuilder.create()
                        .texOffs(173, 61).addBox(-4.0F, -4.0F, 4.0F, 16.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.4142F, 8.0F, -28.9706F, 0.0F, 0.7854F, 0.0F));

        PartDefinition plow_r3 = cart.addOrReplaceChild("plow_r3", CubeListBuilder.create()
                        .texOffs(173, 61).addBox(-12.0F, -4.0F, 4.0F, 16.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.4142F, 8.0F, -12.9706F, 0.0F, -0.7854F, 0.0F));

        PartDefinition plow_r4 = cart.addOrReplaceChild("plow_r4", CubeListBuilder.create()
                        .texOffs(173, 61).addBox(-4.0F, -4.0F, 4.0F, 16.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.4142F, 8.0F, -12.9706F, 0.0F, 0.7854F, 0.0F));

        PartDefinition right_wheel = partdefinition.addOrReplaceChild("right_wheel", CubeListBuilder.create()
                        .texOffs(76, 43).addBox(-1.5F, -8.0F, -8.0F, 3.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)),
                PartPose.offset(12.5F, 16.0F, 18.0F));

        PartDefinition left_wheel = partdefinition.addOrReplaceChild("left_wheel", CubeListBuilder.create()
                        .texOffs(76, 43).addBox(-1.5F, -8.0F, -8.0F, 3.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-12.5F, 16.0F, 18.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.right_wheel.xRot = -entity.getWheelRotation();
        this.left_wheel.xRot = -entity.getWheelRotation();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.cart.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        this.right_wheel.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        this.left_wheel.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
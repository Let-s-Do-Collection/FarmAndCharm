package net.satisfy.farm_and_charm.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.satisfy.farm_and_charm.core.entity.CartEntity;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

@SuppressWarnings("unused")
public class CartModel<T extends CartEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(FarmAndCharmIdentifier.of("cart"), "main");
    private final ModelPart cart;
    private final ModelPart right_wheel;
    private final ModelPart left_wheel;

    public CartModel(ModelPart root) {
        this.cart = root.getChild("cart");
        this.right_wheel = root.getChild("right_wheel");
        this.left_wheel = root.getChild("left_wheel");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition cart = partdefinition.addOrReplaceChild("cart", CubeListBuilder.create().texOffs(88, 0).addBox(5.0F, 1.5F, 9.9167F, 3.0F, 3.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(88, 0).addBox(-8.0F, 1.5F, 9.9167F, 3.0F, 3.0F, 18.0F, new CubeDeformation(0.0F))
                .texOffs(0, 43).addBox(-12.0F, -7.5F, -30.0833F, 3.0F, 9.0F, 32.0F, new CubeDeformation(0.0F))
                .texOffs(0, 43).mirror().addBox(9.0F, -7.5F, -30.0833F, 3.0F, 9.0F, 32.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(88, 21).addBox(-9.0F, -7.5F, -1.0833F, 18.0F, 9.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-12.0F, 1.5F, -30.0833F, 24.0F, 3.0F, 40.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.5F, 10.0833F));

        PartDefinition chest_lid = cart.addOrReplaceChild("chest_lid", CubeListBuilder.create().texOffs(0, 111).addBox(-7.0F, 1.0F, -7.0F, 14.0F, 5.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(0, 111).addBox(-1.0F, 3.0F, -8.0F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.5F, -10.0833F));

        PartDefinition chest = cart.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 130).addBox(-7.0F, -16.0F, -7.0F, 14.0F, 10.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.5F, -10.0833F));

        PartDefinition right_wheel = partdefinition.addOrReplaceChild("right_wheel", CubeListBuilder.create().texOffs(76, 43).addBox(-1.5F, -7.0F, -8.0F, 3.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(13.5F, 15.0F, -5.0F));

        PartDefinition left_wheel = partdefinition.addOrReplaceChild("left_wheel", CubeListBuilder.create().texOffs(76, 43).mirror().addBox(-1.5F, -7.0F, -8.0F, 3.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-13.5F, 15.0F, -5.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.cart.xRot = entity.balance();

        this.right_wheel.xRot = entity.wheelRot();
        this.left_wheel.xRot = entity.wheelRot();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
        this.cart.render(poseStack, vertexConsumer, i, j, k);
        this.right_wheel.render(poseStack, vertexConsumer, i, j, k);
        this.left_wheel.render(poseStack, vertexConsumer, i, j, k);
    }
}
package net.satisfy.farm_and_charm.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.entity.SeederCartEntity;

public class SeederCartModel<T extends SeederCartEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(FarmAndCharm.identifier("seeder"), "main");

    private final ModelPart cart;
    private final ModelPart right_wheel;
    private final ModelPart left_wheel;

    public SeederCartModel(ModelPart root) {
        this.cart = root.getChild("cart");
        this.right_wheel = root.getChild("right_wheel");
        this.left_wheel = root.getChild("left_wheel");
    }

    @SuppressWarnings("unused")
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("cart", CubeListBuilder.create()
                        .texOffs(153, 44).addBox(-11.0F, 0.5F, 4.9167F, 22.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                        .texOffs(175, 24).addBox(3.0F, 1.5F, -11.0833F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
                        .texOffs(175, 24).addBox(-7.0F, 1.5F, -11.0833F, 4.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
                        .texOffs(88, 0).addBox(5.0F, 2.5F, 10.9167F, 3.0F, 3.0F, 18.0F, new CubeDeformation(0.0F))
                        .texOffs(88, 0).addBox(-8.0F, 2.5F, 10.9167F, 3.0F, 3.0F, 18.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 154).addBox(-9.0F, -12.0F, -7.0F, 18.0F, 14.0F, 10.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 178).addBox(-9.0F, 2.0F, -5.0F, 18.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 12.5F, 10.0833F));

        partdefinition.addOrReplaceChild("right_wheel", CubeListBuilder.create()
                        .texOffs(76, 43).addBox(-1.5F, -8.0F, -8.0F, 3.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)),
                PartPose.offset(12.5F, 16.0F, 18.0F));

        partdefinition.addOrReplaceChild("left_wheel", CubeListBuilder.create()
                        .texOffs(76, 43).addBox(-1.5F, -8.0F, -8.0F, 3.0F, 16.0F, 16.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-12.5F, 16.0F, 18.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float partialTick = ageInTicks - entity.tickCount;
        if (partialTick < 0.0F) {
            partialTick = 0.0F;
        } else if (partialTick > 1.0F) {
            partialTick = 1.0F;
        }

        float wheelRotation = entity.getWheelRotation(partialTick);
        this.right_wheel.xRot = wheelRotation;
        this.left_wheel.xRot = wheelRotation;

        float wobbleYaw = 0.0F;

        int hurtTime = entity.getHurtTime();
        if (hurtTime > 0) {
            float damage = Mth.clamp(entity.getDamage() / 40.0F, 0.0F, 1.0F);
            int hurtDir = entity.getHurtDir();
            float time = hurtTime - partialTick;
            wobbleYaw = Mth.sin(time * 2.2F) * time * 0.004F * hurtDir * damage;
        }

        this.cart.yRot = wobbleYaw;
        this.right_wheel.yRot = wobbleYaw;
        this.left_wheel.yRot = wobbleYaw;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.cart.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        this.right_wheel.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        this.left_wheel.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}

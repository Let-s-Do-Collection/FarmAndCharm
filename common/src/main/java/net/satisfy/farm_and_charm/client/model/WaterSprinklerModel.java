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
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

public class WaterSprinklerModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(FarmAndCharmIdentifier.of("water_sprinkler"), "main");
    private final ModelPart rotating;
    private final ModelPart basin;

    public WaterSprinklerModel(ModelPart root) {
        this.rotating = root.getChild("rotating");
        this.basin = root.getChild("basin");
    }

    @SuppressWarnings("unused")
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition rotating = partdefinition.addOrReplaceChild("rotating", CubeListBuilder.create().texOffs(0, 28).addBox(7.0F, -14.0F, 7.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 26).addBox(7.0F, -2.0F, 1.0F, 2.0F, 2.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(28, 15).addBox(1.0F, -2.0F, 7.0F, 14.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 9).addBox(6.0F, -3.0F, 15.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(42, 9).addBox(6.0F, -3.0F, 0.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(42, 0).addBox(0.0F, -3.0F, 6.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(15.0F, -3.0F, 6.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition basin = partdefinition.addOrReplaceChild("basin", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, -16.0F, 1.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(1, 45).addBox(2.0F, -9.0F, 2.0F, 12.0F, 0.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 16).mirror().addBox(2.0F, -15.0F, 1.0F, 12.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 16).mirror().addBox(2.0F, -15.0F, 14.0F, 12.0F, 11.0F, 1.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(12, 15).addBox(14.0F, -15.0F, 1.0F, 1.0F, 11.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(12, 15).addBox(1.0F, -15.0F, 1.0F, 1.0F, 11.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
        rotating.render(poseStack, vertexConsumer, i, j, k);
        basin.render(poseStack, vertexConsumer, i, j, k);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

}
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

public class MincerModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "mincer"), "main");
    private final ModelPart mincer;
    private final ModelPart crank;

    public MincerModel(ModelPart root) {
        this.mincer = root.getChild("mincer");
        this.crank = root.getChild("crank");
    }

    @SuppressWarnings("unused")
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition mincer = partdefinition.addOrReplaceChild("mincer", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -24.0F, 11.0F, 12.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 26).addBox(-1.0F, -23.0F, 13.0F, 5.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(30, 11).addBox(-1.0F, -11.0F, 13.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(24, 20).addBox(-6.0F, -17.0F, 13.0F, 4.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 11).addBox(-2.0F, -18.0F, 12.0F, 7.0F, 7.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(38, 31).addBox(-7.0F, -13.0F, 13.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(38, 38).addBox(-7.0F, -17.0F, 13.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(54, 22).addBox(-7.0F, -16.0F, 13.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 22).addBox(-7.0F, -16.0F, 18.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));

        PartDefinition crank = partdefinition.addOrReplaceChild("crank", CubeListBuilder.create().texOffs(0, 4).addBox(-1.3333F, -1.0F, -1.1667F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(22, 31).addBox(-0.3333F, -1.0F, -1.1667F, 1.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.3333F, -1.0F, 5.8333F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(14.3333F, 10.0F, 8.1667F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
        mincer.render(poseStack, vertexConsumer, i, j, k);
        crank.render(poseStack, vertexConsumer, i, j, k);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

}
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

public class CraftingBowlModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "crafting_bowl"), "main");
    private final ModelPart bowl;
    private final ModelPart swing;

    public CraftingBowlModel(ModelPart root) {
        this.bowl = root.getChild("bowl");
        this.swing = root.getChild("swing");
    }

    @SuppressWarnings("unused")
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition bowl = partdefinition.addOrReplaceChild("bowl", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0F, -8.05F, 3.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)).texOffs(0, 0).addBox(-4.0F, -3.0F, 12.0F, -8.0F, -5.0F, -8.0F, new CubeDeformation(0.0F)).texOffs(30, 0).addBox(-13.0F, -7.0F, 3.0F, 10.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));
        PartDefinition swing = partdefinition.addOrReplaceChild("swing", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, 0.0F, 0.0F, 0.0873F));
        PartDefinition swing_r1 = swing.addOrReplaceChild("swing_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.2556F, 0.5F, 1.125F, 5.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).texOffs(0, 13).addBox(1.7174F, 0.5F, -1.375F, 0.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)).texOffs(10, 18).addBox(0.7174F, -5.5F, 0.625F, 1.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5174F, -2.8143F, -1.125F, 0.0F, 0.0F, -0.7854F));
        PartDefinition item1 = swing.addOrReplaceChild("item1", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 2.0F));
        PartDefinition item2 = swing.addOrReplaceChild("item2", CubeListBuilder.create(), PartPose.offset(-2.0F, 0.67F, 0.0F));
        PartDefinition item3 = swing.addOrReplaceChild("item3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.34F, -2.5F));
        PartDefinition item4 = swing.addOrReplaceChild("item4", CubeListBuilder.create(), PartPose.offset(2.5F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
        bowl.render(poseStack, vertexConsumer, i, j, k);
        swing.render(poseStack, vertexConsumer, i, j, k);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

}
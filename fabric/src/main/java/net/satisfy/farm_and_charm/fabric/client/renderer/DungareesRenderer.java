package net.satisfy.farm_and_charm.fabric.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.core.item.DungareesItem;
import net.satisfy.farm_and_charm.core.registry.ArmorRegistry;

public class DungareesRenderer implements ArmorRenderer {
    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, HumanoidModel<LivingEntity> contextModel) {
        if (stack.getItem() instanceof DungareesItem leggings) {
            Model model = ArmorRegistry.getLeggingsModel(leggings, contextModel.rightLeg, contextModel.leftLeg, contextModel.body);

            model.renderToBuffer(matrices, vertexConsumers.getBuffer(model.renderType(leggings.getLeggingsTexture())), light, OverlayTexture.NO_OVERLAY);
        }
    }
}
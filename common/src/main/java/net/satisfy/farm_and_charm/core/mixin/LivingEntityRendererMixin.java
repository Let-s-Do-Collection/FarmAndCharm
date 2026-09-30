package net.satisfy.farm_and_charm.core.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.client.renderer.entity.SaturationOverlayRenderer;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.util.SaturationTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("TAIL"))
    private void farm_and_charm$renderSaturationOverlay(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (!(entity instanceof Animal animal)) return;

        EntityType<?> type = animal.getType();
        if (!(type == EntityType.COW || type == EntityType.PIG || type == EntityType.SHEEP || type == EntityType.CHICKEN)) return;

        if (!(animal instanceof SaturationTracker.SaturatedAnimal saturated)) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.crosshairPickEntity != animal || mc.player == null) return;

        for (ItemStack stack : mc.player.getArmorSlots()) {
            if (stack.getItem() == ObjectRegistry.DUNGAREES.get()) {
                SaturationTracker tracker = saturated.farm_and_charm$getSaturationTracker();
                SaturationOverlayRenderer.render(poseStack, buffer, animal, tracker.level(), tracker.foodCounter());
                break;
            }
        }
    }
}
package net.satisfy.farm_and_charm.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

public class SaturationOverlayRenderer {

    public static void render(PoseStack poseStack, MultiBufferSource buffer, LivingEntity entity, int level, int foodCounter) {
        if (entity.isBaby()) return;

        EntityType<?> type = entity.getType();
        if (!(type == EntityType.COW || type == EntityType.PIG || type == EntityType.SHEEP || type == EntityType.CHICKEN)) return;

        Minecraft mc = Minecraft.getInstance();
        ItemRenderer itemRenderer = mc.getItemRenderer();

        double yOffset = entity.getBbHeight() + 0.6;
        if (entity.hasCustomName()) {
            yOffset += 0.3;
        }

        poseStack.pushPose();
        poseStack.translate(0.0, yOffset, 0.0);
        poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(0.4f, 0.4f, 0.4f);

        ItemStack icon = resolveIcon(entity);
        int[] thresholds = {5, 10, 15, 20};

        for (int i = 0; i < 3; i++) {
            poseStack.pushPose();
            poseStack.translate((1 - i) * 1.1, 0.0, 0.0);

            float alpha;

            if (level > 0) {
                if (i < level) {
                    alpha = 0.9f;
                } else if (i == level) {
                    float progress = (float) foodCounter / thresholds[level];
                    alpha = Mth.clamp(0.4f + 0.5f * progress, 0.4f, 0.9f);
                } else {
                    alpha = 0.4f;
                }
            } else {
                float progress = getProgress(foodCounter, i, thresholds);
                alpha = 0.4f + (0.5f * progress);
            }

            int light = Mth.floor(240 * alpha);
            itemRenderer.renderStatic(icon, ItemDisplayContext.FIXED, light, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 1);

            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private static float getProgress(int foodCounter, int i, int[] thresholds) {
        int start = switch (i) {
            case 1 -> thresholds[0];
            case 2 -> thresholds[0] + thresholds[1];
            default -> 0;
        };
        int end = switch (i) {
            case 0 -> thresholds[0];
            case 1 -> thresholds[0] + thresholds[1];
            case 2 -> thresholds[0] + thresholds[1] + thresholds[2];
            default -> 1;
        };
        return Mth.clamp((float)(foodCounter - start) / (end - start), 0f, 1f);
    }

    private static ItemStack resolveIcon(LivingEntity entity) {
        if (entity instanceof Cow) return new ItemStack(Items.WHEAT);
        if (entity instanceof Pig) return new ItemStack(Items.APPLE);
        if (entity instanceof Sheep) return new ItemStack(Items.WHEAT);
        if (entity instanceof Chicken) return new ItemStack(Items.WHEAT_SEEDS);
        return new ItemStack(ObjectRegistry.HORSE_FODDER.get());
    }
}

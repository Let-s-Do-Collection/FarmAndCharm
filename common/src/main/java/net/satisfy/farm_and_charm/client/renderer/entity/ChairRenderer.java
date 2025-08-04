package net.satisfy.farm_and_charm.client.renderer.entity;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.core.entity.ChairEntity;

public class ChairRenderer extends EntityRenderer<ChairEntity> {
    public ChairRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public boolean shouldRender(ChairEntity entity, Frustum frustum, double x, double y, double z) {
        return false;
    }

    @Override
    @SuppressWarnings("all")
    public ResourceLocation getTextureLocation(ChairEntity entity) {
        return null;
    }
}
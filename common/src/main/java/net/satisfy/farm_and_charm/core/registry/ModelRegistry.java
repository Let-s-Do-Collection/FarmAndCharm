package net.satisfy.farm_and_charm.core.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

public enum ModelRegistry {
    ;
    public static final ModelLayerLocation CART = new ModelLayerLocation(FarmAndCharmIdentifier.of("cart"), "main");
    public static final ModelLayerLocation PLOW = new ModelLayerLocation(FarmAndCharmIdentifier.of("plow"), "main");
}

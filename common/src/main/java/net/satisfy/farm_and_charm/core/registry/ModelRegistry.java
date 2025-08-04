package net.satisfy.farm_and_charm.core.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

public class ModelRegistry {

    public static final ModelLayerLocation SUPPLY_CART = new ModelLayerLocation(FarmAndCharmIdentifier.of("supply_cart"), "main");
    public static final ModelLayerLocation PLOW = new ModelLayerLocation(FarmAndCharmIdentifier.of("plow"), "main");
}

package net.satisfy.farm_and_charm.core.util;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.FarmAndCharm;

public final class FarmAndCharmIdentifier {

    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, path);
    }

    public static ResourceLocation of(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
}

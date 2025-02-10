package net.satisfy.farm_and_charm.core.util;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.FarmAndCharm;

public class FarmAndCharmIdentifier extends ResourceLocation {

    public FarmAndCharmIdentifier(String path) {
        super(FarmAndCharm.MOD_ID, path);
    }

}

package net.satisfy.farm_and_charm.fabric;

import net.fabricmc.api.ModInitializer;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.fabric.world.FarmAndCharmBiomeModification;
import net.satisfy.farm_and_charm.core.registry.CompostableRegistry;

public class FarmAndCharmFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FarmAndCharm.init();
        CompostableRegistry.registerCompostable();
        FarmAndCharmBiomeModification.init();
    }
}

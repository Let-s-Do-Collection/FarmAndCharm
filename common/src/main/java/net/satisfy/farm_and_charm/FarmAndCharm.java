package net.satisfy.farm_and_charm;

import net.satisfy.farm_and_charm.core.registry.*;

public class FarmAndCharm {
    public static final String MOD_ID = "farm_and_charm";

    public static void init() {
        ObjectRegistry.init();
        EntityTypeRegistry.init();
        MobEffectRegistry.init();
        TabRegistry.init();
        ScreenhandlerTypeRegistry.init();
        SoundEventRegistry.init();
        RecipeTypeRegistry.init();
        VillagerTradeRegistryHandler.init();
    }
}
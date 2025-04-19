package net.satisfy.farm_and_charm;

import net.satisfy.farm_and_charm.core.registry.*;

public class FarmAndCharm {
    public static final String MOD_ID = "farm_and_charm";

    public static void init() {
        DataComponentRegistry.init();
        ObjectRegistry.init();
        EntityTypeRegistry.init();
        TabRegistry.init();
        ScreenhandlerTypeRegistry.init();
        SoundEventRegistry.init();
        RecipeTypeRegistry.init();
    }
}
package net.satisfy.farm_and_charm;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import net.satisfy.farm_and_charm.registry.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class FarmAndCharm {
    public static final String MOD_ID = "farm_and_charm";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static Supplier<RegistrarManager> MANAGER;

    public static void init() {
        MANAGER = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));
        ObjectRegistry.init();
        EntityTypeRegistry.init();
        MobEffectRegistry.init();
        TabRegistry.init();
        ScreenhandlerTypeRegistry.init();
        SoundEventRegistry.init();
        RecipeTypeRegistry.init();
    }
}
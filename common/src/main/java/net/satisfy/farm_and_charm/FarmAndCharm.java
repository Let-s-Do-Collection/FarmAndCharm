package net.satisfy.farm_and_charm;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.RegistrarManager;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.farm_and_charm.core.registry.*;

import java.util.function.Supplier;

public class FarmAndCharm {
    public static final String MOD_ID = "farm_and_charm";
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
        VillagerTradeRegistryHandler.init();
        PacketHandler.init();
    }
}
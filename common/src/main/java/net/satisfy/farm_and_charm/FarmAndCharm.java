package net.satisfy.farm_and_charm;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.core.event.VanillaItemPlacements;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.farm_and_charm.core.registry.*;
import net.satisfy.farm_and_charm.core.util.CartInteractionHooks;

public class FarmAndCharm {
    public static final String MOD_ID = "farm_and_charm";

    public static ResourceLocation identifier(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static void init() {
        MobEffectRegistry.init();
        ObjectRegistry.init();
        ParticleTypeRegistry.init();
        VanillaItemPlacements.init();
        EntityTypeRegistry.init();
        TabRegistry.init();
        ScreenhandlerTypeRegistry.init();
        SoundEventRegistry.init();
        RecipeTypeRegistry.init();
        VillagerTradeRegistryHandler.init();
        PacketHandler.init();
        CartInteractionHooks.init();
    }
}
package net.satisfy.farm_and_charm;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.core.event.CleaverEvents;
import net.satisfy.farm_and_charm.core.event.VanillaItemPlacements;
import net.satisfy.foundation.overlay.BlockInfoSync;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.farm_and_charm.core.registry.*;
import net.satisfy.farm_and_charm.core.util.CartInteractionHooks;
import net.satisfy.foundation.rarity.FoundationRarities;
import net.satisfy.foundation.rarity.FoundationRarity;

public class FarmAndCharm {
    public static final String MOD_ID = "farm_and_charm";

    public static ResourceLocation identifier(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static void init() {
        MobEffectRegistry.init();
        ObjectRegistry.init();
        FlammableBlockRegistry.init();
        FeatureRegistry.init();
        VanillaItemPlacements.init();
        EntityTypeRegistry.init();
        TabRegistry.init();
        ScreenhandlerTypeRegistry.init();
        SoundEventRegistry.init();
        RecipeTypeRegistry.init();
        VillagerTradeRegistryHandler.init();
        PacketHandler.init();
        CartInteractionHooks.init();
        CleaverEvents.init();
        LifecycleEvent.SETUP.register(FarmAndCharm::registerRarities);
    }

    private static void registerRarities() {
        FoundationRarities.register(ObjectRegistry.SCARECROW.get(), FoundationRarity.LEGENDARY);
        FoundationRarities.register(ObjectRegistry.DUNGAREES.get(), FoundationRarity.RARE);
    }
}
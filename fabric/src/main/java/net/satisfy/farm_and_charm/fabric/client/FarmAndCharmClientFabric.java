package net.satisfy.farm_and_charm.fabric.client;

import net.satisfy.foundation.fabric.client.FoundationArmorRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.satisfy.farm_and_charm.client.FarmAndCharmClient;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

public class
FarmAndCharmClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FarmAndCharmClient.preInitClient();
        FarmAndCharmClient.onInitializeClient();
        ArmorRenderer.register(FoundationArmorRenderer.INSTANCE, ObjectRegistry.DUNGAREES.get());
    }
}

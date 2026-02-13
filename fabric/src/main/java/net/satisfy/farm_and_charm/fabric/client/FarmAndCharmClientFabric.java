package net.satisfy.farm_and_charm.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.satisfy.farm_and_charm.client.FarmAndCharmClient;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.fabric.client.renderer.DungareesRenderer;

public class
FarmAndCharmClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FarmAndCharmClient.preInitClient();
        FarmAndCharmClient.onInitializeClient();
        ArmorRenderer.register(new DungareesRenderer(), ObjectRegistry.DUNGAREES.get());
    }
}

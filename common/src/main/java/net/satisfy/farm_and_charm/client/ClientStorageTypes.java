package net.satisfy.farm_and_charm.client;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.renderer.StorageBlockEntityRenderer;
import net.satisfy.farm_and_charm.client.renderer.StorageTypeRenderer;
import net.satisfy.farm_and_charm.client.renderer.ToolRackRenderer;
import net.satisfy.farm_and_charm.client.renderer.WindowSillRenderer;
import net.satisfy.farm_and_charm.core.registry.StorageTypeRegistry;

public class ClientStorageTypes {
    public static void registerStorageType(ResourceLocation location, StorageTypeRenderer renderer) {
        StorageBlockEntityRenderer.registerStorageType(location, renderer);
    }

    public static void init() {
        FarmAndCharm.LOGGER.debug("Registering Storage Block Renderers!");
        registerStorageType(StorageTypeRegistry.TOOL_RACK, new ToolRackRenderer());
        registerStorageType(StorageTypeRegistry.WINDOW_SILL, new WindowSillRenderer());

    }
}

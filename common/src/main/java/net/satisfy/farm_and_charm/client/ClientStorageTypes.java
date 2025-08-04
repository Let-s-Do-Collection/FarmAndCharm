package net.satisfy.farm_and_charm.client;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.render.StorageBlockEntityRenderer;
import net.satisfy.farm_and_charm.client.render.StorageTypeRenderer;
import net.satisfy.farm_and_charm.client.render.ToolRackRenderer;
import net.satisfy.farm_and_charm.client.render.WindowSillRenderer;
import net.satisfy.farm_and_charm.registry.StorageTypeRegistry;

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

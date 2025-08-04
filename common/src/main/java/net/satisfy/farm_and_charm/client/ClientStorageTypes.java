package net.satisfy.farm_and_charm.client;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.client.renderer.block.StorageBlockEntityRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.StorageTypeRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.ToolRackRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.WindowSillRenderer;
import net.satisfy.farm_and_charm.core.registry.StorageTypeRegistry;

public class ClientStorageTypes {
    public static void registerStorageType(ResourceLocation location, StorageTypeRenderer renderer) {
        StorageBlockEntityRenderer.registerStorageType(location, renderer);
    }

    public static void init() {
        registerStorageType(StorageTypeRegistry.TOOL_RACK, new ToolRackRenderer());
        registerStorageType(StorageTypeRegistry.WINDOW_SILL, new WindowSillRenderer());

    }
}

package net.satisfy.farm_and_charm.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

import java.util.Set;

public class StorageTypeRegistry {
    public static final ResourceLocation TOOL_RACK = FarmAndCharmIdentifier.of("tool_rack");
    public static final ResourceLocation WINDOW_SILL = FarmAndCharmIdentifier.of("window_sill");
    public static final ResourceLocation CHICKEN_NEST = FarmAndCharmIdentifier.of("chicken_nest");

    public static Set<Block> registerBlocks(Set<Block> blocks) {
        blocks.add(ObjectRegistry.TOOL_RACK.get());
        blocks.add(ObjectRegistry.WINDOW_SILL.get());
        blocks.add(ObjectRegistry.CHICKEN_NEST.get());
        return blocks;
    }
}

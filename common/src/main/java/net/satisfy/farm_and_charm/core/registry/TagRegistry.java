package net.satisfy.farm_and_charm.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

@SuppressWarnings("unused")
public class TagRegistry {
    public static final TagKey<Block> FARMLAND = TagKey.create(Registries.BLOCK, FarmAndCharmIdentifier.of("farmland"));
    public static final TagKey<Block> SUPPRESS_CAMPFIRE_SMOKE_PARTICLES = TagKey.create(Registries.BLOCK, FarmAndCharmIdentifier.of("suppress_campfire_smoke_particles"));
    public static final TagKey<Block> COOKING_POTS = TagKey.create(Registries.BLOCK, FarmAndCharmIdentifier.of("cooking_pots"));
    public static final TagKey<Block> ALLOWS_COOKING = TagKey.create(Registries.BLOCK, FarmAndCharmIdentifier.of("allows_cooking"));
    public static final TagKey<Block> WILD_CROPS = TagKey.create(Registries.BLOCK, FarmAndCharmIdentifier.of("wild_crops"));
    public static final TagKey<Item> HANGABLE = TagKey.create(Registries.ITEM, FarmAndCharmIdentifier.of("hangable"));
    public static final TagKey<Item> CONTAINER = TagKey.create(Registries.ITEM, FarmAndCharmIdentifier.of("container"));
    public static final TagKey<Item> FEEDING_TROUGH_FODDER = TagKey.create(Registries.ITEM, FarmAndCharmIdentifier.of("feeding_trough_fodder"));

}

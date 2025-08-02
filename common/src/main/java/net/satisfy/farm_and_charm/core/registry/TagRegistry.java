package net.satisfy.farm_and_charm.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

@SuppressWarnings("unused")
public class TagRegistry {
    public static final TagKey<Block> FARMLAND = TagKey.create(Registries.BLOCK, new FarmAndCharmIdentifier("farmland"));
    public static final TagKey<Block> SUPPRESS_CAMPFIRE_SMOKE_PARTICLES = TagKey.create(Registries.BLOCK, new FarmAndCharmIdentifier("suppress_campfire_smoke_particles"));
    public static final TagKey<Block> COOKING_POTS = TagKey.create(Registries.BLOCK, new FarmAndCharmIdentifier("cooking_pots"));
    public static final TagKey<Block> ALLOWS_COOKING = TagKey.create(Registries.BLOCK, new FarmAndCharmIdentifier("allows_cooking"));
    public static final TagKey<Block> WILD_CROPS = TagKey.create(Registries.BLOCK, new FarmAndCharmIdentifier("wild_crops"));
    public static final TagKey<Item> HANGABLE = TagKey.create(Registries.ITEM, new FarmAndCharmIdentifier("hangable"));
    public static final TagKey<Item> CONTAINER = TagKey.create(Registries.ITEM, new FarmAndCharmIdentifier("container"));
    public static final TagKey<Item> FEEDING_TROUGH_FODDER = TagKey.create(Registries.ITEM, new FarmAndCharmIdentifier("feeding_trough_fodder"));
    public static final TagKey<EntityType<?>> IS_WOLF = TagKey.create(Registries.ENTITY_TYPE, new FarmAndCharmIdentifier("is_wolf"));
}

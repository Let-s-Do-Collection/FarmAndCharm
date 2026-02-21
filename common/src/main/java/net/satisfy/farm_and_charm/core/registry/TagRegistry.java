package net.satisfy.farm_and_charm.core.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.FarmAndCharm;

public class TagRegistry {
    public static final TagKey<Block> SUPPRESS_CAMPFIRE_SMOKE_PARTICLES = TagKey.create(Registries.BLOCK, FarmAndCharm.identifier("suppress_campfire_smoke_particles"));
    public static final TagKey<Block> COOKING_POTS = TagKey.create(Registries.BLOCK, FarmAndCharm.identifier("cooking_pots"));
    public static final TagKey<Block> ALLOWS_COOKING = TagKey.create(Registries.BLOCK, FarmAndCharm.identifier("allows_cooking"));
    public static final TagKey<Item> FEEDING_TROUGH_FOOD = TagKey.create(Registries.ITEM, FarmAndCharm.identifier("feeding_trough_food"));
    public static final TagKey<Item> HANGABLE = TagKey.create(Registries.ITEM, FarmAndCharm.identifier("hangable"));
    public static final TagKey<Item> CONTAINER = TagKey.create(Registries.ITEM, FarmAndCharm.identifier("container"));
    public static final TagKey<EntityType<?>> IS_WOLF = TagKey.create(Registries.ENTITY_TYPE, FarmAndCharm.identifier("is_wolf"));
    public static final TagKey<EntityType<?>> COOP_CHICKENS = TagKey.create(Registries.ENTITY_TYPE, FarmAndCharm.identifier("coop_chickens"));
}

package net.satisfy.farm_and_charm.core.world.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;


public class FarmAndCharmPlacedFeature {
    public static final ResourceKey<PlacedFeature> WILD_BEETROOTS_PATCH_CHANCE_KEY = registerKey("wild_beetroots_chance");
    public static final ResourceKey<PlacedFeature> WILD_POTATOES_PATCH_CHANCE_KEY = registerKey("wild_potatoes_chance");
    public static final ResourceKey<PlacedFeature> WILD_CARROTS_PATCH_CHANCE_KEY = registerKey("wild_carrots_chance");
    public static final ResourceKey<PlacedFeature> WILD_LETTUCE_PATCH_CHANCE_KEY = registerKey("wild_lettuce_chance");
    public static final ResourceKey<PlacedFeature> WILD_ONIONS_PATCH_CHANCE_KEY = registerKey("wild_onions_chance");
    public static final ResourceKey<PlacedFeature> WILD_TOMATOES_PATCH_CHANCE_KEY = registerKey("wild_tomatoes_chance");
    public static final ResourceKey<PlacedFeature> WILD_STRAWBERRY_PATCH_CHANCE_KEY = registerKey("wild_strawberry_chance");

    public static final ResourceKey<PlacedFeature> WILD_OAT_PATCH_CHANCE_KEY = registerKey("wild_oat_chance");
    public static final ResourceKey<PlacedFeature> WILD_BARLEY_PATCH_CHANCE_KEY = registerKey("wild_barley_chance");
    public static final ResourceKey<PlacedFeature> WILD_NETTLE_PATCH_CHANCE_KEY = registerKey("wild_nettle_chance");
    public static final ResourceKey<PlacedFeature> WILD_RIBWORT_PATCH_CHANCE_KEY = registerKey("wild_ribwort_chance");
    public static final ResourceKey<PlacedFeature> WILD_EMMER_PATCH_CHANCE_KEY = registerKey("wild_emmer_chance");
    public static final ResourceKey<PlacedFeature> WILD_CORN_PATCH_CHANCE_KEY = registerKey("wild_corn_chance");

    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, FarmAndCharmIdentifier.of(name));
    }
}



package net.satisfy.farm_and_charm.fabric.core.world;

import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.world.feature.FarmAndCharmPlacedFeature;

import java.util.function.Predicate;

public class FarmAndCharmBiomeModification {
    public static void init() {
        BiomeModification world = BiomeModifications.create(FarmAndCharm.identifier("world_features"));
        Predicate<BiomeSelectionContext> spawnsWildFlowersBiomes = getFarm_And_CharmSelector("spawns_wild_flowers");
        Predicate<BiomeSelectionContext> spawnsWildEmmerBiomes = getFarm_And_CharmSelector("spawns_wild_emmer");
        Predicate<BiomeSelectionContext> spawnsWildOatBiomes = getFarm_And_CharmSelector("spawns_wild_oat");
        Predicate<BiomeSelectionContext> spawnsWildBarleyBiomes = getFarm_And_CharmSelector("spawns_wild_barley");
        Predicate<BiomeSelectionContext> spawnsWildTomatoesandCornBiomes = getFarm_And_CharmSelector("spawns_wild_tomatoes_and_corn");
        Predicate<BiomeSelectionContext> spawnsWildStrawberriesBiomes = getFarm_And_CharmSelector("spawns_wild_strawberries");
        Predicate<BiomeSelectionContext> spawnsWildCrops = getFarm_And_CharmSelector("spawns_wild_crops");
        Predicate<BiomeSelectionContext> spawnsWildPotatoes = getFarm_And_CharmSelector("spawns_wild_potatoes");

        world.add(ModificationPhase.ADDITIONS, spawnsWildPotatoes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_POTATOES_PATCH_CHANCE_KEY));

        world.add(ModificationPhase.ADDITIONS, spawnsWildFlowersBiomes, ctx -> {
            ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_NETTLE_PATCH_CHANCE_KEY);
            ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_RIBWORT_PATCH_CHANCE_KEY);
        });

        world.add(ModificationPhase.ADDITIONS, spawnsWildEmmerBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_EMMER_PATCH_CHANCE_KEY));

        world.add(ModificationPhase.ADDITIONS, spawnsWildTomatoesandCornBiomes, ctx -> {
            ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_TOMATOES_PATCH_CHANCE_KEY);
            ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_CORN_PATCH_CHANCE_KEY);
        });

        world.add(ModificationPhase.ADDITIONS, spawnsWildStrawberriesBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_STRAWBERRY_PATCH_CHANCE_KEY));

        world.add(ModificationPhase.ADDITIONS, spawnsWildCrops, ctx -> {
            ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_LETTUCE_PATCH_CHANCE_KEY);
            ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_CARROTS_PATCH_CHANCE_KEY);
            ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_ONIONS_PATCH_CHANCE_KEY);
            ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_BEETROOTS_PATCH_CHANCE_KEY);
        });

        world.add(ModificationPhase.ADDITIONS, spawnsWildOatBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_OAT_PATCH_CHANCE_KEY));

        world.add(ModificationPhase.ADDITIONS, spawnsWildBarleyBiomes, ctx -> ctx.getGenerationSettings().addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, FarmAndCharmPlacedFeature.WILD_BARLEY_PATCH_CHANCE_KEY));
    }

    private static Predicate<BiomeSelectionContext> getFarm_And_CharmSelector(String path) {
        return BiomeSelectors.tag(TagKey.create(Registries.BIOME, FarmAndCharm.identifier(path)));
    }
}

package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.world.feature.AbandonedFieldConfiguration;
import net.satisfy.farm_and_charm.core.world.feature.AbandonedFieldFeature;

public class FeatureRegistry {
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.FEATURE);

    public static final RegistrySupplier<Feature<AbandonedFieldConfiguration>> ABANDONED_FIELD = FEATURES.register(FarmAndCharm.identifier("abandoned_field"), () -> new AbandonedFieldFeature(AbandonedFieldConfiguration.CODEC));

    public static void init() {
        FEATURES.register();
    }
}

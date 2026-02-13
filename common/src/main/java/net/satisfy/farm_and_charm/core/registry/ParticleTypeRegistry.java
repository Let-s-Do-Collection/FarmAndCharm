package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.satisfy.farm_and_charm.FarmAndCharm;

public class ParticleTypeRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> SOUP_BUBBLE = PARTICLE_TYPES.register("soup_bubble", () -> new SimpleParticleType(false) {});
    public static final RegistrySupplier<SimpleParticleType> SOUP_STEAM = PARTICLE_TYPES.register("soup_steam", () -> new SimpleParticleType(false) {});
    public static final RegistrySupplier<SimpleParticleType> SOUP_COOKING_BUBBLE = PARTICLE_TYPES.register("soup_cooking_bubble", () -> new SimpleParticleType(false) {});

    public static void init() {
        PARTICLE_TYPES.register();
    }
}
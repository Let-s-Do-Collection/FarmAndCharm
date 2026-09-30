package net.satisfy.farm_and_charm.core.registry;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.satisfy.farm_and_charm.FarmAndCharm;
import org.jetbrains.annotations.NotNull;

public class ParticleTypeRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> SOUP_BUBBLE = PARTICLE_TYPES.register("soup_bubble", () -> new SimpleParticleType(false) {});
    public static final RegistrySupplier<SimpleParticleType> SOUP_STEAM = PARTICLE_TYPES.register("soup_steam", () -> new SimpleParticleType(false) {});
    public static final RegistrySupplier<SimpleParticleType> SOUP_COOKING_BUBBLE = PARTICLE_TYPES.register("soup_cooking_bubble", () -> new SimpleParticleType(false) {});
    public static final RegistrySupplier<ParticleType<ColorParticleOption>> DYE_SPLASH = PARTICLE_TYPES.register("dye_splash", ParticleTypeRegistry::colored);
    public static final RegistrySupplier<ParticleType<ColorParticleOption>> FEATHER = PARTICLE_TYPES.register("feather", ParticleTypeRegistry::colored);
    public static final RegistrySupplier<SimpleParticleType> WATER_DRIP = PARTICLE_TYPES.register("water_drip", () -> new SimpleParticleType(false) {});
    public static final RegistrySupplier<SimpleParticleType> WATER_SPLASH = PARTICLE_TYPES.register("water_splash", () -> new SimpleParticleType(false) {});

    private static ParticleType<ColorParticleOption> colored() {
        return new ParticleType<>(false) {
            @Override
            public @NotNull MapCodec<ColorParticleOption> codec() {
                return ColorParticleOption.codec(this);
            }

            @Override
            public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, ColorParticleOption> streamCodec() {
                return ColorParticleOption.streamCodec(this);
            }
        };
    }

    public static void init() {
        PARTICLE_TYPES.register();
    }
}

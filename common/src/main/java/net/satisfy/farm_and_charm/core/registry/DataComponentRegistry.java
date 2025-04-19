package net.satisfy.farm_and_charm.core.registry;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.item.food.FoodEffectData;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

import java.util.function.UnaryOperator;

public class DataComponentRegistry {
    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<FoodEffectData>> FOOD_EFFECTS = register("food_effects", (builder) -> builder.persistent(FoodEffectData.CODEC).networkSynchronized(FoodEffectData.STREAM_CODEC));
    public static final RegistrySupplier<DataComponentType<Integer>> FOOD_STAGE = register("food_stage", (builder) -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    private static <T> RegistrySupplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        return COMPONENT_TYPES.register(FarmAndCharmIdentifier.of(name), () -> unaryOperator.apply(DataComponentType.builder()).build());
    }

    public static void init() { COMPONENT_TYPES.register(); }
}

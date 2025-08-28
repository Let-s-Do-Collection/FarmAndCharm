package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.effect.*;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

import java.util.function.Supplier;

public class MobEffectRegistry {
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.MOB_EFFECT);
    private static final Registrar<MobEffect> MOB_EFFECTS_REGISTRAR = MOB_EFFECTS.getRegistrar();

    public static final RegistrySupplier<MobEffect> SWEETS;
    public static final RegistrySupplier<MobEffect> HORSE_FODDER;
    public static final RegistrySupplier<MobEffect> DOG_FOOD;
    public static final RegistrySupplier<MobEffect> CLUCK;
    public static final RegistrySupplier<MobEffect> GRANDMAS_BLESSING;
    public static final RegistrySupplier<MobEffect> RESTED;
    public static final RegistrySupplier<MobEffect> FARMERS_BLESSING;
    public static final RegistrySupplier<MobEffect> SUSTENANCE;
    public static final RegistrySupplier<MobEffect> SATIATION;
    public static final RegistrySupplier<MobEffect> FEAST;

    private static RegistrySupplier<MobEffect> registerEffect(String name, Supplier<MobEffect> effect) {
        if (Platform.isNeoForge()) {
            return MOB_EFFECTS.register(name, effect);
        }
        return MOB_EFFECTS_REGISTRAR.register(FarmAndCharmIdentifier.of(name), effect);
    }

    public static void init() {
        MOB_EFFECTS.register();
    }

    static {
        SWEETS = registerEffect("sweets", SweetsEffect::new);
        HORSE_FODDER = registerEffect("horse_fodder", HorseFodderEffect::new);
        DOG_FOOD = registerEffect("dog_food", DogFoodEffect::new);
        CLUCK = registerEffect("cluck", ChickenEffect::new);
        GRANDMAS_BLESSING = registerEffect("grandmas_blessing", GrandmasBlessingEffect::new);
        RESTED = registerEffect("rested", RestedEffect::new);
        FARMERS_BLESSING = registerEffect("farmers_blessing", FarmersBlessingEffect::new);
        SUSTENANCE = registerEffect("sustenance", SustenanceEffect::new);
        SATIATION = registerEffect("satiation", SatiationEffect::new);
        FEAST = registerEffect("feast", FeastEffect::new);
    }
}

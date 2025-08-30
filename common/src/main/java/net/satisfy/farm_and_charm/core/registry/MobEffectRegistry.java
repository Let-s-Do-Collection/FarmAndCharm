package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.effect.ChickenEffect;
import net.satisfy.farm_and_charm.core.effect.DogFoodEffect;
import net.satisfy.farm_and_charm.core.effect.FarmersBlessingEffect;
import net.satisfy.farm_and_charm.core.effect.FeastEffect;
import net.satisfy.farm_and_charm.core.effect.GrandmasBlessingEffect;
import net.satisfy.farm_and_charm.core.effect.HorseFodderEffect;
import net.satisfy.farm_and_charm.core.effect.RestedEffect;
import net.satisfy.farm_and_charm.core.effect.SatiationEffect;
import net.satisfy.farm_and_charm.core.effect.SustenanceEffect;
import net.satisfy.farm_and_charm.core.effect.SweetsEffect;

public class MobEffectRegistry {
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.MOB_EFFECT);

    public static final RegistrySupplier<MobEffect> SWEETS = MOB_EFFECTS.register("sweets", SweetsEffect::new);
    public static final RegistrySupplier<MobEffect> HORSE_FODDER = MOB_EFFECTS.register("horse_fodder", HorseFodderEffect::new);
    public static final RegistrySupplier<MobEffect> DOG_FOOD = MOB_EFFECTS.register("dog_food", DogFoodEffect::new);
    public static final RegistrySupplier<MobEffect> CLUCK = MOB_EFFECTS.register("cluck", ChickenEffect::new);
    public static final RegistrySupplier<MobEffect> GRANDMAS_BLESSING = MOB_EFFECTS.register("grandmas_blessing", GrandmasBlessingEffect::new);
    public static final RegistrySupplier<MobEffect> RESTED = MOB_EFFECTS.register("rested", RestedEffect::new);
    public static final RegistrySupplier<MobEffect> FARMERS_BLESSING = MOB_EFFECTS.register("farmers_blessing", FarmersBlessingEffect::new);
    public static final RegistrySupplier<MobEffect> SUSTENANCE = MOB_EFFECTS.register("sustenance", SustenanceEffect::new);
    public static final RegistrySupplier<MobEffect> SATIATION = MOB_EFFECTS.register("satiation", SatiationEffect::new);
    public static final RegistrySupplier<MobEffect> FEAST = MOB_EFFECTS.register("feast", FeastEffect::new);

    public static void init() {
        MOB_EFFECTS.register();
    }
}

package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.effect.*;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

import java.util.function.Supplier;

public class MobEffectRegistry {

    public static final Holder<MobEffect> SWEETS;
    public static final Holder<MobEffect> HORSE_FODDER;
    public static final Holder<MobEffect> DOG_FOOD;
    public static final Holder<MobEffect> CLUCK;
    public static final Holder<MobEffect> GRANDMAS_BLESSING;
    public static final Holder<MobEffect> RESTED;
    public static final Holder<MobEffect> FARMERS_BLESSING;
    public static final Holder<MobEffect> SUSTENANCE;
    public static final Holder<MobEffect> SATIATION;
    public static final Holder<MobEffect> FEAST;

    static {
        SWEETS = PlatformHelper.registerEffect("sweets", SweetsEffect::new);
        HORSE_FODDER = PlatformHelper.registerEffect("horse_fodder", HorseFodderEffect::new);
        DOG_FOOD = PlatformHelper.registerEffect("dog_food", DogFoodEffect::new);
        CLUCK = PlatformHelper.registerEffect("cluck", ChickenEffect::new);
        GRANDMAS_BLESSING = PlatformHelper.registerEffect("grandmas_blessing", GrandmasBlessingEffect::new);
        RESTED = PlatformHelper.registerEffect("rested", RestedEffect::new);
        FARMERS_BLESSING = PlatformHelper.registerEffect("farmers_blessing", FarmersBlessingEffect::new);
        SUSTENANCE = PlatformHelper.registerEffect("sustenance", SustenanceEffect::new);
        SATIATION = PlatformHelper.registerEffect("satiation", SatiationEffect::new);
        FEAST = PlatformHelper.registerEffect("feast", FeastEffect::new);
    }
}
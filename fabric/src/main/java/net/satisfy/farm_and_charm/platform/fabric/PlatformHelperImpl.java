package net.satisfy.farm_and_charm.platform.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;
import net.satisfy.farm_and_charm.fabric.core.config.FarmAndCharmFabricConfig;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

import java.util.function.Supplier;

public class PlatformHelperImpl extends PlatformHelper {
    public static boolean isBonemealEffectEnabled() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.blocks.enableBonemealEffect;
    }

    public static int getWaterSprinklerRange() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.blocks.waterSprinklerRange;
    }

    public static boolean isRainGrowthEffectEnabled() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.blocks.enableRainGrowthEffect;
    }

    public static float getRainGrowthMultiplier() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.blocks.rainGrowthMultiplier;
    }

    public static int getFeedingTroughRange() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.blocks.feedingTroughRange;
    }

    public static int getFertilizedSoilRange() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.blocks.fertilizedSoilRange;
    }

    public static boolean isTamingEnabled() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.items.enableTaming;
    }

    public static boolean isHorseTamingEnabled() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.items.enableHorseTaming;
    }

    public static boolean isHorseEffectsEnabled() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.items.enableHorseEffects;
    }

    public static boolean isChickenEffectsEnabled() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.items.enableChickenEffects;
    }

    public static boolean enableCatTamingChance() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.items.enableCatTamingChance;
    }

    public static boolean isFertilizerEnabled() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.items.enableFertilizer;
    }

    public static int getNutrition(String itemName) {
        FarmAndCharmFabricConfig.ItemsSettings items = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig().items;
        return switch (itemName) {
            case "oat_pancake" -> items.nutrition.oatPancakeNutrition;
            case "roasted_corn" -> items.nutrition.roastedCornNutrition;
            case "potato_with_roast_meat" -> items.nutrition.potatoWithRoastMeatNutrition;
            case "baked_lamb_ham" -> items.nutrition.bakedLambHamNutrition;
            case "farmers_breakfast" -> items.nutrition.farmersBreakfastNutrition;
            case "stuffed_chicken" -> items.nutrition.stuffedChickenNutrition;
            case "stuffed_rabbit" -> items.nutrition.stuffedRabbitNutrition;
            case "grandmothers_strawberry_cake" -> items.nutrition.grandmothersStrawberryCakeNutrition;
            case "farmers_bread" -> items.nutrition.farmersBreadNutrition;
            case "farmer_salad" -> items.nutrition.farmerSaladNutrition;
            case "goulash" -> items.nutrition.goulashNutrition;
            case "simple_tomato_soup" -> items.nutrition.simpleTomatoSoupNutrition;
            case "barley_soup" -> items.nutrition.barleySoupNutrition;
            case "onion_soup" -> items.nutrition.onionSoupNutrition;
            case "potato_soup" -> items.nutrition.potatoSoupNutrition;
            case "pasta_with_onion_sauce" -> items.nutrition.pastaWithOnionSauceNutrition;
            case "corn_grits" -> items.nutrition.cornGritsNutrition;
            case "oatmeal_with_strawberries" -> items.nutrition.oatmealWithStrawberriesNutrition;
            case "sausage_with_oat_patty" -> items.nutrition.sausageWithOatPattyNutrition;
            case "lamb_with_corn" -> items.nutrition.lambWithCornNutrition;
            case "beef_patty_with_vegetables" -> items.nutrition.beefPattyWithVegetablesNutrition;
            case "barley_patties_with_potatoes" -> items.nutrition.barleyPattiesWithPotatoesNutrition;
            case "bacon_with_eggs" -> items.nutrition.baconWithEggsNutrition;
            case "chicken_wrapped_in_bacon" -> items.nutrition.chickenWrappedInBaconNutrition;
            case "cooked_salmon" -> items.nutrition.cookedSalmonNutrition;
            case "cooked_cod" -> items.nutrition.cookedCodNutrition;
            case "roasted_chicken" -> items.nutrition.roastedChickenNutrition;
            default -> 0;
        };
    }

    public static float getSaturationMod(String itemName) {
        FarmAndCharmFabricConfig.ItemsSettings items = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig().items;
        return switch (itemName) {
            case "oat_pancake" -> items.nutrition.oatPancakeSaturationMod;
            case "roasted_corn" -> items.nutrition.roastedCornSaturationMod;
            case "potato_with_roast_meat" -> items.nutrition.potatoWithRoastMeatSaturationMod;
            case "baked_lamb_ham" -> items.nutrition.bakedLambHamSaturationMod;
            case "farmers_breakfast" -> items.nutrition.farmersBreakfastSaturationMod;
            case "stuffed_chicken" -> items.nutrition.stuffedChickenSaturationMod;
            case "stuffed_rabbit" -> items.nutrition.stuffedRabbitSaturationMod;
            case "grandmothers_strawberry_cake" -> items.nutrition.grandmothersStrawberryCakeSaturationMod;
            case "farmers_bread" -> items.nutrition.farmersBreadSaturationMod;
            case "farmer_salad" -> items.nutrition.farmerSaladSaturationMod;
            case "goulash" -> items.nutrition.goulashSaturationMod;
            case "simple_tomato_soup" -> items.nutrition.simpleTomatoSoupSaturationMod;
            case "barley_soup" -> items.nutrition.barleySoupSaturationMod;
            case "onion_soup" -> items.nutrition.onionSoupSaturationMod;
            case "potato_soup" -> items.nutrition.potatoSoupSaturationMod;
            case "pasta_with_onion_sauce" -> items.nutrition.pastaWithOnionSauceSaturationMod;
            case "corn_grits" -> items.nutrition.cornGritsSaturationMod;
            case "oatmeal_with_strawberries" -> items.nutrition.oatmealWithStrawberriesSaturationMod;
            case "sausage_with_oat_patty" -> items.nutrition.sausageWithOatPattySaturationMod;
            case "lamb_with_corn" -> items.nutrition.lambWithCornSaturationMod;
            case "beef_patty_with_vegetables" -> items.nutrition.beefPattyWithVegetablesSaturationMod;
            case "barley_patties_with_potatoes" -> items.nutrition.barleyPattiesWithPotatoesSaturationMod;
            case "bacon_with_eggs" -> items.nutrition.baconWithEggsSaturationMod;
            case "chicken_wrapped_in_bacon" -> items.nutrition.chickenWrappedInBaconSaturationMod;
            case "cooked_salmon" -> items.nutrition.cookedSalmonSaturationMod;
            case "cooked_cod" -> items.nutrition.cookedCodSaturationMod;
            case "roasted_chicken" -> items.nutrition.roastedChickenSaturationMod;
            default -> 0.0f;
        };
    }

    public static int getChickenEffectTickInterval() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.effects.chickenEffect.chickenEffectTickInterval;
    }

    public static int getChickenEffectEggChance() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.effects.chickenEffect.chickenEffectEggChance;
    }

    public static int getChickenEffectFeatherChance() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.effects.chickenEffect.chickenEffectFeatherChance;
    }

    public static int getFeastEffectSatiationInterval() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.effects.feastEffect.feastEffectSatiationInterval;
    }

    public static int getFeastEffectSustenanceInterval() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.effects.feastEffect.feastEffectSustenanceInterval;
    }

    public static int getFeastEffectHealAmount() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.effects.feastEffect.feastEffectHealAmount;
    }

    public static int getSustenanceEffectInterval() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.effects.sustenanceEffect.sustenanceEffectInterval;
    }

    public static int getSustenanceEffectHealAmount() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.effects.sustenanceEffect.sustenanceEffectHealAmount;
    }

    public static int getSustenanceEffectFoodIncrement() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.effects.sustenanceEffect.sustenanceEffectFoodIncrement;
    }

    public static int getSatiationEffectInterval() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.effects.satiationEffect.satiationEffectInterval;
    }

    public static int getSatiationEffectHealAmount() {
        FarmAndCharmFabricConfig config = AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
        return config.effects.satiationEffect.satiationEffectHealAmount;
    }

    public static Holder<MobEffect> registerEffect(String name, Supplier<MobEffect> effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, FarmAndCharmIdentifier.of(name), effect.get());
    }
}

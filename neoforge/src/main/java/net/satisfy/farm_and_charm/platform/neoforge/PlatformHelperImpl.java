package net.satisfy.farm_and_charm.platform.neoforge;

import net.satisfy.farm_and_charm.neoforge.core.config.FarmAndCharmNeoForgeConfig;

public class PlatformHelperImpl {

    public static boolean isBonemealEffectEnabled() {
        return FarmAndCharmNeoForgeConfig.enableBonemealEffect;
    }

    public static int getWaterSprinklerRange() {
        return FarmAndCharmNeoForgeConfig.waterSprinklerRange;
    }

    public static boolean isRainGrowthEffectEnabled() {
        return FarmAndCharmNeoForgeConfig.enableRainGrowthEffect;
    }

    public static float getRainGrowthMultiplier() {
        return (float) FarmAndCharmNeoForgeConfig.rainGrowthMultiplier;
    }

    public static int getFeedingTroughRange() {
        return FarmAndCharmNeoForgeConfig.feedingTroughRange;
    }

    public static int getFertilizedSoilRange() {
        return FarmAndCharmNeoForgeConfig.fertilizedSoilRange;
    }

    public static int getNutrition(String itemName) {
        return switch (itemName) {
            case "oat_pancake" -> FarmAndCharmNeoForgeConfig.oatPancakeNutrition;
            case "roasted_corn" -> FarmAndCharmNeoForgeConfig.roastedCornNutrition;
            case "potato_with_roast_meat" -> FarmAndCharmNeoForgeConfig.potatoWithRoastMeatNutrition;
            case "baked_lamb_ham" -> FarmAndCharmNeoForgeConfig.bakedLambHamNutrition;
            case "farmers_breakfast" -> FarmAndCharmNeoForgeConfig.farmersBreakfastNutrition;
            case "stuffed_chicken" -> FarmAndCharmNeoForgeConfig.stuffedChickenNutrition;
            case "stuffed_rabbit" -> FarmAndCharmNeoForgeConfig.stuffedRabbitNutrition;
            case "grandmothers_strawberry_cake" -> FarmAndCharmNeoForgeConfig.grandmothersStrawberryCakeNutrition;
            case "farmers_bread" -> FarmAndCharmNeoForgeConfig.farmersBreadNutrition;
            case "farmer_salad" -> FarmAndCharmNeoForgeConfig.farmerSaladNutrition;
            case "goulash" -> FarmAndCharmNeoForgeConfig.goulashNutrition;
            case "simple_tomato_soup" -> FarmAndCharmNeoForgeConfig.simpleTomatoSoupNutrition;
            case "barley_soup" -> FarmAndCharmNeoForgeConfig.barleySoupNutrition;
            case "onion_soup" -> FarmAndCharmNeoForgeConfig.onionSoupNutrition;
            case "potato_soup" -> FarmAndCharmNeoForgeConfig.potatoSoupNutrition;
            case "pasta_with_onion_sauce" -> FarmAndCharmNeoForgeConfig.pastaWithOnionSauceNutrition;
            case "corn_grits" -> FarmAndCharmNeoForgeConfig.cornGritsNutrition;
            case "oatmeal_with_strawberries" -> FarmAndCharmNeoForgeConfig.oatmealWithStrawberriesNutrition;
            case "sausage_with_oat_patty" -> FarmAndCharmNeoForgeConfig.sausageWithOatPattyNutrition;
            case "lamb_with_corn" -> FarmAndCharmNeoForgeConfig.lambWithCornNutrition;
            case "beef_patty_with_vegetables" -> FarmAndCharmNeoForgeConfig.beefPattyWithVegetablesNutrition;
            case "barley_patties_with_potatoes" -> FarmAndCharmNeoForgeConfig.barleyPattiesWithPotatoesNutrition;
            case "bacon_with_eggs" -> FarmAndCharmNeoForgeConfig.baconWithEggsNutrition;
            case "chicken_wrapped_in_bacon" -> FarmAndCharmNeoForgeConfig.chickenWrappedInBaconNutrition;
            case "cooked_salmon" -> FarmAndCharmNeoForgeConfig.cookedSalmonNutrition;
            case "cooked_cod" -> FarmAndCharmNeoForgeConfig.cookedCodNutrition;
            case "roasted_chicken" -> FarmAndCharmNeoForgeConfig.roastedChickenNutrition;
            default -> 0;
        };
    }

    public static float getSaturationMod(String itemName) {
        return switch (itemName) {
            case "oat_pancake" -> (float) FarmAndCharmNeoForgeConfig.oatPancakeSaturationMod;
            case "roasted_corn" -> (float) FarmAndCharmNeoForgeConfig.roastedCornSaturationMod;
            case "potato_with_roast_meat" -> (float) FarmAndCharmNeoForgeConfig.potatoWithRoastMeatSaturationMod;
            case "baked_lamb_ham" -> (float) FarmAndCharmNeoForgeConfig.bakedLambHamSaturationMod;
            case "farmers_breakfast" -> (float) FarmAndCharmNeoForgeConfig.farmersBreakfastSaturationMod;
            case "stuffed_chicken" -> (float) FarmAndCharmNeoForgeConfig.stuffedChickenSaturationMod;
            case "stuffed_rabbit" -> (float) FarmAndCharmNeoForgeConfig.stuffedRabbitSaturationMod;
            case "grandmothers_strawberry_cake" -> (float) FarmAndCharmNeoForgeConfig.grandmothersStrawberryCakeSaturationMod;
            case "farmers_bread" -> (float) FarmAndCharmNeoForgeConfig.farmersBreadSaturationMod;
            case "farmer_salad" -> (float) FarmAndCharmNeoForgeConfig.farmerSaladSaturationMod;
            case "goulash" -> (float) FarmAndCharmNeoForgeConfig.goulashSaturationMod;
            case "simple_tomato_soup" -> (float) FarmAndCharmNeoForgeConfig.simpleTomatoSoupSaturationMod;
            case "barley_soup" -> (float) FarmAndCharmNeoForgeConfig.barleySoupSaturationMod;
            case "onion_soup" -> (float) FarmAndCharmNeoForgeConfig.onionSoupSaturationMod;
            case "potato_soup" -> (float) FarmAndCharmNeoForgeConfig.potatoSoupSaturationMod;
            case "pasta_with_onion_sauce" -> (float) FarmAndCharmNeoForgeConfig.pastaWithOnionSauceSaturationMod;
            case "corn_grits" -> (float) FarmAndCharmNeoForgeConfig.cornGritsSaturationMod;
            case "oatmeal_with_strawberries" -> (float) FarmAndCharmNeoForgeConfig.oatmealWithStrawberriesSaturationMod;
            case "sausage_with_oat_patty" -> (float) FarmAndCharmNeoForgeConfig.sausageWithOatPattySaturationMod;
            case "lamb_with_corn" -> (float) FarmAndCharmNeoForgeConfig.lambWithCornSaturationMod;
            case "beef_patty_with_vegetables" -> (float) FarmAndCharmNeoForgeConfig.beefPattyWithVegetablesSaturationMod;
            case "barley_patties_with_potatoes" -> (float) FarmAndCharmNeoForgeConfig.barleyPattiesWithPotatoesSaturationMod;
            case "bacon_with_eggs" -> (float) FarmAndCharmNeoForgeConfig.baconWithEggsSaturationMod;
            case "chicken_wrapped_in_bacon" -> (float) FarmAndCharmNeoForgeConfig.chickenWrappedInBaconSaturationMod;
            case "cooked_salmon" -> (float) FarmAndCharmNeoForgeConfig.cookedSalmonSaturationMod;
            case "cooked_cod" -> (float) FarmAndCharmNeoForgeConfig.cookedCodSaturationMod;
            case "roasted_chicken" -> (float) FarmAndCharmNeoForgeConfig.roastedChickenSaturationMod;
            default -> 0.0f;
        };
    }

    public static boolean isTamingEnabled() {
        return FarmAndCharmNeoForgeConfig.enableTaming;
    }

    public static boolean isHorseTamingEnabled() {
        return FarmAndCharmNeoForgeConfig.enableHorseTaming;
    }

    public static boolean isHorseEffectsEnabled() {
        return FarmAndCharmNeoForgeConfig.enableHorseEffects;
    }

    public static boolean isChickenEffectsEnabled() {
        return FarmAndCharmNeoForgeConfig.enableChickenEffects;
    }

    public static boolean enableCatTamingChance() {
        return FarmAndCharmNeoForgeConfig.enableCatTamingChance;
    }

    public static boolean isFertilizerEnabled() {
        return FarmAndCharmNeoForgeConfig.enableFertilizer;
    }

    public static int getChickenEffectTickInterval() {
        return FarmAndCharmNeoForgeConfig.chickenEffectTickInterval;
    }

    public static int getChickenEffectEggChance() {
        return FarmAndCharmNeoForgeConfig.chickenEffectEggChance;
    }

    public static int getChickenEffectFeatherChance() {
        return FarmAndCharmNeoForgeConfig.chickenEffectFeatherChance;
    }

    public static int getFeastEffectSatiationInterval() {
        return FarmAndCharmNeoForgeConfig.feastEffectSatiationInterval;
    }

    public static int getFeastEffectSustenanceInterval() {
        return FarmAndCharmNeoForgeConfig.feastEffectSustenanceInterval;
    }

    public static int getFeastEffectHealAmount() {
        return FarmAndCharmNeoForgeConfig.feastEffectHealAmount;
    }

    public static int getSustenanceEffectInterval() {
        return FarmAndCharmNeoForgeConfig.sustenanceEffectInterval;
    }

    public static int getSustenanceEffectHealAmount() {
        return FarmAndCharmNeoForgeConfig.sustenanceEffectHealAmount;
    }

    public static int getSustenanceEffectFoodIncrement() {
        return FarmAndCharmNeoForgeConfig.sustenanceEffectFoodIncrement;
    }

    public static int getSatiationEffectInterval() {
        return FarmAndCharmNeoForgeConfig.satiationEffectInterval;
    }

    public static int getSatiationEffectHealAmount() {
        return FarmAndCharmNeoForgeConfig.satiationEffectHealAmount;
    }
}

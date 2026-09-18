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
        try {
            return switch (itemName) {
                case "oat_pancake" -> FarmAndCharmNeoForgeConfig.OAT_PANCAKE_NUTRITION.get();
                case "roasted_corn" -> FarmAndCharmNeoForgeConfig.ROASTED_CORN_NUTRITION.get();
                case "potato_with_roast_meat" -> FarmAndCharmNeoForgeConfig.POTATO_WITH_ROAST_MEAT_NUTRITION.get();
                case "baked_lamb_ham" -> FarmAndCharmNeoForgeConfig.BAKED_LAMB_HAM_NUTRITION.get();
                case "farmers_breakfast" -> FarmAndCharmNeoForgeConfig.FARMERS_BREAKFAST_NUTRITION.get();
                case "stuffed_chicken" -> FarmAndCharmNeoForgeConfig.STUFFED_CHICKEN_NUTRITION.get();
                case "stuffed_rabbit" -> FarmAndCharmNeoForgeConfig.STUFFED_RABBIT_NUTRITION.get();
                case "grandmothers_strawberry_cake" -> FarmAndCharmNeoForgeConfig.GRANDMOTHERS_STRAWBERRY_CAKE_NUTRITION.get();
                case "farmers_bread" -> FarmAndCharmNeoForgeConfig.FARMERS_BREAD_NUTRITION.get();
                case "farmer_salad" -> FarmAndCharmNeoForgeConfig.FARMER_SALAD_NUTRITION.get();
                case "goulash" -> FarmAndCharmNeoForgeConfig.GOULASH_NUTRITION.get();
                case "simple_tomato_soup" -> FarmAndCharmNeoForgeConfig.SIMPLE_TOMATO_SOUP_NUTRITION.get();
                case "barley_soup" -> FarmAndCharmNeoForgeConfig.BARLEY_SOUP_NUTRITION.get();
                case "onion_soup" -> FarmAndCharmNeoForgeConfig.ONION_SOUP_NUTRITION.get();
                case "potato_soup" -> FarmAndCharmNeoForgeConfig.POTATO_SOUP_NUTRITION.get();
                case "pasta_with_onion_sauce" -> FarmAndCharmNeoForgeConfig.PASTA_WITH_ONION_SAUCE_NUTRITION.get();
                case "corn_grits" -> FarmAndCharmNeoForgeConfig.CORN_GRITS_NUTRITION.get();
                case "oatmeal_with_strawberries" -> FarmAndCharmNeoForgeConfig.OATMEAL_WITH_STRAWBERRIES_NUTRITION.get();
                case "sausage_with_oat_patty" -> FarmAndCharmNeoForgeConfig.SAUSAGE_WITH_OAT_PATTY_NUTRITION.get();
                case "lamb_with_corn" -> FarmAndCharmNeoForgeConfig.LAMB_WITH_CORN_NUTRITION.get();
                case "beef_patty_with_vegetables" -> FarmAndCharmNeoForgeConfig.BEEF_PATTY_WITH_VEGETABLES_NUTRITION.get();
                case "barley_patties_with_potatoes" -> FarmAndCharmNeoForgeConfig.BARLEY_PATTIES_WITH_POTATOES_NUTRITION.get();
                case "bacon_with_eggs" -> FarmAndCharmNeoForgeConfig.BACON_WITH_EGGS_NUTRITION.get();
                case "chicken_wrapped_in_bacon" -> FarmAndCharmNeoForgeConfig.CHICKEN_WRAPPED_IN_BACON_NUTRITION.get();
                case "cooked_salmon" -> FarmAndCharmNeoForgeConfig.COOKED_SALMON_NUTRITION.get();
                case "cooked_cod" -> FarmAndCharmNeoForgeConfig.COOKED_COD_NUTRITION.get();
                case "roasted_chicken" -> FarmAndCharmNeoForgeConfig.ROASTED_CHICKEN_NUTRITION.get();
                default -> 0;
            };
        } catch (IllegalStateException notLoadedYet) {
            // Item registration (RegisterEvent) runs before ModConfigEvent.Loading on NeoForge,
            // so the spec isn't loaded yet the first time food items are built. Fall back to the
            // cached defaults here; ModifyDefaultComponentsEvent re-patches with the real config
            // value once loading has actually happened (see FarmAndCharmNeoForge).
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
    }

    public static float getSaturationMod(String itemName) {
        try {
            return switch (itemName) {
                case "oat_pancake" -> FarmAndCharmNeoForgeConfig.OAT_PANCAKE_SATURATION_MOD.get().floatValue();
                case "roasted_corn" -> FarmAndCharmNeoForgeConfig.ROASTED_CORN_SATURATION_MOD.get().floatValue();
                case "potato_with_roast_meat" -> FarmAndCharmNeoForgeConfig.POTATO_WITH_ROAST_MEAT_SATURATION_MOD.get().floatValue();
                case "baked_lamb_ham" -> FarmAndCharmNeoForgeConfig.BAKED_LAMB_HAM_SATURATION_MOD.get().floatValue();
                case "farmers_breakfast" -> FarmAndCharmNeoForgeConfig.FARMERS_BREAKFAST_SATURATION_MOD.get().floatValue();
                case "stuffed_chicken" -> FarmAndCharmNeoForgeConfig.STUFFED_CHICKEN_SATURATION_MOD.get().floatValue();
                case "stuffed_rabbit" -> FarmAndCharmNeoForgeConfig.STUFFED_RABBIT_SATURATION_MOD.get().floatValue();
                case "grandmothers_strawberry_cake" -> FarmAndCharmNeoForgeConfig.GRANDMOTHERS_STRAWBERRY_CAKE_SATURATION_MOD.get().floatValue();
                case "farmers_bread" -> FarmAndCharmNeoForgeConfig.FARMERS_BREAD_SATURATION_MOD.get().floatValue();
                case "farmer_salad" -> FarmAndCharmNeoForgeConfig.FARMER_SALAD_SATURATION_MOD.get().floatValue();
                case "goulash" -> FarmAndCharmNeoForgeConfig.GOULASH_SATURATION_MOD.get().floatValue();
                case "simple_tomato_soup" -> FarmAndCharmNeoForgeConfig.SIMPLE_TOMATO_SOUP_SATURATION_MOD.get().floatValue();
                case "barley_soup" -> FarmAndCharmNeoForgeConfig.BARLEY_SOUP_SATURATION_MOD.get().floatValue();
                case "onion_soup" -> FarmAndCharmNeoForgeConfig.ONION_SOUP_SATURATION_MOD.get().floatValue();
                case "potato_soup" -> FarmAndCharmNeoForgeConfig.POTATO_SOUP_SATURATION_MOD.get().floatValue();
                case "pasta_with_onion_sauce" -> FarmAndCharmNeoForgeConfig.PASTA_WITH_ONION_SAUCE_SATURATION_MOD.get().floatValue();
                case "corn_grits" -> FarmAndCharmNeoForgeConfig.CORN_GRITS_SATURATION_MOD.get().floatValue();
                case "oatmeal_with_strawberries" -> FarmAndCharmNeoForgeConfig.OATMEAL_WITH_STRAWBERRIES_SATURATION_MOD.get().floatValue();
                case "sausage_with_oat_patty" -> FarmAndCharmNeoForgeConfig.SAUSAGE_WITH_OAT_PATTY_SATURATION_MOD.get().floatValue();
                case "lamb_with_corn" -> FarmAndCharmNeoForgeConfig.LAMB_WITH_CORN_SATURATION_MOD.get().floatValue();
                case "beef_patty_with_vegetables" -> FarmAndCharmNeoForgeConfig.BEEF_PATTY_WITH_VEGETABLES_SATURATION_MOD.get().floatValue();
                case "barley_patties_with_potatoes" -> FarmAndCharmNeoForgeConfig.BARLEY_PATTIES_WITH_POTATOES_SATURATION_MOD.get().floatValue();
                case "bacon_with_eggs" -> FarmAndCharmNeoForgeConfig.BACON_WITH_EGGS_SATURATION_MOD.get().floatValue();
                case "chicken_wrapped_in_bacon" -> FarmAndCharmNeoForgeConfig.CHICKEN_WRAPPED_IN_BACON_SATURATION_MOD.get().floatValue();
                case "cooked_salmon" -> FarmAndCharmNeoForgeConfig.COOKED_SALMON_SATURATION_MOD.get().floatValue();
                case "cooked_cod" -> FarmAndCharmNeoForgeConfig.COOKED_COD_SATURATION_MOD.get().floatValue();
                case "roasted_chicken" -> FarmAndCharmNeoForgeConfig.ROASTED_CHICKEN_SATURATION_MOD.get().floatValue();
                default -> 0.0f;
            };
        } catch (IllegalStateException notLoadedYet) {
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

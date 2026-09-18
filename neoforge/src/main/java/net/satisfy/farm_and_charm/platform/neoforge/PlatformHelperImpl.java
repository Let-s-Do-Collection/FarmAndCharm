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
    }

    public static float getSaturationMod(String itemName) {
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

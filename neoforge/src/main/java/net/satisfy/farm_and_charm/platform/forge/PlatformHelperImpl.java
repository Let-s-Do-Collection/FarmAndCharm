package net.satisfy.farm_and_charm.platform.forge;

import net.satisfy.farm_and_charm.neoforge.config.FarmAndCharmForgeConfig;

public class PlatformHelperImpl {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, FarmAndCharm.MOD_ID);

    public static boolean isBonemealEffectEnabled() {
        return FarmAndCharmForgeConfig.ENABLE_BONEMEAL_EFFECT.get();
    }

    public static int getWaterSprinklerRange() {
        return FarmAndCharmForgeConfig.WATER_SPRINKLER_RANGE.get();
    }

    public static boolean isRainGrowthEffectEnabled() {
        return FarmAndCharmForgeConfig.ENABLE_RAIN_GROWTH_EFFECT.get();
    }

    public static float getRainGrowthMultiplier() {
        return FarmAndCharmForgeConfig.RAIN_GROWTH_MULTIPLIER.get().floatValue();
    }

    public static int getFeedingTroughRange() {
        return FarmAndCharmForgeConfig.FEEDING_TROUGH_RANGE.get();
    }

    public static int getFertilizedSoilRange() {
        return FarmAndCharmForgeConfig.FERTILIZED_SOIL_RANGE.get();
    }

    public static int getNutrition(String itemName) {
        return switch (itemName) {
            case "oat_pancake" -> FarmAndCharmForgeConfig.OAT_PANCAKE_NUTRITION.get();
            case "roasted_corn" -> FarmAndCharmForgeConfig.ROASTED_CORN_NUTRITION.get();
            case "potato_with_roast_meat" -> FarmAndCharmForgeConfig.POTATO_WITH_ROAST_MEAT_NUTRITION.get();
            case "baked_lamb_ham" -> FarmAndCharmForgeConfig.BAKED_LAMB_HAM_NUTRITION.get();
            case "farmers_breakfast" -> FarmAndCharmForgeConfig.FARMERS_BREAKFAST_NUTRITION.get();
            case "stuffed_chicken" -> FarmAndCharmForgeConfig.STUFFED_CHICKEN_NUTRITION.get();
            case "stuffed_rabbit" -> FarmAndCharmForgeConfig.STUFFED_RABBIT_NUTRITION.get();
            case "grandmothers_strawberry_cake" -> FarmAndCharmForgeConfig.GRANDMOTHERS_STRAWBERRY_CAKE_NUTRITION.get();
            case "farmers_bread" -> FarmAndCharmForgeConfig.FARMERS_BREAD_NUTRITION.get();
            case "farmer_salad" -> FarmAndCharmForgeConfig.FARMER_SALAD_NUTRITION.get();
            case "goulash" -> FarmAndCharmForgeConfig.GOULASH_NUTRITION.get();
            case "simple_tomato_soup" -> FarmAndCharmForgeConfig.SIMPLE_TOMATO_SOUP_NUTRITION.get();
            case "barley_soup" -> FarmAndCharmForgeConfig.BARLEY_SOUP_NUTRITION.get();
            case "onion_soup" -> FarmAndCharmForgeConfig.ONION_SOUP_NUTRITION.get();
            case "potato_soup" -> FarmAndCharmForgeConfig.POTATO_SOUP_NUTRITION.get();
            case "pasta_with_onion_sauce" -> FarmAndCharmForgeConfig.PASTA_WITH_ONION_SAUCE_NUTRITION.get();
            case "corn_grits" -> FarmAndCharmForgeConfig.CORN_GRITS_NUTRITION.get();
            case "oatmeal_with_strawberries" -> FarmAndCharmForgeConfig.OATMEAL_WITH_STRAWBERRIES_NUTRITION.get();
            case "sausage_with_oat_patty" -> FarmAndCharmForgeConfig.SAUSAGE_WITH_OAT_PATTY_NUTRITION.get();
            case "lamb_with_corn" -> FarmAndCharmForgeConfig.LAMB_WITH_CORN_NUTRITION.get();
            case "beef_patty_with_vegetables" -> FarmAndCharmForgeConfig.BEEF_PATTY_WITH_VEGETABLES_NUTRITION.get();
            case "barley_patties_with_potatoes" -> FarmAndCharmForgeConfig.BARLEY_PATTIES_WITH_POTATOES_NUTRITION.get();
            case "bacon_with_eggs" -> FarmAndCharmForgeConfig.BACON_WITH_EGGS_NUTRITION.get();
            case "chicken_wrapped_in_bacon" -> FarmAndCharmForgeConfig.CHICKEN_WRAPPED_IN_BACON_NUTRITION.get();
            case "cooked_salmon" -> FarmAndCharmForgeConfig.COOKED_SALMON_NUTRITION.get();
            case "cooked_cod" -> FarmAndCharmForgeConfig.COOKED_COD_NUTRITION.get();
            case "roasted_chicken" -> FarmAndCharmForgeConfig.ROASTED_CHICKEN_NUTRITION.get();
            default -> 0;
        };
    }

    public static float getSaturationMod(String itemName) {
        return switch (itemName) {
            case "oat_pancake" -> FarmAndCharmForgeConfig.OAT_PANCAKE_SATURATION_MOD.get().floatValue();
            case "roasted_corn" -> FarmAndCharmForgeConfig.ROASTED_CORN_SATURATION_MOD.get().floatValue();
            case "potato_with_roast_meat" -> FarmAndCharmForgeConfig.POTATO_WITH_ROAST_MEAT_SATURATION_MOD.get().floatValue();
            case "baked_lamb_ham" -> FarmAndCharmForgeConfig.BAKED_LAMB_HAM_SATURATION_MOD.get().floatValue();
            case "farmers_breakfast" -> FarmAndCharmForgeConfig.FARMERS_BREAKFAST_SATURATION_MOD.get().floatValue();
            case "stuffed_chicken" -> FarmAndCharmForgeConfig.STUFFED_CHICKEN_SATURATION_MOD.get().floatValue();
            case "stuffed_rabbit" -> FarmAndCharmForgeConfig.STUFFED_RABBIT_SATURATION_MOD.get().floatValue();
            case "grandmothers_strawberry_cake" -> FarmAndCharmForgeConfig.GRANDMOTHERS_STRAWBERRY_CAKE_SATURATION_MOD.get().floatValue();
            case "farmers_bread" -> FarmAndCharmForgeConfig.FARMERS_BREAD_SATURATION_MOD.get().floatValue();
            case "farmer_salad" -> FarmAndCharmForgeConfig.FARMER_SALAD_SATURATION_MOD.get().floatValue();
            case "goulash" -> FarmAndCharmForgeConfig.GOULASH_SATURATION_MOD.get().floatValue();
            case "simple_tomato_soup" -> FarmAndCharmForgeConfig.SIMPLE_TOMATO_SOUP_SATURATION_MOD.get().floatValue();
            case "barley_soup" -> FarmAndCharmForgeConfig.BARLEY_SOUP_SATURATION_MOD.get().floatValue();
            case "onion_soup" -> FarmAndCharmForgeConfig.ONION_SOUP_SATURATION_MOD.get().floatValue();
            case "potato_soup" -> FarmAndCharmForgeConfig.POTATO_SOUP_SATURATION_MOD.get().floatValue();
            case "pasta_with_onion_sauce" -> FarmAndCharmForgeConfig.PASTA_WITH_ONION_SAUCE_SATURATION_MOD.get().floatValue();
            case "corn_grits" -> FarmAndCharmForgeConfig.CORN_GRITS_SATURATION_MOD.get().floatValue();
            case "oatmeal_with_strawberries" -> FarmAndCharmForgeConfig.OATMEAL_WITH_STRAWBERRIES_SATURATION_MOD.get().floatValue();
            case "sausage_with_oat_patty" -> FarmAndCharmForgeConfig.SAUSAGE_WITH_OAT_PATTY_SATURATION_MOD.get().floatValue();
            case "lamb_with_corn" -> FarmAndCharmForgeConfig.LAMB_WITH_CORN_SATURATION_MOD.get().floatValue();
            case "beef_patty_with_vegetables" -> FarmAndCharmForgeConfig.BEEF_PATTY_WITH_VEGETABLES_SATURATION_MOD.get().floatValue();
            case "barley_patties_with_potatoes" -> FarmAndCharmForgeConfig.BARLEY_PATTIES_WITH_POTATOES_SATURATION_MOD.get().floatValue();
            case "bacon_with_eggs" -> FarmAndCharmForgeConfig.BACON_WITH_EGGS_SATURATION_MOD.get().floatValue();
            case "chicken_wrapped_in_bacon" -> FarmAndCharmForgeConfig.CHICKEN_WRAPPED_IN_BACON_SATURATION_MOD.get().floatValue();
            case "cooked_salmon" -> FarmAndCharmForgeConfig.COOKED_SALMON_SATURATION_MOD.get().floatValue();
            case "cooked_cod" -> FarmAndCharmForgeConfig.COOKED_COD_SATURATION_MOD.get().floatValue();
            case "roasted_chicken" -> FarmAndCharmForgeConfig.ROASTED_CHICKEN_SATURATION_MOD.get().floatValue();
            default -> 0.0f;
        };
    }

    public static boolean isTamingEnabled() {
        return FarmAndCharmForgeConfig.ENABLE_TAMING.get();
    }

    public static boolean isHorseTamingEnabled() {
        return FarmAndCharmForgeConfig.ENABLE_HORSE_TAMING.get();
    }

    public static boolean isHorseEffectsEnabled() {
        return FarmAndCharmForgeConfig.ENABLE_HORSE_EFFECTS.get();
    }

    public static boolean isChickenEffectsEnabled() {
        return FarmAndCharmForgeConfig.ENABLE_CHICKEN_EFFECTS.get();
    }

    public static boolean enableCatTamingChance() {
        return FarmAndCharmForgeConfig.ENABLE_CAT_TAMING_CHANCE.get();
    }

    public static boolean isFertilizerEnabled() {
        return FarmAndCharmForgeConfig.ENABLE_FERTILIZER.get();
    }

    public static int getChickenEffectTickInterval() {
        return FarmAndCharmForgeConfig.CHICKEN_EFFECT_TICK_INTERVAL.get();
    }

    public static int getChickenEffectEggChance() {
        return FarmAndCharmForgeConfig.CHICKEN_EFFECT_EGG_CHANCE.get();
    }

    public static int getChickenEffectFeatherChance() {
        return FarmAndCharmForgeConfig.CHICKEN_EFFECT_FEATHER_CHANCE.get();
    }

    public static int getFeastEffectSatiationInterval() {
        return FarmAndCharmForgeConfig.FEAST_EFFECT_SATIATION_INTERVAL.get();
    }

    public static int getFeastEffectSustenanceInterval() {
        return FarmAndCharmForgeConfig.FEAST_EFFECT_SUSTENANCE_INTERVAL.get();
    }

    public static int getFeastEffectHealAmount() {
        return FarmAndCharmForgeConfig.FEAST_EFFECT_HEAL_AMOUNT.get();
    }

    public static int getSustenanceEffectInterval() {
        return FarmAndCharmForgeConfig.SUSTENANCE_EFFECT_INTERVAL.get();
    }

    public static int getSustenanceEffectHealAmount() {
        return FarmAndCharmForgeConfig.SUSTENANCE_EFFECT_HEAL_AMOUNT.get();
    }

    public static int getSustenanceEffectFoodIncrement() {
        return FarmAndCharmForgeConfig.SUSTENANCE_EFFECT_FOOD_INCREMENT.get();
    }

    public static int getSatiationEffectInterval() {
        return FarmAndCharmForgeConfig.SATIATION_EFFECT_INTERVAL.get();
    }

    public static int getSatiationEffectHealAmount() {
        return FarmAndCharmForgeConfig.SATIATION_EFFECT_HEAL_AMOUNT.get();
    }


    public static Holder<MobEffect> registerEffect(String name, Supplier<MobEffect> effect) {
        return MOB_EFFECTS.register(name, effect);
    }
}

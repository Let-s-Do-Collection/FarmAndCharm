package net.satisfy.farm_and_charm.platform.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import net.satisfy.farm_and_charm.fabric.core.config.FarmAndCharmFabricConfig;

public class PlatformHelperImpl {
    private static FarmAndCharmFabricConfig config() {
        return AutoConfig.getConfigHolder(FarmAndCharmFabricConfig.class).getConfig();
    }

    public static boolean isRainGrowthEffectEnabled() {
        return config().farming.enableRainGrowthEffect;
    }

    public static float getRainGrowthMultiplier() {
        return config().farming.rainGrowthMultiplier;
    }

    public static int getBigCropChance() {
        return config().farming.bigCropChance;
    }

    public static boolean isBonemealEffectEnabled() {
        return config().farming.enableBonemealEffect;
    }

    public static boolean isFertilizerEnabled() {
        return config().farming.enableFertilizer;
    }

    public static int getFertilizedSoilRange() {
        return config().farming.fertilizedSoilRange;
    }

    public static int getScarecrowGrowthInterval() {
        return config().farming.scarecrowGrowthInterval;
    }

    public static int getScarecrowRange() {
        return config().farming.scarecrowRange;
    }

    public static int getWildCropDropChance() {
        return config().farming.wildCropDropChance;
    }

    public static int getWaterSprinklerRange() {
        return config().water.waterSprinklerRange;
    }

    public static int getWellGroundwaterDepth() {
        return config().water.wellGroundwaterDepth;
    }

    public static int getWellRainFillChance() {
        return config().water.wellRainFillChance;
    }

    public static int getFeedingTroughRange() {
        return config().animals.feedingTroughRange;
    }

    public static boolean isTamingEnabled() {
        return config().animals.enableDogFoodTaming;
    }

    public static boolean enableCatTamingChance() {
        return config().animals.enableCatTamingChance;
    }

    public static boolean isHorseTamingEnabled() {
        return config().animals.enableHorseTaming;
    }

    public static boolean isHorseEffectsEnabled() {
        return config().animals.enableHorseEffects;
    }

    public static boolean isChickenEffectsEnabled() {
        return config().animals.enableChickenEffects;
    }

    public static int getChickenCoopMaxChickens() {
        return config().animals.chickenCoopMaxChickens;
    }

    public static int getChickenCoopMaxEggs() {
        return config().animals.chickenCoopMaxEggs;
    }

    public static boolean isDogBeggingEnabled() {
        return config().animals.enableDogBegging;
    }

    public static boolean isCatBeggingEnabled() {
        return config().animals.enableCatBegging;
    }

    public static int getPetBowlSearchRange() {
        return config().animals.petBowlSearchRange;
    }

    public static int getFeedingTimeMiddayStart() {
        return config().animals.feedingTimeMiddayStart;
    }

    public static int getFeedingTimeMiddayEnd() {
        return config().animals.feedingTimeMiddayEnd;
    }

    public static int getFeedingTimeEveningStart() {
        return config().animals.feedingTimeEveningStart;
    }

    public static int getFeedingTimeEveningEnd() {
        return config().animals.feedingTimeEveningEnd;
    }

    public static boolean infoTooltipsNeedDungarees() {
        return config().infoTooltips.needDungarees;
    }

    public static boolean showCuttingBoardInfo() {
        return config().infoTooltips.showCuttingBoardInfo;
    }

    public static boolean showMincerInfo() {
        return config().infoTooltips.showMincerInfo;
    }

    public static boolean showCraftingBowlInfo() {
        return config().infoTooltips.showCraftingBowlInfo;
    }

    public static boolean showSiloInfo() {
        return config().infoTooltips.showSiloInfo;
    }

    public static boolean showWaterSprinklerInfo() {
        return config().infoTooltips.showWaterSprinklerInfo;
    }

    public static boolean showScarecrowInfo() {
        return config().infoTooltips.showScarecrowInfo;
    }

    public static int getIronCleaverChops() {
        return config().kitchen.ironCleaverChops;
    }

    public static int getDiamondCleaverChops() {
        return config().kitchen.diamondCleaverChops;
    }

    public static int getNetheriteCleaverChops() {
        return config().kitchen.netheriteCleaverChops;
    }

    public static boolean isCleaverHeadDropsEnabled() {
        return config().kitchen.cleaverHeadDrops;
    }

    public static int getChickenEffectTickInterval() {
        return config().effects.chickenEffect.chickenEffectTickInterval;
    }

    public static int getChickenEffectEggChance() {
        return config().effects.chickenEffect.chickenEffectEggChance;
    }

    public static int getChickenEffectFeatherChance() {
        return config().effects.chickenEffect.chickenEffectFeatherChance;
    }

    public static int getFeastEffectSatiationInterval() {
        return config().effects.feastEffect.feastEffectSatiationInterval;
    }

    public static int getFeastEffectSustenanceInterval() {
        return config().effects.feastEffect.feastEffectSustenanceInterval;
    }

    public static int getFeastEffectHealAmount() {
        return config().effects.feastEffect.feastEffectHealAmount;
    }

    public static int getSustenanceEffectInterval() {
        return config().effects.sustenanceEffect.sustenanceEffectInterval;
    }

    public static int getSustenanceEffectHealAmount() {
        return config().effects.sustenanceEffect.sustenanceEffectHealAmount;
    }

    public static int getSustenanceEffectFoodIncrement() {
        return config().effects.sustenanceEffect.sustenanceEffectFoodIncrement;
    }

    public static int getSatiationEffectInterval() {
        return config().effects.satiationEffect.satiationEffectInterval;
    }

    public static int getSatiationEffectHealAmount() {
        return config().effects.satiationEffect.satiationEffectHealAmount;
    }

    public static int getNutrition(String itemName) {
        FarmAndCharmFabricConfig.FoodSettings food = config().food;
        return switch (itemName) {
            case "oat_pancake" -> food.oatPancakeNutrition;
            case "roasted_corn" -> food.roastedCornNutrition;
            case "potato_with_roast_meat" -> food.potatoWithRoastMeatNutrition;
            case "baked_lamb_ham" -> food.bakedLambHamNutrition;
            case "farmers_breakfast" -> food.farmersBreakfastNutrition;
            case "stuffed_chicken" -> food.stuffedChickenNutrition;
            case "stuffed_rabbit" -> food.stuffedRabbitNutrition;
            case "grandmothers_strawberry_cake" -> food.grandmothersStrawberryCakeNutrition;
            case "farmers_bread" -> food.farmersBreadNutrition;
            case "farmer_salad" -> food.farmerSaladNutrition;
            case "goulash" -> food.goulashNutrition;
            case "simple_tomato_soup" -> food.simpleTomatoSoupNutrition;
            case "barley_soup" -> food.barleySoupNutrition;
            case "onion_soup" -> food.onionSoupNutrition;
            case "potato_soup" -> food.potatoSoupNutrition;
            case "pasta_with_onion_sauce" -> food.pastaWithOnionSauceNutrition;
            case "corn_grits" -> food.cornGritsNutrition;
            case "oatmeal_with_strawberries" -> food.oatmealWithStrawberriesNutrition;
            case "sausage_with_oat_patty" -> food.sausageWithOatPattyNutrition;
            case "lamb_with_corn" -> food.lambWithCornNutrition;
            case "beef_patty_with_vegetables" -> food.beefPattyWithVegetablesNutrition;
            case "barley_patties_with_potatoes" -> food.barleyPattiesWithPotatoesNutrition;
            case "bacon_with_eggs" -> food.baconWithEggsNutrition;
            case "chicken_wrapped_in_bacon" -> food.chickenWrappedInBaconNutrition;
            case "cooked_salmon" -> food.cookedSalmonNutrition;
            case "cooked_cod" -> food.cookedCodNutrition;
            case "roasted_chicken" -> food.roastedChickenNutrition;
            default -> 0;
        };
    }

    public static float getSaturationMod(String itemName) {
        FarmAndCharmFabricConfig.FoodSettings food = config().food;
        return switch (itemName) {
            case "oat_pancake" -> food.oatPancakeSaturationMod;
            case "roasted_corn" -> food.roastedCornSaturationMod;
            case "potato_with_roast_meat" -> food.potatoWithRoastMeatSaturationMod;
            case "baked_lamb_ham" -> food.bakedLambHamSaturationMod;
            case "farmers_breakfast" -> food.farmersBreakfastSaturationMod;
            case "stuffed_chicken" -> food.stuffedChickenSaturationMod;
            case "stuffed_rabbit" -> food.stuffedRabbitSaturationMod;
            case "grandmothers_strawberry_cake" -> food.grandmothersStrawberryCakeSaturationMod;
            case "farmers_bread" -> food.farmersBreadSaturationMod;
            case "farmer_salad" -> food.farmerSaladSaturationMod;
            case "goulash" -> food.goulashSaturationMod;
            case "simple_tomato_soup" -> food.simpleTomatoSoupSaturationMod;
            case "barley_soup" -> food.barleySoupSaturationMod;
            case "onion_soup" -> food.onionSoupSaturationMod;
            case "potato_soup" -> food.potatoSoupSaturationMod;
            case "pasta_with_onion_sauce" -> food.pastaWithOnionSauceSaturationMod;
            case "corn_grits" -> food.cornGritsSaturationMod;
            case "oatmeal_with_strawberries" -> food.oatmealWithStrawberriesSaturationMod;
            case "sausage_with_oat_patty" -> food.sausageWithOatPattySaturationMod;
            case "lamb_with_corn" -> food.lambWithCornSaturationMod;
            case "beef_patty_with_vegetables" -> food.beefPattyWithVegetablesSaturationMod;
            case "barley_patties_with_potatoes" -> food.barleyPattiesWithPotatoesSaturationMod;
            case "bacon_with_eggs" -> food.baconWithEggsSaturationMod;
            case "chicken_wrapped_in_bacon" -> food.chickenWrappedInBaconSaturationMod;
            case "cooked_salmon" -> food.cookedSalmonSaturationMod;
            case "cooked_cod" -> food.cookedCodSaturationMod;
            case "roasted_chicken" -> food.roastedChickenSaturationMod;
            default -> 0.0f;
        };
    }

    public static boolean isFirefliesEnabled() {
        return config().farming.enableFireflies;
    }
}

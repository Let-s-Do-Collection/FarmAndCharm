package net.satisfy.farm_and_charm.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

public abstract class PlatformHelper {
    @ExpectPlatform
    public static boolean isBonemealEffectEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getWaterSprinklerRange() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isRainGrowthEffectEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static float getRainGrowthMultiplier() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getFeedingTroughRange() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getFertilizedSoilRange() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getNutrition(String itemName) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static float getSaturationMod(String itemName) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isTamingEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isHorseTamingEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isHorseEffectsEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isChickenEffectsEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean enableCatTamingChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isFertilizerEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getChickenEffectTickInterval() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getChickenEffectEggChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getChickenEffectFeatherChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getFeastEffectSatiationInterval() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getFeastEffectSustenanceInterval() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getFeastEffectHealAmount() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getSustenanceEffectInterval() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getSustenanceEffectHealAmount() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getSustenanceEffectFoodIncrement() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getSatiationEffectInterval() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getSatiationEffectHealAmount() {
        throw new AssertionError();
    }
}
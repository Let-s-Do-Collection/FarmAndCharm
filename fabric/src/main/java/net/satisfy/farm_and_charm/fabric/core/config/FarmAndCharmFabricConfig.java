package net.satisfy.farm_and_charm.fabric.core.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "farm_and_charm")
@Config.Gui.Background("farm_and_charm:textures/block/fertilized_farmland_top.png")
public class FarmAndCharmFabricConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public BlocksSettings blocks = new BlocksSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public ItemsSettings items = new ItemsSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public EffectsSettings effects = new EffectsSettings();

    public static class BlocksSettings {
        public boolean enableBonemealEffect = true;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int fertilizedSoilRange = 5;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 32)
        public int waterSprinklerRange = 8;

        public boolean enableRainGrowthEffect = true;

        public float rainGrowthMultiplier = 0.5f;

        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int feedingTroughRange = 8;

        public void validate() {
            if (rainGrowthMultiplier < 0.1f) rainGrowthMultiplier = 0.1f;
            if (rainGrowthMultiplier > 2.0f) rainGrowthMultiplier = 2.0f;
        }
    }

    public static class ItemsSettings {
        public boolean enableFertilizer = true;
        public boolean enableTaming = true;
        public boolean enableHorseTaming = true;
        public boolean enableHorseEffects = true;
        public boolean enableChickenEffects = true;
        public boolean enableCatTamingChance = true;

        @ConfigEntry.Gui.CollapsibleObject
        public NutritionSettings nutrition = new NutritionSettings();

        public static class NutritionSettings {
            public int oatPancakeNutrition = 5;
            public float oatPancakeSaturationMod = 0.6f;
            public int roastedCornNutrition = 5;
            public float roastedCornSaturationMod = 0.5f;
            public int potatoWithRoastMeatNutrition = 7;
            public float potatoWithRoastMeatSaturationMod = 0.7f;
            public int bakedLambHamNutrition = 8;
            public float bakedLambHamSaturationMod = 0.9f;
            public int farmersBreakfastNutrition = 12;
            public float farmersBreakfastSaturationMod = 1.2f;
            public int stuffedChickenNutrition = 8;
            public float stuffedChickenSaturationMod = 0.8f;
            public int stuffedRabbitNutrition = 9;
            public float stuffedRabbitSaturationMod = 0.9f;
            public int grandmothersStrawberryCakeNutrition = 4;
            public float grandmothersStrawberryCakeSaturationMod = 0.7f;
            public int farmersBreadNutrition = 6;
            public float farmersBreadSaturationMod = 0.8f;
            public int farmerSaladNutrition = 7;
            public float farmerSaladSaturationMod = 0.6f;
            public int goulashNutrition = 8;
            public float goulashSaturationMod = 0.9f;
            public int simpleTomatoSoupNutrition = 6;
            public float simpleTomatoSoupSaturationMod = 0.6f;
            public int barleySoupNutrition = 5;
            public float barleySoupSaturationMod = 0.8f;
            public int onionSoupNutrition = 7;
            public float onionSoupSaturationMod = 0.6f;
            public int potatoSoupNutrition = 5;
            public float potatoSoupSaturationMod = 0.6f;
            public int pastaWithOnionSauceNutrition = 6;
            public float pastaWithOnionSauceSaturationMod = 0.7f;
            public int cornGritsNutrition = 6;
            public float cornGritsSaturationMod = 0.5f;
            public int oatmealWithStrawberriesNutrition = 4;
            public float oatmealWithStrawberriesSaturationMod = 0.8f;
            public int sausageWithOatPattyNutrition = 8;
            public float sausageWithOatPattySaturationMod = 0.9f;
            public int lambWithCornNutrition = 8;
            public float lambWithCornSaturationMod = 0.8f;
            public int beefPattyWithVegetablesNutrition = 6;
            public float beefPattyWithVegetablesSaturationMod = 0.8f;
            public int barleyPattiesWithPotatoesNutrition = 5;
            public float barleyPattiesWithPotatoesSaturationMod = 0.9f;
            public int baconWithEggsNutrition = 6;
            public float baconWithEggsSaturationMod = 0.7f;
            public int chickenWrappedInBaconNutrition = 9;
            public float chickenWrappedInBaconSaturationMod = 0.9f;
            public int cookedSalmonNutrition = 7;
            public float cookedSalmonSaturationMod = 0.9f;
            public int cookedCodNutrition = 7;
            public float cookedCodSaturationMod = 0.9f;
            public int roastedChickenNutrition = 5;
            public float roastedChickenSaturationMod = 0.8f;
        }
    }

    public static class EffectsSettings {

        @ConfigEntry.Gui.CollapsibleObject
        public ChickenEffectSettings chickenEffect = new ChickenEffectSettings();
        @ConfigEntry.Gui.CollapsibleObject
        public FeastEffectSettings feastEffect = new FeastEffectSettings();
        @ConfigEntry.Gui.CollapsibleObject
        public SustenanceEffectsSettings sustenanceEffect = new SustenanceEffectsSettings();
        @ConfigEntry.Gui.CollapsibleObject
        public SatiationEffectSettings satiationEffect = new SatiationEffectSettings();

        public static class ChickenEffectSettings {
            @ConfigEntry.BoundedDiscrete(min = 10, max = 600)
            public int chickenEffectTickInterval = 120;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
            public int chickenEffectEggChance = 20;

            @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
            public int chickenEffectFeatherChance = 20;
        }

        public static class FeastEffectSettings {
            @ConfigEntry.BoundedDiscrete(min = 10, max = 200)
            public int feastEffectSatiationInterval = 40;

            @ConfigEntry.BoundedDiscrete(min = 50, max = 600)
            public int feastEffectSustenanceInterval = 200;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
            public int feastEffectHealAmount = 1;
        }

        public static class SustenanceEffectsSettings {
            @ConfigEntry.BoundedDiscrete(min = 50, max = 600)
            public int sustenanceEffectInterval = 200;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
            public int sustenanceEffectHealAmount = 1;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
            public int sustenanceEffectFoodIncrement = 1;
        }

        public static class SatiationEffectSettings {
            @ConfigEntry.BoundedDiscrete(min = 10, max = 200)
            public int satiationEffectInterval = 40;

            @ConfigEntry.BoundedDiscrete(min = 1, max = 5)
            public int satiationEffectHealAmount = 1;
        }
    }


    @Override
    public void validatePostLoad() {
        blocks.validate();
    }
}
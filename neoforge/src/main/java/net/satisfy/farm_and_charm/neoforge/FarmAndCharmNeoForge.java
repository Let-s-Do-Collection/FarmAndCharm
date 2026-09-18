package net.satisfy.farm_and_charm.neoforge;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import dev.architectury.registry.registries.RegistrySupplier;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.neoforge.core.config.FarmAndCharmNeoForgeConfig;
import net.satisfy.farm_and_charm.platform.neoforge.PlatformHelperImpl;

import java.util.Objects;

@Mod(FarmAndCharm.MOD_ID)
public class FarmAndCharmNeoForge {

    public FarmAndCharmNeoForge(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, FarmAndCharmNeoForgeConfig.COMMON_CONFIG);
        Objects.requireNonNull(modContainer.getEventBus()).addListener(FarmAndCharmNeoForgeConfig::onLoad);
        modContainer.getEventBus().addListener(FarmAndCharmNeoForgeConfig::onReload);
        modContainer.getEventBus().addListener(FarmAndCharmNeoForge::reapplyFoodConfig);
        FarmAndCharm.init();
    }

    /**
     * Item registration (RegisterEvent) runs before ModConfigEvent.Loading on NeoForge, so the
     * food items in ObjectRegistry are built with whatever nutrition/saturation values were
     * available at that point (see PlatformHelperImpl.getNutrition/getSaturationMod fallback).
     * This event fires after the config has actually loaded, so re-patch the FOOD component here
     * with the real configured values.
     */
    private static void reapplyFoodConfig(ModifyDefaultComponentsEvent event) {
        patchFood(event, ObjectRegistry.OAT_PANCAKE, "oat_pancake");
        patchFood(event, ObjectRegistry.ROASTED_CORN, "roasted_corn");
        patchFood(event, ObjectRegistry.POTATO_WITH_ROAST_MEAT_ITEM, "potato_with_roast_meat");
        patchFood(event, ObjectRegistry.BAKED_LAMB_HAM_ITEM, "baked_lamb_ham");
        patchFood(event, ObjectRegistry.FARMERS_BREAKFAST_ITEM, "farmers_breakfast");
        patchFood(event, ObjectRegistry.STUFFED_CHICKEN_ITEM, "stuffed_chicken");
        patchFood(event, ObjectRegistry.STUFFED_RABBIT_ITEM, "stuffed_rabbit");
        patchFood(event, ObjectRegistry.GRANDMOTHERS_STRAWBERRY_CAKE_ITEM, "grandmothers_strawberry_cake");
        patchFood(event, ObjectRegistry.FARMERS_BREAD_ITEM, "farmers_bread");
        patchFood(event, ObjectRegistry.FARMER_SALAD, "farmer_salad");
        patchFood(event, ObjectRegistry.GOULASH, "goulash");
        patchFood(event, ObjectRegistry.SIMPLE_TOMATO_SOUP, "simple_tomato_soup");
        patchFood(event, ObjectRegistry.BARLEY_SOUP, "barley_soup");
        patchFood(event, ObjectRegistry.ONION_SOUP, "onion_soup");
        patchFood(event, ObjectRegistry.POTATO_SOUP, "potato_soup");
        patchFood(event, ObjectRegistry.PASTA_WITH_ONION_SAUCE, "pasta_with_onion_sauce");
        patchFood(event, ObjectRegistry.CORN_GRITS, "corn_grits");
        patchFood(event, ObjectRegistry.OATMEAL_WITH_STRAWBERRIES, "oatmeal_with_strawberries");
        patchFood(event, ObjectRegistry.SAUSAGE_WITH_OAT_PATTY, "sausage_with_oat_patty");
        patchFood(event, ObjectRegistry.LAMB_WITH_CORN, "lamb_with_corn");
        patchFood(event, ObjectRegistry.BEEF_PATTY_WITH_VEGETABLES, "beef_patty_with_vegetables");
        patchFood(event, ObjectRegistry.BARLEY_PATTIES_WITH_POTATOES, "barley_patties_with_potatoes");
        patchFood(event, ObjectRegistry.BACON_WITH_EGGS, "bacon_with_eggs");
        patchFood(event, ObjectRegistry.CHICKEN_WRAPPED_IN_BACON, "chicken_wrapped_in_bacon");
        patchFood(event, ObjectRegistry.COOKED_SALMON, "cooked_salmon");
        patchFood(event, ObjectRegistry.COOKED_COD, "cooked_cod");
        patchFood(event, ObjectRegistry.ROASTED_CHICKEN, "roasted_chicken");
    }

    private static void patchFood(ModifyDefaultComponentsEvent event, RegistrySupplier<Item> itemSupplier, String key) {
        Item item = itemSupplier.get();
        FoodProperties current = item.components().get(DataComponents.FOOD);
        if (current == null) return;

        FoodProperties updated = new FoodProperties(
                PlatformHelperImpl.getNutrition(key),
                PlatformHelperImpl.getSaturationMod(key),
                current.canAlwaysEat(),
                current.eatSeconds(),
                current.usingConvertsTo(),
                current.effects()
        );
        event.modify(item, builder -> builder.set(DataComponents.FOOD, updated));
    }
}

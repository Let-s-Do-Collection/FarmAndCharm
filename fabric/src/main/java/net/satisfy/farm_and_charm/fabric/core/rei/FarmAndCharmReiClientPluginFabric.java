package net.satisfy.farm_and_charm.fabric.core.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import net.satisfy.farm_and_charm.core.compat.rei.FarmAndCharmREIClientPlugin;


public class FarmAndCharmReiClientPluginFabric implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        FarmAndCharmREIClientPlugin.registerCategories(registry);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        FarmAndCharmREIClientPlugin.registerDisplays(registry);
    }
}

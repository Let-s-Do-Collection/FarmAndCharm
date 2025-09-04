package net.satisfy.farm_and_charm.neoforge.core.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
import net.satisfy.farm_and_charm.core.compat.rei.Farm_And_CharmREIClientPlugin;

@REIPluginClient
@SuppressWarnings("unused")
public class Farm_And_CharmREIClientPluginForge implements REIClientPlugin {
    private final Farm_And_CharmREIClientPlugin delegate = new Farm_And_CharmREIClientPlugin();

    @Override
    public void registerCategories(CategoryRegistry registry) {
        delegate.registerCategories(registry);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        delegate.registerDisplays(registry);
    }
}

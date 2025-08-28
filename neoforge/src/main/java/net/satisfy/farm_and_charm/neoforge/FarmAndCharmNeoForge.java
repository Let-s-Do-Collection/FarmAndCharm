package net.satisfy.farm_and_charm.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.registry.CompostableRegistry;
import net.satisfy.farm_and_charm.neoforge.config.FarmAndCharmNeoForgeConfig;

@Mod(FarmAndCharm.MOD_ID)
public class FarmAndCharmNeoForge {

    public FarmAndCharmNeoForge(IEventBus modBus, ModContainer modContainer) {
        FarmAndCharm.init();
        modContainer.registerConfig(ModConfig.Type.COMMON, FarmAndCharmNeoForgeConfig.COMMON_CONFIG);
        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CompostableRegistry::registerCompostable);
    }
}

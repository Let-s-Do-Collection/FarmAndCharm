package net.satisfy.farm_and_charm.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.registry.CompostableRegistry;

@Mod(FarmAndCharm.MOD_ID)
public class FarmAndCharmNeoForge {

    public FarmAndCharmNeoForge(IEventBus modBus) {
        FarmAndCharm.init();
        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CompostableRegistry::registerCompostable);
    }
}

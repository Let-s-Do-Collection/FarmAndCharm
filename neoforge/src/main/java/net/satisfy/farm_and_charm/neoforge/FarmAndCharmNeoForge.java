package net.satisfy.farm_and_charm.neoforge;

import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.registry.CompostableRegistry;
import net.satisfy.farm_and_charm.neoforge.config.FarmAndCharmForgeConfig;

@Mod(FarmAndCharm.MOD_ID)
public class FarmAndCharmNeoForge {

    public FarmAndCharmNeoForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(FarmAndCharm.MOD_ID, modEventBus);
        FarmAndCharm.init();
        EffectRegisterImpl.MOB_EFFECTS.register(modBus);
        FarmAndCharmForgeConfig.loadConfig(FarmAndCharmForgeConfig.COMMON_CONFIG, Platform.getConfigFolder().resolve("farmandcharm.toml").toString());
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(CompostableRegistry::registerCompostable);
    }
}

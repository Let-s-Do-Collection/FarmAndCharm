package net.satisfy.farm_and_charm.neoforge;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.neoforge.core.config.FarmAndCharmNeoForgeConfig;

@Mod(FarmAndCharm.MOD_ID)
public class FarmAndCharmNeoForge {

    public FarmAndCharmNeoForge(ModContainer modContainer) {
        FarmAndCharm.init();
        modContainer.registerConfig(ModConfig.Type.COMMON, FarmAndCharmNeoForgeConfig.COMMON_CONFIG);
    }

}

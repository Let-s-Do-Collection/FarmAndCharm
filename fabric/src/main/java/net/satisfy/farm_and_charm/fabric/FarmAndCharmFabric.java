package net.satisfy.farm_and_charm.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.fabric.core.registry.CompostableRegistry;
import net.satisfy.farm_and_charm.fabric.core.config.FarmAndCharmFabricConfig;
import net.satisfy.farm_and_charm.fabric.core.world.FarmAndCharmBiomeModification;

public class FarmAndCharmFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AutoConfig.register(FarmAndCharmFabricConfig.class, GsonConfigSerializer::new);
        FarmAndCharm.init();
        CompostableRegistry.registerCompostable();
        FarmAndCharmBiomeModification.init();
        FabricLoader.getInstance().getModContainer(FarmAndCharm.MOD_ID).ifPresent(container ->
                ResourceManagerHelper.registerBuiltinResourcePack(
                        ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "vanilla_blend"),
                        container,
                        Component.translatable("pack.farm_and_charm.vanilla_blend"),
                        ResourcePackActivationType.NORMAL));
    }
}

package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.client.resources.SplashManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.satisfy.farm_and_charm.FarmAndCharm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(SplashManager.class)
public abstract class SplashManagerMixin {
    @Inject(method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    private void farm_and_charm$addSplashes(ResourceManager manager, ProfilerFiller profiler, CallbackInfoReturnable<List<String>> cir) {
        Optional<Resource> resource = manager.getResource(FarmAndCharm.identifier("texts/splashes.txt"));
        if (resource.isEmpty()) {
            return;
        }
        List<String> splashes = new ArrayList<>(cir.getReturnValue());
        try (BufferedReader reader = resource.get().openAsReader()) {
            reader.lines().map(String::trim).filter(line -> !line.isEmpty()).forEach(splashes::add);
        } catch (IOException ignored) {
            return;
        }
        cir.setReturnValue(splashes);
    }
}

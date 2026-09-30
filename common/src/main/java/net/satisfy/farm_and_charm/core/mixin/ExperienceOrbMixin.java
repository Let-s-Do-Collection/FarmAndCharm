package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.satisfy.farm_and_charm.core.registry.MobEffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
    @ModifyArg(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;repairPlayerItems(Lnet/minecraft/server/level/ServerPlayer;I)I"), index = 1)
    private int boostRestedExperience(ServerPlayer player, int experience) {
        MobEffectInstance rested = player.getEffect(MobEffectRegistry.getHolder(MobEffectRegistry.RESTED));
        if (rested == null) {
            return experience;
        }
        return (int) (experience + experience * (1 + rested.getAmplifier()) * 0.5);
    }
}

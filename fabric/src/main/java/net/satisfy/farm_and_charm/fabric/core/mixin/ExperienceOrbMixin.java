package net.satisfy.farm_and_charm.fabric.core.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.satisfy.farm_and_charm.core.registry.MobEffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Objects;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {


    @ModifyArgs(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;repairPlayerItems(Lnet/minecraft/server/level/ServerPlayer;I)I"))
    public void render(Args args) {
        Player player = args.get(0);
        var restedHolder = MobEffectRegistry.getHolder(MobEffectRegistry.RESTED);
        MobEffectInstance effectInstance = player.getEffect(restedHolder);
        if (effectInstance == null) return;

        int amplifier = effectInstance.getAmplifier();
        int originalXp = (int) args.get(1);
        int boostedXp = (int) (originalXp + (originalXp * (1 + amplifier) * 0.5));
        args.set(1, boostedXp);
    }

}

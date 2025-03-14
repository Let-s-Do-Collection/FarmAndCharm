package net.satisfy.farm_and_charm.fabric.core.mixin;

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
        Player p = args.get(0);
        if (p.hasEffect(MobEffectRegistry.RESTED)) {
            int amplifier = Objects.requireNonNull(p.getEffect(MobEffectRegistry.RESTED)).getAmplifier();
            int i = args.get(1);

            int xp = (int) (i + (i * (1 + amplifier) * 0.5));

            args.set(1, xp);
        }
    }

}
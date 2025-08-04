package net.satisfy.farm_and_charm.core.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SatiationEffect extends MobEffect {
    public SatiationEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player) {
            if (player.getFoodData().needsFood() || player.hasEffect(MobEffects.REGENERATION) || player.getFoodData().getSaturationLevel() <= 0f) {
                return false;
            }
            player.heal(1.0F + amplifier);
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return i % 40 == 0;
    }
}

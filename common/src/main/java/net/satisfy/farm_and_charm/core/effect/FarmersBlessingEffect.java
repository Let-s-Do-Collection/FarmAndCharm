package net.satisfy.farm_and_charm.core.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FarmersBlessingEffect extends MobEffect {
    public FarmersBlessingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.getActiveEffectsMap().forEach((effect, instance) -> {
            if (effect.value().getCategory() == MobEffectCategory.HARMFUL) {
                entity.removeEffect(effect);
            }
        });
        return true;
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return true;
    }

    @Override
    public boolean isInstantenous() {
        return false;
    }
}

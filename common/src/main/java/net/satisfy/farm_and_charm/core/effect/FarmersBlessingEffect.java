package net.satisfy.farm_and_charm.core.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class FarmersBlessingEffect extends MobEffect {
    public FarmersBlessingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF228B22);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide()) {
            var toRemove = new java.util.ArrayList<Holder<MobEffect>>();
            entity.getActiveEffectsMap().forEach((effect, instance) -> {
                if (effect.value().getCategory() == MobEffectCategory.HARMFUL) {
                    toRemove.add(effect);
                }
            });
            for (var effect : toRemove) {
                entity.removeEffect(effect);
            }
        }
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

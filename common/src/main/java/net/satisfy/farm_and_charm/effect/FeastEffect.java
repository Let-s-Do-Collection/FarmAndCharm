package net.satisfy.farm_and_charm.effect;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;

import java.util.Objects;

public class FeastEffect extends MobEffect {

    private static final int SATIATION_INTERVAL = 40;
    private static final int SUSTENANCE_INTERVAL = 200;

    public FeastEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide() && entity instanceof Player player) {
            int duration = this.getDuration(entity, this);
            if (duration % SATIATION_INTERVAL == 0) {
                if (!player.getFoodData().needsFood() &&
                        !player.hasEffect(MobEffects.REGENERATION) &&
                        player.getFoodData().getSaturationLevel() > 0f) {
                    player.heal(1.0F + amplifier);
                }
            }

            if (duration % SUSTENANCE_INTERVAL == 0) {
                FoodData foodData = player.getFoodData();
                if (foodData.getFoodLevel() >= 20) {
                    player.heal(1.0F);
                } else {
                    foodData.setFoodLevel(Math.min(foodData.getFoodLevel() + 1, 20));
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % SATIATION_INTERVAL == 0 || duration % SUSTENANCE_INTERVAL == 0;
    }

    private int getDuration(LivingEntity entity, MobEffect effect) {
        return entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect)) != null ? Objects.requireNonNull(entity.getEffect(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect))).getDuration() : 0;
    }
}

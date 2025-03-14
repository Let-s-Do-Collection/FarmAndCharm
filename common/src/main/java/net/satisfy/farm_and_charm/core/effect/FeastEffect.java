package net.satisfy.farm_and_charm.core.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.GameRules;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

public class FeastEffect extends MobEffect {
    public FeastEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide() && entity instanceof Player player) {
            int satiationInterval = PlatformHelper.getFeastEffectSatiationInterval();
            int sustenanceInterval = PlatformHelper.getFeastEffectSustenanceInterval();
            int healAmount = PlatformHelper.getFeastEffectHealAmount();

            int duration = this.getDuration(player);
            boolean naturalRegeneration = player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
            long worldTime = player.level().getDayTime() % 24000;

            if (duration % satiationInterval == 0) {
                if (!player.getFoodData().needsFood() &&
                        !player.hasEffect(MobEffects.REGENERATION) &&
                        player.getFoodData().getSaturationLevel() > 0f &&
                        naturalRegeneration) {
                    player.heal(healAmount + amplifier);
                }
            }

            if (duration % sustenanceInterval == 0) {
                FoodData foodData = player.getFoodData();
                if (foodData.getFoodLevel() >= 20) {
                    if (naturalRegeneration) {
                        player.heal(healAmount);
                    }
                } else {
                    foodData.setFoodLevel(Math.min(foodData.getFoodLevel() + 1, 20));
                }
            }

            if (worldTime >= 10000 && worldTime <= 13000) {
                player.getFoodData().eat(6, 0.6f);
            }
        }
        return false;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int satiationInterval = PlatformHelper.getFeastEffectSatiationInterval();
        int sustenanceInterval = PlatformHelper.getFeastEffectSustenanceInterval();
        return duration % satiationInterval == 0 || duration % sustenanceInterval == 0;
    }

    private int getDuration(LivingEntity entity) {
        return entity.getActiveEffectsMap().entrySet().stream()
                .filter(entry -> entry.getKey().value().equals(this))
                .map(entry -> entry.getValue().getDuration())
                .findFirst()
                .orElse(0);
    }
}

package net.satisfy.farm_and_charm.core.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class SustenanceEffect extends MobEffect {
    private static final int INTERVAL = 200;

    public SustenanceEffect() {
        super(MobEffectCategory.BENEFICIAL, 0);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.getCommandSenderWorld().isClientSide && entity instanceof Player player) {
            if (entity.tickCount % INTERVAL == 0) {
                FoodData foodData = player.getFoodData();
                if (foodData.getFoodLevel() == 20) {
                    player.heal(1.0F);
                } else if (foodData.getFoodLevel() < 20) {
                    foodData.setFoodLevel(foodData.getFoodLevel() + 1);
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return i % INTERVAL == 0;
    }
}

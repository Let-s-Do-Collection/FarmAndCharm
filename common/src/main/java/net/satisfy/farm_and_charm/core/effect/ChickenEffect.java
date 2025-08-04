package net.satisfy.farm_and_charm.core.effect;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ChickenEffect extends MobEffect {
    private static final int TICK_INTERVAL = 120;

    public ChickenEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFFFF);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            if (entity.tickCount % TICK_INTERVAL == 0) {
                if (entity.level().random.nextFloat() < 0.2) {
                    ItemEntity eggEntity = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Items.EGG));
                    entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CHICKEN_EGG, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    entity.level().addFreshEntity(eggEntity);
                }
                if (entity.level().random.nextFloat() < 0.2) {
                    ItemEntity featherEntity = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Items.FEATHER));
                    entity.level().addFreshEntity(featherEntity);
                }
                if (entity instanceof Player && entity.level().random.nextFloat() < 0.1) {
                    entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.CHICKEN_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return true;
    }
}

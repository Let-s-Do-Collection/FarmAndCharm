package net.satisfy.farm_and_charm.core.effect;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.satisfy.farm_and_charm.FarmAndCharm;

public class SweetsEffect extends MobEffect {

    public static final ResourceLocation SPEED_MODIFIER_ID = FarmAndCharm.identifier("sweets_speed_modifier");
    public static final ResourceLocation ATTACK_SPEED_MODIFIER_ID = FarmAndCharm.identifier("sweets_attack_speed_modifier");
    public static final ResourceLocation ATTACK_DAMAGE_MODIFIER_ID = FarmAndCharm.identifier("sweets_attack_damage_modifier");

    public SweetsEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFFC0CB);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            return false;
        }

        int stacks = Math.min(10, Math.max(1, amplifier + 1));
        double percentIncrease = 0.03D * stacks;

        applyModifier(entity, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_ID, percentIncrease);
        applyModifier(entity, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_ID, percentIncrease);
        applyModifier(entity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER_ID, percentIncrease);

        return true;
    }

    private void applyModifier(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id, double percentIncrease) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return;
        }

        AttributeModifier existingModifier = instance.getModifier(id);
        if (existingModifier != null) {
            instance.removeModifier(existingModifier);
        }

        if (!Double.isFinite(percentIncrease) || percentIncrease <= 0.0D) {
            return;
        }

        double amount = attribute.value().getDefaultValue() * percentIncrease;
        if (!Double.isFinite(amount) || amount <= 0.0D) {
            return;
        }

        instance.addTransientModifier(
                new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE)
        );
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
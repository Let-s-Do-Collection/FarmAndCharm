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
    public static final ResourceLocation ATTACK_DAMAGE_MODIFIER_ID = FarmAndCharm.identifier("sweets_attack_damage_modifier");
    public static final ResourceLocation ATTACK_SPEED_MODIFIER_ID = FarmAndCharm.identifier("sweets_attack_speed_modifier");

    public SweetsEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFFC0CB);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (!livingEntity.level().isClientSide) {
            double percentIncrease = 0.02 * (amplifier + 1);
            percentIncrease = Math.min(percentIncrease, 0.3);

            applyModifier(livingEntity, Attributes.MOVEMENT_SPEED, SPEED_MODIFIER_ID, percentIncrease, "Farm_And_Charm speed boost");
            applyModifier(livingEntity, Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_ID, percentIncrease, "Farm_And_Charm attack speed boost");
            applyModifier(livingEntity, Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER_ID, percentIncrease, "Farm_And_Charm attack damage boost");
        }
        return true;
    }

    private void applyModifier(LivingEntity entity, Holder<Attribute> attribute, ResourceLocation id, double percentIncrease, String name) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);

        if (attributeInstance != null) {
            AttributeModifier modifier = attributeInstance.getModifier(id);
            if (modifier != null) {
                attributeInstance.removeModifier(modifier);
            }
            double increase = attribute.value().getDefaultValue() * percentIncrease;
            attributeInstance.addTransientModifier(new AttributeModifier(id, increase, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return true;
    }
}

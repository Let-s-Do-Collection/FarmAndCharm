package net.satisfy.farm_and_charm.core.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

public class DogFoodEffect extends MobEffect {
    private static final ResourceLocation KNOCKBACK_RESISTANCE_MODIFIER_ID = FarmAndCharmIdentifier.of("dog_food_knockback_resistance_modifier");
    private static final ResourceLocation ATTACK_DAMAGE_MODIFIER_ID = FarmAndCharmIdentifier.of("dog_food_attack_damage_modifier");
    private static final ResourceLocation ATTACK_SPEED_MODIFIER_ID = FarmAndCharmIdentifier.of("dog_food_attack_speed_modifier");

    public DogFoodEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFFFF);
        this.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_MODIFIER_ID, 1.5F, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER_ID, 1.0F, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_ID, 1.5F, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return true;
    }
}
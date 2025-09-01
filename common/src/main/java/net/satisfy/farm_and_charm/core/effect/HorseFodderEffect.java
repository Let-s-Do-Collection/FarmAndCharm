package net.satisfy.farm_and_charm.core.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.satisfy.farm_and_charm.FarmAndCharm;

public class HorseFodderEffect extends MobEffect {
    private static final ResourceLocation JUMP_STRENGTH_MODIFIER_ID = FarmAndCharm.identifier("horse_fodder_jump_strength_modifier");
    private static final ResourceLocation MOVEMENT_SPEED_MODIFIER_ID = FarmAndCharm.identifier("horse_fodder_movement_speed_modifier");


    public HorseFodderEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF8B4513);
        this.addAttributeModifier(Attributes.JUMP_STRENGTH, JUMP_STRENGTH_MODIFIER_ID, 0.1F, AttributeModifier.Operation.ADD_VALUE);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_MODIFIER_ID, 0.1F, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int i, int j) {
        return true;
    }
}
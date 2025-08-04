package net.satisfy.farm_and_charm.core.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

public class GrandmasBlessingEffect extends MobEffect {
    private static final ResourceLocation LUCK_MODIFIER_ID = FarmAndCharmIdentifier.of("grandmas_blessing_luck_modifier");
    private static final AttributeModifier LUCK_MODIFIER = new AttributeModifier(LUCK_MODIFIER_ID, 2, AttributeModifier.Operation.ADD_VALUE);

    public GrandmasBlessingEffect() {
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
    public void addAttributeModifiers(AttributeMap attributeMap, int i) {
        super.addAttributeModifiers(attributeMap, i);
        AttributeInstance luckAttribute = attributeMap.getInstance(Attributes.LUCK);
        if (luckAttribute != null && !luckAttribute.hasModifier(LUCK_MODIFIER_ID)) {
            luckAttribute.addPermanentModifier(LUCK_MODIFIER);
        }

    }

    @Override
    public boolean isInstantenous() {
        return false;
    }
}

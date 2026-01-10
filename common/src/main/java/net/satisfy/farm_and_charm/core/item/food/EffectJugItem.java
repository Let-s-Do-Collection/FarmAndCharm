package net.satisfy.farm_and_charm.core.item.food;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class EffectJugItem extends Item {
    private final boolean returnBottle;

    public EffectJugItem(Properties properties, int duration, boolean returnBottle) {
        super(properties);
        this.returnBottle = returnBottle;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity) {
        ItemStack eaten = livingEntity.eat(level, itemStack);
        if (this.returnBottle) {
            return GeneralUtil.convertStackAfterFinishUsing(livingEntity, eaten, Items.GLASS_BOTTLE, this);
        }
        return eaten;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, tooltip, tooltipFlag);

        List<FoodProperties.PossibleEffect> possibleEffects = itemStack.has(DataComponents.FOOD)
                ? Objects.requireNonNull(itemStack.get(DataComponents.FOOD)).effects()
                : Lists.newArrayList();

        List<Pair<Holder<Attribute>, AttributeModifier>> attributeModifiers = Lists.newArrayList();

        if (possibleEffects.isEmpty()) {
            tooltip.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            for (FoodProperties.PossibleEffect possibleEffect : possibleEffects) {
                MutableComponent effectLine = Component.translatable(possibleEffect.effect().getDescriptionId());
                MobEffect mobEffect = possibleEffect.effect().getEffect().value();

                mobEffect.createModifiers(possibleEffect.effect().getAmplifier(), (attributeHolder, baseModifier) -> {
                    AttributeModifier scaledModifier = new AttributeModifier(
                            baseModifier.id(),
                            baseModifier.amount() * (double) (possibleEffect.effect().getAmplifier() + 1),
                            baseModifier.operation()
                    );
                    attributeModifiers.add(new Pair<>(attributeHolder, scaledModifier));
                });

                if (possibleEffect.effect().getDuration() > 20) {
                    effectLine = Component.translatable(
                            "potion.withDuration",
                            effectLine,
                            MobEffectUtil.formatDuration(possibleEffect.effect(), possibleEffect.probability(), tooltipContext.tickRate())
                    );
                }

                tooltip.add(effectLine.withStyle(mobEffect.getCategory().getTooltipFormatting()));
            }
        }

        if (!attributeModifiers.isEmpty()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

            for (Pair<Holder<Attribute>, AttributeModifier> pair : attributeModifiers) {
                AttributeModifier modifier = pair.getSecond();
                double amount = modifier.amount();
                double displayValue;

                if (modifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && modifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    displayValue = amount;
                } else {
                    displayValue = amount * 100.0;
                }

                if (amount > 0.0) {
                    tooltip.add(Component.translatable(
                            "attribute.modifier.plus." + modifier.operation().id(),
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(displayValue),
                            Component.translatable(pair.getFirst().value().getDescriptionId())
                    ).withStyle(ChatFormatting.BLUE));
                } else if (amount < 0.0) {
                    displayValue *= -1.0;
                    tooltip.add(Component.translatable(
                            "attribute.modifier.take." + modifier.operation().id(),
                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(displayValue),
                            Component.translatable(pair.getFirst().value().getDescriptionId())
                    ).withStyle(ChatFormatting.RED));
                }
            }
        }
    }
}

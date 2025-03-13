package net.satisfy.farm_and_charm.core.item;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TeaJugItem extends BlockItem {
    public TeaJugItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        List<FoodProperties.PossibleEffect> list2 = itemStack.has(DataComponents.FOOD) ? itemStack.get(DataComponents.FOOD).effects() : Lists.newArrayList();
        List<Pair<Holder<Attribute>, AttributeModifier>> list3 = Lists.newArrayList();
        if (list2.isEmpty()) {
            tooltip.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            for (FoodProperties.PossibleEffect statusEffectInstance : list2) {
                MutableComponent mutableText = Component.translatable(statusEffectInstance.effect().getDescriptionId());
                MobEffect statusEffect = statusEffectInstance.effect().getEffect().value();

                statusEffect.createModifiers(statusEffectInstance.effect().getAmplifier(), (holderx, attributeModifierx) -> {
                    AttributeModifier entityAttributeModifier = new AttributeModifier(
                            attributeModifierx.id(),
                            attributeModifierx.amount() * (double)(statusEffectInstance.effect().getAmplifier() + 1),
                            attributeModifierx.operation()
                    );
                    list3.add(new Pair<>(holderx, entityAttributeModifier));
                });

                if (statusEffectInstance.effect().getDuration() > 20) {
                    mutableText = Component.translatable(
                            "potion.withDuration",
                            mutableText, MobEffectUtil.formatDuration(statusEffectInstance.effect(), statusEffectInstance.probability(), tooltipContext.tickRate()));
                }

                tooltip.add(mutableText.withStyle(statusEffect.getCategory().getTooltipFormatting()));
            }
        }

        if (!list3.isEmpty()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

            for (Pair<Holder<Attribute>, AttributeModifier> pair : list3) {
                AttributeModifier entityAttributeModifier3 = pair.getSecond();
                double d = entityAttributeModifier3.amount();
                double e;
                if (entityAttributeModifier3.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && entityAttributeModifier3.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    e = entityAttributeModifier3.amount();
                } else {
                    e = entityAttributeModifier3.amount() * 100.0;
                }

                if (d > 0.0) {
                    tooltip.add(
                            Component.translatable(
                                            "attribute.modifier.plus." + entityAttributeModifier3.operation().id(),
                                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable(pair.getFirst().value().getDescriptionId()))
                                    .withStyle(ChatFormatting.BLUE)
                    );
                } else if (d < 0.0) {
                    e *= -1.0;
                    tooltip.add(
                            Component.translatable(
                                            "attribute.modifier.take." + entityAttributeModifier3.operation().id(),
                                            ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable(pair.getFirst().value().getDescriptionId()))
                                    .withStyle(ChatFormatting.RED)
                    );
                }
            }
        }
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player != null && player.isShiftKeyDown()) {
            return super.place(context);
        }
        return InteractionResult.PASS;
    }
}
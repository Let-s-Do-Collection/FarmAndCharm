package net.satisfy.farm_and_charm.core.item.food;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public class EffectBlockItem extends BlockItem {
    public EffectBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    private static final DecimalFormat ATTRIBUTE_MODIFIER_FORMAT = Util.make(
            new DecimalFormat("#.##"), var0 -> var0.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT))
    );

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        Optional<FoodProperties> foodProperties = Optional.ofNullable(itemStack.get(DataComponents.FOOD));
        List<FoodProperties.PossibleEffect> effects = foodProperties.map(FoodProperties::effects).orElseGet(List::of);
        List<Pair<Attribute, AttributeModifier>> attributes = Lists.newArrayList();
        if (effects.isEmpty()) {
            tooltip.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            effects.forEach(chance -> {
                MutableComponent mutableText = Component.translatable(chance.effect().getDescriptionId());
                MobEffect statusEffect = chance.effect().getEffect().value();
                statusEffect.createModifiers(chance.effect().getAmplifier(), (holder, modifier) -> {
                    attributes.add(new Pair<>(holder.value(), modifier));
                });
                if (chance.effect().getDuration() > 20) {
                    mutableText = Component.translatable(
                            "potion.withDuration",
                            mutableText, MobEffectUtil.formatDuration(chance.effect(), chance.probability(), 1));
                }

                tooltip.add(mutableText.withStyle(statusEffect.getCategory().getTooltipFormatting()));
            });
        }

        if (!attributes.isEmpty()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));

            attributes.forEach(pair -> {
                AttributeModifier modifier = pair.getSecond();
                double d = modifier.amount();
                double e;
                if (modifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_BASE && modifier.operation() != AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    e = modifier.amount();
                } else {
                    e = modifier.amount() * 100.0;
                }

                if (d > 0.0) {
                    tooltip.add(
                            Component.translatable(
                                            "attribute.modifier.plus." + modifier.operation().getSerializedName(),
                                            ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable(pair.getFirst().getDescriptionId()))
                                    .withStyle(ChatFormatting.BLUE)
                    );
                } else if (d < 0.0) {
                    e *= -1.0;
                    tooltip.add(
                            Component.translatable(
                                            "attribute.modifier.take." + modifier.operation().getSerializedName(),
                                            ATTRIBUTE_MODIFIER_FORMAT.format(e), Component.translatable(pair.getFirst().getDescriptionId()))
                                    .withStyle(ChatFormatting.RED)
                    );
                }
            });
        }
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
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

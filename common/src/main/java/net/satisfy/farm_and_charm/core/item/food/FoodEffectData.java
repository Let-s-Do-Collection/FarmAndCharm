package net.satisfy.farm_and_charm.core.item.food;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.List;
import java.util.function.Consumer;

public record FoodEffectData(List<FoodProperties.PossibleEffect> effects) implements TooltipProvider {

    public static Codec<FoodEffectData> CODEC = FoodProperties.PossibleEffect.CODEC.listOf()
            .xmap(FoodEffectData::new, FoodEffectData::effects);

    public static StreamCodec<ByteBuf, FoodEffectData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    @Override
    public void addToTooltip(Item.TooltipContext tooltipContext, Consumer<Component> tooltip, TooltipFlag tooltipFlag) {
        if (effects().isEmpty()) {
            tooltip.accept(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            effects.forEach(effectPair -> {
                MobEffectInstance statusEffect = effectPair.effect();
                MutableComponent mutableText = Component.translatable(statusEffect.getDescriptionId());

                if (statusEffect.getAmplifier() > 0) {
                    mutableText = Component.translatable("potion.withAmplifier", mutableText, Component.translatable("potion.potency." + statusEffect.getAmplifier()));
                }
                if (effectPair.effect().getDuration() > 20) {
                    mutableText = Component.translatable("potion.withDuration", mutableText, MobEffectUtil.formatDuration(statusEffect, 1, 1));
                }

                MobEffect actual = statusEffect.getEffect().value();
                tooltip.accept(mutableText.withStyle(actual.getCategory().getTooltipFormatting()));
            });
        }
    }
}

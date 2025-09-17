package net.satisfy.farm_and_charm.core.item.food;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomModelData;

import java.util.List;
import java.util.Optional;

public class EffectFoodHelper {

    private static float saturationModifierOf(FoodProperties base) {
        if (base.nutrition() <= 0) return 0.0f;
        return base.saturation() / (base.nutrition() * 2.0f);
    }

    public static void addEffect(ItemStack stack, Pair<MobEffectInstance, Float> effect) {
        var base = stack.get(DataComponents.FOOD);
        if (base == null) return;
        var target = effect.getFirst().getEffect();
        for (var e : base.effects()) {
            if (e.effect().getEffect() == target) return;
        }
        var b = new FoodProperties.Builder().nutrition(base.nutrition()).saturationModifier(saturationModifierOf(base));
        if (base.canAlwaysEat()) b.alwaysEdible();
        if (base.eatSeconds() == 0.8f) b.fast();
        for (var e : base.effects()) b.effect(e.effect(), e.probability());
        b.effect(effect.getFirst(), effect.getSecond());
        stack.set(DataComponents.FOOD, b.build());
    }

    private static void rebuildWithout(ItemStack stack) {
        var base = stack.get(DataComponents.FOOD);
        if (base == null) return;
        var b = new FoodProperties.Builder().nutrition(base.nutrition()).saturationModifier(saturationModifierOf(base));
        if (base.canAlwaysEat()) b.alwaysEdible();
        if (base.eatSeconds() == 0.8f) b.fast();
        for (var e : base.effects()) {
            if (e.effect().getEffect() != MobEffects.HUNGER) b.effect(e.effect(), e.probability());
        }
        stack.set(DataComponents.FOOD, b.build());
    }

    private static void removeHungerEffect(ItemStack stack) {
        rebuildWithout(stack);
    }

    private static void removeRawChickenEffects(ItemStack stack) {
        if (stack.getItem() != Items.CHICKEN) return;
        removeHungerEffect(stack);
    }

    public static List<Pair<MobEffectInstance, Float>> getEffects(ItemStack stack) {
        removeHungerEffect(stack);
        removeRawChickenEffects(stack);
        var out = Lists.<Pair<MobEffectInstance, Float>>newArrayList();
        if (stack.getItem() instanceof PotionItem) {
            var pc = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            for (var e : pc.customEffects()) out.add(new Pair<>(e, 1.0f));
            return out;
        }
        var base = stack.get(DataComponents.FOOD);
        if (base != null) for (var e : base.effects()) out.add(new Pair<>(e.effect(), e.probability()));
        return out;
    }

    public static void applyEffects(ItemStack stack) {
        var pc = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (pc.hasEffects()) return;
        var base = stack.get(DataComponents.FOOD);
        if (base == null) return;
        var b = new FoodProperties.Builder().nutrition(base.nutrition()).saturationModifier(saturationModifierOf(base));
        if (base.canAlwaysEat()) b.alwaysEdible();
        if (base.eatSeconds() == 0.8f) b.fast();
        for (var e : base.effects()) b.effect(e.effect(), e.probability());
        stack.set(DataComponents.FOOD, b.build());
    }

    public static CompoundTag createNbt(short id, Pair<MobEffectInstance, Float> effect) {
        CompoundTag nbtCompound = new CompoundTag();
        nbtCompound.putShort("id", id);
        nbtCompound.putInt("duration", effect.getFirst().getDuration());
        nbtCompound.putInt("amplifier", effect.getFirst().getAmplifier());
        nbtCompound.putFloat("chance", effect.getSecond());
        return nbtCompound;
    }

    public static List<Pair<MobEffectInstance, Float>> fromNbt(ListTag list) {
        List<Pair<MobEffectInstance, Float>> effects = Lists.newArrayList();
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag nbtCompound = list.getCompound(i);
            Optional<Holder.Reference<MobEffect>> effect = BuiltInRegistries.MOB_EFFECT.getHolder(nbtCompound.getShort("id"));
            effect.ifPresent(mobEffectReference -> effects.add(new Pair<>(new MobEffectInstance(mobEffectReference, nbtCompound.getInt("duration"), nbtCompound.getInt("amplifier")), nbtCompound.getFloat("chance"))));
        }
        return effects;
    }

    public static ItemStack setStage(ItemStack stack, int stage) {
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(stage));
        return stack;
    }

    public static int getStage(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(0)).value();
    }

    public static void getTooltip(ItemStack stack, List<Component> tooltip) {
        List<Pair<MobEffectInstance, Float>> effects = getEffects(stack);
        if (effects.isEmpty()) {
            tooltip.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
        } else {
            for (Pair<MobEffectInstance, Float> effectPair : effects) {
                MobEffectInstance statusEffect = effectPair.getFirst();
                MutableComponent mutableText = Component.translatable(statusEffect.getDescriptionId());
                if (statusEffect.getAmplifier() > 0) {
                    mutableText = Component.translatable("potion.withAmplifier", mutableText, Component.translatable("potion.potency." + statusEffect.getAmplifier()));
                }
                if (effectPair.getFirst().getDuration() > 20) {
                    mutableText = Component.translatable("potion.withDuration", mutableText, MobEffectUtil.formatDuration(statusEffect, 1.0f, 1));
                }
                tooltip.add(mutableText.withStyle(statusEffect.getEffect().value().getCategory().getTooltipFormatting()));
            }
        }
    }
}

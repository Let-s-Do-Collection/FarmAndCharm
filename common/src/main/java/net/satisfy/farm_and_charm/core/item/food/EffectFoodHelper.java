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
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.*;

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
            pc.potion().ifPresent(p -> {
                for (var e : p.value().getEffects()) out.add(new Pair<>(e, 1.0f));
            });
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

    public static void getTooltip(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> tooltip) {
        List<FoodProperties.PossibleEffect> effects = stack.has(DataComponents.FOOD) ? Objects.requireNonNull(stack.get(DataComponents.FOOD)).effects() : Lists.newArrayList();
        List<Pair<Holder<Attribute>, AttributeModifier>> attrs = Lists.newArrayList();
        if (effects.isEmpty()) {
            tooltip.add(Component.translatable("effect.none").withStyle(ChatFormatting.GRAY));
            return;
        }
        for (FoodProperties.PossibleEffect pe : effects) {
            MutableComponent name = Component.translatable(pe.effect().getDescriptionId());
            MobEffect eff = pe.effect().getEffect().value();
            eff.createModifiers(pe.effect().getAmplifier(), (h, base) -> {
                AttributeModifier m = new AttributeModifier(base.id(), base.amount() * (double)(pe.effect().getAmplifier() + 1), base.operation());
                attrs.add(new Pair<>(h, m));
            });
            if (pe.effect().getDuration() > 20) {
                name = Component.translatable("potion.withDuration", name, MobEffectUtil.formatDuration(pe.effect(), pe.probability(), tooltipContext.tickRate()));
            }
            tooltip.add(name.withStyle(eff.getCategory().getTooltipFormatting()));
        }
        if (!attrs.isEmpty()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
            for (Pair<Holder<Attribute>, AttributeModifier> pair : attrs) {
                AttributeModifier m = pair.getSecond();
                double amt = m.amount();
                double shown = (m.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE || m.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) ? amt * 100.0 : amt;
                if (amt > 0.0) {
                    tooltip.add(Component.translatable("attribute.modifier.plus." + m.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(shown), Component.translatable(pair.getFirst().value().getDescriptionId())).withStyle(ChatFormatting.BLUE));
                } else if (amt < 0.0) {
                    tooltip.add(Component.translatable("attribute.modifier.take." + m.operation().id(), ItemAttributeModifiers.ATTRIBUTE_MODIFIER_FORMAT.format(-shown), Component.translatable(pair.getFirst().value().getDescriptionId())).withStyle(ChatFormatting.RED));
                }
            }
        }
    }

    public static Map<Holder<MobEffect>, MobEffectInstance> bestEffects(Iterable<Pair<MobEffectInstance, Float>> effects) {
        Map<Holder<MobEffect>, MobEffectInstance> best = new HashMap<>();
        for (Pair<MobEffectInstance, Float> p : effects) {
            MobEffectInstance cur = p.getFirst();
            Holder<MobEffect> key = cur.getEffect();
            MobEffectInstance prev = best.get(key);
            if (prev == null || cur.getAmplifier() > prev.getAmplifier() || (cur.getAmplifier() == prev.getAmplifier() && cur.getDuration() > prev.getDuration())) {
                best.put(key, cur);
            }
        }
        return best;
    }

    public static List<MobEffectInstance> collectMergedSortedEffects(Iterable<ItemStack> stacks) {
        List<Pair<MobEffectInstance, Float>> collected = new ArrayList<>();
        for (ItemStack in : stacks) if (!in.isEmpty()) collected.addAll(getEffects(in));
        Map<Holder<MobEffect>, MobEffectInstance> best = bestEffects(collected);
        List<MobEffectInstance> sorted = new ArrayList<>(best.values());
        sorted.sort(Comparator.comparingInt(a -> BuiltInRegistries.MOB_EFFECT.getId(a.getEffect().value())));
        return sorted;
    }

    public static List<MobEffectInstance> collectMergedSortedEffects(Container container, int from, int to) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = from; i <= to; i++) stacks.add(container.getItem(i));
        return collectMergedSortedEffects(stacks);
    }
}
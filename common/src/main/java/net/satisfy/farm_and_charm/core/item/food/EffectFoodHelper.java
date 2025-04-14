package net.satisfy.farm_and_charm.core.item.food;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
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

import java.util.List;

public class EffectFoodHelper {
    public static final String STORED_EFFECTS_KEY = "StoredEffects";
    public static final String FOOD_STAGE = "CustomModelData";

    public static void addEffect(ItemStack stack, Pair<MobEffectInstance, Float> effect) {
        removeHungerEffect(stack);
        removeRawChickenEffects(stack);
        ListTag nbtList = getEffectNbt(stack);
        boolean bl = true;
        int id = BuiltInRegistries.MOB_EFFECT.asHolderIdMap().getId(effect.getFirst().getEffect()) + 1;

        for (int i = 0; i < nbtList.size(); ++i) {
            CompoundTag nbtCompound = nbtList.getCompound(i);
            int idTemp = nbtCompound.getInt("id");
            if (idTemp == id) {
                bl = false;
                break;
            }
        }

        if (bl) {
            nbtList.add(createNbt((short) id, effect));
        }

        stack.getOrCreateTag().put(STORED_EFFECTS_KEY, nbtList);
    }

    private static ListTag getEffectNbt(ItemStack stack) {
        CompoundTag nbtCompound = stack.getTag();
        return nbtCompound != null ? nbtCompound.getList(STORED_EFFECTS_KEY, 10) : new ListTag();
    }

    public static CompoundTag createNbt(short id, Pair<MobEffectInstance, Float> effect) {
        CompoundTag nbtCompound = new CompoundTag();
        nbtCompound.putShort("id", id);
        nbtCompound.putInt("duration", effect.getFirst().getDuration());
        nbtCompound.putInt("amplifier", effect.getFirst().getAmplifier());
        nbtCompound.putFloat("chance", effect.getSecond());
        return nbtCompound;
    }

    public static List<Pair<MobEffectInstance, Float>> getEffects(ItemStack stack) {
        removeHungerEffect(stack);
        removeRawChickenEffects(stack);
        List<Pair<MobEffectInstance, Float>> effects = Lists.newArrayList();
        if (stack.getItem() instanceof EffectFood) {
            effects = fromNbt(getEffectNbt(stack));
        } else if (stack.getItem() instanceof PotionItem) {
            List<MobEffectInstance> potionEffects = PotionUtils.getMobEffects(stack);
            for (MobEffectInstance effect : potionEffects) {
                effects.add(new Pair<>(effect, 1.0f));
            }
        } else {
            FoodProperties foodComponent = stack.getItem().getFoodProperties();
            if (foodComponent != null) {
                effects = foodComponent.getEffects();
            }
        }
        return effects;
    }

    private static void removeHungerEffect(ItemStack stack) {
        ListTag nbtList = getEffectNbt(stack);
        ListTag updatedList = new ListTag();

        for (int i = 0; i < nbtList.size(); ++i) {
            CompoundTag nbtCompound = nbtList.getCompound(i);
            MobEffect effect = MobEffect.byId(nbtCompound.getShort("id"));
            if (effect != MobEffects.HUNGER) {
                updatedList.add(nbtCompound);
            }
        }

        stack.getOrCreateTag().put(STORED_EFFECTS_KEY, updatedList);
    }

    private static void removeRawChickenEffects(ItemStack stack) {
        if (stack.getItem() == Items.CHICKEN) {
            ListTag nbtList = getEffectNbt(stack);
            ListTag updatedList = new ListTag();

            for (int i = 0; i < nbtList.size(); ++i) {
                CompoundTag nbtCompound = nbtList.getCompound(i);
                MobEffect effect = MobEffect.byId(nbtCompound.getShort("id"));
                if (effect != MobEffects.HUNGER) {
                    updatedList.add(nbtCompound);
                }
            }

            stack.getOrCreateTag().put(STORED_EFFECTS_KEY, updatedList);
        }
    }

    public static void applyEffects(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(STORED_EFFECTS_KEY)) {
            FoodProperties foodProperties = stack.getItem().getFoodProperties();
            if (foodProperties != null) {
                ListTag nbtList = new ListTag();
                for (Pair<MobEffectInstance, Float> effect : foodProperties.getEffects()) {
                    int id = MobEffect.getId(effect.getFirst().getEffect());
                    CompoundTag effectTag = new CompoundTag();
                    effectTag.putShort("id", (short) id);
                    effectTag.putInt("duration", effect.getFirst().getDuration());
                    effectTag.putInt("amplifier", effect.getFirst().getAmplifier());
                    effectTag.putFloat("chance", effect.getSecond());
                    nbtList.add(effectTag);
                }
                tag.put(STORED_EFFECTS_KEY, nbtList);
            }
        }
    }

    public static List<Pair<MobEffectInstance, Float>> fromNbt(ListTag list) {
        List<Pair<MobEffectInstance, Float>> effects = Lists.newArrayList();
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag nbtCompound = list.getCompound(i);
            MobEffect effect = MobEffect.byId(nbtCompound.getShort("id"));
            if (effect != null) {
                effects.add(new Pair<>(new MobEffectInstance(effect, nbtCompound.getInt("duration"), nbtCompound.getInt("amplifier")), nbtCompound.getFloat("chance")));
            }
        }
        return effects;
    }

    public static ItemStack setStage(ItemStack stack, int stage) {
        CompoundTag nbtCompound = stack.getTag() != null ? stack.getTag() : new CompoundTag();
        nbtCompound.putInt(FOOD_STAGE, stage);
        stack.setTag(nbtCompound);
        return stack;
    }

    public static int getStage(ItemStack stack) {
        CompoundTag nbtCompound = stack.getTag();
        return nbtCompound != null ? nbtCompound.getInt(FOOD_STAGE) : 0;
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
                    mutableText = Component.translatable("potion.withDuration", mutableText, MobEffectUtil.formatDuration(statusEffect, 1.0f));
                }

                tooltip.add(mutableText.withStyle(statusEffect.getEffect().getCategory().getTooltipFormatting()));
            }
        }
    }
}
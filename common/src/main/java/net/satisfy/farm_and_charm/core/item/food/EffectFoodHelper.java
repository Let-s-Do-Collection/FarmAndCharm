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


    /**
     * Adds an effect to the given item stack,
     * removing any existing effects that have the same effect as the one being added and
     * removing the hunger effect if it exists.
     * **/
    public static void addEffect(ItemStack stack, Pair<MobEffectInstance, Float> effect) {
        removeHungerEffect(stack);
        removeRawChickenEffects(stack);
        List<FoodProperties.PossibleEffect> effectList = getPossibleEffects(stack);
        boolean bl = true;
        int id = BuiltInRegistries.MOB_EFFECT.asHolderIdMap().getId(effect.getFirst().getEffect()) + 1;

        for (FoodProperties.PossibleEffect possibleEffect : effectList) {
            int idTemp = BuiltInRegistries.MOB_EFFECT.asHolderIdMap().getId(possibleEffect.effect().getEffect()) + 1;
            if (idTemp == id) {
                bl = false;
                break;
            }
        }

        if (bl) {
            effectList.add(createPossibleEffect(effect));
        }

        FoodProperties.Builder builder = new FoodProperties.Builder();
        for (FoodProperties.PossibleEffect possibleEffect : effectList) {
            builder.effect(possibleEffect.effect(), possibleEffect.probability());
        }

        FoodProperties foodProperties = builder.build();
        stack.set(DataComponents.FOOD, foodProperties);
    }

    /**
     * get the list of possible effects from the given item stack
     * if the item stack has no effects, an empty list is returned
     * **/
    private static List<FoodProperties.PossibleEffect> getPossibleEffects(ItemStack stack) {
        return stack.has(DataComponents.FOOD) ? stack.get(DataComponents.FOOD).effects() : Lists.newArrayList();
    }

    /**
     * create a PossibleEffect object from the given effect
     * **/
    public static FoodProperties.PossibleEffect createPossibleEffect(Pair<MobEffectInstance, Float> effect) {
        return new FoodProperties.PossibleEffect(effect.getFirst(), effect.getSecond());
    }

    public static CompoundTag createNbt(short id, Pair<MobEffectInstance, Float> effect) {
        CompoundTag nbtCompound = new CompoundTag();
        nbtCompound.putShort("id", id);
        nbtCompound.putInt("duration", effect.getFirst().getDuration());
        nbtCompound.putInt("amplifier", effect.getFirst().getAmplifier());
        nbtCompound.putFloat("chance", effect.getSecond());
        return nbtCompound;
    }


    /**
     * Get the list of effects from the given item stack
     * if the item stack has no effects, an empty list is returned
     * if the item stack is a potion, the custom effects are returned
     * if the item stack is a food item, the effects are returned
     * **/
    public static List<Pair<MobEffectInstance, Float>> getEffects(ItemStack stack) {
        removeHungerEffect(stack);
        removeRawChickenEffects(stack);
        List<Pair<MobEffectInstance, Float>> effects = Lists.newArrayList();
        if (stack.getItem() instanceof EffectFood) {
            effects = fromPossibleEffect(getPossibleEffects(stack));
        } else if (stack.getItem() instanceof PotionItem) {
            PotionContents potionContents = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);

            for (MobEffectInstance effect : potionContents.customEffects()) {
                effects.add(new Pair<>(effect, 1.0f));
            }
        } else {
            FoodProperties foodComponent = stack.get(DataComponents.FOOD);
            if (foodComponent != null) {
                for(FoodProperties.PossibleEffect effect : foodComponent.effects()) {
                    effects.add(new Pair<>(effect.effect(), effect.probability()));
                }
            }
        }
        return effects;
    }

    /**
     * Removes the hunger effect from the given item stack
     * **/
    private static void removeHungerEffect(ItemStack stack) {
        List<FoodProperties.PossibleEffect> effectList = getPossibleEffects(stack);
        List<FoodProperties.PossibleEffect> updatedList = Lists.newArrayList();

        for (FoodProperties.PossibleEffect effect : effectList) {
            if (effect.effect().getEffect() != MobEffects.HUNGER) {
                updatedList.add(effect);
            }
        }

        FoodProperties.Builder builder = new FoodProperties.Builder();
        for (FoodProperties.PossibleEffect possibleEffect : updatedList) {
            builder.effect(possibleEffect.effect(), possibleEffect.probability());
        }

        FoodProperties properties = builder.build();
        stack.set(DataComponents.FOOD, properties);
    }

    /**
     * Removes the hunger effect from the given item stack if the item stack is a raw chicken
     * and returns the list of effects that are not the hunger effect
     * **/
    private static void removeRawChickenEffects(ItemStack stack) {
        if (stack.getItem() == Items.CHICKEN) {
            List<FoodProperties.PossibleEffect> effectList = getPossibleEffects(stack);
            List<FoodProperties.PossibleEffect> updatedList = Lists.newArrayList();

            for (FoodProperties.PossibleEffect effect : effectList) {
                if (effect.effect().getEffect() != MobEffects.HUNGER) {
                    updatedList.add(effect);
                }
            }

            FoodProperties.Builder builder = new FoodProperties.Builder();
            for (FoodProperties.PossibleEffect possibleEffect : updatedList) {
                builder.effect(possibleEffect.effect(), possibleEffect.probability());
            }

            FoodProperties foodProperties = builder.build();
            stack.set(DataComponents.FOOD, foodProperties);
        }
    }

    /**
     * Converts a list of PossibleEffect objects to a list of Pair objects
     * **/
    public static List<Pair<MobEffectInstance, Float>> fromPossibleEffect(List<FoodProperties.PossibleEffect> list) {
        List<Pair<MobEffectInstance, Float>> effects = Lists.newArrayList();
        for(FoodProperties.PossibleEffect effect : list) {
            effects.add(new Pair<>(effect.effect(), effect.probability()));
        }
        return effects;
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

    /**
     * Set the stage of the given item stack
     * **/
    public static ItemStack setStage(ItemStack stack, int stage) {
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(stage));
        return stack;
    }

    /**
     * Get the stage of the given item stack
     * **/
    public static int getStage(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(0)).value();
    }

    /**
     * Get the tooltip for the given item stack
     * **/
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
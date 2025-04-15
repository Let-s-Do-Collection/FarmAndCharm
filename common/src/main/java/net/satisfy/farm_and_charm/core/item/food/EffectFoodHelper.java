package net.satisfy.farm_and_charm.core.item.food;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.PossibleEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.satisfy.farm_and_charm.core.registry.DataComponentRegistry;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class EffectFoodHelper {

    public static void addEffect(ItemStack stack, List<PossibleEffect> newEffects) {
        removeHungerEffect(stack);
        removeRawChickenEffects(stack);
        List<PossibleEffect> effects = stack.getOrDefault(
                DataComponentRegistry.FOOD_EFFECTS.get(), new FoodEffectData(List.of())
        ).effects();
        effects.addAll(newEffects);
        stack.set(DataComponentRegistry.FOOD_EFFECTS.get(), new FoodEffectData(newEffects));
    }

    public static CompoundTag createNbt(short id, Pair<MobEffectInstance, Float> effect) {
        CompoundTag nbtCompound = new CompoundTag();
        nbtCompound.putShort("id", id);
        nbtCompound.putInt("duration", effect.getFirst().getDuration());
        nbtCompound.putInt("amplifier", effect.getFirst().getAmplifier());
        nbtCompound.putFloat("chance", effect.getSecond());
        return nbtCompound;
    }

    public static List<PossibleEffect> getEffects(ItemStack stack) {
        removeHungerEffect(stack);
        removeRawChickenEffects(stack);
        if (stack.getItem() instanceof EffectFood) {
            return stack.getOrDefault(
                    DataComponentRegistry.FOOD_EFFECTS.get(), new FoodEffectData(List.of())
            ).effects();
        } else if (stack.getItem() instanceof PotionItem) {
            PotionContents potionEffects = stack.get(DataComponents.POTION_CONTENTS);
            if (potionEffects == null) return List.of();
            return StreamSupport.stream(potionEffects.getAllEffects().spliterator(), false)
                    .map(obj -> new PossibleEffect(obj, 1))
                    .toList();
        } else {
            FoodProperties foodComponent = stack.get(DataComponents.FOOD);
            return foodComponent != null ? foodComponent.effects() : List.of();
        }
    }

    private static void removeHungerEffect(ItemStack stack) {
        if (!stack.has(DataComponentRegistry.FOOD_EFFECTS.get())) return;
        stack.set(DataComponentRegistry.FOOD_EFFECTS.get(), new FoodEffectData(
            stack.getOrDefault(
                    DataComponentRegistry.FOOD_EFFECTS.get(), new FoodEffectData(List.of())
            ).effects().stream().filter(
                    chance -> !chance.effect().is(MobEffects.HUNGER)
            ).toList()
        ));
    }

    private static void removeRawChickenEffects(ItemStack stack) {
        if (!stack.is(Items.CHICKEN) || !stack.has(DataComponentRegistry.FOOD_EFFECTS.get())) return;
        stack.set(DataComponentRegistry.FOOD_EFFECTS.get(), new FoodEffectData(
                stack.getOrDefault(
                        DataComponentRegistry.FOOD_EFFECTS.get(), new FoodEffectData(List.of())
                ).effects().stream().filter(
                        chance -> !chance.effect().is(MobEffects.HUNGER)
                ).toList()
        ));
    }

    public static void applyEffects(ItemStack stack) {
        if (!stack.has(DataComponentRegistry.FOOD_EFFECTS.get()) && stack.has(DataComponents.FOOD)) {
            FoodProperties foodComponent = stack.get(DataComponents.FOOD); assert foodComponent != null;
            stack.set(DataComponentRegistry.FOOD_EFFECTS.get(), new FoodEffectData(foodComponent.effects()));
        }
    }

    public static ItemStack setStage(ItemStack stack, int stage) {
        stack.set(DataComponentRegistry.FOOD_STAGE.get(), stage);
        return stack;
    }

    public static int getStage(ItemStack stack) {
        return Optional.ofNullable(stack.get(DataComponentRegistry.FOOD_STAGE.get()))
                .orElse(0);
    }
}
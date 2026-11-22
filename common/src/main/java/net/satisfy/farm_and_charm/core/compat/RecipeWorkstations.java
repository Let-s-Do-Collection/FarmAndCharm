package net.satisfy.farm_and_charm.core.compat;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class RecipeWorkstations {
    public enum Type {
        COOKING_POT,
        STOVE,
        ROASTER,
        CRAFTING_BOWL,
        MINCER
    }

    private static final Map<Type, List<Supplier<? extends ItemLike>>> WORKSTATIONS = new EnumMap<>(Type.class);

    private RecipeWorkstations() {
    }

    public static synchronized void add(Type type, Supplier<? extends ItemLike> workstation) {
        WORKSTATIONS.computeIfAbsent(type, key -> new ArrayList<>()).add(workstation);
    }

    public static synchronized List<ItemStack> get(Type type) {
        List<ItemStack> stacks = new ArrayList<>();
        for (Supplier<? extends ItemLike> workstation : WORKSTATIONS.getOrDefault(type, List.of())) {
            ItemStack stack = new ItemStack(workstation.get());
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        return stacks;
    }
}

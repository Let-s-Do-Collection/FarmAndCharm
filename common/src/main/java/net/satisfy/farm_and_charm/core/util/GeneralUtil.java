package net.satisfy.farm_and_charm.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GeneralUtil {
    public GeneralUtil() {
    }

    public static boolean matchesRecipe(RecipeInput inventory, NonNullList<Ingredient> recipe, int startIndex, int endIndex) {
        List<ItemStack> inputStacks = new ArrayList<>();
        for (int i = startIndex; i <= endIndex; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) inputStacks.add(stack.copy());
        }

        if (inputStacks.size() != recipe.size()) return false;

        List<Ingredient> unmatched = new ArrayList<>(recipe);
        for (ItemStack input : inputStacks) {
            boolean matched = false;
            Iterator<Ingredient> iter = unmatched.iterator();
            while (iter.hasNext()) {
                if (iter.next().test(input)) {
                    iter.remove();
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }

        return unmatched.isEmpty();
    }

    public enum FoodType implements StringRepresentable {
        NONE("none"),
        CAT("cat"),
        DOG("dog");

        private final String name;

        FoodType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

     public static class GrowthSpeedUtil {
        private static Method blockStateMethod;
        private static Method blockMethod;
        private static boolean initialized = false;

        private static void init() {
            if (initialized) return;

            try {
                blockStateMethod = CropBlock.class.getDeclaredMethod("getGrowthSpeed",
                        BlockState.class, LevelReader.class, BlockPos.class);
                blockStateMethod.setAccessible(true);
            } catch (NoSuchMethodException ignored) {
            }

            try {
                blockMethod = CropBlock.class.getDeclaredMethod("getGrowthSpeed",
                        Block.class, BlockGetter.class, BlockPos.class);
                blockMethod.setAccessible(true);
            } catch (NoSuchMethodException ignored) {
            }

            initialized = true;
        }

        public static float getGrowthSpeed(BlockState state, ServerLevel level, BlockPos pos) {
            init();
            try {
                if (blockStateMethod != null) {
                    return (float) blockStateMethod.invoke(null, state, level, pos);
                }
                if (blockMethod != null) {
                    return (float) blockMethod.invoke(null, state.getBlock(), level, pos);
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to invoke CropBlock.getGrowthSpeed", e);
            }
            return 1.0F;
        }
    }
}
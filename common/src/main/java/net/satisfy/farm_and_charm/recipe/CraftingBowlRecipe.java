package net.satisfy.farm_and_charm.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.util.GeneralUtil;
import net.satisfy.farm_and_charm.util.StreamCodecUtil;
import org.jetbrains.annotations.NotNull;

public class CraftingBowlRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;
    private final int outputCount;

    public CraftingBowlRecipe(NonNullList<Ingredient> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
        this.outputCount = output.getCount();
    }

    public int getOutputCount() {
        return outputCount;
    }


    @Override
    public boolean matches(RecipeInput inventory, Level world) {
        int nonEmptySlots = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (!inventory.getItem(i).isEmpty()) {
                nonEmptySlots++;
            }
        }
        return nonEmptySlots >= 1 && nonEmptySlots <= inputs.size() && GeneralUtil.matchesRecipe(inventory, inputs, 0, 3);
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        ItemStack result = this.output.copy();
        result.setCount(this.outputCount);
        return result;
    }

    public ItemStack getResultItem() {
        return getResultItem(null);
    }

    public @NotNull ResourceLocation getId() {
        return RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.getId();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.get();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<CraftingBowlRecipe> {
        public static final MapCodec<CraftingBowlRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                            Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                            if (ingredients.length == 0) {
                                return DataResult.error(() -> "No ingredients for shapeless recipe");
                            }
                            return DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                        }, DataResult::success).forGetter(CraftingBowlRecipe::getIngredients),
                        ItemStack.CODEC.fieldOf("result").forGetter(CraftingBowlRecipe::getResultItem)
                ).apply(instance, CraftingBowlRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CraftingBowlRecipe> STREAM_CODEC = StreamCodec.composite(
                StreamCodecUtil.nonNullList(Ingredient.CONTENTS_STREAM_CODEC, Ingredient.EMPTY), CraftingBowlRecipe::getIngredients,
                ItemStack.STREAM_CODEC, CraftingBowlRecipe::getResultItem,
                CraftingBowlRecipe::new
        );

        @Override
        public @NotNull MapCodec<CraftingBowlRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CraftingBowlRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

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

public class CookingPotRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> inputs;
    private final ItemStack container;
    private final ItemStack output;

    public CookingPotRecipe(NonNullList<Ingredient> inputs, ItemStack container, ItemStack output) {
        this.inputs = inputs;
        this.container = container;
        this.output = output;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return GeneralUtil.matchesRecipe(recipeInput, inputs, 0, 6);
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    public ResourceLocation getId() {
        return RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.getId();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.output.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegistry.COOKING_POT_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    public ItemStack getContainer() {
        return container;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<CookingPotRecipe> {
        public static final MapCodec<CookingPotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                            Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                            if (ingredients.length == 0) {
                                return DataResult.error(() -> "No ingredients for shapeless recipe");
                            }
                            return DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                        }, DataResult::success).forGetter(CookingPotRecipe::getIngredients),
                        ItemStack.CODEC.fieldOf("container").forGetter(CookingPotRecipe::getContainer),
                        ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.output)
                ).apply(instance, CookingPotRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> STREAM_CODEC = StreamCodec.composite(
                StreamCodecUtil.nonNullList(Ingredient.CONTENTS_STREAM_CODEC, Ingredient.EMPTY), CookingPotRecipe::getIngredients,
                ItemStack.STREAM_CODEC, CookingPotRecipe::getContainer,
                ItemStack.STREAM_CODEC, recipe -> recipe.output,
                CookingPotRecipe::new
        );

        @Override
        public @NotNull MapCodec<CookingPotRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
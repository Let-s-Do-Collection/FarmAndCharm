package net.satisfy.farm_and_charm.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class MincerRecipe implements Recipe<RecipeInput> {

    private final String recipe_type;
    private final Ingredient input;
    private final ItemStack output;

    public MincerRecipe(String type, Ingredient input, ItemStack output) {
        this.recipe_type = type;
        this.input = input;
        this.output = output;
    }

    public Ingredient getInput() {
        return this.input;
    }

    public ItemStack getOutput() {
        return this.output.copy();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonNullList = NonNullList.create();
        nonNullList.add(this.input);
        return nonNullList;
    }

    public String getRecipeType() {
        return this.recipe_type;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return input.test(recipeInput.getItem(0));
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return output.copy();
    }

    public @NotNull ResourceLocation getId() {
        return RecipeTypeRegistry.MINCER_RECIPE_TYPE.getId();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegistry.MINCER_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypeRegistry.MINCER_RECIPE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<MincerRecipe> {

        public static final MapCodec<MincerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("recipe_type").forGetter(MincerRecipe::getRecipeType),
                Ingredient.CODEC.fieldOf("ingredient").forGetter(MincerRecipe::getInput),
                ItemStack.CODEC.fieldOf("result").forGetter(MincerRecipe::getOutput)
                ).apply(instance, MincerRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, MincerRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.fromCodec(Codec.STRING), MincerRecipe::getRecipeType,
                Ingredient.CONTENTS_STREAM_CODEC, MincerRecipe::getInput,
                ItemStack.STREAM_CODEC, MincerRecipe::getOutput,
                MincerRecipe::new
        );

        @Override
        public @NotNull MapCodec<MincerRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, MincerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

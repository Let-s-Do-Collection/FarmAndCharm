package net.satisfy.farm_and_charm.core.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

public class SiloRecipe implements Recipe<RecipeInput> {

    private final Ingredient input;
    private final ItemStack output;

    public SiloRecipe(Ingredient input, ItemStack output) {
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

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return IntStream.range(0, recipeInput.size())
                .mapToObj(recipeInput::getItem)
                .allMatch(input);
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

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegistry.SILO_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypeRegistry.SILO_RECIPE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<SiloRecipe> {

        @Override
        public @NotNull MapCodec<SiloRecipe> codec() {
            return RecordCodecBuilder.mapCodec(obj -> obj.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(SiloRecipe::getInput),
                    ItemStack.CODEC.fieldOf("result").forGetter(SiloRecipe::getOutput)
            ).apply(obj, SiloRecipe::new));
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, SiloRecipe> streamCodec() {
            return ByteBufCodecs.fromCodecWithRegistries(codec().codec());
        }

    }
}

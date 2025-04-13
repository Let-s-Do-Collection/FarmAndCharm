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
import net.satisfy.farm_and_charm.core.block.entity.MincerBlockEntity.MincingType;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class MincerRecipe implements Recipe<RecipeInput> {

    private final MincingType mincingType;
    private final Ingredient input;
    private final ItemStack output;

    public MincerRecipe(MincingType type, Ingredient input, ItemStack output) {
        this.mincingType = type;
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
        return NonNullList.of(this.input);
    }

    public MincingType getRecipeType() {
        return this.mincingType;
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

        @Override
        public @NotNull MapCodec<MincerRecipe> codec() {
            return RecordCodecBuilder.mapCodec(obj -> obj.group(
                    MincingType.CODEC.fieldOf("recipe_type").forGetter(MincerRecipe::getRecipeType),
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(MincerRecipe::getInput),
                    ItemStack.CODEC.fieldOf("result").forGetter(MincerRecipe::getOutput)
            ).apply(obj, MincerRecipe::new));
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, MincerRecipe> streamCodec() {
            return ByteBufCodecs.fromCodecWithRegistries(codec().codec());
        }

    }
}

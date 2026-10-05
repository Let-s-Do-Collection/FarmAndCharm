package net.satisfy.farm_and_charm.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CuttingBoardRecipe implements Recipe<SingleRecipeInput> {
    private final Ingredient ingredient;
    private final ItemStack result;
    private final List<ItemStack> byproducts;
    private final int chops;
    private final float experience;

    public CuttingBoardRecipe(Ingredient ingredient, ItemStack result, List<ItemStack> byproducts, int chops, float experience) {
        this.ingredient = ingredient;
        this.result = result;
        this.byproducts = List.copyOf(byproducts);
        this.chops = chops;
        this.experience = experience;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.ingredient.test(input.item());
    }

    @Override
    public @NotNull ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider provider) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.result;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.ingredient);
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegistry.CUTTING_BOARD_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get();
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public List<ItemStack> getByproducts() {
        return this.byproducts;
    }

    public int getChops() {
        return this.chops;
    }

    public float getExperience() {
        return this.experience;
    }

    public static class Serializer implements RecipeSerializer<CuttingBoardRecipe> {
        public static final MapCodec<CuttingBoardRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CuttingBoardRecipe::getIngredient),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(CuttingBoardRecipe::getResult),
                ItemStack.STRICT_CODEC.listOf().optionalFieldOf("byproducts", List.of()).forGetter(CuttingBoardRecipe::getByproducts),
                ExtraCodecs.POSITIVE_INT.optionalFieldOf("chops", 3).forGetter(CuttingBoardRecipe::getChops),
                Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(CuttingBoardRecipe::getExperience)
        ).apply(instance, CuttingBoardRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CuttingBoardRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, CuttingBoardRecipe::getIngredient,
                ItemStack.STREAM_CODEC, CuttingBoardRecipe::getResult,
                ItemStack.LIST_STREAM_CODEC, CuttingBoardRecipe::getByproducts,
                ByteBufCodecs.VAR_INT, CuttingBoardRecipe::getChops,
                ByteBufCodecs.FLOAT, CuttingBoardRecipe::getExperience,
                CuttingBoardRecipe::new
        );

        @Override
        public @NotNull MapCodec<CuttingBoardRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CuttingBoardRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

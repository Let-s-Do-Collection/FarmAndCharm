package net.satisfy.farm_and_charm.core.recipe;

import com.mojang.serialization.Codec;
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
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;


public class StoveRecipe implements Recipe<RecipeInput> {
    protected final NonNullList<Ingredient> inputs;
    protected final ItemStack output;
    protected final float experience;
    private final boolean requiresLearning;

    public StoveRecipe(NonNullList<Ingredient> inputs, ItemStack output, float experience, boolean requiresLearning) {
        this.inputs = inputs;
        this.output = output;
        this.experience = experience;
        this.requiresLearning = requiresLearning;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return GeneralUtil.matchesRecipe(recipeInput, inputs, 1, 3);
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
        return this.output.copy();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    public @NotNull ResourceLocation getId() {
        return RecipeTypeRegistry.STOVE_RECIPE_TYPE.getId();
    }

    public float getExperience() {
        return experience;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegistry.STOVE_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypeRegistry.STOVE_RECIPE_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public boolean requiresLearning() {
        return requiresLearning;
    }

    public static class Serializer implements RecipeSerializer<StoveRecipe> {
        public static final MapCodec<StoveRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                    Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                    if (ingredients.length == 0) {
                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                    }
                    return DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                }, DataResult::success).forGetter(StoveRecipe::getIngredients),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                Codec.FLOAT.fieldOf("experience").forGetter(StoveRecipe::getExperience),
                Codec.BOOL.fieldOf("requiresLearning").forGetter(StoveRecipe::requiresLearning)
                ).apply(instance, StoveRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, StoveRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        public static @NotNull StoveRecipe fromNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            int i = registryFriendlyByteBuf.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
            nonNullList.replaceAll((ingredient) -> Ingredient.CONTENTS_STREAM_CODEC.decode(registryFriendlyByteBuf));
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);
            float experience = registryFriendlyByteBuf.readFloat();
            boolean requiresLearning = registryFriendlyByteBuf.readBoolean();
            return new StoveRecipe(nonNullList, itemStack, experience, requiresLearning);
        }

        public static void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, StoveRecipe recipe) {
            registryFriendlyByteBuf.writeVarInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(registryFriendlyByteBuf, ingredient);
            }
            registryFriendlyByteBuf.writeFloat(recipe.experience);
            ItemStack.STREAM_CODEC.encode(registryFriendlyByteBuf, recipe.output);
            registryFriendlyByteBuf.writeBoolean(recipe.requiresLearning);
        }

        @Override
        public @NotNull MapCodec<StoveRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, StoveRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
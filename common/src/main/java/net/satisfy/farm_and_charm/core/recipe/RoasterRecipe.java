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

public class RoasterRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> inputs;
    private final ItemStack container;
    private final ItemStack output;
    private final boolean requiresLearning;

    public RoasterRecipe(NonNullList<Ingredient> inputs, ItemStack container, ItemStack output, boolean requiresLearning) {
        this.inputs = inputs;
        this.container = container;
        this.output = output;
        this.requiresLearning = requiresLearning;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return GeneralUtil.matchesRecipe(recipeInput, inputs, 0, 5);
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

    public ItemStack getResult() {
        return this.output.copy();
    }

    public @NotNull ResourceLocation getId() {
        return RecipeTypeRegistry.ROASTER_RECIPE_TYPE.getId();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegistry.ROASTER_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypeRegistry.ROASTER_RECIPE_TYPE.get();
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

    public boolean requiresLearning() {
        return requiresLearning;
    }

    public static class Serializer implements RecipeSerializer<RoasterRecipe> {
        public static final MapCodec<RoasterRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(list -> {
                            Ingredient[] ingredients = list.toArray(Ingredient[]::new);
                            if (ingredients.length == 0) {
                                return DataResult.error(() -> "No ingredients for shapeless recipe");
                            }
                            return DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredients));
                        }, DataResult::success).forGetter(RoasterRecipe::getIngredients),
                        ItemStack.CODEC.fieldOf("container").forGetter(RoasterRecipe::getContainer),
                        ItemStack.CODEC.fieldOf("result").forGetter(RoasterRecipe::getResult),
                        Codec.BOOL.fieldOf("requiresLearning").forGetter(RoasterRecipe::requiresLearning)
                ).apply(instance, RoasterRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, RoasterRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        public static @NotNull RoasterRecipe fromNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            int i = registryFriendlyByteBuf.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
            nonNullList.replaceAll((ingredient) -> Ingredient.CONTENTS_STREAM_CODEC.decode(registryFriendlyByteBuf));
            ItemStack containerItem = ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);
            boolean requiresLearning = registryFriendlyByteBuf.readBoolean();
            return new RoasterRecipe(nonNullList, containerItem, itemStack, requiresLearning);
        }

        public static void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, RoasterRecipe recipe) {
            registryFriendlyByteBuf.writeVarInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(registryFriendlyByteBuf, ingredient);
            }
            ItemStack.STREAM_CODEC.encode(registryFriendlyByteBuf, recipe.getContainer());
            ItemStack.STREAM_CODEC.encode(registryFriendlyByteBuf, recipe.output);
            registryFriendlyByteBuf.writeBoolean(recipe.requiresLearning);
        }

        @Override
        public @NotNull MapCodec<RoasterRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, RoasterRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}
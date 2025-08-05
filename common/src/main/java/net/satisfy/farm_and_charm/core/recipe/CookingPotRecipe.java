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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

public class CookingPotRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> inputs;
    private final boolean containerRequired;
    private final ItemStack containerItem;
    private final ItemStack output;
    private final boolean requiresLearning;

    public CookingPotRecipe(NonNullList<Ingredient> inputs, boolean containerRequired, ItemStack containerItem, ItemStack output, boolean requiresLearning) {
        this.inputs = inputs;
        this.containerRequired = containerRequired;
        this.containerItem = containerItem;
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

    public boolean isContainerRequired() {
        return containerRequired;
    }

    public ItemStack getContainerItem() {
        return containerItem;
    }

    public static ResourceLocation getIdStatic() {
        return RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.getId();
    }

    public boolean requiresLearning() {
        return requiresLearning;
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
                        Codec.BOOL.fieldOf("requireContainer").forGetter(CookingPotRecipe::isContainerRequired),
                        ItemStack.CODEC.fieldOf("container").forGetter(CookingPotRecipe::getContainerItem),
                        ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                        Codec.BOOL.fieldOf("requiresLearning").forGetter(CookingPotRecipe::requiresLearning)
                ).apply(instance, CookingPotRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> STREAM_CODEC =
                StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        public static @NotNull CookingPotRecipe fromNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
            int i = registryFriendlyByteBuf.readVarInt();
            NonNullList<Ingredient> nonNullList = NonNullList.withSize(i, Ingredient.EMPTY);
            nonNullList.replaceAll((ingredient) -> Ingredient.CONTENTS_STREAM_CODEC.decode(registryFriendlyByteBuf));
            boolean containerRequired = registryFriendlyByteBuf.readBoolean();
            ItemStack containerItem = ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);
            ItemStack itemStack = ItemStack.STREAM_CODEC.decode(registryFriendlyByteBuf);
            boolean requiresLearning = registryFriendlyByteBuf.readBoolean();
            return new CookingPotRecipe(nonNullList, containerRequired, containerItem, itemStack, requiresLearning);
        }

        public static void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, CookingPotRecipe recipe) {
            registryFriendlyByteBuf.writeVarInt(recipe.getIngredients().size());

            for (Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(registryFriendlyByteBuf, ingredient);
            }
            registryFriendlyByteBuf.writeBoolean(recipe.containerRequired);
            ItemStack.STREAM_CODEC.encode(registryFriendlyByteBuf, recipe.containerItem);
            ItemStack.STREAM_CODEC.encode(registryFriendlyByteBuf, recipe.output);
            registryFriendlyByteBuf.writeBoolean(recipe.requiresLearning);
        }

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
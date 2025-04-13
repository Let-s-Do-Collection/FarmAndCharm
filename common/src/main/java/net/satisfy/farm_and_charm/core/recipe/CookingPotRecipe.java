package net.satisfy.farm_and_charm.core.recipe;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PairMapCodec;
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
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CookingPotRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> inputs;
    private final Pair<Boolean, ItemStack> container;
    private final ItemStack output;
    private final boolean requiresLearning;

    public CookingPotRecipe(List<Ingredient> inputs, Pair<Boolean, ItemStack> container, ItemStack output, boolean requiresLearning) {
        this.inputs = GeneralUtil.nonNullList(inputs, Ingredient.class);
        this.container = container;
        this.output = output;
        this.requiresLearning = requiresLearning;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return GeneralUtil.matchesRecipe(recipeInput, inputs, 0, 6);
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

    public ResourceLocation getId() {
        return RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.getId();
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

    public Pair<Boolean, ItemStack> getContainer() {
        return container;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public boolean isContainerRequired() {
        return container.getFirst();
    }

    public ItemStack getContainerItem() {
        return container.getSecond();
    }

    public boolean requiresLearning() {
        return requiresLearning;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<CookingPotRecipe> {
        public PairMapCodec<Boolean, ItemStack> containerCodec = new PairMapCodec<>(
                Codec.BOOL.fieldOf("required"), ItemStack.CODEC.fieldOf("result")
        );

        @Override
        public @NotNull MapCodec<CookingPotRecipe> codec() {
            return RecordCodecBuilder.mapCodec(obj -> obj.group(
                    Ingredient.CODEC.listOf().fieldOf("ingredients").validate(list -> {
                        if (list.isEmpty()) return DataResult.error(() -> "No ingredients for CookingPot Recipe");
                        else if (list.size() > 6) return DataResult.error(() -> "Too many ingredients for CookingPot Recipe");
                        return DataResult.success(list);
                    }).forGetter(CookingPotRecipe::getIngredients),
                    containerCodec.fieldOf("container").forGetter(CookingPotRecipe::getContainer),
                    ItemStack.CODEC.fieldOf("output").forGetter(CookingPotRecipe::getOutput),
                    Codec.BOOL.fieldOf("requiresLearning").orElse(false).forGetter(CookingPotRecipe::requiresLearning)
            ).apply(obj, CookingPotRecipe::new));
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CookingPotRecipe> streamCodec() {
            return ByteBufCodecs.fromCodecWithRegistries(codec().codec());
        }
    }
}

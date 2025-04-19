package net.satisfy.farm_and_charm.core.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RoasterRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> inputs;
    private final ItemStack container;
    private final ItemStack output;
    private final boolean requiresLearning;

    public RoasterRecipe(List<Ingredient> inputs, ItemStack container, ItemStack output, boolean requiresLearning) {
        this.inputs = GeneralUtil.nonNullList(inputs, Ingredient.class);
        this.container = container;
        this.output = output;
        this.requiresLearning = requiresLearning;
    }

    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return GeneralUtil.matchesRecipe(recipeInput, GeneralUtil.nonNullList(inputs, Ingredient.class), 0, 6);
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

    public ItemStack getOutput() {
        return output;
    }

    public boolean requiresLearning() {
        return requiresLearning;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<RoasterRecipe> {
        @Override
        public @NotNull MapCodec<RoasterRecipe> codec() {
            return RecordCodecBuilder.mapCodec(obj -> obj.group(
                    Ingredient.CODEC.listOf().fieldOf("ingredients").validate(list -> {
                        if (list.stream().anyMatch(Objects::isNull)) return DataResult.error(() -> "Ingredients may not be null");
                        if (list.isEmpty()) return DataResult.error(() -> "No ingredients for Roaster Recipe");
                        else if (list.size() > 6) return DataResult.error(() -> "Too many ingredients for Roaster Recipe");
                        return DataResult.success(list);
                    }).forGetter(RoasterRecipe::getIngredients),
                    ItemStack.CODEC.fieldOf("container").forGetter(RoasterRecipe::getContainer),
                    ItemStack.CODEC.fieldOf("result").forGetter(RoasterRecipe::getOutput),
                    Codec.BOOL.fieldOf("requiresLearning").orElse(false).forGetter(RoasterRecipe::requiresLearning)
            ).apply(obj, RoasterRecipe::new));
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, RoasterRecipe> streamCodec() {
            return ByteBufCodecs.fromCodecWithRegistries(codec().codec());
        }
    }
}

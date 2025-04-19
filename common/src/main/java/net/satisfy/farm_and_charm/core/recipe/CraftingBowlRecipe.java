package net.satisfy.farm_and_charm.core.recipe;

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
import java.util.stream.IntStream;

public class CraftingBowlRecipe implements Recipe<RecipeInput> {
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;

    public CraftingBowlRecipe(List<Ingredient> inputs, ItemStack output) {
        this.inputs = GeneralUtil.nonNullList(inputs, Ingredient.class);
        this.output = output;
    }

    @Override
    public boolean matches(RecipeInput inventory, Level world) {
        int nonEmptySlots = IntStream.range(0, inventory.size())
                .map(i -> inventory.getItem(i).isEmpty() ? 0 : 1)
                .sum();
        return nonEmptySlots >= 1 && nonEmptySlots <= inputs.size() && GeneralUtil.matchesRecipe(inventory, inputs, 0, 3);
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    public int getOutputCount() {
        return output.getCount();
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
        return RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.get();
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<CraftingBowlRecipe> {

        @Override
        public @NotNull MapCodec<CraftingBowlRecipe> codec() {
            return RecordCodecBuilder.mapCodec(obj -> obj.group(
                    Ingredient.CODEC.listOf().fieldOf("ingredients").validate(list -> {
                        if (list.stream().anyMatch(Objects::isNull)) return DataResult.error(() -> "Ingredients may not be null");
                        return DataResult.success(list);
                    }).forGetter(CraftingBowlRecipe::getIngredients),
                    ItemStack.CODEC.fieldOf("result").forGetter(CraftingBowlRecipe::getOutput)
            ).apply(obj, CraftingBowlRecipe::new));
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CraftingBowlRecipe> streamCodec() {
            return ByteBufCodecs.fromCodecWithRegistries(codec().codec());
        }

    }
}

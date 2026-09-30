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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.block.entity.CuttingBoardBlockEntity;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class CuttingBoardAssemblyRecipe implements Recipe<CuttingBoardAssemblyRecipe.Input> {
    public static final int MAX_ORDERED_ITEMS = 3;

    private final NonNullList<Ingredient> ingredients;
    private final Optional<Ingredient> finisher;
    private final ItemStack result;
    private final boolean ordered;

    public CuttingBoardAssemblyRecipe(List<Ingredient> ingredients, Optional<Ingredient> finisher, ItemStack result, boolean ordered) {
        this.ingredients = NonNullList.of(Ingredient.EMPTY, ingredients.toArray(Ingredient[]::new));
        this.finisher = finisher;
        this.result = result;
        this.ordered = ordered;
    }

    @Override
    public boolean matches(Input input, Level level) {
        return input.size() == this.totalSize() && this.accepts(input.items());
    }

    public boolean accepts(List<ItemStack> items) {
        int count = this.ingredients.size();
        if (items.size() > this.totalSize()) {
            return false;
        }
        if (this.ordered) {
            for (int i = 0; i < items.size(); i++) {
                Ingredient expected = i < count ? this.ingredients.get(i) : this.finisher.orElseThrow();
                if (!expected.test(items.get(i))) {
                    return false;
                }
            }
            return true;
        }
        if (this.finisher.isPresent() && items.size() == count + 1) {
            return this.finisher.get().test(items.getLast()) && assign(items.subList(0, count), 0, new boolean[count]);
        }
        return assign(items, 0, new boolean[count]);
    }

    private int totalSize() {
        return this.ingredients.size() + (this.finisher.isPresent() ? 1 : 0);
    }

    private boolean assign(List<ItemStack> items, int index, boolean[] used) {
        if (index == items.size()) {
            return true;
        }
        for (int i = 0; i < this.ingredients.size(); i++) {
            if (!used[i] && this.ingredients.get(i).test(items.get(index))) {
                used[i] = true;
                if (assign(items, index + 1, used)) {
                    return true;
                }
                used[i] = false;
            }
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(Input input, HolderLookup.Provider provider) {
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
        if (this.finisher.isEmpty()) {
            return this.ingredients;
        }
        NonNullList<Ingredient> all = NonNullList.create();
        all.addAll(this.ingredients);
        all.add(this.finisher.get());
        return all;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get();
    }

    public List<Ingredient> getBaseIngredients() {
        return this.ingredients;
    }

    public Optional<Ingredient> getFinisher() {
        return this.finisher;
    }

    public ItemStack getResult() {
        return this.result;
    }

    public boolean isOrdered() {
        return this.ordered;
    }

    public record Input(List<ItemStack> items) implements RecipeInput {
        @Override
        public @NotNull ItemStack getItem(int index) {
            return this.items.get(index);
        }

        @Override
        public int size() {
            return this.items.size();
        }
    }

    public static class Serializer implements RecipeSerializer<CuttingBoardAssemblyRecipe> {
        private static final Codec<List<Ingredient>> INGREDIENTS_CODEC = Ingredient.CODEC_NONEMPTY.listOf().flatXmap(list -> {
            if (list.size() < 2 || list.size() > CuttingBoardBlockEntity.MAX_ITEMS) {
                return DataResult.error(() -> "Cutting board assembly needs 2 to " + CuttingBoardBlockEntity.MAX_ITEMS + " ingredients");
            }
            return DataResult.success(list);
        }, DataResult::success);

        public static final MapCodec<CuttingBoardAssemblyRecipe> CODEC = RecordCodecBuilder.<CuttingBoardAssemblyRecipe>mapCodec(instance -> instance.group(
                INGREDIENTS_CODEC.fieldOf("ingredients").forGetter(CuttingBoardAssemblyRecipe::getBaseIngredients),
                Ingredient.CODEC_NONEMPTY.optionalFieldOf("finisher").forGetter(CuttingBoardAssemblyRecipe::getFinisher),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(CuttingBoardAssemblyRecipe::getResult),
                Codec.BOOL.optionalFieldOf("ordered", false).forGetter(CuttingBoardAssemblyRecipe::isOrdered)
        ).apply(instance, CuttingBoardAssemblyRecipe::new)).validate(recipe -> {
            if (recipe.totalSize() > CuttingBoardBlockEntity.MAX_ITEMS) {
                return DataResult.error(() -> "Cutting board assembly holds at most " + CuttingBoardBlockEntity.MAX_ITEMS + " items including the finisher");
            }
            if (recipe.isOrdered() && recipe.totalSize() > MAX_ORDERED_ITEMS) {
                return DataResult.error(() -> "Ordered cutting board assembly holds at most " + MAX_ORDERED_ITEMS + " items including the finisher");
            }
            return DataResult.success(recipe);
        });

        public static final StreamCodec<RegistryFriendlyByteBuf, CuttingBoardAssemblyRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), CuttingBoardAssemblyRecipe::getBaseIngredients,
                ByteBufCodecs.optional(Ingredient.CONTENTS_STREAM_CODEC), CuttingBoardAssemblyRecipe::getFinisher,
                ItemStack.STREAM_CODEC, CuttingBoardAssemblyRecipe::getResult,
                ByteBufCodecs.BOOL, CuttingBoardAssemblyRecipe::isOrdered,
                CuttingBoardAssemblyRecipe::new
        );

        @Override
        public @NotNull MapCodec<CuttingBoardAssemblyRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, CuttingBoardAssemblyRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

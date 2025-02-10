package net.satisfy.farm_and_charm.core.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

public class StoveRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final NonNullList<Ingredient> inputs;
    private final ItemStack output;
    private final float experience;
    private final boolean requiresLearning;

    public StoveRecipe(ResourceLocation id, NonNullList<Ingredient> inputs, ItemStack output, float experience, boolean requiresLearning) {
        this.id = id;
        this.inputs = inputs;
        this.output = output;
        this.experience = experience;
        this.requiresLearning = requiresLearning;
    }

    @Override
    public boolean matches(Container inventory, Level world) {
        return GeneralUtil.matchesRecipe(inventory, inputs, 1, 3);
    }

    @Override
    public @NotNull ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return this.output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryAccess) {
        return this.output.copy();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.id;
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
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.inputs;
    }

    public float getExperience() {
        return experience;
    }

    public boolean requiresLearning() {
        return requiresLearning;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<StoveRecipe> {
        @Override
        public @NotNull StoveRecipe fromJson(ResourceLocation id, JsonObject json) {
            final var ingredients = GeneralUtil.deserializeIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for Stove Recipe");
            } else if (ingredients.size() > 3) {
                throw new JsonParseException("Too many ingredients for Stove Recipe");
            } else {
                boolean requiresLearning = GsonHelper.getAsBoolean(json, "requiresLearning", false);
                return new StoveRecipe(id, ingredients,
                        ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result")),
                        GsonHelper.getAsFloat(json, "experience", 0.0F),
                        requiresLearning
                );
            }
        }

        @Override
        public @NotNull StoveRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            final var ingredients = NonNullList.withSize(buf.readVarInt(), Ingredient.EMPTY);
            ingredients.replaceAll(ignored -> Ingredient.fromNetwork(buf));
            ItemStack output = buf.readItem();
            float xp = buf.readFloat();
            boolean requiresLearning = buf.readBoolean();
            return new StoveRecipe(id, ingredients, output, xp, requiresLearning);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, StoveRecipe recipe) {
            buf.writeVarInt(recipe.inputs.size());
            recipe.inputs.forEach(entry -> entry.toNetwork(buf));
            buf.writeItem(recipe.output);
            buf.writeFloat(recipe.experience);
            buf.writeBoolean(recipe.requiresLearning);
        }
    }
}

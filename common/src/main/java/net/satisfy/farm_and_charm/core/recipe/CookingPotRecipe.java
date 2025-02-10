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
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

public class CookingPotRecipe implements Recipe<Container> {
    final ResourceLocation id;
    private final NonNullList<Ingredient> inputs;
    private final boolean containerRequired;
    private final ItemStack containerItem;
    private final ItemStack output;
    private final boolean requiresLearning;

    public CookingPotRecipe(ResourceLocation id, NonNullList<Ingredient> inputs, boolean containerRequired, ItemStack containerItem, ItemStack output, boolean requiresLearning) {
        this.id = id;
        this.inputs = inputs;
        this.containerRequired = containerRequired;
        this.containerItem = containerItem;
        this.output = output;
        this.requiresLearning = requiresLearning;
    }

    @Override
    public boolean matches(Container inventory, Level world) {
        return GeneralUtil.matchesRecipe(inventory, inputs, 0, 6);
    }

    @Override
    public @NotNull ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return ItemStack.EMPTY;
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
        return id;
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

    public boolean requiresLearning() {
        return requiresLearning;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<CookingPotRecipe> {
        @Override
        public @NotNull CookingPotRecipe fromJson(ResourceLocation id, JsonObject json) {
            NonNullList<Ingredient> ingredients = GeneralUtil.deserializeIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for CookingPot Recipe");
            } else if (ingredients.size() > 6) {
                throw new JsonParseException("Too many ingredients for CookingPot Recipe");
            }
            JsonObject containerObj = GsonHelper.getAsJsonObject(json, "container");
            boolean required = GsonHelper.getAsBoolean(containerObj, "required", false);
            ItemStack containerStack = ItemStack.EMPTY;
            if (required) {
                containerStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(containerObj, "item"));
            }
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            boolean requiresLearning = GsonHelper.getAsBoolean(json, "requiresLearning", false);
            return new CookingPotRecipe(id, ingredients, required, containerStack, result, requiresLearning);
        }

        @Override
        public @NotNull CookingPotRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> ingredients = NonNullList.withSize(buf.readVarInt(), Ingredient.EMPTY);
            ingredients.replaceAll(ignored -> Ingredient.fromNetwork(buf));
            boolean required = buf.readBoolean();
            ItemStack containerStack = required ? buf.readItem() : ItemStack.EMPTY;
            ItemStack output = buf.readItem();
            boolean requiresLearning = buf.readBoolean();
            return new CookingPotRecipe(id, ingredients, required, containerStack, output, requiresLearning);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CookingPotRecipe recipe) {
            buf.writeVarInt(recipe.inputs.size());
            recipe.inputs.forEach(entry -> entry.toNetwork(buf));
            buf.writeBoolean(recipe.containerRequired);
            if (recipe.containerRequired) {
                buf.writeItem(recipe.containerItem);
            }
            buf.writeItem(recipe.output);
            buf.writeBoolean(recipe.requiresLearning);
        }
    }
}

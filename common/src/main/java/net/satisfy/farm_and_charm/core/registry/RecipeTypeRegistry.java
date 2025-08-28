package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.recipe.*;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

import java.util.function.Supplier;

public class RecipeTypeRegistry {
    private static final Registrar<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.RECIPE_TYPE).getRegistrar();
    private static final Registrar<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.RECIPE_SERIALIZER).getRegistrar();
    public static final RegistrySupplier<RecipeType<CookingPotRecipe>> COOKING_POT_RECIPE_TYPE = create("pot_cooking");
    public static final RegistrySupplier<RecipeType<CraftingBowlRecipe>> CRAFTING_BOWL_RECIPE_TYPE = create("crafting_bowl");
    public static final RegistrySupplier<RecipeType<StoveRecipe>> STOVE_RECIPE_TYPE = create("stove");
    public static final RegistrySupplier<RecipeType<MincerRecipe>> MINCER_RECIPE_TYPE = create("mincer");
    public static final RegistrySupplier<RecipeType<RoasterRecipe>> ROASTER_RECIPE_TYPE = create("roaster");
    public static final RegistrySupplier<RecipeType<SiloRecipe>> SILO_RECIPE_TYPE = create("drying");
    public static final RegistrySupplier<RecipeSerializer<CookingPotRecipe>> COOKING_POT_RECIPE_SERIALIZER = create("pot_cooking", CookingPotRecipe.Serializer::new);
    public static final RegistrySupplier<RecipeSerializer<CraftingBowlRecipe>> CRAFTING_BOWL_RECIPE_SERIALIZER = create("crafting_bowl", CraftingBowlRecipe.Serializer::new);
    public static final RegistrySupplier<RecipeSerializer<StoveRecipe>> STOVE_RECIPE_SERIALIZER = create("stove", StoveRecipe.Serializer::new);
    public static final RegistrySupplier<RecipeSerializer<MincerRecipe>> MINCER_RECIPE_SERIALIZER = create("mincer", MincerRecipe.Serializer::new);
    public static final RegistrySupplier<RecipeSerializer<RoasterRecipe>> ROASTER_RECIPE_SERIALIZER = create("roaster", RoasterRecipe.Serializer::new);
    public static final RegistrySupplier<RecipeSerializer<SiloRecipe>> SILO_RECIPE_SERIALIZER = create("drying", SiloRecipe.Serializer::new);


    private static <T extends Recipe<?>> RegistrySupplier<RecipeSerializer<T>> create(String name, Supplier<RecipeSerializer<T>> serializer) {
        return RECIPE_SERIALIZERS.register(FarmAndCharmIdentifier.of(name), serializer);
    }

    private static <T extends Recipe<?>> RegistrySupplier<RecipeType<T>> create(String name) {
        Supplier<RecipeType<T>> type = () -> new RecipeType<>() {
            @Override
            public String toString() {
                return name;
            }
        };
        return RECIPE_TYPES.register(FarmAndCharmIdentifier.of(name), type);
    }

    public static void init() {
    }
}

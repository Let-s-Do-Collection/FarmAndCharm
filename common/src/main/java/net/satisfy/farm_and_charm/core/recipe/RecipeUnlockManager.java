package net.satisfy.farm_and_charm.core.recipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import java.util.*;

public class RecipeUnlockManager {
    private static final Map<UUID, Set<ResourceLocation>> unlockedRecipes = new HashMap<>();

    @SuppressWarnings("unused")
    public static void unlockRecipes(ServerPlayer player, List<Recipe<?>> recipes) {
        UUID uuid = player.getUUID();
        Set<ResourceLocation> set = unlockedRecipes.computeIfAbsent(uuid, k -> new HashSet<>());
        for (Recipe<?> recipe : recipes) {
            set.add(recipe.getId());
        }
    }

    public static boolean isRecipeUnlocked(ServerPlayer player, ResourceLocation recipeId) {
        return unlockedRecipes.getOrDefault(player.getUUID(), Collections.emptySet()).contains(recipeId);
    }
}

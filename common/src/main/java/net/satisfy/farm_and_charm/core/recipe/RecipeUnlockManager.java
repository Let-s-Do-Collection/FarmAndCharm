package net.satisfy.farm_and_charm.core.recipe;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import java.util.*;

public class RecipeUnlockManager {

    @SuppressWarnings("unused")
    public static void unlockRecipes(ServerPlayer player, List<ResourceKey<Recipe<?>>> recipes) {
        Set<ResourceKey<Recipe<?>>> unlocked = loadUnlockedRecipes(player);
        unlocked.addAll(recipes);
        saveUnlockedRecipes(player, unlocked);
    }

    public static boolean isRecipeLocked(ServerPlayer player, ResourceKey<Recipe<?>> recipeId) {
        return !loadUnlockedRecipes(player).contains(recipeId);
    }

    public static void saveUnlockedRecipes(ServerPlayer player, Set<ResourceKey<Recipe<?>>> recipes) {
        ServerLevel level = (ServerLevel) player.getCommandSenderWorld();
        RecipeUnlockSavedData data = level.getDataStorage()
                .computeIfAbsent(RecipeUnlockSavedData.factory, RecipeUnlockSavedData.DATA_NAME);
        data.setPlayerRecipes(player.getUUID(), recipes);
    }

    public static Set<ResourceKey<Recipe<?>>> loadUnlockedRecipes(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.getCommandSenderWorld();
        RecipeUnlockSavedData data = level.getDataStorage()
                .computeIfAbsent(RecipeUnlockSavedData.factory, RecipeUnlockSavedData.DATA_NAME);
        return data.getPlayerRecipes(player.getUUID());
    }
}

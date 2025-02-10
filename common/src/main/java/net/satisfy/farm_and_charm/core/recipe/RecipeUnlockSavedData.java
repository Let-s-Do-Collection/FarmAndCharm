package net.satisfy.farm_and_charm.core.recipe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RecipeUnlockSavedData extends SavedData {
    public static final String DATA_NAME = "farm_and_charm_recipe_unlock_data";
    private final Map<UUID, Set<ResourceLocation>> playerRecipes = new HashMap<>();

    public RecipeUnlockSavedData() {}

    public static RecipeUnlockSavedData fromNbt(CompoundTag tag) {
        RecipeUnlockSavedData data = new RecipeUnlockSavedData();
        CompoundTag playersTag = tag.getCompound("players");
        for (String key : playersTag.getAllKeys()) {
            ListTag list = playersTag.getList(key, 8);
            Set<ResourceLocation> recipes = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                recipes.add(new ResourceLocation(list.getString(i)));
            }
            try {
                UUID uuid = UUID.fromString(key);
                data.playerRecipes.put(uuid, recipes);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return data;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        CompoundTag playersTag = new CompoundTag();
        for (Map.Entry<UUID, Set<ResourceLocation>> entry : playerRecipes.entrySet()) {
            ListTag list = new ListTag();
            for (ResourceLocation recipe : entry.getValue()) {
                list.add(StringTag.valueOf(recipe.toString()));
            }
            playersTag.put(entry.getKey().toString(), list);
        }
        tag.put("players", playersTag);
        return tag;
    }

    public Set<ResourceLocation> getPlayerRecipes(UUID uuid) {
        return playerRecipes.computeIfAbsent(uuid, k -> new HashSet<>());
    }

    public void setPlayerRecipes(UUID uuid, Set<ResourceLocation> recipes) {
        playerRecipes.put(uuid, recipes);
        setDirty();
    }
}

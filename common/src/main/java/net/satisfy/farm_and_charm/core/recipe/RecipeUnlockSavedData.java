package net.satisfy.farm_and_charm.core.recipe;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RecipeUnlockSavedData extends SavedData {
    private final Map<UUID, Set<ResourceLocation>> playerRecipes = new HashMap<>();
    public static final String DATA_NAME = "farm_and_charm_recipe_unlock_data";

    public RecipeUnlockSavedData() {}

    public RecipeUnlockSavedData(CompoundTag compoundTag, HolderLookup.Provider provider) {
    }

    public static RecipeUnlockSavedData fromNbt(CompoundTag tag) {
        RecipeUnlockSavedData data = new RecipeUnlockSavedData();
        CompoundTag playersTag = tag.getCompound("players");
        for (String key : playersTag.getAllKeys()) {
            ListTag list = playersTag.getList(key, 8);
            Set<ResourceLocation> recipes = new HashSet<>();
            for (int i = 0; i < list.size(); i++) {
                recipes.add(ResourceLocation.parse(list.getString(i)));
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
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        CompoundTag playersTag = new CompoundTag();
        for (Map.Entry<UUID, Set<ResourceLocation>> entry : playerRecipes.entrySet()) {
            ListTag list = new ListTag();
            for (ResourceLocation recipe : entry.getValue()) {
                list.add(StringTag.valueOf(recipe.toString()));
            }
            playersTag.put(entry.getKey().toString(), list);
        }
        compoundTag.put("players", playersTag);
        return compoundTag;
    }

    public Set<ResourceLocation> getPlayerRecipes(UUID uuid) {
        return playerRecipes.computeIfAbsent(uuid, k -> new HashSet<>());
    }

    public void setPlayerRecipes(UUID uuid, Set<ResourceLocation> recipes) {
        playerRecipes.put(uuid, recipes);
        setDirty();
    }

    public static Factory<RecipeUnlockSavedData> factory() {
        return new SavedData.Factory<RecipeUnlockSavedData>(RecipeUnlockSavedData::new, RecipeUnlockSavedData::new, DataFixTypes.SAVED_DATA_MAP_DATA);
    }
}
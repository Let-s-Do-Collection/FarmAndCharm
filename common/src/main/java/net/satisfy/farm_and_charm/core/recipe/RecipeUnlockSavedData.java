package net.satisfy.farm_and_charm.core.recipe;

import com.mojang.serialization.DataResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RecipeUnlockSavedData extends SavedData {
    public static final String DATA_NAME = "farm_and_charm_recipe_unlock_data";
    private Map<UUID, Set<ResourceKey<Recipe<?>>>> playerRecipes = new HashMap<>();

    // FAPI and NF both patch out null DataFixers, will run smoothly
    public static SavedData.Factory<RecipeUnlockSavedData> factory = new SavedData.Factory<>(
            RecipeUnlockSavedData::new, RecipeUnlockSavedData::load, null
    );

    public RecipeUnlockSavedData() {}

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag playersTag = !tag.contains("players", 10) ? new CompoundTag()
                : tag.getCompound("players"); // Player map exists, merge
        playerRecipes.forEach((uuid, recipes) -> {
            ListTag list = !tag.contains(uuid.toString(), 9) ? new ListTag()
                    : tag.getList(uuid.toString(), 8); // Player has recipes, merge
            recipes.stream().map(id -> {
                DataResult<Tag> encoded = ResourceKey.codec(Registries.RECIPE).encodeStart(NbtOps.INSTANCE, id);
                return encoded.getOrThrow();
            }).filter(t -> !list.contains(t)).forEach(list::add);
            playersTag.put(uuid.toString(), list);
        });
        tag.put("players", playersTag);
        return tag;
    }

    public static RecipeUnlockSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        RecipeUnlockSavedData decoded = new RecipeUnlockSavedData();

        assert tag.contains("players", 10) : "Players tag invalid";
        CompoundTag playersTag = !tag.contains("players", 10) ? new CompoundTag()
                : tag.getCompound("players");

        decoded.playerRecipes = playersTag.getAllKeys().stream()
                .collect(Collectors.toMap(
                        UUID::fromString,
                        pId -> {
                            ListTag list = playersTag.getList(pId, 8);
                            Set<ResourceKey<Recipe<?>>> recipes = new HashSet<>();
                            list.forEach(t -> {
                                DataResult<ResourceKey<Recipe<?>>> decodedRecipe =
                                        ResourceKey.codec(Registries.RECIPE).parse(NbtOps.INSTANCE, t);
                                recipes.add(decodedRecipe.getOrThrow());
                            });
                            return recipes;
                        }
                ));

        return decoded;
    }

    public Set<ResourceKey<Recipe<?>>> getPlayerRecipes(UUID uuid) {
        return playerRecipes.computeIfAbsent(uuid, k -> new HashSet<>());
    }

    public void setPlayerRecipes(UUID uuid, Set<ResourceKey<Recipe<?>>> recipes) {
        playerRecipes.put(uuid, recipes);
        setDirty();
    }
}

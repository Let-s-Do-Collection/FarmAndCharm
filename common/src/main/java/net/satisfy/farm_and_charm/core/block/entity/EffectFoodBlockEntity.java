package net.satisfy.farm_and_charm.core.block.entity;

import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.item.food.FoodEffectData;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class EffectFoodBlockEntity extends BlockEntity {
    public static final String STORED_EFFECTS_KEY = "StoredEffects";
    private FoodEffectData effectData;

    public EffectFoodBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EntityTypeRegistry.EFFECT_FOOD_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @SuppressWarnings("all")
    public void addEffects(List<FoodProperties.PossibleEffect> effects) {
        this.effectData = new FoodEffectData(effects.stream()
                .filter(e -> e.effect() != MobEffects.HUNGER).toList()
        );
    }

    public List<FoodProperties.PossibleEffect> getEffectData() {
        return effectData != null ? effectData.effects() : Lists.newArrayList();
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        this.effectData = FoodEffectData.CODEC.decode(
                NbtOps.INSTANCE, nbt.getList(STORED_EFFECTS_KEY, 10)
        ).getOrThrow().getFirst();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);
        if (effectData == null) return;
        DataResult<Tag> encoded = FoodEffectData.CODEC.encodeStart(NbtOps.INSTANCE, effectData);
        if (encoded.result().isPresent()) nbt.put(STORED_EFFECTS_KEY, encoded.getOrThrow());
    }
}



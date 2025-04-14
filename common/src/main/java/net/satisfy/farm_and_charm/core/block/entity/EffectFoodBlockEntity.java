package net.satisfy.farm_and_charm.core.block.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.PairCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.item.food.EffectFoodHelper;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class EffectFoodBlockEntity extends BlockEntity {
    public static final String STORED_EFFECTS_KEY = "StoredEffects";
    private List<Pair<MobEffectInstance, Float>> effects; // instance : chance map

    public static final Codec<List<Pair<MobEffectInstance, Float>>> effectsCodec = new PairCodec<>(
            MobEffectInstance.CODEC, Codec.FLOAT
    ).listOf();

    public EffectFoodBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(EntityTypeRegistry.EFFECT_FOOD_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    @SuppressWarnings("all")
    public void addEffects(List<Pair<MobEffectInstance, Float>> effects) {
        List<Pair<MobEffectInstance, Float>> filteredEffects = effects.stream()
                .filter(effectPair -> effectPair.getFirst().getEffect() != MobEffects.HUNGER)
                .collect(Collectors.toList());

        this.effects = filteredEffects;
    }

    public List<Pair<MobEffectInstance, Float>> getEffects() {
        return effects != null ? effects : Lists.newArrayList();
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        this.effects = effectsCodec.decode(
                NbtOps.INSTANCE, nbt != null ? nbt.getList(STORED_EFFECTS_KEY, 10) : new ListTag()
        ).getOrThrow().getFirst();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);
        if (effects == null) return;
        DataResult<Tag> encoded = effectsCodec.encodeStart(NbtOps.INSTANCE, effects);
        if (encoded.result().isPresent()) nbt.put(STORED_EFFECTS_KEY, encoded.getOrThrow());
    }
}



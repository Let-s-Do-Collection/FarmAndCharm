package net.satisfy.farm_and_charm.core.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.List;
import java.util.Optional;

public record AbandonedFieldConfiguration(List<Entry> entries, IntProvider width, IntProvider length, float pathChance, float matureChance, float decorationChance) implements FeatureConfiguration {
    public static final Codec<AbandonedFieldConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.nonEmptyList(Entry.CODEC.listOf()).fieldOf("entries").forGetter(AbandonedFieldConfiguration::entries),
            IntProvider.codec(2, 16).fieldOf("width").forGetter(AbandonedFieldConfiguration::width),
            IntProvider.codec(2, 16).fieldOf("length").forGetter(AbandonedFieldConfiguration::length),
            Codec.floatRange(0.0F, 1.0F).fieldOf("path_chance").forGetter(AbandonedFieldConfiguration::pathChance),
            Codec.floatRange(0.0F, 1.0F).fieldOf("mature_chance").forGetter(AbandonedFieldConfiguration::matureChance),
            Codec.floatRange(0.0F, 1.0F).fieldOf("decoration_chance").forGetter(AbandonedFieldConfiguration::decorationChance)
    ).apply(instance, AbandonedFieldConfiguration::new));

    public record Entry(Block crop, Optional<Block> decoration, int weight) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("crop").forGetter(Entry::crop),
                BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("decoration").forGetter(Entry::decoration),
                Codec.intRange(1, 1000).optionalFieldOf("weight", 1).forGetter(Entry::weight)
        ).apply(instance, Entry::new));
    }
}

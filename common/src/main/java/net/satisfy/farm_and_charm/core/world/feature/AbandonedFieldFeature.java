package net.satisfy.farm_and_charm.core.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.List;

public class AbandonedFieldFeature extends Feature<AbandonedFieldConfiguration> {
    private static final float MAX_INVALID_CORE_RATIO = 0.25F;
    private static final double CORE_RADIUS = 0.8;
    private static final double OUTER_RADIUS = 1.3;
    private static final double SHAPE_NOISE_SCALE = 0.22;
    private static final double SHAPE_NOISE_STRENGTH = 0.3;
    private static final double MATERIAL_NOISE_SCALE = 0.35;

    public AbandonedFieldFeature(Codec<AbandonedFieldConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<AbandonedFieldConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        AbandonedFieldConfiguration config = context.config();
        BlockPos origin = context.origin();
        int baseY = origin.getY() - 1;

        double radiusX = config.width().sample(random) / 2.0 + 0.5;
        double radiusZ = config.length().sample(random) / 2.0 + 0.5;
        if (random.nextBoolean()) {
            double swap = radiusX;
            radiusX = radiusZ;
            radiusZ = swap;
        }

        SimplexNoise shapeNoise = new SimplexNoise(random);
        SimplexNoise materialNoise = new SimplexNoise(random);
        int extentX = Mth.ceil(radiusX * OUTER_RADIUS) + 1;
        int extentZ = Mth.ceil(radiusZ * OUTER_RADIUS) + 1;

        int coreCells = 0;
        int invalidCore = 0;
        for (int dx = -extentX; dx <= extentX; dx++) {
            for (int dz = -extentZ; dz <= extentZ; dz++) {
                if (distance(shapeNoise, dx, dz, radiusX, radiusZ) > CORE_RADIUS) continue;
                coreCells++;
                if (findGround(level, origin.getX() + dx, origin.getZ() + dz, baseY) == null) invalidCore++;
            }
        }
        if (coreCells == 0 || invalidCore > coreCells * MAX_INVALID_CORE_RATIO) return false;

        AbandonedFieldConfiguration.Entry entry = pickEntry(config.entries(), random);
        BlockState cropDefault = entry.crop().defaultBlockState();
        BlockState path = ObjectRegistry.PACKED_DIRT.get().defaultBlockState();
        BlockState grass = Blocks.GRASS_BLOCK.defaultBlockState();
        double pathThreshold = config.pathChance() * 2.0 - 1.0;

        for (int dx = -extentX; dx <= extentX; dx++) {
            for (int dz = -extentZ; dz <= extentZ; dz++) {
                double distance = distance(shapeNoise, dx, dz, radiusX, radiusZ);
                if (distance > OUTER_RADIUS) continue;
                BlockPos ground = findGround(level, origin.getX() + dx, origin.getZ() + dz, baseY);
                if (ground == null) continue;

                double material = materialNoise.getValue(dx * MATERIAL_NOISE_SCALE, dz * MATERIAL_NOISE_SCALE) + (random.nextDouble() - 0.5) * 0.3;

                if (distance <= CORE_RADIUS) {
                    clearAbove(level, ground);
                    if (material < pathThreshold) {
                        level.setBlock(ground, path, 2);
                    } else {
                        level.setBlock(ground, Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, random.nextInt(8)), 2);
                        level.setBlock(ground.above(), withRandomAge(cropDefault, random, config.matureChance()), 2);
                    }
                    continue;
                }

                double fade = (distance - CORE_RADIUS) / (OUTER_RADIUS - CORE_RADIUS);
                if (random.nextDouble() < fade) continue;
                if (material < pathThreshold + 0.4) {
                    clearAbove(level, ground);
                    level.setBlock(ground, path, 2);
                } else if (!level.getBlockState(ground).is(Blocks.GRASS_BLOCK)) {
                    level.setBlock(ground, grass, 2);
                }
            }
        }

        if (entry.decoration().isPresent() && random.nextFloat() < config.decorationChance()) {
            placeDecorations(level, random, entry.decoration().get(), origin, baseY, radiusX, radiusZ);
        }
        return true;
    }

    private static double distance(SimplexNoise noise, int dx, int dz, double radiusX, double radiusZ) {
        double nx = dx / radiusX;
        double nz = dz / radiusZ;
        return Math.sqrt(nx * nx + nz * nz) + noise.getValue(dx * SHAPE_NOISE_SCALE, dz * SHAPE_NOISE_SCALE) * SHAPE_NOISE_STRENGTH;
    }

    private static BlockPos findGround(WorldGenLevel level, int x, int z, int baseY) {
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) - 1;
        if (Math.abs(y - baseY) > 1) return null;
        BlockPos ground = new BlockPos(x, y, z);
        BlockState state = level.getBlockState(ground);
        if (!state.is(BlockTags.DIRT) || state.is(Blocks.MUD) || state.is(Blocks.MUDDY_MANGROVE_ROOTS)) return null;
        BlockState above = level.getBlockState(ground.above());
        return above.isAir() || (above.canBeReplaced() && above.getFluidState().isEmpty()) ? ground : null;
    }

    private static void clearAbove(WorldGenLevel level, BlockPos ground) {
        for (int dy = 1; dy <= 2; dy++) {
            BlockPos pos = ground.above(dy);
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && state.canBeReplaced() && state.getFluidState().isEmpty()) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    private static AbandonedFieldConfiguration.Entry pickEntry(List<AbandonedFieldConfiguration.Entry> entries, RandomSource random) {
        int total = 0;
        for (AbandonedFieldConfiguration.Entry entry : entries) total += entry.weight();
        int roll = random.nextInt(total);
        for (AbandonedFieldConfiguration.Entry entry : entries) {
            roll -= entry.weight();
            if (roll < 0) return entry;
        }
        return entries.getFirst();
    }

    private static BlockState withRandomAge(BlockState state, RandomSource random, float matureChance) {
        for (Property<?> property : state.getProperties()) {
            if (property instanceof IntegerProperty age && property.getName().equals("age")) {
                int max = age.getPossibleValues().stream().max(Integer::compare).orElse(0);
                int value = max > 0 && random.nextFloat() >= matureChance ? random.nextInt(max) : max;
                return state.setValue(age, value);
            }
        }
        return state;
    }

    private static void placeDecorations(WorldGenLevel level, RandomSource random, Block decoration, BlockPos origin, int baseY, double radiusX, double radiusZ) {
        int count = 1 + random.nextInt(2);
        double angle = random.nextDouble() * Math.PI * 2.0;
        for (int attempt = 0; attempt < 10 && count > 0; attempt++) {
            double a = angle + (random.nextDouble() - 0.5) * 0.8;
            double scale = 1.0 + random.nextDouble() * 0.2;
            int x = origin.getX() + Mth.floor(Math.cos(a) * radiusX * scale);
            int z = origin.getZ() + Mth.floor(Math.sin(a) * radiusZ * scale);
            BlockPos ground = findGround(level, x, z, baseY);
            if (ground == null || level.getBlockState(ground).is(Blocks.FARMLAND)) continue;
            clearAbove(level, ground);
            level.setBlock(ground.above(), withRandomFacing(decoration.defaultBlockState(), random), 2);
            count--;
        }
    }

    private static BlockState withRandomFacing(BlockState state, RandomSource random) {
        for (Property<?> property : state.getProperties()) {
            if (property instanceof DirectionProperty facing && property.getName().equals("facing")) {
                Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                if (facing.getPossibleValues().contains(direction)) return state.setValue(facing, direction);
            }
        }
        return state;
    }
}

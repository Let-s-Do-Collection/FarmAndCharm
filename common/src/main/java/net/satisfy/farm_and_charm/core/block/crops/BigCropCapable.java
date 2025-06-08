package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

public interface BigCropCapable {
    int getMaxAge();
    IntegerProperty getAgeProperty();
    BooleanProperty getBigProperty();
    BooleanProperty getGiantProperty();

    default void tryTransformToBigCrop(Level level, BlockPos pos, BlockState state, boolean usedCompost) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        IntegerProperty age = getAgeProperty();
        BooleanProperty big = getBigProperty();
        BooleanProperty giant = getGiantProperty();

        if (!state.hasProperty(age) || !state.hasProperty(big) || !state.hasProperty(giant)) return;

        int currentAge = state.getValue(age);
        boolean isBig = state.getValue(big);
        boolean isGiant = state.getValue(giant);

        if (currentAge != getMaxAge()) return;

        boolean hasWater = hasWaterSourceNearby(level, pos);
        boolean isRaining = level.isRainingAt(pos.above());
        int lightLevel = level.getMaxLocalRawBrightness(pos.above());

        RandomSource random = level.getRandom();

        if (usedCompost) {
            if (!hasWater) return;

            if (!isBig && random.nextFloat() < 0.144f) {
                level.setBlock(pos, state.setValue(big, true), 2);
                spawnHappyParticles(serverLevel, pos);
            } else if (isBig && !isGiant && random.nextFloat() < 0.1f) {
                level.setBlock(pos, state.setValue(giant, true), 2);
                spawnHappyParticles(serverLevel, pos);
            }
        } else {
            long seed = pos.asLong();
            RandomSource staticRandom = RandomSource.create(seed);
            if (staticRandom.nextFloat() >= 0.25f) return;

            if (!isBig && isRaining && lightLevel > 12 && random.nextFloat() < 0.03f) {
                level.setBlock(pos, state.setValue(big, true), 2);
                spawnHappyParticles(serverLevel, pos);
            } else if (!isBig && hasWater && random.nextFloat() < 0.144f) {
                level.setBlock(pos, state.setValue(big, true), 2);
                spawnHappyParticles(serverLevel, pos);
            }
        }
    }

    private boolean hasWaterSourceNearby(Level level, BlockPos pos) {
        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (level.getFluidState(checkPos).is(FluidTags.WATER)) return true;
        }

        for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-2, 0, -2), pos.offset(2, 2, 2))) {
            if (level.getBlockState(checkPos).is(ObjectRegistry.WATER_SPRINKLER.get())) return true;
        }

        return false;
    }

    private void spawnHappyParticles(ServerLevel level, BlockPos pos) {
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.75;
        double z = pos.getZ() + 0.5;
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 8, 0.25, 0.25, 0.25, 0.0);
    }
}

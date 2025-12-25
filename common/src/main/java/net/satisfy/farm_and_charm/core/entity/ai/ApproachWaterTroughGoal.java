package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.WaterTroughBlock;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ApproachWaterTroughGoal extends MoveToBlockGoal {
    protected final Animal animal;
    private int drinkCooldownTicks;

    public ApproachWaterTroughGoal(Animal animal, double speed) {
        super(animal, speed, PlatformHelper.getFeedingTroughRange());
        this.animal = animal;
    }

    @Override
    public void tick() {
        Level level = this.animal.level();
        if (!level.isClientSide && drinkCooldownTicks > 0) {
            drinkCooldownTicks--;
        }

        if (!level.isClientSide) {
            BlockState state = level.getBlockState(this.blockPos);
            if (drinkCooldownTicks <= 0 && state.getBlock() instanceof WaterTroughBlock && state.getValue(WaterTroughBlock.LEVEL) > 0) {
                this.animal.getLookControl().setLookAt(this.blockPos.getX() + 0.5D, this.blockPos.getY(), this.blockPos.getZ() + 0.5D, 10.0F, this.animal.getMaxHeadXRot());
                if (this.isReachedTarget()) {
                    boolean drank = drainConnected(level, this.blockPos, state);
                    if (drank) {
                        drinkCooldownTicks = 200 + level.random.nextInt(200);
                    } else {
                        drinkCooldownTicks = 60 + level.random.nextInt(80);
                    }
                }
            }
        }

        super.tick();
    }

    @Override
    public boolean canUse() {
        return drinkCooldownTicks <= 0 && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return drinkCooldownTicks <= 0 && super.canContinueToUse();
    }

    @Override
    public double acceptedDistance() {
        return 2.25D;
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        BlockState state = levelReader.getBlockState(blockPos);
        return state.getBlock() instanceof WaterTroughBlock && state.getValue(WaterTroughBlock.LEVEL) > 0;
    }

    private boolean drainConnected(Level level, BlockPos startPos, BlockState startState) {
        Direction facing = startState.getValue(WaterTroughBlock.FACING);
        List<BlockPos> component = getComponent(level, startPos, facing);

        boolean anyDrained = false;

        for (BlockPos componentPos : component) {
            BlockState state = level.getBlockState(componentPos);
            if (!(state.getBlock() instanceof WaterTroughBlock)) continue;
            if (state.getValue(WaterTroughBlock.FACING) != facing) continue;

            int currentLevel = state.getValue(WaterTroughBlock.LEVEL);
            if (currentLevel <= 0) continue;
            if (isInfiniteSource(component, componentPos, facing)) continue;

            level.setBlock(componentPos, state.setValue(WaterTroughBlock.LEVEL, currentLevel - 1), 3);
            anyDrained = true;
        }

        if (anyDrained) {
            int age = this.animal.getAge();
            if (age > 0) {
                this.animal.setAge(Math.max(0, age - 400));
            }

            double x = this.blockPos.getX() + 0.5D;
            double y = this.blockPos.getY() + 0.25D;
            double z = this.blockPos.getZ() + 0.5D;

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SPLASH, x, y, z, 8, 0.20D, 0.05D, 0.20D, 0.02D);
            }

            level.playSound(null, x, y, z, SoundEvents.GENERIC_SPLASH, SoundSource.NEUTRAL, 0.4F, 0.9F + level.random.nextFloat() * 0.2F);
        }

        return anyDrained;
    }

    private List<BlockPos> getComponent(Level level, BlockPos start, Direction facing) {
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();

        HashSet<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        ArrayList<BlockPos> result = new ArrayList<>();

        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            result.add(current);

            for (Direction step : new Direction[]{left, right}) {
                BlockPos next = current.relative(step);
                if (!visited.add(next)) continue;

                BlockState nextState = level.getBlockState(next);
                if (!(nextState.getBlock() instanceof WaterTroughBlock)) continue;
                if (nextState.getValue(WaterTroughBlock.FACING) != facing) continue;

                queue.add(next);
            }
        }

        return result;
    }

    private boolean isInfiniteSource(List<BlockPos> component, BlockPos pos, Direction facing) {
        if (component.size() < 3) return false;

        boolean usesX = facing.getAxis() == Direction.Axis.Z;

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        for (BlockPos componentPos : component) {
            int value = usesX ? componentPos.getX() : componentPos.getZ();
            if (value < min) min = value;
            if (value > max) max = value;
        }

        int self = usesX ? pos.getX() : pos.getZ();
        return self != min && self != max;
    }
}
package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.EnumSet;

public class MeowAtBowlGoal extends Goal {
    private final Cat cat;
    private BlockPos bowlPos;
    private int meowTicks;
    private long lastCheckTime;
    private boolean active;

    private static final int CHECK_INTERVAL_TICKS = 40;
    private static final int MAX_MEOW_TICKS = 300;
    private static final int MEOW_INTERVAL = 60;
    private static final int ANGRY_PARTICLE_INTERVAL = 100;
    private static final double NAVIGATION_SPEED = 1.0;
    private static final double CLOSE_ENOUGH_DIST = 1.1;
    private static final double LOOK_OFFSET = 0.5;
    private static final long MORNING_START = 5800;
    private static final long MORNING_END = 6200;
    private static final long EVENING_START = 11500;
    private static final long EVENING_END = 12500;

    public MeowAtBowlGoal(Cat cat) {
        this.cat = cat;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.meowTicks = 0;
        this.lastCheckTime = -1;
        this.active = false;
    }

    @Override
    public boolean canUse() {
        if (!cat.isTame() || cat.isOrderedToSit()) return false;
        Level level = cat.level();
        if (!(level instanceof ServerLevel server)) return false;

        long gameTime = server.getGameTime();
        if (lastCheckTime != -1 && gameTime - lastCheckTime < CHECK_INTERVAL_TICKS) return false;
        lastCheckTime = gameTime;

        long timeOfDay = server.getDayTime() % 24000;
        if (timeOfDay < MORNING_START || (timeOfDay > MORNING_END && timeOfDay < EVENING_START) || timeOfDay > EVENING_END)
            return false;

        BlockPos catPos = cat.blockPosition();
        double closestDistance = Double.MAX_VALUE;
        BlockPos closest = null;

        for (BlockPos pos : BlockPos.betweenClosed(catPos.offset(-32, -4, -32), catPos.offset(32, 4, 32))) {
            if (!level.getBlockState(pos).is(ObjectRegistry.PET_BOWL.get()) || !level.getBlockState(pos).hasBlockEntity()) continue;
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PetBowlBlockEntity bowl && bowl.isEmpty() && bowl.canBeUsedBy(cat)) {
                double dist = cat.position().distanceToSqr(Vec3.atCenterOf(pos));
                if (dist < closestDistance) {
                    closestDistance = dist;
                    closest = pos.immutable();
                }
            }
        }

        if (closest != null) {
            bowlPos = closest;
            return true;
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (!active || meowTicks >= MAX_MEOW_TICKS || bowlPos == null) return false;
        Level level = cat.level();
        if (!(level instanceof ServerLevel server)) return false;
        BlockEntity be = server.getBlockEntity(bowlPos);
        return be instanceof PetBowlBlockEntity bowl && bowl.isEmpty() && bowl.canBeUsedBy(cat);
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        if (bowlPos != null) {
            if (!isNearBowl()) {
                cat.getNavigation().moveTo(bowlPos.getX() + LOOK_OFFSET, bowlPos.getY(), bowlPos.getZ() + LOOK_OFFSET, NAVIGATION_SPEED);
            }
            meowTicks = 0;
            active = true;
        }
    }

    @Override
    public void tick() {
        if (bowlPos == null) {
            stop();
            return;
        }

        Level level = cat.level();
        if (!(level instanceof ServerLevel server)) {
            stop();
            return;
        }

        BlockEntity be = server.getBlockEntity(bowlPos);
        if (!(be instanceof PetBowlBlockEntity bowl) || !bowl.isEmpty()) {
            stop();
            return;
        }

        if (isNearBowl()) {
            if (cat.getNavigation().isInProgress()) {
                cat.getNavigation().stop();
            }
            if (!cat.isOrderedToSit()) {
                cat.setOrderedToSit(true);
            }
            cat.getLookControl().setLookAt(bowlPos.getX() + LOOK_OFFSET, bowlPos.getY() + LOOK_OFFSET, bowlPos.getZ() + LOOK_OFFSET);
        } else if (!cat.getNavigation().isInProgress()) {
            cat.getNavigation().moveTo(bowlPos.getX() + LOOK_OFFSET, bowlPos.getY(), bowlPos.getZ() + LOOK_OFFSET, NAVIGATION_SPEED);
        }

        if (meowTicks % MEOW_INTERVAL == 0) {
            cat.playSound(SoundEvents.CAT_BEG_FOR_FOOD, 1.0f, 1.0f);
        }

        if (meowTicks % ANGRY_PARTICLE_INTERVAL == 0) {
            Vec3 pos = cat.position().add(0, LOOK_OFFSET, 0);
            server.sendParticles(ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y, pos.z, 6, 0.3, 0.3, 0.3, 0.01);
        }

        if (++meowTicks >= MAX_MEOW_TICKS) {
            cat.playSound(SoundEvents.CAT_HISS, 1.0f, 1.0f);
            Vec3 pos = cat.position().add(0, LOOK_OFFSET, 0);
            server.sendParticles(ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y, pos.z, 15, 0.3, 0.3, 0.3, 0.01);
            stop();
        }
    }

    @Override
    public void stop() {
        bowlPos = null;
        meowTicks = 0;
        active = false;
        if (cat.isOrderedToSit()) {
            cat.setOrderedToSit(false);
        }
    }

    private boolean isNearBowl() {
        return bowlPos != null && cat.position().distanceToSqr(Vec3.atCenterOf(bowlPos)) < CLOSE_ENOUGH_DIST * CLOSE_ENOUGH_DIST;
    }
}

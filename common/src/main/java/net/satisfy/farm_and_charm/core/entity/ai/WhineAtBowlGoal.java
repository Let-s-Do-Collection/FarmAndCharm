package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.EnumSet;
import java.util.List;

public class WhineAtBowlGoal extends Goal {
    private final Wolf wolf;
    private BlockPos bowlPos;
    private Vec3 lastTargetPos;
    private int whineTicks;
    private int fadeOutTicks;
    private long lastScanTick;
    private long lastWhineSoundTick;
    private boolean active;

    private static final int SCAN_INTERVAL_TICKS = 40;
    private static final int MAX_WHINE_TICKS = 300;
    private static final int WHINE_INTERVAL = 60;
    private static final int ANGRY_PARTICLE_INTERVAL = 100;
    private static final int WHINE_PARTICLE_COUNT = 6;
    private static final int FINAL_PARTICLE_COUNT = 15;
    private static final int FADE_OUT_DURATION = 30;
    private static final double BASE_SPEED = 1.0;
    private static final double EVENING_SPEED_FACTOR = 0.8;
    private static final double CLOSE_ENOUGH_DIST = 1.1;
    private static final float BASE_VOLUME = 0.4f;
    private static final float BASE_PITCH = 0.4f;
    private static final int LOOK_YAW = 10;
    private static final int LOOK_PITCH = 30;
    private static final int RANGE_XZ = 10;
    private static final int RANGE_Y = 2;
    private static final long MORNING_START = 5800;
    private static final long MORNING_END = 6200;
    private static final long EVENING_START = 11500;
    private static final long EVENING_END = 12500;
    private static final double NAVIGATION_RECALC_THRESHOLD_SQR = 0.5;

    private static final List<SoundEvent> WHINE_SOUNDS = List.of(
            SoundEvents.WOLF_WHINE,
            SoundEvents.WOLF_PANT
    );

    public WhineAtBowlGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.whineTicks = 0;
        this.lastScanTick = -SCAN_INTERVAL_TICKS;
        this.lastWhineSoundTick = -WHINE_INTERVAL;
        this.active = false;
        this.lastTargetPos = null;
    }

    @Override
    public boolean canUse() {
        if (!wolf.isAlive() || !wolf.isTame() || wolf.isOrderedToSit()) return false;
        if (!(wolf.level() instanceof ServerLevel server)) return false;
        if (!shouldScanForBowl(server.getGameTime())) return false;
        if (!isValidWhineTime(server.getDayTime())) return false;

        BlockPos wolfPos = wolf.blockPosition();
        double closestDistance = Double.MAX_VALUE;
        BlockPos closest = null;

        for (BlockPos pos : BlockPos.betweenClosed(wolfPos.offset(-RANGE_XZ, -RANGE_Y, -RANGE_XZ), wolfPos.offset(RANGE_XZ, RANGE_Y, RANGE_XZ))) {
            if (!wolf.level().getBlockState(pos).is(ObjectRegistry.PET_BOWL.get())) continue;
            BlockEntity be = wolf.level().getBlockEntity(pos);
            if (be instanceof PetBowlBlockEntity bowl && bowl.isEmpty() && bowl.canBeUsedBy(wolf)) {
                double dist = wolf.position().distanceToSqr(Vec3.atCenterOf(pos));
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
        if (!active || !wolf.isAlive() || bowlPos == null) return false;
        if (!(wolf.level() instanceof ServerLevel server)) return false;
        BlockEntity be = server.getBlockEntity(bowlPos);
        return be instanceof PetBowlBlockEntity bowl && bowl.isEmpty() && bowl.canBeUsedBy(wolf);
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        if (!(wolf.level() instanceof ServerLevel server)) return;
        BlockEntity be = server.getBlockEntity(bowlPos);
        if (!(be instanceof PetBowlBlockEntity bowl) || !bowl.isEmpty() || !bowl.canBeUsedBy(wolf)) {
            stop();
            return;
        }
        wolf.setOrderedToSit(false);
        moveToBowl(server.getDayTime());
        whineTicks = 0;
        fadeOutTicks = 0;
        lastWhineSoundTick = -WHINE_INTERVAL;
        active = true;
    }

    @Override
    public void tick() {
        if (!(wolf.level() instanceof ServerLevel server) || !wolf.isAlive()) {
            stop();
            return;
        }

        BlockEntity be = server.getBlockEntity(bowlPos);
        if (!(be instanceof PetBowlBlockEntity bowl) || !bowl.isEmpty()) {
            stop();
            return;
        }

        Vec3 bowlCenter = Vec3.atCenterOf(bowlPos);
        if (isNearBowl()) {
            if (wolf.getNavigation().isInProgress()) {
                wolf.getNavigation().stop();
            }
            if (!wolf.isInSittingPose()) {
                wolf.setOrderedToSit(true);
            }
            wolf.getLookControl().setLookAt(bowlCenter.x, bowlCenter.y, bowlCenter.z, LOOK_YAW, LOOK_PITCH);
        } else {
            if (wolf.isInSittingPose()) {
                wolf.setOrderedToSit(false);
            }
            if (!wolf.getNavigation().isInProgress() || needsRepath(bowlCenter)) {
                moveToBowl(server.getDayTime());
            }
        }

        if (whineTicks - lastWhineSoundTick >= WHINE_INTERVAL) {
            playWhineSound();
            lastWhineSoundTick = whineTicks;
        }

        if (whineTicks % ANGRY_PARTICLE_INTERVAL == 0) {
            Vec3 pos = wolf.position().add(0, 0.5, 0);
            server.sendParticles(ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y, pos.z, WHINE_PARTICLE_COUNT, 0.3, 0.3, 0.3, 0.01);
        }

        LivingEntity owner = wolf.getOwner();
        if (owner != null && wolf.distanceTo(owner) < 4.0 && server.getRandom().nextInt(100) < 10) {
            wolf.playSound(SoundEvents.WOLF_WHINE, 0.6f, 0.9f + server.getRandom().nextFloat() * 0.2f);
        }

        if (++whineTicks >= MAX_WHINE_TICKS) {
            if (fadeOutTicks < FADE_OUT_DURATION) {
                fadeOutTicks++;
                return;
            }
            BlockEntity currentBowl = server.getBlockEntity(bowlPos);
            if (currentBowl instanceof PetBowlBlockEntity finalBowl && finalBowl.isEmpty()) {
                wolf.playSound(SoundEvents.WOLF_GROWL, BASE_VOLUME, BASE_PITCH);
                Vec3 pos = wolf.position().add(0, 0.5, 0);
                server.sendParticles(ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y, pos.z, FINAL_PARTICLE_COUNT, 0.3, 0.3, 0.3, 0.01);
            }
            stop();
        }
    }

    @Override
    public void stop() {
        bowlPos = null;
        whineTicks = 0;
        fadeOutTicks = 0;
        active = false;
        lastTargetPos = null;
        wolf.setOrderedToSit(false);
        if (wolf.getNavigation().isInProgress()) {
            wolf.getNavigation().stop();
        }
    }

    private void playWhineSound() {
        SoundEvent sound = WHINE_SOUNDS.get(wolf.getRandom().nextInt(WHINE_SOUNDS.size()));
        float volume = BASE_VOLUME + wolf.getRandom().nextFloat() * 0.3f;
        float pitch = BASE_PITCH + wolf.getRandom().nextFloat() * 0.4f;
        wolf.playSound(sound, volume, pitch);
    }

    private boolean isNearBowl() {
        return bowlPos != null && wolf.position().distanceToSqr(Vec3.atCenterOf(bowlPos)) < CLOSE_ENOUGH_DIST * CLOSE_ENOUGH_DIST;
    }

    private boolean shouldScanForBowl(long currentTick) {
        if (currentTick - lastScanTick < SCAN_INTERVAL_TICKS) return false;
        lastScanTick = currentTick;
        return true;
    }

    private boolean isValidWhineTime(long timeOfDay) {
        long dayTime = timeOfDay % 24000;
        return (dayTime >= MORNING_START && dayTime <= MORNING_END) || (dayTime >= EVENING_START && dayTime <= EVENING_END);
    }

    private void moveToBowl(long timeOfDay) {
        if (bowlPos != null) {
            Vec3 target = Vec3.atCenterOf(bowlPos);
            wolf.getNavigation().moveTo(target.x, target.y, target.z, getSpeed(timeOfDay));
            lastTargetPos = target;
        }
    }

    private boolean needsRepath(Vec3 target) {
        return lastTargetPos == null || lastTargetPos.distanceToSqr(target) > NAVIGATION_RECALC_THRESHOLD_SQR;
    }

    private double getSpeed(long timeOfDay) {
        long dayTime = timeOfDay % 24000;
        if (dayTime >= EVENING_START && dayTime <= EVENING_END) return BASE_SPEED * EVENING_SPEED_FACTOR;
        return BASE_SPEED;
    }
}

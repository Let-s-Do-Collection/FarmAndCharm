package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

import java.util.EnumSet;

public abstract class BegAtBowlGoal<T extends TamableAnimal> extends Goal {
    private static final int SCAN_INTERVAL_TICKS = 40;
    private static final int MAX_BEG_TICKS = 300;
    private static final int BEG_SOUND_INTERVAL = 60;
    private static final int ANGRY_PARTICLE_INTERVAL = 100;
    private static final int BEG_PARTICLE_COUNT = 6;
    private static final int GIVE_UP_PARTICLE_COUNT = 15;
    private static final int SEARCH_RANGE_Y = 4;
    private static final double CLOSE_ENOUGH_DIST = 1.1;

    protected final T animal;
    protected BlockPos bowlPos;
    private int begTicks;
    private long lastScanTick = -SCAN_INTERVAL_TICKS;

    protected BegAtBowlGoal(T animal) {
        this.animal = animal;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    protected abstract boolean isEnabled();

    protected abstract void playBegSound();

    protected abstract void playGiveUpSound();

    protected double getSpeed(long dayTime) {
        return 1.0;
    }

    protected void onBegTick(ServerLevel level) {
    }

    public static boolean isFeedingTime(long dayTime) {
        long time = dayTime % 24000;
        return isBetween(time, PlatformHelper.getFeedingTimeMiddayStart(), PlatformHelper.getFeedingTimeMiddayEnd())
                || isBetween(time, PlatformHelper.getFeedingTimeEveningStart(), PlatformHelper.getFeedingTimeEveningEnd());
    }

    public static boolean isEveningFeedingTime(long dayTime) {
        return isBetween(dayTime % 24000, PlatformHelper.getFeedingTimeEveningStart(), PlatformHelper.getFeedingTimeEveningEnd());
    }

    private static boolean isBetween(long time, int start, int end) {
        return start <= end ? time >= start && time <= end : time >= start || time <= end;
    }

    @Override
    public boolean canUse() {
        if (!this.isEnabled() || !this.animal.isAlive() || !this.animal.isTame() || this.animal.isOrderedToSit()) return false;
        if (!(this.animal.level() instanceof ServerLevel server)) return false;
        long gameTime = server.getGameTime();
        if (gameTime - this.lastScanTick < SCAN_INTERVAL_TICKS) return false;
        this.lastScanTick = gameTime;
        if (!isFeedingTime(server.getDayTime())) return false;

        int range = PlatformHelper.getPetBowlSearchRange();
        BlockPos origin = this.animal.blockPosition();
        double closestDistance = Double.MAX_VALUE;
        BlockPos closest = null;
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-range, -SEARCH_RANGE_Y, -range), origin.offset(range, SEARCH_RANGE_Y, range))) {
            if (!server.getBlockState(pos).is(ObjectRegistry.PET_BOWL.get())) continue;
            if (server.getBlockEntity(pos) instanceof PetBowlBlockEntity bowl && bowl.isEmpty() && bowl.canBeUsedBy(this.animal)) {
                double distance = this.animal.position().distanceToSqr(Vec3.atCenterOf(pos));
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closest = pos.immutable();
                }
            }
        }
        this.bowlPos = closest;
        return closest != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.bowlPos != null && this.animal.isAlive() && this.begTicks < MAX_BEG_TICKS && this.isBowlStillEmpty();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        this.begTicks = 0;
        this.animal.setOrderedToSit(false);
        this.moveToBowl();
    }

    @Override
    public void tick() {
        if (!(this.animal.level() instanceof ServerLevel server) || this.bowlPos == null || !this.isBowlStillEmpty()) {
            return;
        }

        Vec3 bowlCenter = Vec3.atCenterOf(this.bowlPos);
        if (this.isNearBowl()) {
            this.animal.getNavigation().stop();
            if (!this.animal.isOrderedToSit()) {
                this.animal.setOrderedToSit(true);
            }
            this.animal.getLookControl().setLookAt(bowlCenter.x, bowlCenter.y, bowlCenter.z);
        } else {
            if (this.animal.isOrderedToSit()) {
                this.animal.setOrderedToSit(false);
            }
            if (!this.animal.getNavigation().isInProgress()) {
                this.moveToBowl();
            }
        }

        if (this.begTicks % BEG_SOUND_INTERVAL == 0) {
            this.playBegSound();
        }
        if (this.begTicks % ANGRY_PARTICLE_INTERVAL == 0) {
            this.spawnAngryParticles(server, BEG_PARTICLE_COUNT);
        }
        this.onBegTick(server);

        if (++this.begTicks >= MAX_BEG_TICKS) {
            this.playGiveUpSound();
            this.spawnAngryParticles(server, GIVE_UP_PARTICLE_COUNT);
        }
    }

    @Override
    public void stop() {
        this.bowlPos = null;
        this.begTicks = 0;
        this.animal.setOrderedToSit(false);
        this.animal.getNavigation().stop();
    }

    private boolean isBowlStillEmpty() {
        BlockEntity blockEntity = this.animal.level().getBlockEntity(this.bowlPos);
        return blockEntity instanceof PetBowlBlockEntity bowl && bowl.isEmpty() && bowl.canBeUsedBy(this.animal);
    }

    private boolean isNearBowl() {
        return this.animal.position().distanceToSqr(Vec3.atCenterOf(this.bowlPos)) < CLOSE_ENOUGH_DIST * CLOSE_ENOUGH_DIST;
    }

    private void moveToBowl() {
        if (this.bowlPos != null) {
            Vec3 target = Vec3.atCenterOf(this.bowlPos);
            this.animal.getNavigation().moveTo(target.x, target.y, target.z, this.getSpeed(this.animal.level().getDayTime()));
        }
    }

    private void spawnAngryParticles(ServerLevel level, int count) {
        Vec3 pos = this.animal.position().add(0, 0.5, 0);
        level.sendParticles(ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y, pos.z, count, 0.3, 0.3, 0.3, 0.01);
    }
}

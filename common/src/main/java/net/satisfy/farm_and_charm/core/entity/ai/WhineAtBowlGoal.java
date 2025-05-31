package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.EnumSet;

public class WhineAtBowlGoal extends Goal {
    private final Wolf wolf;
    private BlockPos bowlPos;
    private int whineTicks;
    private long lastCheckTime;
    private int cooldownTicks;
    private boolean active;

    private static final int CHECK_INTERVAL_TICKS = 20;
    private static final int MAX_WHINE_TICKS = 300;
    private static final int WHINE_INTERVAL = 60;
    private static final int ANGRY_PARTICLE_INTERVAL = 100;

    public WhineAtBowlGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.whineTicks = 0;
        this.lastCheckTime = -1;
        this.cooldownTicks = 0;
        this.active = false;
    }

    @Override
    public boolean canUse() {
        if (cooldownTicks > 0) {
            cooldownTicks--;
            return false;
        }
        if (!wolf.isTame() || wolf.isOrderedToSit()) return false;
        Level level = wolf.level();
        if (!(level instanceof ServerLevel server)) return false;

        long gameTime = server.getGameTime();
        if (lastCheckTime != -1 && gameTime - lastCheckTime < CHECK_INTERVAL_TICKS) return false;

        long timeOfDay = server.getDayTime() % 24000;
        boolean at11000 = timeOfDay == 11000;
        boolean aroundMidday = timeOfDay >= 5800 && timeOfDay <= 6200;
        boolean hourBeforeSleep = timeOfDay >= 11500 && timeOfDay <= 12500;
        if (!(at11000 || aroundMidday || hourBeforeSleep)) return false;

        BlockPos wolfPos = wolf.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(wolfPos.offset(-32, -4, -32), wolfPos.offset(32, 4, 32))) {
            if (level.getBlockState(pos).is(ObjectRegistry.PET_BOWL.get())) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof PetBowlBlockEntity bowl && bowl.isEmpty() && bowl.canBeUsedBy(wolf)) {
                    bowlPos = pos.immutable();
                    lastCheckTime = gameTime;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (!active || whineTicks >= MAX_WHINE_TICKS) return false;
        Level level = wolf.level();
        if (level instanceof ServerLevel server && bowlPos != null) {
            BlockEntity be = server.getBlockEntity(bowlPos);
            return be instanceof PetBowlBlockEntity bowl && bowl.isEmpty();
        }
        return false;
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        if (bowlPos != null) {
            if (!wolf.blockPosition().closerThan(bowlPos, 1.1)) {
                wolf.getNavigation().moveTo(
                        bowlPos.getX() + 0.5,
                        bowlPos.getY(),
                        bowlPos.getZ() + 0.5,
                        1.0
                );
            }
            whineTicks = 0;
            active = true;
        }
    }

    @Override
    public void tick() {
        if (bowlPos == null) return;
        Level level = wolf.level();
        if (!(level instanceof ServerLevel server)) return;

        BlockEntity be = server.getBlockEntity(bowlPos);
        if (be instanceof PetBowlBlockEntity bowl && !bowl.isEmpty()) {
            stop();
            return;
        }

        if (wolf.blockPosition().closerThan(bowlPos, 1.1)) {
            if (wolf.getNavigation().isDone()) {
                if (!wolf.isOrderedToSit()) {
                    wolf.setOrderedToSit(true);
                }
            }
        } else {
            if (!wolf.getNavigation().isInProgress()) {
                wolf.getNavigation().moveTo(
                        bowlPos.getX() + 0.5,
                        bowlPos.getY(),
                        bowlPos.getZ() + 0.5,
                        1.0
                );
            }
        }

        if (whineTicks % WHINE_INTERVAL == 0) {
            wolf.playSound(SoundEvents.WOLF_WHINE, 0.4f, 0.4f);
        }

        if (whineTicks % ANGRY_PARTICLE_INTERVAL == 0) {
            Vec3 pos = wolf.position().add(0, 0.5, 0);
            server.sendParticles(ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y, pos.z,
                    6, 0.3, 0.3, 0.3, 0.01);
        }

        if (++whineTicks >= MAX_WHINE_TICKS) {
            wolf.playSound(SoundEvents.WOLF_GROWL, 0.4f, 0.4f);
            Vec3 pos = wolf.position().add(0, 0.5, 0);
            server.sendParticles(ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y, pos.z,
                    15, 0.3, 0.3, 0.3, 0.01);
            stop();
        }
    }

    @Override
    public void stop() {
        boolean bowlStillEmpty = true;
        if (bowlPos != null) {
            BlockEntity be = wolf.level().getBlockEntity(bowlPos);
            if (be instanceof PetBowlBlockEntity bowl && !bowl.isEmpty()) {
                bowlStillEmpty = false;
            }
        }

        bowlPos = null;
        whineTicks = 0;
        active = false;
        cooldownTicks = bowlStillEmpty ? 400 : CHECK_INTERVAL_TICKS;

        if (wolf.isOrderedToSit()) {
            wolf.setOrderedToSit(false);
        }

        if (!bowlStillEmpty) {
            wolf.playSound(SoundEvents.WOLF_PANT, 1.0f, 1.0f);
            if (wolf.level() instanceof ServerLevel server) {
                Vec3 pos = wolf.position().add(0, 0.5, 0);
                server.sendParticles(ParticleTypes.HEART, pos.x, pos.y, pos.z,
                        5, 0.3, 0.3, 0.3, 0.0);
            }
        }
    }
}

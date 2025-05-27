package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.EnumSet;

public class MeowAtBowlGoal extends Goal {
    private final Cat cat;
    private BlockPos bowlPos;
    private int meowTicks = 0;
    private long lastCheckTime = -1;
    private int cooldownTicks = 0;

    public MeowAtBowlGoal(Cat cat) {
        this.cat = cat;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (cooldownTicks > 0) return false;
        if (!cat.isTame() || cat.isOrderedToSit()) return false;

        Level level = cat.level();
        if (!(level instanceof ServerLevel serverLevel)) return false;

        long timeOfDay = serverLevel.getDayTime() % 24000;
        boolean isMorning = timeOfDay <= 2000;
        boolean isEvening = timeOfDay >= 11000 && timeOfDay <= 13000;

        if (!isMorning && !isEvening) return false;
        if (lastCheckTime == serverLevel.getDayTime()) return false;
        lastCheckTime = serverLevel.getDayTime();

        BlockPos catPos = cat.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(catPos.offset(-32, -4, -32), catPos.offset(32, 4, 32))) {
            if (level.getBlockState(pos).is(ObjectRegistry.PET_BOWL.get())) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof PetBowlBlockEntity bowl && bowl.isEmpty()) {
                    bowlPos = pos.immutable();
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (bowlPos == null || meowTicks >= 600) return false;

        Level level = cat.level();
        if (!(level instanceof ServerLevel)) return false;

        BlockEntity be = level.getBlockEntity(bowlPos);
        return be instanceof PetBowlBlockEntity bowl && bowl.isEmpty();
    }

    @Override
    public void start() {
        if (bowlPos != null) {
            cat.getNavigation().moveTo(bowlPos.getX() + 0.5, bowlPos.getY(), bowlPos.getZ() + 0.5, 1.0);
            meowTicks = 0;
        }
    }

    @Override
    public void tick() {
        if (cooldownTicks > 0) cooldownTicks--;

        if (bowlPos != null && cat.blockPosition().closerThan(bowlPos, 3)) {
            if (!cat.isOrderedToSit()) cat.setOrderedToSit(true);

            if (meowTicks % 40 == 0) {
                cat.playSound(SoundEvents.CAT_AMBIENT, 1.0f, 1.0f);
            }

            if (meowTicks % 120 == 0 && cat.level() instanceof ServerLevel server) {
                Vec3 pos = cat.position().add(0, 0.5, 0);
                server.sendParticles(net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER, pos.x, pos.y, pos.z, 1, 0.1, 0.1, 0.1, 0.0);
                cat.gameEvent(GameEvent.ENTITY_PLACE);
            }

            meowTicks++;
        }
    }

    @Override
    public void stop() {
        bowlPos = null;
        meowTicks = 0;
        cooldownTicks = 100;
        if (cat.isOrderedToSit()) cat.setOrderedToSit(false);
    }
}

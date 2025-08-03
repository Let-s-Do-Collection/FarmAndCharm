package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.mixin.ChickenAccessor;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.EnumSet;

public class ChickenGotoAndEnterCoopGoal extends Goal {
    private final Chicken chicken;
    private int tickCounter;

    public ChickenGotoAndEnterCoopGoal(Chicken chicken) {
        this.chicken = chicken;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!((ChickenCoopAccess) chicken).farmAndCharm$hasCoopTarget()) return false;

        BlockPos target = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        BlockEntity be = chicken.level().getBlockEntity(target);
        if (!(be instanceof ChickenCoopBlockEntity coop)) return false;

        Vec3 targetCenter = new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        double distance = chicken.position().distanceTo(targetCenter);

        int eggTime = ((ChickenAccessor) chicken).farmAndCharm$getEggTime();

        return coop.hasSpaceForChicken()
                && !coop.containsChicken(chicken)
                && eggTime <= 0
                && distance <= 1.5;
    }

    @Override
    public void start() {
        tickCounter = 0;

        BlockPos target = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        BlockEntity be = chicken.level().getBlockEntity(target);
        if (!(be instanceof ChickenCoopBlockEntity coop)) return;

        if (coop.hasSpaceForChicken()) {
            chicken.level().playSound(null, chicken.blockPosition(), SoundEvents.BEEHIVE_ENTER, chicken.getSoundSource(), 1.0F, 1.0F);
            coop.addChicken(chicken);

            ((ChickenAccessor) chicken).farmAndCharm$setEggTime(chicken.getRandom().nextInt(6000) + 6000);
            ((ChickenCoopAccess) chicken).farmAndCharm$clearCoopTarget();
            ((ChickenCoopAccess) chicken).farmAndCharm$setSearchedForCoop(false);
        }
    }

    @Override
    public void stop() {
        tickCounter = 0;
        chicken.getNavigation().stop();
    }

    @Override
    public void tick() {
        tickCounter++;

        BlockPos coop = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        if (coop == null) return;

        if (!chicken.level().getBlockState(coop).is(ObjectRegistry.CHICKEN_COOP.get())) {
            ((ChickenCoopAccess) chicken).farmAndCharm$clearCoopTarget();
            chicken.getNavigation().stop();
            return;
        }

        chicken.getNavigation().moveTo(coop.getX(), coop.getY(), coop.getZ(), 1.0);
    }
}

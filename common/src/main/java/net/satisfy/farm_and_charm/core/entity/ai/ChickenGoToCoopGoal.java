package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.EnumSet;

public class ChickenGoToCoopGoal extends Goal {
    private final Chicken chicken;
    private int tickCounter;

    public ChickenGoToCoopGoal(Chicken chicken) {
        this.chicken = chicken;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        BlockPos coopPos = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        if (coopPos == null || !chicken.level().getBlockState(coopPos).is(ObjectRegistry.CHICKEN_COOP.get()))
            return false;

        BlockEntity be = chicken.level().getBlockEntity(coopPos);
        return be instanceof ChickenCoopBlockEntity coop && coop.hasSpaceForChicken();
    }

    @Override
    public void start() {
        tickCounter = 0;
    }

    @Override
    public void stop() {
        chicken.getNavigation().stop();
    }

    @Override
    public void tick() {
        tickCounter++;

        BlockPos coop = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        BlockEntity be = chicken.level().getBlockEntity(coop);

        if (!chicken.level().getBlockState(coop).is(ObjectRegistry.CHICKEN_COOP.get())) {
            ((ChickenCoopAccess) chicken).farmAndCharm$clearCoopTarget();
            chicken.getNavigation().stop();
            return;
        }

        boolean success = chicken.getNavigation().moveTo(coop.getX(), coop.getY(), coop.getZ(), 1.0);

        if (!success || tickCounter > 1200) {
            ((ChickenCoopAccess) chicken).farmAndCharm$clearCoopTarget();
            chicken.getNavigation().stop();
        }
    }
}

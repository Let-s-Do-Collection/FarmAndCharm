package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;

import java.util.EnumSet;

public class ChickenEnterCoopGoal extends Goal {
    private final Chicken chicken;

    public ChickenEnterCoopGoal(Chicken chicken) {
        this.chicken = chicken;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!((ChickenCoopAccess) chicken).farmAndCharm$hasCoopTarget()) return false;
        BlockPos target = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();

        if (!(chicken.level().getBlockEntity(target) instanceof ChickenCoopBlockEntity coop)) return false;

        Vec3 chickenPos = chicken.position();
        Vec3 targetCenter = new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        double distance = chickenPos.distanceTo(targetCenter);

        boolean result = distance < 1.5
                && coop.hasSpaceForChicken()
                && !coop.containsChicken(chicken);

        if (result) {
            System.out.println("ChickenEnterCoopGoal triggered for " + chicken.getUUID());
        }

        boolean willLayEgg = !chicken.level().isClientSide && chicken.isAlive() && !chicken.isBaby() && !chicken.isChickenJockey() && --chicken.eggTime <= 0;// @author wdog5 - check the egg lay timer is 0 and it will be lay eggs
        return result && willLayEgg;
    }

    @Override
    public void start() {
        BlockPos target = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        if (chicken.level().getBlockEntity(target) instanceof ChickenCoopBlockEntity coop && coop.hasSpaceForChicken()) {
            chicken.level().playSound(null, chicken.blockPosition(), SoundEvents.BEEHIVE_ENTER, chicken.getSoundSource(), 1.0F, 1.0F);
            coop.addChicken(chicken);
            ((ChickenCoopAccess) chicken).farmAndCharm$clearCoopTarget();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}

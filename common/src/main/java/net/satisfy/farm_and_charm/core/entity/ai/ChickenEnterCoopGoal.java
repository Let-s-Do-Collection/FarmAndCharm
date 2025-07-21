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

        return result && --chicken.eggTime <= 0;// @author wdog5 - check the egg lay timer is 0
    }

    @Override
    public void start() {
        BlockPos target = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        if (chicken.level().getBlockEntity(target) instanceof ChickenCoopBlockEntity coop && coop.hasSpaceForChicken()) {
            chicken.level().playSound(null, chicken.blockPosition(), SoundEvents.BEEHIVE_ENTER, chicken.getSoundSource(), 1.0F, 1.0F);
            // @author wdog5 - make chicken stay for 15 secs and reduce egg count
            boolean checkPosValid = chicken.onGround() && !chicken.isInWall() && !chicken.isInPowderSnow && !chicken.isInLava() && !chicken.isInWaterOrBubble() && !chicken.isOnFire();
            if (checkPosValid) {
                for (int i = 1; i <= 15; i++) {
                    chicken.setNoAi(true);
                }
                coop.addChicken(chicken);
                coop.setEggCount(coop.getEggCount() - 1);
            }

            ((ChickenCoopAccess) chicken).farmAndCharm$clearCoopTarget();
        }
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }
}

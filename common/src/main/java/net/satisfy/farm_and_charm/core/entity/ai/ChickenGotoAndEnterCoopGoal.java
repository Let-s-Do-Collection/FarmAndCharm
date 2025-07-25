package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.ChickenCoopBlock;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.mixin.ChickenAccessor;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.EnumSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ChickenGotoAndEnterCoopGoal extends Goal {
    private final Chicken chicken;
    public int eggTime = new Random().nextInt(6000) + 6000;
    private int tickCounter;

    public ChickenGotoAndEnterCoopGoal(Chicken chicken) {
        this.chicken = chicken;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!((ChickenCoopAccess) chicken).farmAndCharm$hasCoopTarget()) return false;
        BlockPos target = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();

        if (!(chicken.level().getBlockEntity(target) instanceof ChickenCoopBlockEntity coop)) return false;
        Vec3 targetCenter = new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        Vec3 chickenPos = chicken.position();
        double distance = chickenPos.distanceTo(targetCenter);
        boolean result = coop.hasSpaceForChicken()
                && !coop.containsChicken(chicken) && --eggTime <= 0;

        if (result && distance <= 1.5) {
            chicken.setInvisible(true);
            chicken.setNoAi(true);
            System.out.println("ChickenEnterCoopGoal triggered for " + chicken.getUUID());
            // @author wdog5 - delay 15 secs
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    chicken.setInvisible(false);
                    chicken.setNoAi(false);
                    // @author wdog5 - add egg to coop
                    if (coop.getEggCount() < 9) {
                        coop.addEggCount(1);
                    }
                    // @author wdog5 - move chicken
                    float f = chicken.getBbWidth();
                    double d = 0.55 + (double)(f / 2.0F);
                    double e = chicken.getX() + 1.5 + d * (double)coop.getBlockState().getValue(ChickenCoopBlock.FACING).getStepX();
                    double g = chicken.getY() + 1.5 - (double)(chicken.getBbHeight() / 2.0F);
                    double h = chicken.getZ() + 1.5 + d * (double)coop.getBlockState().getValue(ChickenCoopBlock.FACING).getStepZ();
                    chicken.moveTo(e, g, h, chicken.getYRot(), chicken.getXRot());
                    coop.removeChicken(chicken);
                    chicken.level().playSound(null, chicken.blockPosition(), SoundEvents.CHICKEN_EGG, chicken.getSoundSource(), 1.0F, 1.0F);
                    ChickenAccessor accessor = (ChickenAccessor) chicken;
                    accessor.farmAndCharm$setEggTime(chicken.getRandom().nextInt(6000) + 6000);
                    System.out.println("Chicken Outside triggered for " + chicken.getUUID());
                }
            }, 15000);
        }
        return result;
    }

    @Override
    public void start() {
        tickCounter = 0;
        Vec3 chickenPos = chicken.position();
        BlockPos target = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        Vec3 targetCenter = new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        double distance = chickenPos.distanceTo(targetCenter);
        if (distance >= 1.5) return;
        if (target != null && chicken.level().getBlockEntity(target) instanceof ChickenCoopBlockEntity coop && coop.hasSpaceForChicken()) {
            chicken.level().playSound(null, chicken.blockPosition(), SoundEvents.BEEHIVE_ENTER, chicken.getSoundSource(), 1.0F, 1.0F);
            coop.addChicken(chicken);
            ((ChickenCoopAccess) chicken).farmAndCharm$clearCoopTarget();
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
        --eggTime;

        BlockPos coop = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        if (coop == null) return;

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

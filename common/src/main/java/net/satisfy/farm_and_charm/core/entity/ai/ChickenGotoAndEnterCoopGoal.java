package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.EnumSet;

public class ChickenGotoAndEnterCoopGoal extends Goal {
    private final Chicken chicken;
    private int nextRepathTick;

    public ChickenGotoAndEnterCoopGoal(Chicken chicken) {
        this.chicken = chicken;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        ChickenCoopAccess acc = (ChickenCoopAccess) chicken;
        if (acc.farmAndCharm$getCoopCooldown() > 0) return false;
        if (!acc.farmAndCharm$hasCoopTarget()) return false;
        BlockPos target = acc.farmAndCharm$getCoopTarget();
        BlockEntity be = chicken.level().getBlockEntity(target);
        if (!(be instanceof ChickenCoopBlockEntity coop)) return false;
        if (!coop.hasSpaceForChicken()) return false;
        if (coop.containsChicken(chicken)) return false;
        return chicken.getNavigation().createPath(target, 0) != null;
    }

    @Override
    public void start() {
        BlockPos coopPos = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        if (coopPos == null) return;
        nextRepathTick = chicken.tickCount;
        Vec3 center = Vec3.atCenterOf(coopPos);
        double d2 = chicken.position().distanceToSqr(center);
        if (d2 <= 1.44) {
            BlockEntity be = chicken.level().getBlockEntity(coopPos);
            if (be instanceof ChickenCoopBlockEntity coop && coop.hasSpaceForChicken()) {
                chicken.level().playSound(null, chicken.blockPosition(), SoundEvents.BEEHIVE_ENTER, chicken.getSoundSource(), 1.0F, 1.0F);
                coop.addChicken(chicken);
                ((ChickenCoopAccess) chicken).farmAndCharm$clearCoopTarget();
                ((ChickenCoopAccess) chicken).farmAndCharm$setCoopCooldown(20 * 60 * (3 + chicken.getRandom().nextInt(10)));
                chicken.getNavigation().stop();
                return;
            }
        }
        chicken.getNavigation().moveTo(center.x, center.y, center.z, 1.0);
        nextRepathTick = chicken.tickCount + 10;
    }

    @Override
    public void tick() {
        BlockPos coopPos = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        if (coopPos == null) return;
        if (!chicken.level().getBlockState(coopPos).is(ObjectRegistry.CHICKEN_COOP.get())) {
            ((ChickenCoopAccess) chicken).farmAndCharm$clearCoopTarget();
            chicken.getNavigation().stop();
            return;
        }
        Vec3 center = Vec3.atCenterOf(coopPos);
        double d2 = chicken.position().distanceToSqr(center);
        if (d2 <= 1.44) {
            BlockEntity be = chicken.level().getBlockEntity(coopPos);
            if (be instanceof ChickenCoopBlockEntity coop && coop.hasSpaceForChicken()) {
                chicken.level().playSound(null, chicken.blockPosition(), SoundEvents.BEEHIVE_ENTER, chicken.getSoundSource(), 1.0F, 1.0F);
                coop.addChicken(chicken);
                ((ChickenCoopAccess) chicken).farmAndCharm$clearCoopTarget();
                ((ChickenCoopAccess) chicken).farmAndCharm$setCoopCooldown(20 * 60 * (3 + chicken.getRandom().nextInt(10)));
                chicken.getNavigation().stop();
                return;
            }
        }
        if (chicken.tickCount >= nextRepathTick) {
            chicken.getNavigation().moveTo(center.x, center.y, center.z, 1.0);
            nextRepathTick = chicken.tickCount + 10;
        }
    }

    @Override
    public void stop() {
        chicken.getNavigation().stop();
    }
}

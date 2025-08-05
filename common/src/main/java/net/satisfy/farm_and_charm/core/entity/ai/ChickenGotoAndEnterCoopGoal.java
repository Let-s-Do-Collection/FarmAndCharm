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

    public ChickenGotoAndEnterCoopGoal(Chicken chicken) {
        this.chicken = chicken;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (((ChickenCoopAccess) chicken).farmAndCharm$getCoopCooldown() > 0) return false;
        if (!((ChickenCoopAccess) chicken).farmAndCharm$hasCoopTarget()) return false;
        BlockPos target = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        BlockEntity be = chicken.level().getBlockEntity(target);
        return be instanceof ChickenCoopBlockEntity coop && coop.hasSpaceForChicken() && !coop.containsChicken(chicken);
    }

    @Override
    public void start() {
        BlockPos coopPos = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        if (coopPos == null) return;
        double distance = chicken.position().distanceTo(Vec3.atCenterOf(coopPos));
        if (distance <= 1.2) {
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
        chicken.getNavigation().moveTo(coopPos.getX() + 0.5, coopPos.getY() + 0.5, coopPos.getZ() + 0.5, 1.0);
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
        double distance = chicken.position().distanceTo(Vec3.atCenterOf(coopPos));
        if (distance <= 1.2) {
            BlockEntity be = chicken.level().getBlockEntity(coopPos);
            if (be instanceof ChickenCoopBlockEntity coop && coop.hasSpaceForChicken()) {
                chicken.level().playSound(null, chicken.blockPosition(), SoundEvents.BEEHIVE_ENTER, chicken.getSoundSource(), 1.0F, 1.0F);
                coop.addChicken(chicken);
                ((ChickenCoopAccess) chicken).farmAndCharm$clearCoopTarget();
                ((ChickenCoopAccess) chicken).farmAndCharm$setCoopCooldown(20 * 60 * (3 + chicken.getRandom().nextInt(10)));
                chicken.getNavigation().stop();
            }
        }
    }

    @Override
    public void stop() {
        chicken.getNavigation().stop();
    }
}

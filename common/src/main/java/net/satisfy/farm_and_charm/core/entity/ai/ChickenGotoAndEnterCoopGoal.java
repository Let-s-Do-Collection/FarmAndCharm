package net.satisfy.farm_and_charm.core.entity.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

public class ChickenGotoAndEnterCoopGoal extends Goal {
    private final Chicken chicken;
    private int nextRepathTick;
    private int failCount;
    private BlockPos approachPos;
    private int losNextTick;
    private int spaceNextTick;

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
        BlockPos approach = findApproach(target);
        if (approach == null) return false;
        if (hasLineOfSight(Vec3.atCenterOf(approach))) return false;
        return chicken.getNavigation().createPath(approach, 0) != null;
    }

    @Override
    public void start() {
        BlockPos coopPos = ((ChickenCoopAccess) chicken).farmAndCharm$getCoopTarget();
        if (coopPos == null) return;
        failCount = 0;
        approachPos = findApproach(coopPos);
        if (approachPos == null) {
            clearWithCooldown(200);
            return;
        }
        Vec3 center = Vec3.atCenterOf(approachPos);
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
        nextRepathTick = chicken.tickCount + 10 + chicken.getRandom().nextInt(5);
        losNextTick = chicken.tickCount;
        spaceNextTick = chicken.tickCount;
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
        if (approachPos == null) {
            approachPos = findApproach(coopPos);
            if (approachPos == null) {
                clearWithCooldown(200);
                return;
            }
        }
        if (chicken.tickCount >= losNextTick && hasLineOfSight(Vec3.atCenterOf(approachPos))) {
            losNextTick = chicken.tickCount + 10;
            failCount++;
            if (failCount >= 3) {
                clearWithCooldown(200);
            }
            return;
        }
        if (chicken.tickCount >= spaceNextTick) {
            BlockEntity be = chicken.level().getBlockEntity(coopPos);
            if (!(be instanceof ChickenCoopBlockEntity coop) || !coop.hasSpaceForChicken()) {
                clearWithCooldown(100);
                return;
            }
            spaceNextTick = chicken.tickCount + 10;
        }
        Vec3 center = Vec3.atCenterOf(approachPos);
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
            BlockPos targetPos = BlockPos.containing(center);
            if (chicken.getNavigation().createPath(targetPos, 0) == null) {
                failCount++;
                if (failCount >= 3) {
                    clearWithCooldown(200);
                    return;
                }
            } else {
                chicken.getNavigation().moveTo(center.x, center.y, center.z, 1.0);
            }
            nextRepathTick = chicken.tickCount + 10 + chicken.getRandom().nextInt(5);
        }
    }

    @Override
    public void stop() {
        chicken.getNavigation().stop();
    }

    private void clearWithCooldown(int ticks) {
        ((ChickenCoopAccess) chicken).farmAndCharm$clearCoopTarget();
        ((ChickenCoopAccess) chicken).farmAndCharm$setCoopCooldown(ticks);
        chicken.getNavigation().stop();
        approachPos = null;
        failCount = 0;
    }

    private BlockPos findApproach(BlockPos coopPos) {
        for (Direction d : Direction.Plane.HORIZONTAL) {
            BlockPos side = coopPos.relative(d);
            if (chicken.level().getBlockState(side).isAir() && chicken.level().getBlockState(side.above()).isAir()) return side;
        }
        return null;
    }

    private boolean hasLineOfSight(Vec3 to) {
        if ((chicken.tickCount & 9) != 0) return false;
        Vec3 from = chicken.getEyePosition();
        HitResult hit = chicken.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, chicken));
        return hit.getType() != HitResult.Type.MISS;
    }
}

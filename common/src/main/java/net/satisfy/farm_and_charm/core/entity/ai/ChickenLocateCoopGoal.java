package net.satisfy.farm_and_charm.core.entity.ai;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

public class ChickenLocateCoopGoal extends Goal {
    private final Chicken chicken;
    private BlockPos foundCoop;
    private BlockPos foundApproach;
    private final Set<BlockPos> cachedCoops = new HashSet<>();
    private int nextScanTick;

    public ChickenLocateCoopGoal(Chicken chicken) {
        this.chicken = chicken;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (chicken.isBaby()) return false;
        ChickenCoopAccess access = (ChickenCoopAccess) chicken;
        if (access.farmAndCharm$hasCoopTarget()) return false;
        if (access.farmAndCharm$searchedForCoop()) return false;
        if (access.farmAndCharm$getCoopCooldown() > 0) return false;
        if (chicken.tickCount < nextScanTick) return false;

        ServerLevel level = (ServerLevel) chicken.level();

        if (foundCoop != null) {
            if (level.getBlockState(foundCoop).is(ObjectRegistry.CHICKEN_COOP.get())) {
                BlockEntity be = level.getBlockEntity(foundCoop);
                if (be instanceof ChickenCoopBlockEntity coop && coop.hasSpaceForChicken()) {
                    BlockPos approach = findApproach(level, foundCoop);
                    if (approach != null && hasLineOfSight(level, chicken.getEyePosition(), Vec3.atCenterOf(approach))) {
                        if (chicken.getNavigation().createPath(approach, 0) != null) {
                            foundApproach = approach;
                            return true;
                        }
                    }
                }
            }
            foundCoop = null;
            foundApproach = null;
        }

        Iterator<BlockPos> it = cachedCoops.iterator();
        while (it.hasNext()) {
            BlockPos cached = it.next();
            if (!level.getBlockState(cached).is(ObjectRegistry.CHICKEN_COOP.get())) {
                it.remove();
                continue;
            }
            BlockEntity be = level.getBlockEntity(cached);
            if (!(be instanceof ChickenCoopBlockEntity coop) || !coop.hasSpaceForChicken()) {
                it.remove();
                continue;
            }
            BlockPos approach = findApproach(level, cached);
            if (approach == null) {
                it.remove();
                continue;
            }
            if (!hasLineOfSight(level, chicken.getEyePosition(), Vec3.atCenterOf(approach))) {
                continue;
            }
            if (chicken.getNavigation().createPath(approach, 0) != null) {
                foundCoop = cached;
                foundApproach = approach;
                nextScanTick = chicken.tickCount + 40;
                return true;
            }
        }

        BlockPos pos = chicken.blockPosition();
        foundCoop = BlockPos.findClosestMatch(pos, 12, 2, check -> {
            if (!level.getBlockState(check).is(ObjectRegistry.CHICKEN_COOP.get())) return false;
            BlockEntity be = level.getBlockEntity(check);
            if (!(be instanceof ChickenCoopBlockEntity coop) || !coop.hasSpaceForChicken()) return false;
            BlockPos approach = findApproach(level, check);
            if (approach == null) return false;
            if (!hasLineOfSight(level, chicken.getEyePosition(), Vec3.atCenterOf(approach))) return false;
            if (chicken.getNavigation().createPath(approach, 0) == null) return false;
            if (cachedCoops.size() >= 16) {
                BlockPos far = null;
                double max = -1.0;
                for (BlockPos p : cachedCoops) {
                    double d = p.distSqr(pos);
                    if (d > max) {
                        max = d;
                        far = p;
                    }
                }
                if (far != null) cachedCoops.remove(far);
            }
            cachedCoops.add(check);
            foundApproach = approach;
            return true;
        }).orElse(null);

        nextScanTick = chicken.tickCount + 40;
        return foundCoop != null && foundApproach != null;
    }

    @Override
    public void start() {
        ((ChickenCoopAccess) chicken).farmAndCharm$setCoopTarget(foundCoop);
        ((ChickenCoopAccess) chicken).farmAndCharm$setSearchedForCoop(true);
        Vec3 center = Vec3.atCenterOf(foundApproach != null ? foundApproach : foundCoop);
        chicken.getNavigation().moveTo(center.x, center.y, center.z, 1.0);
    }

    private static BlockPos findApproach(ServerLevel level, BlockPos coopPos) {
        for (Direction d : Direction.Plane.HORIZONTAL) {
            BlockPos side = coopPos.relative(d);
            if (level.getBlockState(side).isAir() && level.getBlockState(side.above()).isAir()) return side;
        }
        return null;
    }

    private boolean hasLineOfSight(ServerLevel level, Vec3 from, Vec3 to) {
        if ((chicken.tickCount & 9) != 0) return true;
        HitResult hit = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, chicken));
        return hit.getType() == HitResult.Type.MISS;
    }
}

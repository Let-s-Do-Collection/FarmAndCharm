package net.satisfy.farm_and_charm.core.entity.ai;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.EnumSet;

public class ChickenLocateCoopGoal extends Goal {
    private final Chicken chicken;
    private BlockPos foundCoop;
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
                    if (chicken.getNavigation().createPath(foundCoop, 0) != null) return true;
                }
            }
            foundCoop = null;
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
            if (chicken.getNavigation().createPath(cached, 0) != null) {
                foundCoop = cached;
                nextScanTick = chicken.tickCount + 40;
                return true;
            }
        }

        BlockPos pos = chicken.blockPosition();
        foundCoop = BlockPos.findClosestMatch(pos, 16, 4, check -> {
            if (!level.getBlockState(check).is(ObjectRegistry.CHICKEN_COOP.get())) return false;
            BlockEntity be = level.getBlockEntity(check);
            if (!(be instanceof ChickenCoopBlockEntity coop) || !coop.hasSpaceForChicken()) return false;
            if (chicken.getNavigation().createPath(check, 0) == null) return false;
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
            return true;
        }).orElse(null);

        nextScanTick = chicken.tickCount + 40;
        return foundCoop != null;
    }

    @Override
    public void start() {
        ((ChickenCoopAccess) chicken).farmAndCharm$setCoopTarget(foundCoop);
        ((ChickenCoopAccess) chicken).farmAndCharm$setSearchedForCoop(true);
        chicken.getNavigation().moveTo(foundCoop.getX() + 0.5, foundCoop.getY() + 0.5, foundCoop.getZ() + 0.5, 1.0);
    }
}

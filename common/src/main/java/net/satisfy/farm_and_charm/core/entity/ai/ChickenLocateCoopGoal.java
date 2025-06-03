package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class ChickenLocateCoopGoal extends Goal {
    private final Chicken chicken;
    private BlockPos foundCoop;
    private final List<BlockPos> cachedCoops = new ArrayList<>();

    public ChickenLocateCoopGoal(Chicken chicken) {
        this.chicken = chicken;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (chicken.isBaby() || ((ChickenCoopAccess) chicken).farmAndCharm$hasCoopTarget())
            return false;

        ServerLevel level = (ServerLevel) chicken.level();

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
                return true;
            }
        }

        BlockPos pos = chicken.blockPosition();
        foundCoop = BlockPos.findClosestMatch(pos, 16, 4, check -> {
            if (!level.getBlockState(check).is(ObjectRegistry.CHICKEN_COOP.get())) return false;
            BlockEntity be = level.getBlockEntity(check);
            if (!(be instanceof ChickenCoopBlockEntity coop) || !coop.hasSpaceForChicken()) return false;
            if (chicken.getNavigation().createPath(check, 0) == null) return false;

            if (!cachedCoops.contains(check)) cachedCoops.add(check);
            return true;
        }).orElse(null);

        return foundCoop != null;
    }

    @Override
    public void start() {
        ((ChickenCoopAccess) chicken).farmAndCharm$setCoopTarget(foundCoop);
    }
}

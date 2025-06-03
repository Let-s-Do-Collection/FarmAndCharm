package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.block.entity.StorageBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.mixin.ChickenAccessor;

import java.util.EnumSet;

public class LayEggInNestGoal extends Goal {
    private final Chicken chicken;
    private final Level level;
    private BlockPos cachedNest;
    private int retryCooldown = 0;

    public LayEggInNestGoal(Chicken chicken) {
        this.chicken = chicken;
        this.level = chicken.level();
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (chicken.isBaby() || !chicken.isAlive() || chicken.isChickenJockey() || level.isClientSide) {
            return false;
        }
        ChickenAccessor accessor = (ChickenAccessor) chicken;
        if (accessor.farmAndCharm$getEggTime() > 0) {
            return false;
        }

        if (!isValidNest(cachedNest)) {
            if (retryCooldown > 0) {
                retryCooldown--;
                return false;
            }
            cachedNest = findNearestNest();
            retryCooldown = 100;
        }

        return cachedNest != null;
    }

    @Override
    public void start() {
        if (cachedNest != null) {
            Vec3 vec3 = Vec3.atCenterOf(cachedNest);
            if (chicken.getNavigation().createPath(vec3.x, vec3.y, vec3.z, 0) != null) {
                chicken.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0);
            } else {
                cachedNest = null;
                retryCooldown = 100;
            }
        }
    }

    @Override
    public void tick() {
        if (cachedNest != null && chicken.blockPosition().closerThan(cachedNest, 2.0)) {
            BlockEntity be = level.getBlockEntity(cachedNest);
            if (be instanceof StorageBlockEntity storage) {
                for (int i = 0; i < storage.getInventory().size(); i++) {
                    if (storage.getInventory().get(i).isEmpty()) {
                        storage.setStack(i, new ItemStack(Items.EGG));
                        storage.setChanged();
                        ChickenAccessor accessor = (ChickenAccessor) chicken;
                        accessor.farmAndCharm$setEggTime(chicken.getRandom().nextInt(6000) + 6000);
                        break;
                    }
                }
            }
            cachedNest = null;
            retryCooldown = 100;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return cachedNest != null && chicken.getNavigation().isInProgress();
    }

    private boolean isValidNest(BlockPos pos) {
        if (pos == null || !level.getBlockState(pos).is(ObjectRegistry.CHICKEN_NEST.get())) {
            return false;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof StorageBlockEntity storage)) {
            return false;
        }
        for (int i = 0; i < storage.getInventory().size(); i++) {
            if (storage.getInventory().get(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private BlockPos findNearestNest() {
        BlockPos origin = chicken.blockPosition();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        double closestDistanceSq = Double.MAX_VALUE;
        BlockPos closest = null;

        for (int dx = -6; dx <= 6; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -6; dz <= 6; dz++) {
                    mutable.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (level.getBlockState(mutable).is(ObjectRegistry.CHICKEN_NEST.get())) {
                        BlockEntity be = level.getBlockEntity(mutable);
                        if (be instanceof StorageBlockEntity storage) {
                            for (int i = 0; i < storage.getInventory().size(); i++) {
                                if (storage.getInventory().get(i).isEmpty()) {
                                    double dist = mutable.distSqr(origin);
                                    if (dist < closestDistanceSq) {
                                        closestDistanceSq = dist;
                                        closest = mutable.immutable();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        return closest;
    }
}

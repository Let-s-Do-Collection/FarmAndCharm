package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
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
import java.util.Map;
import java.util.WeakHashMap;

public class LayEggInNestGoal extends Goal {
    private final Chicken chicken;
    private final ChickenAccessor accessor;
    private final Level level;
    private BlockPos cachedNest;
    private Vec3 lastPos;
    private Vec3 targetCenter;
    private int retryCooldown = 0;
    private int stuckTicks = 0;
    private int targetSlot = -1;

    private static final int MAX_DISTANCE = 6;
    private static final int MAX_VERTICAL_SEARCH = 2;
    private static final int MAX_STUCK_TICKS = 200;
    private static final int RETRY_COOLDOWN_TICKS = 100;
    private static final double REACHED_DISTANCE_SQR = 1.5 * 1.5;
    private static final double MIN_MOVEMENT_THRESHOLD = 0.02;
    private static final int MIN_EGG_TIME = 6000;
    private static final int MAX_EGG_TIME = 12000;

    private static final Map<Chicken, BlockPos> claimedNests = new WeakHashMap<>();
    private static final Map<BlockPos, Chicken> nestOwners = new WeakHashMap<>();

    private record NestTarget(BlockPos pos, int slot) {}

    public LayEggInNestGoal(Chicken chicken) {
        this.chicken = chicken;
        this.accessor = (ChickenAccessor) chicken;
        this.level = chicken.level();
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (chicken.isBaby() || !chicken.isAlive() || chicken.isChickenJockey() || level.isClientSide) {
            return false;
        }

        if (accessor.farmAndCharm$getEggTime() > 0) {
            return false;
        }

        if (!isValidNest(cachedNest)) {
            if (retryCooldown > 0) {
                retryCooldown--;
                return false;
            }

            NestTarget result = findNearestNest();
            if (result != null) {
                cachedNest = result.pos();
                targetSlot = result.slot();
                targetCenter = Vec3.atCenterOf(cachedNest);
            } else {
                cachedNest = null;
                targetSlot = -1;
                targetCenter = null;
            }

            retryCooldown = RETRY_COOLDOWN_TICKS;
        }

        return isValidNest(cachedNest);
    }

    @Override
    public void start() {
        if (cachedNest != null && targetCenter != null) {
            if (chicken.getNavigation().moveTo(targetCenter.x, targetCenter.y, targetCenter.z, 1.0)) {
                claimedNests.put(chicken, cachedNest);
                nestOwners.put(cachedNest, chicken);
                stuckTicks = 0;
                lastPos = chicken.position();
            } else {
                stop();
            }
        }
    }

    @Override
    public void tick() {
        if (!claimedNests.containsKey(chicken) || !isValidNest(cachedNest)) {
            stop();
            return;
        }

        Vec3 currentPos = chicken.position();
        if (lastPos != null && currentPos.distanceToSqr(lastPos) < MIN_MOVEMENT_THRESHOLD) {
            stuckTicks += chicken.getNavigation().isDone() ? 2 : 1;
        } else {
            stuckTicks = 0;
        }

        lastPos = currentPos;

        if (stuckTicks > MAX_STUCK_TICKS) {
            stop();
            return;
        }

        if (targetCenter != null && currentPos.distanceToSqr(targetCenter) > REACHED_DISTANCE_SQR) return;

        BlockEntity be = level.getBlockEntity(cachedNest);
        if (be instanceof StorageBlockEntity storage && targetSlot >= 0 && targetSlot < storage.getInventory().size()) {
            if (storage.getInventory().get(targetSlot).isEmpty()) {
                storage.setStack(targetSlot, new ItemStack(Items.EGG));

                if (chicken.getRandom().nextFloat() < 0.05f) {
                    for (int i = 0; i < storage.getInventory().size(); i++) {
                        if (i != targetSlot && storage.getInventory().get(i).isEmpty()) {
                            storage.setStack(i, new ItemStack(Items.FEATHER));
                            break;
                        }
                    }
                }

                storage.setChanged();
                accessor.farmAndCharm$setEggTime(chicken.getRandom().nextInt(MAX_EGG_TIME - MIN_EGG_TIME) + MIN_EGG_TIME);
                chicken.playSound(SoundEvents.CHICKEN_EGG, 1.0f, 1.0f);
            }
        }

        stop();
    }

    @Override
    public boolean canContinueToUse() {
        return chicken.getNavigation().isInProgress() && isValidNest(cachedNest);
    }

    @Override
    public void stop() {
        if (claimedNests.containsKey(chicken)) {
            BlockPos pos = claimedNests.remove(chicken);
            if (pos != null) {
                nestOwners.remove(pos, chicken);
            }
        }
        cachedNest = null;
        targetCenter = null;
        retryCooldown = RETRY_COOLDOWN_TICKS;
        stuckTicks = 0;
        lastPos = null;
        targetSlot = -1;
    }

    private boolean isValidNest(BlockPos pos) {
        if (pos == null || !level.getBlockState(pos).is(ObjectRegistry.CHICKEN_NEST.get())) return false;
        if (nestOwners.containsKey(pos) && nestOwners.get(pos) != chicken) return false;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof StorageBlockEntity storage)) return false;

        for (int i = 0; i < storage.getInventory().size(); i++) {
            if (storage.getInventory().get(i).isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private NestTarget findNearestNest() {
        BlockPos origin = chicken.blockPosition();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        double closestDistanceSq = Double.MAX_VALUE;
        NestTarget closest = null;

        for (int dx = -MAX_DISTANCE; dx <= MAX_DISTANCE; dx++) {
            for (int dy = -MAX_VERTICAL_SEARCH; dy <= MAX_VERTICAL_SEARCH; dy++) {
                for (int dz = -MAX_DISTANCE; dz <= MAX_DISTANCE; dz++) {
                    mutable.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (!level.getBlockState(mutable).is(ObjectRegistry.CHICKEN_NEST.get())) continue;
                    if (nestOwners.containsKey(mutable)) continue;

                    BlockEntity be = level.getBlockEntity(mutable);
                    if (be instanceof StorageBlockEntity storage) {
                        for (int i = 0; i < storage.getInventory().size(); i++) {
                            if (storage.getInventory().get(i).isEmpty()) {
                                double dist = mutable.distSqr(origin);
                                if (dist < closestDistanceSq) {
                                    closestDistanceSq = dist;
                                    closest = new NestTarget(mutable.immutable(), i);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }

        return closest;
    }
}

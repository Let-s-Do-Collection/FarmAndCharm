package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.PetBowlBlock;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.entity.BowlAccessor;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.joml.Vector3f;

public class DogEatFromBowlGoal extends Goal {
    private final Wolf dog;
    private BlockPos targetBowl;
    private Vector3f targetVec;
    private int eatTicks;
    private ItemStack foodStack;

    public DogEatFromBowlGoal(Wolf dog) {
        this.dog = dog;
    }

    @Override
    public boolean canUse() {
        if (!dog.isTame() || dog.isOrderedToSit()) return false;

        Level level = dog.level();
        BlockPos dogPos = dog.blockPosition();
        double closestDist = Double.MAX_VALUE;
        BlockPos closest = null;
        ItemStack candidateFood = ItemStack.EMPTY;

        for (BlockPos pos : BlockPos.betweenClosed(dogPos.offset(-16, -4, -16), dogPos.offset(16, 4, 16))) {
            BlockState state = level.getBlockState(pos);
            if (!state.is(ObjectRegistry.PET_BOWL.get()) || !state.hasProperty(PetBowlBlock.FOOD_TYPE)) continue;
            if (state.getValue(PetBowlBlock.FOOD_TYPE) != GeneralUtil.FoodType.DOG) continue;

            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof PetBowlBlockEntity bowl) || bowl.isEmpty() || !bowl.canBeUsedBy(dog)) continue;

            double dist = pos.distSqr(dogPos);
            if (dist < closestDist) {
                closestDist = dist;
                closest = pos.immutable();
                candidateFood = bowl.getItem(0).copy();
            }
        }

        if (closest != null) {
            targetBowl = closest;
            targetVec = new Vector3f(closest.getX() + 0.5F, closest.getY(), closest.getZ() + 0.5F);
            foodStack = candidateFood;
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        dog.getNavigation().moveTo(targetVec.x(), targetVec.y(), targetVec.z(), 1.0);
        eatTicks = 0;
    }

    @Override
    public boolean canContinueToUse() {
        if (targetBowl == null) return false;

        Level level = dog.level();
        BlockState state = level.getBlockState(targetBowl);
        if (!(state.getBlock() instanceof PetBowlBlock) || !state.hasProperty(PetBowlBlock.FOOD_TYPE)) return false;

        BlockEntity be = level.getBlockEntity(targetBowl);
        if (!(be instanceof PetBowlBlockEntity bowl) || bowl.isEmpty() || !bowl.canBeUsedBy(dog)) return false;

        float distSqr = targetVec.distanceSquared((float) dog.getX(), (float) dog.getY(), (float) dog.getZ());
        return distSqr > 4.0F || eatTicks < 60;
    }

    @Override
    public void tick() {
        if (targetBowl == null) {
            stop();
            return;
        }

        Level level = dog.level();
        BlockEntity be = level.getBlockEntity(targetBowl);
        if (!(be instanceof PetBowlBlockEntity bowl) || bowl.isEmpty()) {
            stop();
            return;
        }

        float distSqr = targetVec.distanceSquared((float) dog.getX(), (float) dog.getY(), (float) dog.getZ());
        if (distSqr <= 4.0F) {
            eatTicks++;
            dog.setOrderedToSit(true);
            dog.getLookControl().setLookAt(targetVec.x(), targetVec.y(), targetVec.z());

            if (!level.isClientSide && eatTicks <= 40) {
                ParticleOptions particle = getParticleFromFood();
                if (particle != null) {
                    ((ServerLevel) level).sendParticles(particle, targetVec.x(), targetVec.y() + 0.09375F, targetVec.z(), 3, 0.2, 0.2, 0.2, 0.05);
                }

                if (eatTicks % 10 == 0) {
                    level.playSound(null, dog.blockPosition(), SoundEvents.WOLF_GROWL, SoundSource.NEUTRAL, 0.4F, 0.4F);
                }
            }

            if (eatTicks == 40) {
                bowl.decreaseFood();
                ((BowlAccessor.StayNearBowl) dog).farmAndCharm$setStayCenter(targetBowl);

                if (!level.isClientSide) {
                    ((ServerLevel) level).sendParticles(ParticleTypes.HEART, dog.getX(), dog.getY() + 0.5F, dog.getZ(), 3, 0.3, 0.3, 0.3, 0.01);
                    level.playSound(null, dog.blockPosition(), SoundEvents.WOLF_HOWL, dog.getSoundSource(), 0.4F, 0.4F);

                    BlockState old = level.getBlockState(targetBowl);
                    if (old.getBlock() instanceof PetBowlBlock && old.hasProperty(PetBowlBlock.FOOD_TYPE)) {
                        level.setBlockAndUpdate(targetBowl, old.setValue(PetBowlBlock.FOOD_TYPE, GeneralUtil.FoodType.NONE));
                    }
                }
            }
        }
    }

    @Override
    public void stop() {
        targetBowl = null;
        targetVec = null;
        eatTicks = 0;
        foodStack = null;
        dog.setOrderedToSit(false);
    }

    private ParticleOptions getParticleFromFood() {
        if (foodStack == null || foodStack.isEmpty()) return null;
        return new ItemParticleOption(ParticleTypes.ITEM, foodStack);
    }
}

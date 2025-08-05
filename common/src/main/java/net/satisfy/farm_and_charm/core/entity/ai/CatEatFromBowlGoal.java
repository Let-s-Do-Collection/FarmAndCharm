package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Cat;
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

public class CatEatFromBowlGoal extends Goal {
    private final Cat cat;
    private BlockPos targetBowl;
    private Vector3f targetVec;
    private int eatTicks;
    private ItemStack foodStack;

    public CatEatFromBowlGoal(Cat cat) {
        this.cat = cat;
    }

    @Override
    public boolean canUse() {
        if (!cat.isTame() || cat.isOrderedToSit()) return false;

        Level level = cat.level();
        BlockPos catPos = cat.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(catPos.offset(-16, -4, -16), catPos.offset(16, 4, 16))) {
            BlockState state = level.getBlockState(pos);
            if (!state.is(ObjectRegistry.PET_BOWL.get())) continue;
            if (!state.hasProperty(PetBowlBlock.FOOD_TYPE)) continue;
            if (state.getValue(PetBowlBlock.FOOD_TYPE) != GeneralUtil.FoodType.CAT) continue;

            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof PetBowlBlockEntity bowl)) continue;
            if (bowl.isEmpty()) continue;
            if (!bowl.canBeUsedBy(cat)) continue;

            targetBowl = pos.immutable();
            foodStack = bowl.getItem(0).copy();
            targetVec = new Vector3f(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
            return true;
        }

        return false;
    }


    @Override
    public void start() {
        cat.getNavigation().moveTo(targetVec.x(), targetVec.y(), targetVec.z(), 1.0);
        eatTicks = 0;
    }

    @Override
    public boolean canContinueToUse() {
        if (targetBowl == null) return false;
        float distSqr = targetVec.distanceSquared((float) cat.getX(), (float) cat.getY(), (float) cat.getZ());
        return distSqr > 4.0F || eatTicks < 60;
    }

    @Override
    public void tick() {
        if (targetBowl == null) return;

        float distSqr = targetVec.distanceSquared((float) cat.getX(), (float) cat.getY(), (float) cat.getZ());
        if (distSqr <= 4.0F) {
            eatTicks++;
            cat.setOrderedToSit(true);
            cat.getLookControl().setLookAt(targetVec.x(), targetVec.y(), targetVec.z());

            Level level = cat.level();

            if (!level.isClientSide && eatTicks <= 40) {
                ParticleOptions particle = getParticleFromFood();
                if (particle != null) {
                    ((ServerLevel) level).sendParticles(
                            particle,
                            targetVec.x(), targetVec.y() + 0.09375F, targetVec.z(),
                            3, 0.2, 0.2, 0.2, 0.05
                    );
                }

                if (eatTicks % 10 == 0) {
                    level.playSound(null, cat.blockPosition(), SoundEvents.CAT_EAT, SoundSource.NEUTRAL, 0.8F, 1.0F);
                }
            }

            if (eatTicks == 40) {
                BlockEntity be = level.getBlockEntity(targetBowl);
                if (be instanceof PetBowlBlockEntity bowl) {
                    bowl.decreaseFood();
                    ((BowlAccessor.FedTracker) cat).farmAndCharm$$markAsFed();
                }

                if (!level.isClientSide) {
                    ((ServerLevel) level).sendParticles(
                            ParticleTypes.HEART,
                            cat.getX(), cat.getY() + 0.5F, cat.getZ(),
                            3, 0.3, 0.3, 0.3, 0.01
                    );
                    level.playSound(null, cat.blockPosition(), SoundEvents.CAT_PURR, cat.getSoundSource(), 1.0F, 1.0F);

                    BlockState old = level.getBlockState(targetBowl);
                    BlockState nw = old.setValue(PetBowlBlock.FOOD_TYPE, GeneralUtil.FoodType.NONE);
                    level.setBlockAndUpdate(targetBowl, nw);
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
        cat.setOrderedToSit(false);
    }

    private ParticleOptions getParticleFromFood() {
        if (foodStack == null || foodStack.isEmpty()) return null;
        return new ItemParticleOption(ParticleTypes.ITEM, foodStack);
    }
}
package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.PetBowlBlock;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import net.satisfy.farm_and_charm.core.util.GeneralUtil.FoodType;

public class EatFromBowlGoal extends Goal {
    private final Cat cat;
    private BlockPos targetBowl;
    private int eatTicks;

    public EatFromBowlGoal(Cat cat) {
        this.cat = cat;
    }

    @Override
    public boolean canUse() {
        if (!cat.isTame() || cat.isOrderedToSit()) return false;
        Level level = cat.level();
        BlockPos catPos = cat.blockPosition();

        for (BlockPos pos : BlockPos.betweenClosed(catPos.offset(-16, -4, -16), catPos.offset(16, 4, 16))) {
            BlockState state = level.getBlockState(pos);
            if (state.is(ObjectRegistry.PET_BOWL.get()) &&
                    state.getValue(PetBowlBlock.FOOD_TYPE) == FoodType.CAT) {

                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof PetBowlBlockEntity bowl && !bowl.isEmpty()) {
                    targetBowl = pos.immutable();
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void start() {
        cat.getNavigation().moveTo(targetBowl.getX() + 0.5, targetBowl.getY(), targetBowl.getZ() + 0.5, 1.0);
        eatTicks = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return targetBowl != null &&
                cat.distanceToSqr(targetBowl.getX() + 0.5, targetBowl.getY(), targetBowl.getZ() + 0.5) > 2.0;
    }

    @Override
    public void tick() {
        if (targetBowl != null && cat.blockPosition().closerThan(targetBowl, 2)) {
            eatTicks++;
            if (eatTicks == 20) {
                Level level = cat.level();
                BlockEntity be = level.getBlockEntity(targetBowl);
                if (be instanceof PetBowlBlockEntity bowl && !bowl.isEmpty()) {
                    bowl.decreaseFood();
                    ((GeneralUtil.FedTracker) cat).farmAndCharm$$markAsFed();

                    level.playSound(null, targetBowl, SoundEvents.CAT_EAT, cat.getSoundSource(), 1.0f, 1.0f);

                    if (!level.isClientSide) {
                        ((ServerLevel) level).sendParticles(ParticleTypes.HEART, cat.getX(), cat.getY() + 0.5, cat.getZ(), 3, 0.3, 0.3, 0.3, 0.01);
                        ((ServerLevel) level).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, ObjectRegistry.CAT_FOOD.get().getDefaultInstance()), targetBowl.getX() + 0.5, targetBowl.getY() + 0.4, targetBowl.getZ() + 0.5, 6, 0.2, 0.1, 0.2, 0.05);
                    }
                }
            }
        }
    }

    @Override
    public void stop() {
        targetBowl = null;
        eatTicks = 0;
    }
}
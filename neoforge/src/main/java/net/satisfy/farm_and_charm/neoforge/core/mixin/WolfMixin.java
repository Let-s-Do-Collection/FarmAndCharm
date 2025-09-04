package net.satisfy.farm_and_charm.neoforge.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Wolf;
import net.satisfy.farm_and_charm.core.entity.BowlAccessor;
import net.satisfy.farm_and_charm.core.entity.ai.DogEatFromBowlGoal;
import net.satisfy.farm_and_charm.core.entity.ai.WhineAtBowlGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Wolf.class)
public class WolfMixin implements BowlAccessor.StayNearBowl {
    @Unique
    private boolean fedRecently = false;
    @Unique
    private BlockPos stayCenter = null;
    @Unique
    private boolean wasSittingLastTick = false;

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void farmAndCharm$addFeedingGoal(CallbackInfo ci) {
        Mob self = (Mob)(Object)this;
        ((MobAccessor)self).farmAndCharm$getGoalSelector().addGoal(13, new DogEatFromBowlGoal((Wolf)(Object)this));
        ((MobAccessor)self).farmAndCharm$getGoalSelector().addGoal(14, new WhineAtBowlGoal((Wolf)(Object)this));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void farmAndCharm$handleStayRestriction(CallbackInfo ci) {
        Wolf wolf = (Wolf)(Object)this;

        boolean isSittingNow = wolf.isInSittingPose();
        if (!wasSittingLastTick && isSittingNow) {
            fedRecently = false;
            stayCenter = null;
        }
        wasSittingLastTick = isSittingNow;

        if (fedRecently && stayCenter != null && !isSittingNow) {
            double dist = stayCenter.distSqr(wolf.blockPosition());
            if (dist > 256) {
                wolf.getNavigation().moveTo(
                        stayCenter.getX() + 0.5,
                        stayCenter.getY(),
                        stayCenter.getZ() + 0.5,
                        1.0
                );
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void farmAndCharm$clearOnDeath(CallbackInfo ci) {
        fedRecently = false;
        stayCenter = null;
    }

    @Override
    public void farmAndCharm$setStayCenter(BlockPos pos) {
        this.fedRecently = true;
        this.stayCenter = pos.immutable();
    }

    @Override
    public void farmAndCharm$clearStayRestriction() {
        this.fedRecently = false;
        this.stayCenter = null;
    }

    @Override
    public boolean farmAndCharm$isWithinStayRange(BlockPos pos) {
        return fedRecently && stayCenter != null && pos.distSqr(stayCenter) <= 256;
    }

    @Override
    public BlockPos farmAndCharm$getStayCenter() {
        return stayCenter != null ? stayCenter : ((Wolf)(Object)this).blockPosition();
    }

    @Override
    public boolean farmAndCharm$hasStayRestriction() {
        return fedRecently;
    }
}
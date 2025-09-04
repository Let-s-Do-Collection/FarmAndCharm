package net.satisfy.farm_and_charm.neoforge.core.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.core.entity.BowlAccessor;
import net.satisfy.farm_and_charm.core.entity.ai.CatEatFromBowlGoal;
import net.satisfy.farm_and_charm.core.entity.ai.MeowAtBowlGoal;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cat.class)
public class CatMixin implements BowlAccessor.FedTracker {
    @Unique
    private boolean farmAndCharm$fedFromBowl = false;

    @Unique
    private int farmAndCharm$fedTimer = 0;

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void farmAndCharm$addFeedingGoal(CallbackInfo ci) {
        Mob self = (Mob)(Object)this;
        ((MobAccessor)self).farmAndCharm$getGoalSelector().addGoal(13, new CatEatFromBowlGoal((Cat)(Object)this));
        ((MobAccessor)self).farmAndCharm$getGoalSelector().addGoal(14, new MeowAtBowlGoal((Cat)(Object)this));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void farmAndCharm$tickFedStatus(CallbackInfo ci) {
        if (farmAndCharm$fedFromBowl) {
            farmAndCharm$fedTimer++;
            if (farmAndCharm$fedTimer >= 12000) {
                farmAndCharm$fedFromBowl = false;
                farmAndCharm$fedTimer = 0;
            }
        }
    }
    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    private void farmAndCharm$allowCatFood(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (PlatformHelper.enableCatTamingChance() && itemStack.is(ObjectRegistry.CAT_FOOD.get().asItem())) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public void farmAndCharm$$resetFed() {
        farmAndCharm$fedFromBowl = false;
        farmAndCharm$fedTimer = 0;
    }

    @Override
    public void farmAndCharm$$markAsFed() {
        farmAndCharm$fedFromBowl = true;
        farmAndCharm$fedTimer = 0;
    }

    @Override
    public boolean farmAndCharm$$isFed() {
        return farmAndCharm$fedFromBowl;
    }
}

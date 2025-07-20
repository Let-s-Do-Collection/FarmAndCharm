package net.satisfy.farm_and_charm.core.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.StorageBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ai.ChickenEnterCoopGoal;
import net.satisfy.farm_and_charm.core.entity.ai.ChickenGoToCoopGoal;
import net.satisfy.farm_and_charm.core.entity.ai.ChickenLocateCoopGoal;
import net.satisfy.farm_and_charm.core.entity.ai.LayEggInNestGoal;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Chicken.class)
public class ChickenMixin implements ChickenCoopAccess {
    @Unique
    private BlockPos farmAndCharm$coopTarget;

    @Override
    public BlockPos farmAndCharm$getCoopTarget() {
        return farmAndCharm$coopTarget;
    }

    @Override
    public void farmAndCharm$setCoopTarget(BlockPos pos) {
        this.farmAndCharm$coopTarget = pos;
    }

    @Override
    public void farmAndCharm$clearCoopTarget() {
        this.farmAndCharm$coopTarget = null;
    }

    @Override
    public boolean farmAndCharm$hasCoopTarget() {
        return this.farmAndCharm$coopTarget != null;
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addCustomGoals(CallbackInfo ci) {
        Chicken chicken = (Chicken)(Object)this;
        MobAccessor accessor = (MobAccessor) chicken;
        accessor.farmAndCharm$getGoalSelector().addGoal(5, new LayEggInNestGoal(chicken));
        accessor.farmAndCharm$getGoalSelector().addGoal(8, new ChickenLocateCoopGoal(chicken));
        accessor.farmAndCharm$getGoalSelector().addGoal(9, new ChickenGoToCoopGoal(chicken));
        accessor.farmAndCharm$getGoalSelector().addGoal(10, new ChickenEnterCoopGoal(chicken));
    }

    @Unique
    private AtomicBoolean isNestFounded = new AtomicBoolean(false);

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void farmAndCharm$redirectEggLaying(CallbackInfo ci) {
        Chicken chicken = (Chicken)(Object)this;
        Level level = chicken.level();
        if (level.isClientSide || chicken.isBaby() || !chicken.isAlive() || chicken.isChickenJockey()) return;

        ChickenAccessor accessor = (ChickenAccessor) chicken;
        if (accessor.farmAndCharm$getEggTime() > 0) return;

        BlockPos origin = chicken.blockPosition();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int dx = -6; dx <= 6; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -6; dz <= 6; dz++) {
                    mutable.set(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz);
                    if (level.getBlockState(mutable).is(ObjectRegistry.CHICKEN_NEST.get())) {
                        isNestFounded.getAndSet(true);
                        BlockEntity be = level.getBlockEntity(mutable);
                        if (be instanceof StorageBlockEntity storage) {
                            for (int i = 0; i < storage.getInventory().size(); i++) {
                                if (storage.getInventory().get(i).isEmpty()) {
                                    ci.cancel();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Chicken;isAlive()Z"))
    private boolean farmAndCharm$checkNestFounded(boolean original) {
        return original && !isNestFounded.get();
    }
}

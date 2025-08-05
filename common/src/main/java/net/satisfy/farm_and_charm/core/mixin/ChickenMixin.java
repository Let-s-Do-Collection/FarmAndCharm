package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.StorageBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Chicken.class)
public class ChickenMixin {
    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    private void addCustomFoodItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ObjectRegistry.CORN.get())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Chicken;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"), cancellable = true)
    private void divertEggToNest(CallbackInfo ci) {
        Chicken chicken = (Chicken)(Object) this;
        Level level = chicken.level();
        BlockPos chickenPos = chicken.blockPosition();
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    mutable.set(chickenPos.getX() + dx, chickenPos.getY() + dy, chickenPos.getZ() + dz);
                    BlockEntity blockEntity = level.getBlockEntity(mutable);
                    if (blockEntity instanceof StorageBlockEntity storage &&
                            level.getBlockState(mutable).is(ObjectRegistry.CHICKEN_NEST.get())) {
                        for (int i = 0; i < storage.getInventory().size(); i++) {
                            if (storage.getInventory().get(i).isEmpty()) {
                                storage.setStack(i, new ItemStack(Items.EGG));
                                storage.setChanged();
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
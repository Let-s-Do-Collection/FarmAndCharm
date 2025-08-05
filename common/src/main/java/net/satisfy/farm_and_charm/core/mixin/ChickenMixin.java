package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

@Mixin(Chicken.class)
public class ChickenMixin {
    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    private void addCustomFoodItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ObjectRegistry.CORN.get())) {
            cir.setReturnValue(true);
        }
    }
}
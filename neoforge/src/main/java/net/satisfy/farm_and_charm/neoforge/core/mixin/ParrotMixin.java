package net.satisfy.farm_and_charm.neoforge.core.mixin;

import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Parrot.class)
public class ParrotMixin {
    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    private void addCustomFoodItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ObjectRegistry.CORN.get())) {
            cir.setReturnValue(true);
        }
    }
}
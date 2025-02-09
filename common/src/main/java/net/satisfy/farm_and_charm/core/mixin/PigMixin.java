package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

@Mixin(Pig.class)
public class PigMixin {
    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    private void addCustomFoodItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ObjectRegistry.TOMATO.get()) ||
                stack.is(ObjectRegistry.STRAWBERRY.get()) ||
                stack.is(ObjectRegistry.ONION.get()) ||
                stack.is(ObjectRegistry.LETTUCE.get()) ||
                stack.is(ObjectRegistry.BARLEY.get()) ||
                stack.is(ObjectRegistry.CORN.get()) ||
                stack.is(ObjectRegistry.OAT.get())) {
            cir.setReturnValue(true);
        }
    }
}

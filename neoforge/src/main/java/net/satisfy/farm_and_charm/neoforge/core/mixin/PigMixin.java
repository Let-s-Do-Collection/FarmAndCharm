package net.satisfy.farm_and_charm.neoforge.core.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pig.class)
public abstract class PigMixin extends Animal {

    protected PigMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("RETURN"))
    private void farmAndCharm$addCustomFoodTempts(CallbackInfo ci) {
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.2, (itemStack) -> {
            return itemStack.is(ObjectRegistry.TOMATO.get()) ||
                    itemStack.is(ObjectRegistry.STRAWBERRY.get()) ||
                    itemStack.is(ObjectRegistry.ONION.get()) ||
                    itemStack.is(ObjectRegistry.LETTUCE.get()) ||
                    itemStack.is(ObjectRegistry.BARLEY.get()) ||
                    itemStack.is(ObjectRegistry.CORN.get()) ||
                    itemStack.is(ObjectRegistry.OAT.get());
        }, false));
    }

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
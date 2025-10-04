package net.satisfy.farm_and_charm.fabric.core.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.farm_and_charm.core.item.HorseFodderItem;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public class AbstractHorseMixin {
    @Inject(method = "isFood", at = @At("HEAD"), cancellable = true)
    private void farmandcharm$isFood(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(ObjectRegistry.BARLEY.get()) || stack.is(ObjectRegistry.OAT.get()) || stack.getItem() instanceof HorseFodderItem) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "fedFood", at = @At("HEAD"), cancellable = true)
    private void farmandcharm$fedFood(Player player, ItemStack stack, CallbackInfoReturnable<InteractionResult> cir) {
        AbstractHorse self = (AbstractHorse) (Object) this;
        boolean barley = stack.is(ObjectRegistry.BARLEY.get());
        boolean oat = stack.is(ObjectRegistry.OAT.get());
        boolean fodder = stack.getItem() instanceof HorseFodderItem;
        if (!barley && !oat && !fodder) return;
        if (fodder && !self.isTamed()) {
            cir.setReturnValue(InteractionResult.PASS);
            return;
        }
        float heal = fodder ? 4.0F : (oat ? 3.0F : 2.0F);
        int age = fodder ? 0 : (oat ? 60 : 20);
        int temper = fodder ? 0 : 3;
        boolean changed = false;
        if (self.getHealth() < self.getMaxHealth() && heal > 0.0F) {
            self.heal(heal);
            changed = true;
        }
        if (!fodder && self.isBaby() && age > 0) {
            self.level().addParticle(ParticleTypes.HAPPY_VILLAGER, self.getRandomX(1.0), self.getRandomY() + 0.5, self.getRandomZ(1.0), 0.0, 0.0, 0.0);
            if (!self.level().isClientSide) {
                self.ageUp(age);
                changed = true;
            }
        }
        if (!fodder && temper > 0 && (changed || !self.isTamed()) && self.getTemper() < self.getMaxTemper() && !self.level().isClientSide) {
            self.modifyTemper(temper);
            changed = true;
        }
        if (!changed) {
            cir.setReturnValue(InteractionResult.PASS);
            return;
        }
        if (!self.level().isClientSide) {
            stack.consume(1, player);
        }
        self.gameEvent(GameEvent.EAT);
        cir.setReturnValue(self.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS);
    }
}
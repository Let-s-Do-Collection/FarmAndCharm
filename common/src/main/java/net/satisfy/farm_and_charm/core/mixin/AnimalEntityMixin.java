package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.entity.ai.ApproachFeedingTroughGoal;
import net.satisfy.farm_and_charm.core.util.SaturationTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class AnimalEntityMixin extends Mob implements SaturationTracker.SaturatedAnimal {

    @Unique
    private final SaturationTracker farm_and_charm$saturation = new SaturationTracker();

    protected AnimalEntityMixin(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void AFTAddSelfFeedingGoal(EntityType<? extends Mob> entityType, Level world, CallbackInfo ci) {
        if (!world.isClientSide) {
            this.goalSelector.addGoal(3, new ApproachFeedingTroughGoal((Animal) (Object) this, 1.2D));
        }
    }

    @Override
    public SaturationTracker farm_and_charm$getSaturationTracker() {
        return farm_and_charm$saturation;
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void farm_and_charm$tickSaturation(CallbackInfo ci) {
        EntityType<?> type = this.getType();
        if (!(type == EntityType.COW || type == EntityType.PIG || type == EntityType.SHEEP || type == EntityType.CHICKEN)) return;
        farm_and_charm$saturation.tick((Animal)(Object)this);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void farm_and_charm$injectSaturationFeeding(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        EntityType<?> type = this.getType();
        if (!(type == EntityType.COW || type == EntityType.PIG || type == EntityType.SHEEP || type == EntityType.CHICKEN)) return;

        Animal animal = (Animal)(Object)this;
        ItemStack stack = player.getItemInHand(hand);

        if (!animal.isFood(stack) || animal.isBaby()) return;

        farm_and_charm$saturation.tryFeed(animal, player, hand);

        if (!animal.level().isClientSide) {
            ((ServerLevel)animal.level()).sendParticles(ParticleTypes.HAPPY_VILLAGER, animal.getX(), animal.getY() + 1.0, animal.getZ(), 5, 0.2, 0.2, 0.2, 0.05);
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(animal.level().isClientSide));
    }
}

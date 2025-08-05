package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.entity.ai.ApproachFeedingTroughGoal;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.farm_and_charm.core.network.packet.SyncSaturationPacket;
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
    private SaturationTracker farm_and_charm$saturation;

    protected AnimalEntityMixin(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public SaturationTracker farm_and_charm$getSaturationTracker() {
        if (farm_and_charm$saturation == null) {
            farm_and_charm$saturation = new SaturationTracker();
        }
        return farm_and_charm$saturation;
    }

    @Override
    public void farm_and_charm$setSaturationTracker(SaturationTracker tracker) {
        this.farm_and_charm$saturation = tracker;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void farm_and_charm$addSelfFeedingGoal(EntityType<? extends Mob> entityType, Level level, CallbackInfo ci) {
        if (!level.isClientSide) {
            this.goalSelector.addGoal(3, new ApproachFeedingTroughGoal((Animal) (Object) this, 1.2D));
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void farm_and_charm$tickSaturation(CallbackInfo ci) {
        if (!this.level().isClientSide) {
            EntityType<?> type = this.getType();
            if (!(type == EntityType.COW || type == EntityType.PIG || type == EntityType.SHEEP || type == EntityType.CHICKEN)) return;

            SaturationTracker tracker = farm_and_charm$getSaturationTracker();
            tracker.tick((Animal)(Object)this);

            SyncSaturationPacket packet = new SyncSaturationPacket(this.getId(), tracker.level(), tracker.foodCounter());
            PacketHandler.sendSaturationSync(packet, this);
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void farm_and_charm$injectSaturationFeeding(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        EntityType<?> type = this.getType();
        if (!(type == EntityType.COW || type == EntityType.PIG || type == EntityType.SHEEP || type == EntityType.CHICKEN)) return;

        Animal animal = (Animal)(Object)this;
        ItemStack stack = player.getItemInHand(hand);

        if (!animal.isFood(stack) || animal.isBaby()) return;

        if (animal.canFallInLove()) return;

        SaturationTracker tracker = farm_and_charm$getSaturationTracker();
        tracker.tryFeed(animal, player, hand);

        if (!animal.level().isClientSide) {
            SyncSaturationPacket packet = new SyncSaturationPacket(this.getId(), tracker.level(), tracker.foodCounter());

            if (player instanceof ServerPlayer serverPlayer) {
                PacketHandler.sendToClient(serverPlayer, packet);
            }

            ((ServerLevel)animal.level()).sendParticles(ParticleTypes.HAPPY_VILLAGER, animal.getX(), animal.getY() + 1.0, animal.getZ(), 5, 0.2, 0.2, 0.2, 0.05);
        }

        cir.setReturnValue(InteractionResult.sidedSuccess(animal.level().isClientSide));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void farm_and_charm$saveSaturation(CompoundTag tag, CallbackInfo ci) {
        SaturationTracker tracker = farm_and_charm$getSaturationTracker();
        CompoundTag trackerTag = new CompoundTag();
        trackerTag.putInt("SaturationLevel", tracker.level());
        trackerTag.putInt("SaturationCounter", tracker.foodCounter());
        trackerTag.putLong("SaturationLastFed", tracker.getLastFedTick());
        trackerTag.putInt("SaturationDecayDelay", tracker.getDecayDelay());
        tag.put("FarmAndCharmSaturation", trackerTag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void farm_and_charm$loadSaturation(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("FarmAndCharmSaturation", 10)) {
            CompoundTag trackerTag = tag.getCompound("FarmAndCharmSaturation");
            SaturationTracker tracker = new SaturationTracker();
            tracker.setLevel(trackerTag.getInt("SaturationLevel"));
            tracker.setFoodCounter(trackerTag.getInt("SaturationCounter"));
            tracker.setLastFedTick(trackerTag.getLong("SaturationLastFed"));
            tracker.setDecayDelay(trackerTag.getInt("SaturationDecayDelay"));
            farm_and_charm$setSaturationTracker(tracker);
        }
    }
}

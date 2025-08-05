package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.farm_and_charm.core.block.entity.StorageBlockEntity;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.entity.ai.ChickenGotoAndEnterCoopGoal;
import net.satisfy.farm_and_charm.core.entity.ai.ChickenLocateCoopGoal;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chicken.class)
public class ChickenMixin implements ChickenCoopAccess {

    @Unique
    private BlockPos farmAndCharm$coopTarget;
    @Unique
    private boolean farmAndCharm$searchedForCoop = false;
    @Unique
    private int farmAndCharm$coopCooldown = 0;


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
    public boolean farmAndCharm$searchedForCoop() { return this.farmAndCharm$searchedForCoop;}

    @Override
    public boolean farmAndCharm$hasCoopTarget() {
        return this.farmAndCharm$coopTarget != null;
    }

    @Override
    public void farmAndCharm$setSearchedForCoop(boolean value) {
        this.farmAndCharm$searchedForCoop = value;
    }

    @Override
    public int farmAndCharm$getCoopCooldown() {
        return this.farmAndCharm$coopCooldown;
    }

    @Override
    public void farmAndCharm$setCoopCooldown(int cooldown) {
        this.farmAndCharm$coopCooldown = cooldown;
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void farmAndCharm$tickCoopCooldown(CallbackInfo ci) {
        if (farmAndCharm$coopCooldown > 0) farmAndCharm$coopCooldown--;
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addCustomGoals(CallbackInfo ci) {
        Chicken chicken = (Chicken) (Object) this;
        GoalSelector goalSelector = ((MobAccessor) chicken).farmAndCharm$getGoalSelector();
        goalSelector.addGoal(8, new ChickenLocateCoopGoal(chicken));
        goalSelector.addGoal(9, new ChickenGotoAndEnterCoopGoal(chicken));
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Chicken;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity redirectEggLaying(Chicken instance, ItemLike item) {
        if (!item.equals(Items.EGG)) return instance.spawnAtLocation(item);

        Level level = instance.level();
        if (level.isClientSide() || instance.isBaby() || !instance.isAlive() || instance.isChickenJockey()) {
            return instance.spawnAtLocation(item);
        }

        BlockPos origin = instance.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-6, -2, -6), origin.offset(6, 2, 6))) {
            if (level.getBlockState(pos).is(ObjectRegistry.CHICKEN_NEST.get())) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof StorageBlockEntity storage) {
                    for (int i = 0; i < storage.getInventory().size(); i++) {
                        if (storage.getInventory().get(i).isEmpty()) {
                            storage.getInventory().set(i, new ItemStack(Items.EGG));
                            storage.setChanged();
                            level.getChunkAt(pos).setUnsaved(true);
                            level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                            instance.gameEvent(GameEvent.ENTITY_PLACE);
                            return null;
                        }
                    }
                }
            }
        }

        return instance.spawnAtLocation(item);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void farmAndCharm$saveCoopData(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("CoopCooldown", farmAndCharm$coopCooldown);
        tag.putBoolean("SearchedForCoop", farmAndCharm$searchedForCoop);
        if (farmAndCharm$coopTarget != null) {
            tag.putInt("CoopTargetX", farmAndCharm$coopTarget.getX());
            tag.putInt("CoopTargetY", farmAndCharm$coopTarget.getY());
            tag.putInt("CoopTargetZ", farmAndCharm$coopTarget.getZ());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void farmAndCharm$loadCoopData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("CoopCooldown")) {
            farmAndCharm$coopCooldown = tag.getInt("CoopCooldown");
        }
        if (tag.contains("SearchedForCoop")) {
            farmAndCharm$searchedForCoop = tag.getBoolean("SearchedForCoop");
        }
        if (tag.contains("CoopTargetX") && tag.contains("CoopTargetY") && tag.contains("CoopTargetZ")) {
            int x = tag.getInt("CoopTargetX");
            int y = tag.getInt("CoopTargetY");
            int z = tag.getInt("CoopTargetZ");
            farmAndCharm$coopTarget = new BlockPos(x, y, z);
        }
    }
}
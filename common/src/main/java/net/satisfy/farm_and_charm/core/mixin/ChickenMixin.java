package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.BlockPos;
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

    @Override
    public boolean farmAndCharm$hasSearchedForCoop() {
        return this.farmAndCharm$searchedForCoop;
    }

    @Override
    public void farmAndCharm$setSearchedForCoop(boolean value) {
        this.farmAndCharm$searchedForCoop = value;
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void addCustomGoals(CallbackInfo ci) {
        Chicken chicken = (Chicken)(Object)this;
        GoalSelector goalSelector = ((MobAccessor)chicken).farmAndCharm$getGoalSelector();
        goalSelector.addGoal(8, new ChickenLocateCoopGoal(chicken));
        goalSelector.addGoal(9, new ChickenGotoAndEnterCoopGoal(chicken));
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/Chicken;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
    private ItemEntity redirectEggLaying(Chicken instance, ItemLike item) {
        if (!item.equals(Items.EGG)) return instance.spawnAtLocation(item);
        if (((ChickenCoopAccess) instance).farmAndCharm$hasCoopTarget()) return null;
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
}

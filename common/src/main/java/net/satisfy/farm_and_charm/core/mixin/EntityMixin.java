package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.foundation.storage.StorageBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("HEAD"), cancellable = true)
    private void farmAndCharm$layEggInNest(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
        if (!((Object) this instanceof Animal animal)) return;
        if (stack.isEmpty() || !stack.is(TagRegistry.NEST_EGGS) || !animal.getType().is(TagRegistry.NEST_LAYERS)) return;

        Level level = animal.level();
        if (level.isClientSide() || animal.isBaby() || !animal.isAlive()) return;

        BlockPos origin = animal.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-6, -2, -6), origin.offset(6, 2, 6))) {
            if (!level.getBlockState(pos).is(ObjectRegistry.CHICKEN_NEST.get())) continue;
            if (!(level.getBlockEntity(pos) instanceof StorageBlockEntity storage)) continue;
            for (int i = 0; i < storage.getInventory().size(); i++) {
                if (storage.getInventory().get(i).isEmpty()) {
                    storage.getInventory().set(i, stack.copyWithCount(1));
                    storage.setChanged();
                    level.getChunkAt(pos).setUnsaved(true);
                    level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), 3);
                    animal.gameEvent(GameEvent.ENTITY_PLACE);
                    cir.setReturnValue(null);
                    return;
                }
            }
        }
    }
}

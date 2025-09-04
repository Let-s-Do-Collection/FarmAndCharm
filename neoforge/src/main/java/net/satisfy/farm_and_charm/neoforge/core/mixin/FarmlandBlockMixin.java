package net.satisfy.farm_and_charm.neoforge.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmBlock.class)
public class FarmlandBlockMixin {
    @Inject(method = "isNearWater", at = @At("HEAD"), cancellable = true)
    private static void injectWaterSprinklerCheck(LevelReader levelReader, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        int range = PlatformHelper.getWaterSprinklerRange();
        for (BlockPos p : BlockPos.betweenClosed(
                blockPos.offset(-range, -1, -range),
                blockPos.offset(range, 1, range))) {
            if (levelReader.getBlockState(p).is(ObjectRegistry.WATER_SPRINKLER.get())) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Inject(method = "fallOn", at = @At("HEAD"), cancellable = true)
    private void farm_and_charm$preventTrampleWithDungarees(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float f, CallbackInfo ci) {
        if (!(entity instanceof Player player)) return;
        ItemStack legs = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS);
        if (legs.getItem() == ObjectRegistry.DUNGAREES.get()) ci.cancel();
    }
}

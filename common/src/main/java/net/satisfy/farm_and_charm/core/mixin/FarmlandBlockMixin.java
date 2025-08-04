package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(FarmBlock.class)
public class FarmlandBlockMixin {
    @Inject(method = "isNearWater", at = @At("HEAD"), cancellable = true)
    private static void injectWaterSprinklerCheck(LevelReader levelReader, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        int range = PlatformHelper.getWaterSprinklerRange();
        Iterator<BlockPos> var2 = BlockPos.betweenClosed(blockPos.offset(-range, 1, -range), blockPos.offset(range, 1, range)).iterator();

        BlockPos blockPos2;
        do {
            if (!var2.hasNext()) {
                return;
            }
            blockPos2 = var2.next();
        } while (!levelReader.getBlockState(blockPos2).is(ObjectRegistry.WATER_SPRINKLER.get()));
        cir.setReturnValue(true);
    }
}

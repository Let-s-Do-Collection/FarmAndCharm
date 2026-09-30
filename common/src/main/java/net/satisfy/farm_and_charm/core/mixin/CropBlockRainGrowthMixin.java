package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public abstract class CropBlockRainGrowthMixin {
    @Unique
    private static boolean farm_and_charm$inRainBonus;

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void farm_and_charm$growFasterInRain(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (farm_and_charm$inRainBonus || !PlatformHelper.isRainGrowthEffectEnabled() || !level.isRainingAt(pos.above())) {
            return;
        }
        float bonus = PlatformHelper.getRainGrowthMultiplier();
        int extraRolls = (int) bonus + (random.nextFloat() < bonus - (int) bonus ? 1 : 0);
        farm_and_charm$inRainBonus = true;
        try {
            for (int i = 0; i < extraRolls; i++) {
                BlockState current = level.getBlockState(pos);
                if (!(current.getBlock() instanceof CropBlock crop) || crop.isMaxAge(current)) {
                    break;
                }
                current.randomTick(level, pos, random);
            }
        } finally {
            farm_and_charm$inRainBonus = false;
        }
    }
}

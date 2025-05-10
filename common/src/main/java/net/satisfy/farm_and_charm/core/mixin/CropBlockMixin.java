package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin {

    @Shadow
    public abstract BlockState getStateForAge(int age);

    @Shadow
    public abstract int getAge(BlockState state);

    @Shadow public abstract boolean isMaxAge(BlockState arg);

    @Inject(at = @At("HEAD"), method = "randomTick")
    public void boostGrowthInRain(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (PlatformHelper.isRainGrowthEffectEnabled() && level.isRainingAt(pos.above()) && !this.isMaxAge(state)) {
            float growthChance = level.isThundering() ? 0.7f : 0.5f;
            growthChance *= PlatformHelper.getRainGrowthMultiplier();
            if (random.nextFloat() < growthChance) {
                level.setBlock(pos, this.getStateForAge(this.getAge(state) + 1), 2);
                for (int i = 0; i < 5; i++) {
                    double offsetX = random.nextDouble() - 0.5;
                    double offsetY = random.nextDouble() * 0.5;
                    double offsetZ = random.nextDouble() - 0.5;
                    level.sendParticles(ParticleTypes.WAX_OFF, pos.getX() + 0.5 + offsetX, pos.getY() + 1.0 + offsetY, pos.getZ() + 0.5 + offsetZ, 1, 0.0, 0.0, 0.0, 0.1);
                }
            }
        }
    }


    @Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
    private void mayPlaceOn(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (blockState.is(ObjectRegistry.FERTILIZED_FARM_BLOCK.get())) {
            cir.setReturnValue(true);
        }
    }
}


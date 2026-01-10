package net.satisfy.farm_and_charm.neoforge.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;
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

    @Inject(method = "getGrowthSpeed", at = @At("HEAD"), cancellable = true)
    private static void getGrowthSpeed(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Float> cir) {
        Block block = blockState.getBlock();
        float value = 1.0F;
        BlockPos below = blockPos.below();

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                float g = 0.0F;

                label: {
                    BlockState blockState2 = blockGetter.getBlockState(below.offset(i, 0, j));
                    TriState soilDecision = blockState2.canSustainPlant(blockGetter, below.offset(i, 0, j), Direction.UP, blockState);
                    if (soilDecision.isDefault()) {
                        if (!(blockState2.getBlock() instanceof FarmBlock)) {
                            break label;
                        }
                    } else if (!soilDecision.isTrue()) {
                        break label;
                    }

                    g = 1.0F;
                    if (blockState2.isFertile(blockGetter, blockPos.offset(i, 0, j))) {
                        g = 3.0F;
                    } else if (blockState.is(ObjectRegistry.FERTILIZED_FARM_BLOCK.get())) {
                        g = 4.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    g /= 4.0F;
                }

                value += g;
            }
        }

        BlockPos north = blockPos.north();
        BlockPos south = blockPos.south();
        BlockPos west = blockPos.west();
        BlockPos east = blockPos.east();
        boolean isFertilized1 = blockGetter.getBlockState(west).is(block) || blockGetter.getBlockState(east).is(block);
        boolean isFertilized2 = blockGetter.getBlockState(north).is(block) || blockGetter.getBlockState(south).is(block);
        if (isFertilized1 && isFertilized2) {
            value /= 2.0F;
        } else {
            boolean isFertilized3 = blockGetter.getBlockState(west.north()).is(block) || blockGetter.getBlockState(east.north()).is(block) || blockGetter.getBlockState(east.south()).is(block) || blockGetter.getBlockState(west.south()).is(block);
            if (isFertilized3) {
                value /= 2.0F;
            }
        }

        cir.setReturnValue(value);
    }
}

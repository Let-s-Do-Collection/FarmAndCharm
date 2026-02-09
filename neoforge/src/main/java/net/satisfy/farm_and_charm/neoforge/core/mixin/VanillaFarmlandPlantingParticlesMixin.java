package net.satisfy.farm_and_charm.neoforge.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public class VanillaFarmlandPlantingParticlesMixin {
    @Inject(method = "neighborChanged(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;Z)V", at = @At("TAIL"))
    private void farm_and_charm$vanillaFarmlandPlantingParticles(BlockState blockState, Level level, BlockPos blockPos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (!blockState.is(Blocks.FARMLAND)) {
            return;
        }

        if (!neighborPos.equals(blockPos.above())) {
            return;
        }

        BlockState stateAbove = serverLevel.getBlockState(neighborPos);
        if (!(stateAbove.getBlock() instanceof CropBlock)) {
            return;
        }

        RandomSource randomSource = serverLevel.random;

        for (int i = 0; i < 10; i++) {
            double offsetX = blockPos.getX() + 0.5 + (randomSource.nextDouble() - 0.5) * 0.8;
            double offsetY = blockPos.getY() + 1.0;
            double offsetZ = blockPos.getZ() + 0.5 + (randomSource.nextDouble() - 0.5) * 0.8;

            serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), offsetX, offsetY, offsetZ, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }
}
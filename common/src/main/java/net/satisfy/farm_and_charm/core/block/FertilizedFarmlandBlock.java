package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;

public class FertilizedFarmlandBlock extends FarmBlock {
    public FertilizedFarmlandBlock(Properties properties) {
        super(properties);
    }

    public static void turnToSoil(@Nullable Entity entity, BlockState blockState, Level level, BlockPos blockPos) {
        BlockState blockState2 = pushEntitiesUp(blockState, ObjectRegistry.FERTILIZED_SOIL_BLOCK.get().defaultBlockState(), level, blockPos);
        level.setBlockAndUpdate(blockPos, blockState2);
        level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(entity, blockState2));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return !this.defaultBlockState().canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos()) ? ObjectRegistry.FERTILIZED_SOIL_BLOCK.get().defaultBlockState() : super.getStateForPlacement(blockPlaceContext);
    }

    @Override
    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (randomSource.nextFloat() < getGrowthChance(serverLevel, blockPos)) {
            applyBonemealEffect(serverLevel, blockPos, randomSource);
        }
    }

    private float getGrowthChance(ServerLevel serverLevel, BlockPos blockPos) {
        int lightLevel = serverLevel.getMaxLocalRawBrightness(blockPos.above());
        return lightLevel >= 10 ? 0.055f : 0.05f;
    }

    private void applyBonemealEffect(ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!PlatformHelper.isBonemealEffectEnabled()) {
            return;
        }
        BlockPos posAbove = blockPos.above();
        BlockState stateAbove = serverLevel.getBlockState(posAbove);
        if (stateAbove.getBlock() instanceof BonemealableBlock bonemealableBlock && bonemealableBlock.isValidBonemealTarget(serverLevel, posAbove, stateAbove)) {
            bonemealableBlock.performBonemeal(serverLevel, randomSource, posAbove, stateAbove);
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, posAbove.getX() + 0.5, posAbove.getY() + 1.0, posAbove.getZ() + 0.5, 5, 0.5, 0.5, 0.5, 0.5);
        }
        checkAndTurnToSoil(serverLevel, blockPos, serverLevel.getBlockState(blockPos));
    }

    private void checkAndTurnToSoil(ServerLevel serverLevel, BlockPos blockPos, BlockState currentBlockState) {
        if (currentBlockState.is(ObjectRegistry.FERTILIZED_SOIL_BLOCK.get())) {
            turnToSoil(null, currentBlockState, serverLevel, blockPos);
        }
    }

    @Override
    public void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float f) {
    }

}

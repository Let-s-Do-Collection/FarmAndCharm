package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

public class ScarecrowBlockEntity extends BlockEntity {
    private long lastTickTime = 0;

    public ScarecrowBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.SCARECROW_BLOCK_ENTITY.get(), pos, state);
    }

    public void tick() {
        if (this.level instanceof ServerLevel serverLevel && !this.level.isClientSide) {
            long currentTime = serverLevel.getGameTime();
            long bonemealInterval = 20 * 25;
            if (currentTime - lastTickTime > bonemealInterval) {
                lastTickTime = currentTime;
                applyBonemealEffect(serverLevel);
            }
        }
    }

    private void applyBonemealEffect(ServerLevel serverLevel) {
        BlockPos.betweenClosedStream(this.getBlockPos().offset(-8, -1, -8), this.getBlockPos().offset(8, 1, 8))
                .forEach(pos -> {
                            BlockState blockState = serverLevel.getBlockState(pos);
                            CropBlock cropBlock = (CropBlock) blockState.getBlock();
                            if (!cropBlock.isMaxAge(blockState)) {
                                cropBlock.randomTick(blockState, serverLevel, pos, serverLevel.random);
                                serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
                            }
                        }
                );
    }
}

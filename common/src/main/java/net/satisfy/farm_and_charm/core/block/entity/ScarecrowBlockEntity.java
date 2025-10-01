package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

public class ScarecrowBlockEntity extends BlockEntity {

    private long lastTickTime = 0;

    public ScarecrowBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.SCARECROW_BLOCK_ENTITY.get(), pos, state);
    }

    public static <T extends BlockEntity> void tick(Level level, T be) {
        if (!(be instanceof ScarecrowBlockEntity self)) return;
        if (level instanceof ServerLevel serverLevel) {
            long current = serverLevel.getGameTime();
            long interval = 20 * 25;
            if (current - self.lastTickTime > interval) {
                self.lastTickTime = current;
                BlockPos.betweenClosedStream(
                        self.worldPosition.offset(-8, -1, -8),
                        self.worldPosition.offset(8, 1, 8)
                ).forEach(p -> {
                    var bs = serverLevel.getBlockState(p);
                    if (bs.getBlock() instanceof CropBlock crop && !crop.isMaxAge(bs)) {
                        crop.randomTick(bs, serverLevel, p, serverLevel.random);
                        serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, p, GameEvent.Context.of(bs));
                    } else if (bs.getBlock() instanceof ClimbingCropBlock) {
                        ((ClimbingCropBlock) bs.getBlock()).randomTick(bs, serverLevel, p, serverLevel.random);
                        serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, p, GameEvent.Context.of(bs));
                    }
                });
            }
        }
    }
}

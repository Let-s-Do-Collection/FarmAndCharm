package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

public class ScarecrowBlockEntity extends BlockEntity {
    private static final long GROWTH_INTERVAL_TICKS = 20L * 25L;

    private long nextGrowthTime;

    public ScarecrowBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.SCARECROW_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.nextGrowthTime = tag.getLong("NextGrowthTime");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putLong("NextGrowthTime", this.nextGrowthTime);
    }

    public static <T extends BlockEntity> void tick(Level level, T be) {
        if (!(be instanceof ScarecrowBlockEntity self)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        long currentTime = serverLevel.getGameTime();

        if (self.nextGrowthTime == 0L) {
            self.nextGrowthTime = currentTime + GROWTH_INTERVAL_TICKS;
            self.setChanged();
            return;
        }

        if (currentTime < self.nextGrowthTime) return;

        self.nextGrowthTime = currentTime + GROWTH_INTERVAL_TICKS;
        self.setChanged();

        BlockPos.betweenClosedStream(
                self.worldPosition.offset(-8, -1, -8),
                self.worldPosition.offset(8, 1, 8)
        ).forEach(targetPos -> {
            BlockState targetState = serverLevel.getBlockState(targetPos);

            if (targetState.getBlock() instanceof CropBlock crop && !crop.isMaxAge(targetState)) {
                crop.randomTick(targetState, serverLevel, targetPos, serverLevel.random);
                serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Context.of(targetState));
                return;
            }

            if (targetState.getBlock() instanceof ClimbingCropBlock climbingCropBlock) {
                climbingCropBlock.randomTick(targetState, serverLevel, targetPos, serverLevel.random);
                serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Context.of(targetState));
            }
        });
    }
}

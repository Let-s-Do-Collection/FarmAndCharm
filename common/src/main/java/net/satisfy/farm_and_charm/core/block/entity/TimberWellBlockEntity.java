package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

/**
 * Only exists on the top part of the Timber Well so the pump lever can be animated. Nothing is saved.
 */
public class TimberWellBlockEntity extends BlockEntity {
    public static final int PUMP_EVENT = 1;
    public static final int PUMP_DURATION_TICKS = 12;

    private long pumpStartTick = -PUMP_DURATION_TICKS;

    public TimberWellBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.TIMBER_WELL_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean isPumping(long gameTime) {
        return gameTime - this.pumpStartTick < PUMP_DURATION_TICKS;
    }

    public long getPumpStartTick() {
        return this.pumpStartTick;
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == PUMP_EVENT && this.level != null) {
            this.pumpStartTick = this.level.getGameTime();
            return true;
        }
        return super.triggerEvent(id, type);
    }
}

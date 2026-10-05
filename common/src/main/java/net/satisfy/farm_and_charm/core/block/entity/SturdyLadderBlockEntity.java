package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.SturdyLadderBlock;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class SturdyLadderBlockEntity extends BlockEntity {
    private long lastClimbed;
    private long shakeStart;

    public SturdyLadderBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.STURDY_LADDER_BLOCK_ENTITY.get(), pos, state);
    }

    public long getLastClimbed() {
        return this.lastClimbed;
    }

    public void setLastClimbed(long time) {
        this.lastClimbed = time;
    }

    public long getShakeStart() {
        return this.shakeStart;
    }

    @Override
    public void setBlockState(@NotNull BlockState state) {
        if (this.level != null && state.getValue(SturdyLadderBlock.SHAKING) && !this.getBlockState().getValue(SturdyLadderBlock.SHAKING)) {
            this.shakeStart = this.level.getGameTime();
        }
        super.setBlockState(state);
    }
}

package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

public class FeedingTroughBlockEntity extends BlockEntity {

    public FeedingTroughBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.FEEDING_TROUGH_BLOCK_ENTITY.get(), pos, state);
    }
}
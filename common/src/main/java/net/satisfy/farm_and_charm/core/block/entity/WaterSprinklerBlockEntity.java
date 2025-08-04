package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

public class WaterSprinklerBlockEntity extends BlockEntity {
    private float rotationAngle;

    public WaterSprinklerBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.SPRINKLER_BLOCK_ENTITY.get(), pos, state);
    }

    public float getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(float rotationAngle) {
        this.rotationAngle = rotationAngle;
    }
}

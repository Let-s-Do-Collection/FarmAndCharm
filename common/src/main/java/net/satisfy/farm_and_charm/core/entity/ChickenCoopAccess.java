package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.BlockPos;

public interface ChickenCoopAccess {
    BlockPos farmAndCharm$getCoopTarget();
    void farmAndCharm$setCoopTarget(BlockPos pos);
    void farmAndCharm$clearCoopTarget();
    boolean farmAndCharm$hasCoopTarget();

    boolean farmAndCharm$hasSearchedForCoop();
    void farmAndCharm$setSearchedForCoop(boolean value);
}
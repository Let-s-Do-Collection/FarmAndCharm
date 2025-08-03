package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.BlockPos;

public interface ChickenCoopAccess {
    boolean farmAndCharm$hasCoopTarget();

    BlockPos farmAndCharm$getCoopTarget();

    void farmAndCharm$setCoopTarget(BlockPos pos);

    void farmAndCharm$clearCoopTarget();

    boolean farmAndCharm$searchedForCoop();

    void farmAndCharm$setSearchedForCoop(boolean searched);

    int farmAndCharm$getCoopCooldown();

    void farmAndCharm$setCoopCooldown(int ticks);
}

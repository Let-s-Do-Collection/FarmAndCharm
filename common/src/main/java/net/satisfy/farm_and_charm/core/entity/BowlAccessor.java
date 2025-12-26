package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.BlockPos;

@SuppressWarnings("unused")
public interface BowlAccessor {
    interface StayNearBowl {
        void farmAndCharm$setStayCenter(BlockPos pos);
        void farmAndCharm$clearStayRestriction();
        boolean farmAndCharm$hasStayRestriction();
        boolean farmAndCharm$isWithinStayRange(BlockPos pos);
        BlockPos farmAndCharm$getStayCenter();
    }

    interface FedTracker {
        void farmAndCharm$$markAsFed();
        boolean farmAndCharm$$isFed();
        void farmAndCharm$$resetFed();
    }
}
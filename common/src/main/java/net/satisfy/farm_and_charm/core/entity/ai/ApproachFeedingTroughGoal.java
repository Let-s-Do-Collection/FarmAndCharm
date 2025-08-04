package net.satisfy.farm_and_charm.core.entity.ai;


import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.FeedingTroughBlock;

public class ApproachFeedingTroughGoal extends MoveToBlockGoal {
    protected final Animal animal;

    public ApproachFeedingTroughGoal(Animal animal, double speed) {
        super(animal, speed, 8);
        this.animal = animal;
    }

    @Override
    public void tick() {
        Level world = this.animal.getCommandSenderWorld();
        if (!world.isClientSide() && this.animal.canFallInLove()) {
            BlockState blockState = world.getBlockState(this.blockPos);
            if (blockState.getBlock() instanceof FeedingTroughBlock && blockState.getValue(FeedingTroughBlock.SIZE) > 0) {
                this.animal.getLookControl().setLookAt((double) this.blockPos.getX() + 0.5D, this.blockPos.getY(), (double) this.blockPos.getZ() + 0.5D, 10.0F, (float) this.animal.getMaxHeadXRot());
                if (this.isReachedTarget()) {
                    world.setBlock(this.blockPos, blockState.setValue(FeedingTroughBlock.SIZE, blockState.getValue(FeedingTroughBlock.SIZE) - 1), 3);
                    this.animal.setInLove(null);
                }
            }
        }
        super.tick();
    }

    @Override
    public boolean canUse() {
        return this.animal.canFallInLove() && this.animal.getAge() == 0 && super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.animal.canFallInLove() && this.animal.getAge() == 0;
    }

    @Override
    public double acceptedDistance() {
        return 2.25D;
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        BlockState blockState = levelReader.getBlockState(blockPos);
        if (blockState.getBlock() instanceof FeedingTroughBlock) {
            return blockState.getValue(FeedingTroughBlock.SIZE) > 0;
        }
        return false;
    }
}

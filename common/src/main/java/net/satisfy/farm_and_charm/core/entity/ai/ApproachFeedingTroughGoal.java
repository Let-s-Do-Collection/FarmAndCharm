package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.FeedingTroughBlock;
import net.satisfy.farm_and_charm.core.block.entity.FeedingTroughBlockEntity;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

public class ApproachFeedingTroughGoal extends MoveToBlockGoal {
    protected final Animal animal;

    public ApproachFeedingTroughGoal(Animal animal, double speed) {
        super(animal, speed, PlatformHelper.getFeedingTroughRange());
        this.animal = animal;
    }

    @Override
    public void tick() {
        super.tick();
        Level world = this.animal.getCommandSenderWorld();
        if (world.isClientSide()) return;
        if (!this.animal.canFallInLove()) return;

        BlockState state = world.getBlockState(this.blockPos);
        if (!(state.getBlock() instanceof FeedingTroughBlock)) return;
        if (state.getValue(FeedingTroughBlock.SIZE) <= 0) return;

        this.animal.getLookControl().setLookAt(this.blockPos.getX() + 0.5D, this.blockPos.getY(), this.blockPos.getZ() + 0.5D, 10.0F, this.animal.getMaxHeadXRot());
        if (!this.isReachedTarget()) return;
        world.setBlock(this.blockPos, state.setValue(FeedingTroughBlock.SIZE, state.getValue(FeedingTroughBlock.SIZE) - 1), 3);
        this.animal.setInLove(null);

        BlockEntity be = world.getBlockEntity(this.blockPos);
        if (be instanceof FeedingTroughBlockEntity trough) {
            trough.onAnimalFed(this.animal);
        }
    }

    @Override
    public boolean canUse() {
        Player p = animal.level().getNearestPlayer(animal, 8.0);
        if (p != null) {
            if (animal.isFood(p.getMainHandItem()) || animal.isFood(p.getOffhandItem())) return false;
        }
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
        return blockState.getBlock() instanceof FeedingTroughBlock && blockState.getValue(FeedingTroughBlock.SIZE) > 0;
    }
}

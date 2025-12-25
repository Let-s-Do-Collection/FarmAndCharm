package net.satisfy.farm_and_charm.core.entity.ai;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
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
        Level world = this.animal.getCommandSenderWorld();
        if (!world.isClientSide() && this.animal.canFallInLove()) {
            BlockState blockState = world.getBlockState(this.blockPos);
            if (blockState.getBlock() instanceof FeedingTroughBlock && blockState.getValue(FeedingTroughBlock.SIZE) > 0) {
                this.animal.getLookControl().setLookAt(this.blockPos.getX() + 0.5D, this.blockPos.getY(), this.blockPos.getZ() + 0.5D, 10.0F, this.animal.getMaxHeadXRot());
                if (this.isReachedTarget()) {
                    world.setBlock(this.blockPos, blockState.setValue(FeedingTroughBlock.SIZE, blockState.getValue(FeedingTroughBlock.SIZE) - 1), 3);
                    this.animal.setInLove(null);

                    if (world instanceof ServerLevel serverLevel) {
                        BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.HAY_BLOCK.defaultBlockState());
                        double x = this.blockPos.getX() + 0.5D;
                        double y = this.blockPos.getY() + 0.35D;
                        double z = this.blockPos.getZ() + 0.5D;
                        serverLevel.sendParticles(particle, x, y, z, 10, 0.25D, 0.12D, 0.25D, 0.02D);
                    }

                    BlockEntity be = world.getBlockEntity(this.blockPos);
                    if (be instanceof FeedingTroughBlockEntity trough) {
                        trough.onAnimalFed(this.animal);
                    }
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
        return blockState.getBlock() instanceof FeedingTroughBlock && blockState.getValue(FeedingTroughBlock.SIZE) > 0;
    }
}

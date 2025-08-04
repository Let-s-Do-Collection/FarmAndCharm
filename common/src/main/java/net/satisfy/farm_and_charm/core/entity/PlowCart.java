package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;

public class PlowCart extends CartEntity {

    public PlowCart(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void move(MoverType moverType, Vec3 vec3) {
        super.move(moverType, vec3);
        if (this.onGround()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                BlockPos blockPos = this.getOnPos();
                Direction direction = Direction.fromYRot(this.getYRot());
                BlockPos behindPos = blockPos.relative(direction.getOpposite());
                BlockPos leftPos = behindPos.relative(direction.getClockWise());
                BlockPos rightPos = behindPos.relative(direction.getCounterClockWise());
                transformGroundAndDestroyAbove(serverLevel, behindPos);
                transformGroundAndDestroyAbove(serverLevel, leftPos);
                transformGroundAndDestroyAbove(serverLevel, rightPos);
            }
        }
    }

    private void transformGroundAndDestroyAbove(ServerLevel serverLevel, BlockPos pos) {
        BlockState groundBlockState = serverLevel.getBlockState(pos);
        if (canTransformToFarmland(groundBlockState)) {
            serverLevel.setBlockAndUpdate(pos, Blocks.FARMLAND.defaultBlockState());
        }
        if (groundBlockState.is(Blocks.FARMLAND)) {
            BlockPos abovePos = pos.above();
            BlockState aboveBlockState = serverLevel.getBlockState(abovePos);
            if (aboveBlockState.is(BlockTags.SMALL_FLOWERS) || aboveBlockState.is(TagRegistry.WILD_CROPS) || aboveBlockState.is(BlockTags.TALL_FLOWERS) || aboveBlockState.is(Blocks.GRASS_BLOCK) || aboveBlockState.is(Blocks.TALL_GRASS)) {
                destroyBlockWithParticles(serverLevel, abovePos, aboveBlockState);
            }
        }
    }

    private boolean canTransformToFarmland(BlockState blockState) {
        return blockState.is(Blocks.DIRT) || blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.PODZOL);
    }


    private void destroyBlockWithParticles(ServerLevel serverLevel, BlockPos pos, BlockState blockState) {
        serverLevel.destroyBlock(pos, true);
        BlockParticleOption particleOption = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
        serverLevel.sendParticles(particleOption, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0.1);
    }

    @Override
    protected void destroy(DamageSource damageSource) {
        this.spawnAtLocation(ObjectRegistry.PLOW.get());
    }

    @Override
    protected float firstPoint() {
        return 3.0F;
    }

    @Override
    protected float lastPoint() {
        return 1.0F;
    }

    @Override
    protected float wheelRadius() {
        return 1.0F;
    }
}

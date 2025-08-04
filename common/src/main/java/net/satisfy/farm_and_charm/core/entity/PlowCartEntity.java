package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.FertilizedFarmlandBlock;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

public class PlowCartEntity extends AbstractTowableEntity {

    public PlowCartEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
    }

    @Override
    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.08, 0.0));
        }
        super.tick();
        this.tickLerp();
        if (this.driver != null) {
            this.pulledTick();
        }
        this.leftWheel.tick();
        this.rightWheel.tick();
        this.move(MoverType.SELF, this.getDeltaMovement());

        if (level().isClientSide()) {
            handleClientSide();
        } else {
            handleServerSide();
        }
    }

    private void handleClientSide() {
        BlockPos currentPos = this.blockPosition();
        BlockPos[] positions = new BlockPos[] {
                currentPos.below(),
                currentPos.below().east()
        };

        for (BlockPos pos : positions) {
            BlockState blockState = level().getBlockState(pos);
            BlockState newBlockState = null;

            if (blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.DIRT)) {
                newBlockState = Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 0);
            } else if (blockState.is(ObjectRegistry.FERTILIZED_SOIL_BLOCK.get())) {
                newBlockState = ObjectRegistry.FERTILIZED_FARM_BLOCK.get().defaultBlockState().setValue(FertilizedFarmlandBlock.MOISTURE, 0);
            }

            if (newBlockState != null) {
                for (int i = 0; i < 200; i++) {
                    double x = pos.getX() + level().random.nextDouble();
                    double y = pos.getY() + level().random.nextDouble();
                    double z = pos.getZ() + level().random.nextDouble();
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, newBlockState), x, y, z, 0, 0, 0);
                }
            }
        }
    }

    private void handleServerSide() {
        BlockPos currentPos = this.blockPosition();
        BlockPos[] positions = new BlockPos[] {
                currentPos.below(),
                currentPos.below().east()
        };

        for (BlockPos pos : positions) {
            BlockState blockState = level().getBlockState(pos);
            BlockState newBlockState = null;

            if (blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.DIRT)) {
                newBlockState = Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 0);
            } else if (blockState.is(ObjectRegistry.FERTILIZED_SOIL_BLOCK.get())) {
                newBlockState = ObjectRegistry.FERTILIZED_FARM_BLOCK.get().defaultBlockState().setValue(FertilizedFarmlandBlock.MOISTURE, 0);
            }

            if (newBlockState != null) {
                level().setBlock(pos, newBlockState, 3);
            }

            BlockPos cropPos = pos.above();
            BlockState cropState = level().getBlockState(cropPos);

            if (cropState.getBlock() instanceof CropBlock cropBlock) {
                if (cropBlock.isMaxAge(cropState)) {
                    BlockState newCropState = cropBlock.getStateForAge(0);
                    level().setBlock(cropPos, newCropState, 3);
                    level().updateNeighborsAt(cropPos, newCropState.getBlock());

                    if (level() instanceof ServerLevel serverLevel) {
                        for (ItemStack drop : Block.getDrops(cropState, serverLevel, cropPos, null)) {
                            if (!drop.isEmpty()) {
                                double dropX = cropPos.getX() + 0.5 + (level().random.nextDouble() - 0.5) * 0.5;
                                double dropY = cropPos.getY() + 1.0;
                                double dropZ = cropPos.getZ() + 0.5 + (level().random.nextDouble() - 0.5) * 0.5;

                                ItemEntity itemEntity = new ItemEntity(level(), dropX, dropY, dropZ, drop);
                                level().addFreshEntity(itemEntity);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected ItemStack getDropItem() {
        return new ItemStack(ObjectRegistry.PLOW.get());
    }
}
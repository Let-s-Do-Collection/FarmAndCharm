package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
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

public class PlowCartEntity extends AbstractCartEntity {
    public PlowCartEntity(EntityType<? extends AbstractCartEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public void tick() {
        double prevX = this.getX();
        double prevZ = this.getZ();

        super.tick();

        if (this.level().isClientSide) {
            return;
        }
        if (!(this.getPulling() instanceof net.minecraft.world.entity.player.Player)) {
            return;
        }
        if (Math.abs(this.getX() - prevX) < 1.0E-4 && Math.abs(this.getZ() - prevZ) < 1.0E-4) {
            return;
        }

        this.handlePlowServer();
    }

    @Override
    protected ItemStack getCartItemStack() {
        return new ItemStack(ObjectRegistry.PLOW.get());
    }

    private void handlePlowServer() {
        BlockPos currentPos = this.blockPosition();
        BlockPos[] positions = new BlockPos[]{currentPos.below(), currentPos.below().east()};

        for (BlockPos blockPos : positions) {
            BlockState blockState = this.level().getBlockState(blockPos);
            BlockState newBlockState = null;

            if (blockState.is(Blocks.GRASS_BLOCK) || blockState.is(Blocks.DIRT)) {
                newBlockState = Blocks.FARMLAND.defaultBlockState().setValue(FarmBlock.MOISTURE, 0);
            } else if (blockState.is(ObjectRegistry.FERTILIZED_SOIL_BLOCK.get())) {
                newBlockState = ObjectRegistry.FERTILIZED_FARM_BLOCK.get().defaultBlockState().setValue(FertilizedFarmlandBlock.MOISTURE, 0);
            }

            if (newBlockState != null) {
                this.level().setBlock(blockPos, newBlockState, 3);
            }

            BlockPos cropPos = blockPos.above();
            BlockState cropState = this.level().getBlockState(cropPos);

            if (cropState.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(cropState)) {
                BlockState resetState = cropBlock.getStateForAge(0);
                this.level().setBlock(cropPos, resetState, 3);
                this.level().updateNeighborsAt(cropPos, resetState.getBlock());

                if (this.level() instanceof ServerLevel serverLevel) {
                    for (ItemStack drop : Block.getDrops(cropState, serverLevel, cropPos, null)) {
                        if (!drop.isEmpty()) {
                            double dropX = cropPos.getX() + 0.5 + (this.level().random.nextDouble() - 0.5) * 0.5;
                            double dropY = cropPos.getY() + 1.0;
                            double dropZ = cropPos.getZ() + 0.5 + (this.level().random.nextDouble() - 0.5) * 0.5;
                            this.level().addFreshEntity(new ItemEntity(this.level(), dropX, dropY, dropZ, drop));
                        }
                    }
                }
            }
        }
    }

    @Override
    public Component getDisplayName() {
        return ObjectRegistry.PLOW.get().getDefaultInstance().getHoverName();
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ObjectRegistry.PLOW.get());
    }
}
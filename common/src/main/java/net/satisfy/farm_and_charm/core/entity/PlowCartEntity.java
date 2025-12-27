package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.FertilizedFarmlandBlock;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class PlowCartEntity extends AbstractCartEntity {
    private static final EntityDataAccessor<Integer> DATA_PLOW_EFFECT_TICKS = SynchedEntityData.defineId(PlowCartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockPos> DATA_PLOW_EFFECT_POS = SynchedEntityData.defineId(PlowCartEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Boolean> DATA_PLOW_ENABLED = SynchedEntityData.defineId(PlowCartEntity.class, EntityDataSerializers.BOOLEAN);

    public PlowCartEntity(EntityType<? extends AbstractCartEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_PLOW_EFFECT_TICKS, 0);
        builder.define(DATA_PLOW_EFFECT_POS, BlockPos.ZERO);
        builder.define(DATA_PLOW_ENABLED, false);
    }

    public int getPlowEffectTicks() {
        return this.entityData.get(DATA_PLOW_EFFECT_TICKS);
    }

    public boolean isPlowEnabled() {
        return this.entityData.get(DATA_PLOW_ENABLED);
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty() && this.getPulling() == player) {
            if (!this.level().isClientSide) {
                this.entityData.set(DATA_PLOW_ENABLED, !this.entityData.get(DATA_PLOW_ENABLED));
            }
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        int effectTicks = this.entityData.get(DATA_PLOW_EFFECT_TICKS);
        if (effectTicks <= 0) {
            return;
        }

        if (this.level().isClientSide) {
            BlockPos effectPos = this.entityData.get(DATA_PLOW_EFFECT_POS);
            BlockState particleState = this.level().getBlockState(effectPos);
            this.level().addDestroyBlockEffect(effectPos, particleState);
            return;
        }

        this.entityData.set(DATA_PLOW_EFFECT_TICKS, effectTicks - 1);
    }

    @Override
    public void pulledPostTick() {
        double prevX = this.getX();
        double prevZ = this.getZ();

        super.pulledPostTick();

        if (this.level().isClientSide) {
            return;
        }
        if (!this.entityData.get(DATA_PLOW_ENABLED)) {
            return;
        }
        if (!(this.getPulling() instanceof Player)) {
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

    private void triggerPlowEffect(BlockPos blockPos) {
        if (this.level().isClientSide) {
            return;
        }
        this.entityData.set(DATA_PLOW_EFFECT_POS, blockPos);
        int current = this.entityData.get(DATA_PLOW_EFFECT_TICKS);
        this.entityData.set(DATA_PLOW_EFFECT_TICKS, Math.max(current, 6));
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
                newBlockState = ObjectRegistry.FERTILIZED_FARM_BLOCK.get()
                        .defaultBlockState()
                        .setValue(FertilizedFarmlandBlock.MOISTURE, 0);
            }

            if (newBlockState != null) {
                BlockPos abovePos = blockPos.above();
                BlockState aboveState = this.level().getBlockState(abovePos);

                if (!aboveState.isAir() && aboveState.is(BlockTags.REPLACEABLE) && !(aboveState.getBlock() instanceof CropBlock)) {
                    this.level().destroyBlock(abovePos, true);
                    this.triggerPlowEffect(abovePos);
                }

                this.level().setBlock(blockPos, newBlockState, 3);
                this.triggerPlowEffect(blockPos);
            }

            BlockPos cropPos = blockPos.above();
            BlockState cropState = this.level().getBlockState(cropPos);

            if (cropState.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(cropState)) {
                BlockState resetState = cropBlock.getStateForAge(0);
                this.level().setBlock(cropPos, resetState, 3);
                this.level().updateNeighborsAt(cropPos, resetState.getBlock());
                this.triggerPlowEffect(cropPos);

                if (this.level() instanceof ServerLevel serverLevel) {
                    for (ItemStack drop : Block.getDrops(cropState, serverLevel, cropPos, null)) {
                        if (!drop.isEmpty()) {
                            double dropX = cropPos.getX() + 0.5 + (this.level().random.nextDouble() - 0.5) * 0.5;
                            double dropY = cropPos.getY() + 1.0;
                            double dropZ = cropPos.getZ() + 0.5 + (this.level().random.nextDouble() - 0.5) * 0.5;
                            this.level().addFreshEntity(
                                    new ItemEntity(this.level(), dropX, dropY, dropZ, drop)
                            );
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
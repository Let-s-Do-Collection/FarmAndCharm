package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.FertilizedFarmlandBlock;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SeederCartEntity extends AbstractCartEntity implements MenuProvider {
    private static final int SEED_FLIGHT_TICKS = 16;

    private static final EntityDataAccessor<Integer> DATA_SOW_EFFECT_TICKS = SynchedEntityData.defineId(SeederCartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<BlockPos> DATA_SOW_EFFECT_POS = SynchedEntityData.defineId(SeederCartEntity.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<ItemStack> DATA_SOW_ITEM = SynchedEntityData.defineId(SeederCartEntity.class, EntityDataSerializers.ITEM_STACK);

    private final SimpleContainer inventory;
    private final List<PendingSow> pendingSows = new ArrayList<>();
    private final Set<BlockPos> reservedPositions = new HashSet<>();
    private int clientPrevSowEffectTicks;

    private static final class PendingSow {
        final BlockPos pos;
        final BlockState state;
        int ticks;

        PendingSow(BlockPos pos, BlockState state, int ticks) {
            this.pos = pos;
            this.state = state;
            this.ticks = ticks;
        }
    }

    public SeederCartEntity(EntityType<? extends AbstractCartEntity> entityType, Level level) {
        super(entityType, level);
        this.inventory = new SimpleContainer(5) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return isSeedItem(stack);
            }
        };
    }

    private static boolean isSeedItem(ItemStack stack) {
        return !stack.isEmpty() && stack.is(TagRegistry.SEEDS) && stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CropBlock;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SOW_EFFECT_TICKS, 0);
        builder.define(DATA_SOW_EFFECT_POS, BlockPos.ZERO);
        builder.define(DATA_SOW_ITEM, ItemStack.EMPTY);
    }

    public int getSowEffectTicks() {
        return this.entityData.get(DATA_SOW_EFFECT_TICKS);
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand hand) {
        if (player.isShiftKeyDown() && player.getItemInHand(hand).isEmpty() && this.getPulling() == player) {
            if (this.level().isClientSide) {
                return InteractionResult.SUCCESS;
            }
            player.openMenu(this);
            return InteractionResult.CONSUME;
        }
        return super.interact(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            int effectTicks = this.entityData.get(DATA_SOW_EFFECT_TICKS);
            if (effectTicks > 0 && this.clientPrevSowEffectTicks <= 0) {
                this.spawnSeedFlightParticle(effectTicks);
            } else if (effectTicks <= 0 && this.clientPrevSowEffectTicks > 0) {
                this.spawnSeedLandingEffect(this.entityData.get(DATA_SOW_EFFECT_POS));
            }
            this.clientPrevSowEffectTicks = effectTicks;
            return;
        }

        int effectTicks = this.entityData.get(DATA_SOW_EFFECT_TICKS);
        if (effectTicks > 0) {
            this.entityData.set(DATA_SOW_EFFECT_TICKS, effectTicks - 1);
        }

        this.tickPendingSows();

        if (this.tickCount % 4 == 0) {
            this.suckInNearbySeeds();
        }
    }

    private void tickPendingSows() {
        if (this.pendingSows.isEmpty()) {
            return;
        }
        Iterator<PendingSow> iterator = this.pendingSows.iterator();
        while (iterator.hasNext()) {
            PendingSow sow = iterator.next();
            sow.ticks--;
            if (sow.ticks <= 0) {
                this.level().setBlock(sow.pos, sow.state, 3);
                this.reservedPositions.remove(sow.pos);
                iterator.remove();
            }
        }
    }

    private void suckInNearbySeeds() {
        List<ItemEntity> nearbyItems = this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.6D, 0.4D, 0.6D));
        for (ItemEntity itemEntity : nearbyItems) {
            if (!itemEntity.isAlive() || !isSeedItem(itemEntity.getItem())) {
                continue;
            }
            HopperBlockEntity.addItem(this.inventory, itemEntity);
        }
    }

    private void spawnSeedFlightParticle(int travelTicks) {
        ItemStack itemStack = this.entityData.get(DATA_SOW_ITEM);
        if (itemStack.isEmpty()) {
            return;
        }
        BlockPos targetPos = this.entityData.get(DATA_SOW_EFFECT_POS);

        double startX = this.getX();
        double startY = this.getY() + 0.7D;
        double startZ = this.getZ();

        double endX = targetPos.getX() + 0.5D;
        double endY = targetPos.getY() + 0.2D;
        double endZ = targetPos.getZ() + 0.5D;

        double ticks = Math.max(travelTicks, 1);
        double velocityX = (endX - startX) / ticks;
        double velocityZ = (endZ - startZ) / ticks;
        // Vanilla item particles fall with gravity (~0.04/tick); aim high enough that
        // the arc peaks roughly halfway through the flight before dropping onto the target.
        double gravity = 0.04D;
        double velocityY = (endY - startY) / ticks + gravity * ticks * 0.5D;

        ItemParticleOption particleOption = new ItemParticleOption(ParticleTypes.ITEM, itemStack);
        this.level().addParticle(particleOption, startX, startY, startZ, velocityX, velocityY, velocityZ);
        this.level().addParticle(particleOption, startX, startY, startZ,
                velocityX + (this.random.nextDouble() - 0.5D) * 0.03D,
                velocityY + (this.random.nextDouble() - 0.5D) * 0.03D,
                velocityZ + (this.random.nextDouble() - 0.5D) * 0.03D);

        float yawRad = this.getYRot() * ((float) Math.PI / 180.0F);
        float backX = Mth.sin(yawRad);
        float backZ = -Mth.cos(yawRad);
        for (int i = 0; i < 3; i++) {
            double dustX = this.getX() + backX * 1.3D + (this.random.nextDouble() - 0.5D) * 0.4D;
            double dustY = this.getY() + 0.35D;
            double dustZ = this.getZ() + backZ * 1.3D + (this.random.nextDouble() - 0.5D) * 0.4D;
            this.level().addParticle(ParticleTypes.CRIT, dustX, dustY, dustZ, backX * -0.02D, -0.02D, backZ * -0.02D);
        }
    }

    private void spawnSeedLandingEffect(BlockPos cropPos) {
        BlockPos farmlandPos = cropPos.below();
        BlockState farmlandState = this.level().getBlockState(farmlandPos);
        this.level().addDestroyBlockEffect(farmlandPos, farmlandState);

        for (int i = 0; i < 5; i++) {
            double offsetX = cropPos.getX() + 0.5D + (this.random.nextDouble() - 0.5D) * 0.8D;
            double offsetY = cropPos.getY() + 0.1D + this.random.nextDouble() * 0.5D;
            double offsetZ = cropPos.getZ() + 0.5D + (this.random.nextDouble() - 0.5D) * 0.8D;
            this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, offsetX, offsetY, offsetZ, 0.0D, 0.05D, 0.0D);
        }
    }

    @Override
    public void pulledPostTick() {
        double prevX = this.getX();
        double prevZ = this.getZ();

        super.pulledPostTick();

        if (this.level().isClientSide) {
            return;
        }
        if (!(this.getPulling() instanceof Player)) {
            return;
        }
        if (Math.abs(this.getX() - prevX) < 1.0E-4 && Math.abs(this.getZ() - prevZ) < 1.0E-4) {
            return;
        }

        this.handleSowServer();
    }

    private void triggerSowEffect(BlockPos blockPos, ItemStack itemStack) {
        if (this.level().isClientSide) {
            return;
        }
        this.entityData.set(DATA_SOW_EFFECT_POS, blockPos);
        this.entityData.set(DATA_SOW_ITEM, itemStack.copyWithCount(1));
        this.entityData.set(DATA_SOW_EFFECT_TICKS, SEED_FLIGHT_TICKS);
    }

    private void handleSowServer() {
        BlockPos groundPos = this.getOnPos();
        Direction sidePos = Direction.fromYRot(this.getYRot()).getClockWise();
        BlockPos[] positions = new BlockPos[]{groundPos, groundPos.relative(sidePos)};

        for (BlockPos blockPos : positions) {
            BlockState blockState = this.level().getBlockState(blockPos);
            boolean isFarmland = blockState.is(Blocks.FARMLAND) || blockState.is(ObjectRegistry.FERTILIZED_FARM_BLOCK.get());
            if (!isFarmland) {
                continue;
            }

            BlockPos abovePos = blockPos.above();
            if (this.reservedPositions.contains(abovePos)) {
                continue;
            }

            BlockState aboveState = this.level().getBlockState(abovePos);
            if (!aboveState.isAir()) {
                continue;
            }

            int seedSlot = this.findSeedSlot(blockState);
            if (seedSlot < 0) {
                continue;
            }

            ItemStack seedStack = this.inventory.getItem(seedSlot);
            BlockItem blockItem = (BlockItem) seedStack.getItem();
            BlockState cropState = blockItem.getBlock().defaultBlockState();
            ItemStack particleItem = seedStack.copyWithCount(1);
            seedStack.shrink(1);

            this.reservedPositions.add(abovePos);
            this.pendingSows.add(new PendingSow(abovePos, cropState, SEED_FLIGHT_TICKS));
            this.triggerSowEffect(abovePos, particleItem);
        }
    }

    private int findSeedSlot(BlockState soilState) {
        for (int slot = 0; slot < this.inventory.getContainerSize(); slot++) {
            ItemStack stack = this.inventory.getItem(slot);
            if (isSeedItem(stack) && FertilizedFarmlandBlock.canPlantOn(soilState, stack)) {
                return slot;
            }
        }
        return -1;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        NonNullList<ItemStack> items = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, items, this.registryAccess());
        for (int slotIndex = 0; slotIndex < items.size(); slotIndex++) {
            this.inventory.setItem(slotIndex, items.get(slotIndex));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        NonNullList<ItemStack> items = NonNullList.withSize(this.inventory.getContainerSize(), ItemStack.EMPTY);
        for (int slotIndex = 0; slotIndex < items.size(); slotIndex++) {
            items.set(slotIndex, this.inventory.getItem(slotIndex));
        }
        ContainerHelper.saveAllItems(compoundTag, items, this.registryAccess());
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new HopperMenu(syncId, playerInventory, this.inventory);
    }

    @Override
    public void remove(RemovalReason removalReason) {
        if (!this.level().isClientSide && removalReason == RemovalReason.KILLED) {
            Containers.dropContents(this.level(), this.blockPosition(), this.inventory);
        }
        super.remove(removalReason);
    }

    @Override
    protected ItemStack getCartItemStack() {
        return new ItemStack(ObjectRegistry.SEEDER.get());
    }

    @Override
    public @NotNull Component getDisplayName() {
        return ObjectRegistry.SEEDER.get().getDefaultInstance().getHoverName();
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ObjectRegistry.SEEDER.get());
    }
}

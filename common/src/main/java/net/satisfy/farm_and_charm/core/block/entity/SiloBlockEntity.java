package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.satisfy.farm_and_charm.core.block.SiloBlock;
import net.satisfy.farm_and_charm.core.recipe.SiloRecipe;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.util.ConnectivityHandler;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import net.satisfy.farm_and_charm.core.util.IMultiBlockEntityContainer;
import net.satisfy.farm_and_charm.core.world.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SiloBlockEntity extends BlockEntity implements IMultiBlockEntityContainer.Inventory, ImplementedInventory, MenuProvider, BlockEntityTicker<SiloBlockEntity> {
    private static final int MAX_WIDTH = 3;
    private static final int MAX_HEIGHT = 9;
    public static final int MAX_CAPACITY = MAX_WIDTH * MAX_WIDTH * MAX_HEIGHT;
    private static final int DRY_TIME = 10 * 20;
    protected BlockPos controller;
    protected boolean updateConnectivity;
    protected int width;
    protected int height;
    private NonNullList<ItemStack> items = NonNullList.withSize(MAX_CAPACITY * 2, ItemStack.EMPTY);
    private int[] times = new int[MAX_CAPACITY];

    public SiloBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.SILO_BLOCK_ENTITY.get(), pos, state);
        updateConnectivity = false;
        height = 1;
        width = 1;
    }

    public static void dropStack(Level level, double x, double y, double z, ItemStack itemStack, Direction direction) {
        double g = EntityType.ITEM.getWidth();
        double h = 1.0 - g;
        double i = g / 2.0;
        double j = Math.floor(x) + level.random.nextDouble() * h + i;
        double k = Math.floor(y) + level.random.nextDouble() * h;
        double l = Math.floor(z) + level.random.nextDouble() * h + i;

        while (!itemStack.isEmpty()) {
            ItemEntity itemEntity = new ItemEntity(level, j, k, l, itemStack.split(level.random.nextInt(21) + 10));
            itemEntity.setDeltaMovement(level.random.triangle(direction.getStepX() * 0.4, 0.11485000171139836), level.random.triangle(-0.2, 0.11485000171139836), level.random.triangle(direction.getStepZ() * 0.4, 0.11485000171139836));
            level.addFreshEntity(itemEntity);
        }
    }

    @Override
    public void preventConnectivityUpdate() {
        updateConnectivity = false;
    }

    @Override
    public boolean isController() {
        return controller == null || this.worldPosition.equals(controller);
    }

    @Override
    public BlockPos getController() {
        return isController() ? worldPosition : controller;
    }

    @Override
    public void setController(BlockPos controller) {
        assert this.level != null;
        if (this.level.isClientSide || controller.equals(this.controller))
            return;
        this.controller = controller;
        Containers.dropContents(this.level, this.worldPosition, this);
    }

    @Override
    public <T extends BlockEntity & IMultiBlockEntityContainer> T getControllerBE() {
        if (this.isController()) {
            @SuppressWarnings("unchecked")
            T result = (T) this;
            return result;
        }
        assert level != null;
        BlockEntity be = level.getBlockEntity(controller);
        if (be instanceof IMultiBlockEntityContainer) {
            @SuppressWarnings("unchecked")
            T result = (T) be;
            return result;
        }
        return null;
    }

    @Override
    public void removeController(boolean keepContents) {
        if (level == null || level.isClientSide)
            return;
        updateConnectivity = true;
        controller = null;
        width = 1;
        height = 1;

        BlockState state = getBlockState();
        if (SiloBlock.isSilo(state)) {
            state = state.setValue(SiloBlock.BOTTOM, true);
            state = state.setValue(SiloBlock.TOP, true);
            state = state.setValue(SiloBlock.SHAPE, SiloBlock.Shape.NONE);
            level.setBlock(worldPosition, state, 23);
        }
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, SiloBlockEntity blockEntity) {
        if (this.level == null)
            this.level = level;
        if (updateConnectivity)
            updateConnectivity();
        dry();
        tryDropFinish(blockState);
    }

    public void updateConnectivity() {
        updateConnectivity = false;
        if (level == null || level.isClientSide)
            return;
        if (!isController())
            return;
        ConnectivityHandler.formMulti(this);
    }

    public boolean tryAddItem(ItemStack itemStack) {
        boolean added = false;
        for (int slot = 0; !itemStack.isEmpty() && slot < this.getCapacity(); slot++)
            if (this.getItem(slot).isEmpty()) {
                this.setItem(slot, itemStack.split(1));
                added = true;
            }
        return added;
    }

    public ItemStack tryRemoveItem() {
        for (int slot = MAX_CAPACITY + this.getCapacity(); slot > MAX_CAPACITY; --slot) {
            ItemStack stack = this.getItem(slot);
            if (!stack.isEmpty())
                return this.removeItem(slot, stack.getCount());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void notifyMultiUpdated() {
        BlockState state = this.getBlockState();
        if (SiloBlock.isSilo(state) && level != null) {
            state = state.setValue(SiloBlock.BOTTOM, getController().getY() == getBlockPos().getY());
            state = state.setValue(SiloBlock.TOP, getController().getY() + height - 1 == getBlockPos().getY());
            state = state.setValue(SiloBlock.OPEN, level.getBlockState(getController()).getValue(BlockStateProperties.OPEN));
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, level.getBlockState(getController()).getValue(BlockStateProperties.HORIZONTAL_FACING));
            level.setBlock(worldPosition, state, 6);
        }
        if (isController())
            updateShape();
    }

    public void updateShape() {
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    assert this.level != null;
                    BlockState blockState = this.level.getBlockState(pos);
                    if (!SiloBlock.isSilo(blockState))
                        continue;
                    SiloBlock.Shape shape = SiloBlock.Shape.NONE;
                    if (width == 2)
                        shape = xOffset == 0 ? zOffset == 0 ? SiloBlock.Shape.NORTH_WEST : SiloBlock.Shape.SOUTH_WEST
                                : zOffset == 0 ? SiloBlock.Shape.NORTH_EAST : SiloBlock.Shape.SOUTH_EAST;
                    if (width == 3)
                        shape = switch (xOffset) {
                            case 0 ->
                                    zOffset == 0 ? SiloBlock.Shape.NORTH_WEST : zOffset == 2 ? SiloBlock.Shape.SOUTH_WEST : SiloBlock.Shape.WEST;
                            case 1 ->
                                    zOffset == 0 ? SiloBlock.Shape.NORTH : zOffset == 2 ? SiloBlock.Shape.SOUTH : SiloBlock.Shape.NONE;
                            case 2 ->
                                    zOffset == 0 ? SiloBlock.Shape.NORTH_EAST : zOffset == 2 ? SiloBlock.Shape.SOUTH_EAST : SiloBlock.Shape.EAST;
                            default -> SiloBlock.Shape.NONE;
                        };
                    level.setBlockAndUpdate(pos, blockState.setValue(SiloBlock.SHAPE, shape));
                }
            }
        }
    }

    private void dry() {
        for (int fresh = 0; fresh < this.getCapacity(); fresh++) {
            ItemStack freshStack = this.getItem(fresh);
            assert level != null;
            Optional<SiloRecipe> recipe = SiloBlock.getDryItemRecipe(level, freshStack);
            if (recipe.isPresent() && !freshStack.isEmpty()) {
                int dryTime = this.times[fresh];
                dryTime++;
                if (dryTime >= DRY_TIME) {
                    for (int finish = MAX_CAPACITY; finish < MAX_CAPACITY + this.getCapacity(); finish++) {
                        if (this.getItem(finish).isEmpty()) {
                            ItemStack finishStack = this.removeItem(fresh, freshStack.getCount());
                            ItemStack outputStack = recipe.get().getResultItem(level.registryAccess()).copyWithCount(finishStack.getCount());
                            this.setItem(finish, SiloBlock.isDryItem(level, finishStack) ? outputStack : finishStack);
                            dryTime = 0;

                            if (level != null && !level.isClientSide) {
                                level.playSound(null, worldPosition, SoundEvents.COMPOSTER_FILL_SUCCESS,
                                        net.minecraft.sounds.SoundSource.BLOCKS, 0.7f, 1.0f);
                            }

                            break;
                        }
                    }
                }
                this.times[fresh] = dryTime;
            }
        }
    }


    private void tryDropFinish(BlockState blockState) {
        if (this.level == null || !blockState.getValue(SiloBlock.OPEN))
            return;
        for (int finish = MAX_CAPACITY; finish < MAX_CAPACITY + this.getCapacity(); finish++) {
            ItemStack finishStack = this.getItem(finish);
            if (!finishStack.isEmpty()) {
                Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
                double x = switch (direction) {
                    case EAST -> width;
                    case WEST -> 0;
                    default -> width / 2.0;
                };
                double z = switch (direction) {
                    case NORTH -> 0;
                    case SOUTH -> width;
                    default -> width / 2.0;
                };
                dropStack(this.level, this.worldPosition.getX() + x, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + z, finishStack, direction);
                return;
            }
        }
    }

    public void open(boolean open) {
        if (!this.isController())
            return;
        for (int yOffset = 0; yOffset < height; yOffset++) {
            for (int xOffset = 0; xOffset < width; xOffset++) {
                for (int zOffset = 0; zOffset < width; zOffset++) {
                    BlockPos pos = this.worldPosition.offset(xOffset, yOffset, zOffset);
                    assert this.level != null;
                    BlockState blockState = this.level.getBlockState(pos);
                    if (!SiloBlock.isSilo(blockState))
                        continue;
                    level.setBlockAndUpdate(pos, blockState.setValue(SiloBlock.OPEN, open));
                }
            }
        }
    }

    @Override
    public Direction.Axis getMainConnectionAxis() {
        return Direction.Axis.Y;
    }

    @Override
    public int getMaxLength(Direction.Axis longAxis, int width) {
        return longAxis.isHorizontal() ? this.getMaxWidth() : MAX_HEIGHT;
    }

    @Override
    public int getMaxWidth() {
        return MAX_WIDTH;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    public int getCapacity() {
        return !isController() ? 0 : this.width * this.width * this.height;
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public boolean hasInventory() {
        return isController();
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        if (this.controller != null)
            GeneralUtil.putBlockPos(compoundTag, this.controller);
        compoundTag.putBoolean("Update", this.updateConnectivity);
        compoundTag.putInt("Width", this.width);
        compoundTag.putInt("Height", this.height);
        ContainerHelper.saveAllItems(compoundTag, this.items, provider);
        compoundTag.putIntArray("Times", this.times);
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.controller = GeneralUtil.readBlockPos(compoundTag);
        this.updateConnectivity = !compoundTag.contains("Update") || compoundTag.getBoolean("Update");
        this.width = compoundTag.contains("Width") ? compoundTag.getInt("Width") : 1;
        this.height = compoundTag.contains("Height") ? compoundTag.getInt("Height") : 1;
        this.items = NonNullList.withSize(MAX_CAPACITY * 2, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, this.items, provider);
        this.times = compoundTag.contains("Times") ? compoundTag.getIntArray("Times") : new int[MAX_CAPACITY];
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.empty();
    }


    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, net.minecraft.world.entity.player.Inventory inventory, Player player) {
        return null;
    }
}
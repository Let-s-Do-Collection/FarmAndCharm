package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.SiloBlockEntity;
import net.satisfy.farm_and_charm.core.recipe.SiloRecipe;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.ConnectivityHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings({"deprecation", "unused"})
public class SiloBlock extends FacingBlock implements EntityBlock {
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);
    static final VoxelShape CAMPFIRE_SMOKE_CLIP = Block.box(0, 4, 0, 16, 16, 16);

    public SiloBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(TOP, true)
                .setValue(BOTTOM, true)
                .setValue(OPEN, false)
                .setValue(SHAPE, Shape.NONE)
                .setValue(FACING, Direction.NORTH));
    }

    public static boolean isSilo(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item instanceof BlockItem bi &&
                (bi.getBlock() == ObjectRegistry.SILO_WOOD.get() ||
                        bi.getBlock() == ObjectRegistry.SILO_COPPER.get());
    }

    public static boolean isSilo(BlockState state) {
        return state.getBlock() instanceof SiloBlock;
    }

    public static boolean isDryItem(Level level, ItemStack itemStack) {
        return getDryItemRecipe(level, itemStack).isPresent();
    }

    public static Optional<RecipeHolder<SiloRecipe>> getDryItemRecipe(Level level, ItemStack itemStack) {
        return level.getRecipeManager().getAllRecipesFor(RecipeTypeRegistry.SILO_RECIPE_TYPE.get()).stream().filter(siloRecipe -> siloRecipe.value().getInput().test(itemStack)).findFirst();
    }

    public Direction getFacing(BlockState state) {
        return state.getValue(FACING);
    }

    public BlockState setFacing(BlockState state, Direction facing) {
        return state.setValue(FACING, facing);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        InteractionHand interactionHand = blockHitResult.getDirection() == Direction.UP ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (level.isClientSide)
            return itemStack.isEmpty() || isDryItem(level, itemStack) ? InteractionResult.SUCCESS : isSilo(itemStack) || player.isDiscrete() ? InteractionResult.PASS : InteractionResult.CONSUME;
        BlockEntity be = level.getBlockEntity(blockPos);
        if (be instanceof SiloBlockEntity siloBE) {
            SiloBlockEntity siloController = siloBE.getControllerBE();
            if (siloController == null)
                return InteractionResult.PASS;
            if (itemStack.isEmpty()) {
                if (player.isDiscrete()) {
                    ItemStack returnStack = siloBE.tryRemoveItem();
                    if (!returnStack.isEmpty()) {
                        player.addItem(itemStack);
                        level.playSound(null, blockPos, SoundEvents.COMPOSTER_EMPTY,
                                net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                } else {
                    boolean isOpen = blockState.getValue(OPEN);
                    siloController.open(!isOpen);
                    level.playSound(null, blockPos, isOpen ? SoundEvents.IRON_TRAPDOOR_CLOSE : SoundEvents.IRON_TRAPDOOR_OPEN,
                            net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                return InteractionResult.SUCCESS;
            } else if (isDryItem(level, itemStack) && siloController.tryAddItem(itemStack)) {
                level.playSound(null, blockPos, SoundEvents.COMPOSTER_FILL_SUCCESS,
                        net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }
        return isSilo(itemStack) || player.isDiscrete() ? InteractionResult.PASS : InteractionResult.CONSUME;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.getBlock() == state.getBlock())
            return;
        if (notify)
            return;

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof SiloBlockEntity siloBlockEntity)
            siloBlockEntity.updateConnectivity();
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState newState, boolean moved) {
        if (blockState.hasBlockEntity() && (blockState.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = level.getBlockEntity(blockPos);
            if (!(be instanceof SiloBlockEntity siloBE))
                return;
            Containers.dropContents(level, blockPos, siloBE);
            level.removeBlockEntity(blockPos);
            ConnectivityHandler.splitMulti(siloBE);
        }
    }

    @Override
    public boolean skipRendering(BlockState blockState, BlockState blockState2, Direction direction) {
        return blockState2.is(this) || super.skipRendering(blockState, blockState2, direction);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState blockState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
        return level.getBlockEntity(blockPos, EntityTypeRegistry.SILO_BLOCK_ENTITY.get()).map(AbstractContainerMenu::getRedstoneSignalFromContainer).orElse(0);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context == CollisionContext.empty())
            return CAMPFIRE_SMOKE_CLIP;
        return state.getShape(world, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TOP, BOTTOM, OPEN, SHAPE, FACING);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof BlockEntityTicker<?>) {
                ((BlockEntityTicker<T>) blockEntity).tick(world, pos, state1, blockEntity);
            }
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SiloBlockEntity(pos, state);
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE)
            return state;
        return switch (state.getValue(SHAPE)) {
            case NORTH -> state.setValue(SHAPE, Shape.SOUTH);
            case NORTH_EAST -> state.setValue(SHAPE, Shape.SOUTH_WEST);
            case EAST -> state.setValue(SHAPE, Shape.WEST);
            case SOUTH_EAST -> state.setValue(SHAPE, Shape.NORTH_WEST);
            case SOUTH -> state.setValue(SHAPE, Shape.NORTH);
            case SOUTH_WEST -> state.setValue(SHAPE, Shape.NORTH_EAST);
            case WEST -> state.setValue(SHAPE, Shape.EAST);
            case NORTH_WEST -> state.setValue(SHAPE, Shape.SOUTH_EAST);
            default -> state;
        };
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        for (int i = 0; i < rotation.ordinal(); i++)
            state = rotateOnce(state);
        return state;
    }

    private BlockState rotateOnce(BlockState state) {
        return switch (state.getValue(SHAPE)) {
            case NORTH -> state.setValue(SHAPE, Shape.EAST);
            case NORTH_EAST -> state.setValue(SHAPE, Shape.SOUTH_EAST);
            case EAST -> state.setValue(SHAPE, Shape.SOUTH);
            case SOUTH_EAST -> state.setValue(SHAPE, Shape.SOUTH_WEST);
            case SOUTH -> state.setValue(SHAPE, Shape.WEST);
            case SOUTH_WEST -> state.setValue(SHAPE, Shape.NORTH_WEST);
            case WEST -> state.setValue(SHAPE, Shape.NORTH);
            case NORTH_WEST -> state.setValue(SHAPE, Shape.NORTH_EAST);
            default -> state;
        };
    }

    public enum Shape implements StringRepresentable {
        NONE, NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST;

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase();
        }
    }
}
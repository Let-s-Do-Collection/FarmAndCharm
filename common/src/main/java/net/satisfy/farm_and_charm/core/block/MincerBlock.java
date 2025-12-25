package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.MincerBlockEntity;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MincerBlock extends BaseEntityBlock {
    public static final MapCodec<MincerBlock> CODEC = simpleCodec(MincerBlock::new);
    public static final int CRANKS_NEEDED = 20;
    public static final IntegerProperty CRANK = IntegerProperty.create("crank", 0, 32);
    public static final IntegerProperty CRANKED = IntegerProperty.create("cranked", 0, 100);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0, 0.1875, 0.9375, 0.0625, 0.8125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.0625, 0.3125, 0.5625, 0.375, 0.6875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0.375, 0.25, 0.625, 0.8125, 0.75), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.625, 0.4375, 0.3125, 0.9375, 0.75, 0.6875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.8125, 0.3125, 0.5625, 1, 0.6875), BooleanOp.OR);
        return shape;
    };
    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
        }
    });

    public MincerBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(CRANK, 0).setValue(CRANKED, 0).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, CRANK, CRANKED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> dropsOriginal = super.getDrops(state, builder);
        if (!dropsOriginal.isEmpty())
            return dropsOriginal;
        return Collections.singletonList(new ItemStack(this, 1));
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionHand hand = player.getUsedItemHand();
        BlockEntity entity = level.getBlockEntity(pos);
        ItemStack playerStack = player.getItemInHand(hand);

        if (entity instanceof MincerBlockEntity mincer) {

            if (player.isShiftKeyDown()) {
                Containers.dropContents(level, pos, mincer);
                return InteractionResult.SUCCESS;
            }

            ItemStack inputStack = mincer.getItem(mincer.INPUT_SLOT);

            int crank = state.getValue(CRANK);
            int cranked = state.getValue(CRANKED);

            if (!playerStack.isEmpty() && crank == 0) {

                if (!level.isClientSide && !mincer.hasValidRecipe(level, playerStack)) {
                    return InteractionResult.PASS;
                }

                if (player.isCreative()) {
                    ItemStack playerStackCopy = playerStack.copy();
                    playerStackCopy.setCount(playerStackCopy.getMaxStackSize());
                    mincer.setItem(mincer.INPUT_SLOT, playerStackCopy);
                    return InteractionResult.SUCCESS;
                }

                if (mincer.canPlaceItem(mincer.INPUT_SLOT, playerStack)) {
                    if (inputStack.is(playerStack.getItem())) {

                        int countInPlayerHand = playerStack.getCount();
                        int insertableCount = inputStack.getMaxStackSize() - inputStack.getCount();
                        int countToTakeFromPlayer = 0;

                        while (countToTakeFromPlayer < insertableCount && countToTakeFromPlayer < countInPlayerHand) {
                            countToTakeFromPlayer += 1;
                        }

                        inputStack.setCount(inputStack.getCount() + countToTakeFromPlayer);
                        mincer.setItem(mincer.INPUT_SLOT, inputStack);
                        playerStack.shrink(countToTakeFromPlayer);
                    } else if (inputStack.isEmpty()) {
                        inputStack = playerStack.copy();
                        mincer.setItem(mincer.INPUT_SLOT, inputStack);
                        playerStack.shrink(playerStack.getCount());
                    }

                    return InteractionResult.SUCCESS;
                }

                if (level.isClientSide() && playerStack.getItem() instanceof BlockItem) {
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            } else if (playerStack.isEmpty()) {

                if (cranked >= CRANKS_NEEDED && crank == 0) {
                    level.setBlock(pos, state.setValue(CRANKED, 0), Block.UPDATE_ALL);
                    return InteractionResult.SUCCESS;
                }
                if (level instanceof ServerLevel serverWorld) {
                    for (ItemStack stack : mincer.getItems()) {
                        if (!stack.isEmpty() && mincer.getItem(mincer.OUTPUT_SLOT) != stack) {
                            ItemParticleOption particleOption = new ItemParticleOption(ParticleTypes.ITEM, stack);
                            serverWorld.sendParticles(particleOption, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.4, 3, 0.2, 0.1, 0, 0.1);
                        }
                    }
                }

                if (crank <= 6) {
                    level.setBlock(pos, state.setValue(CRANK, 10), Block.UPDATE_ALL);
                    mincer.addCrankImpulse(0.35F);
                    level.playSound(null, pos, SoundEventRegistry.MINCER_CRANKING.get(), SoundSource.BLOCKS, 1.0F, 2.5F);
                    return InteractionResult.SUCCESS;
                }
            }

            if (cranked >= CRANKS_NEEDED && crank == 0) {
                level.setBlock(pos, state.setValue(CRANKED, 0), Block.UPDATE_ALL);
                return InteractionResult.SUCCESS;
            }
            if (level instanceof ServerLevel serverWorld) {
                for (ItemStack stack : mincer.getItems()) {
                    if (!stack.isEmpty() && mincer.getItem(mincer.OUTPUT_SLOT) != stack) {
                        ItemParticleOption particleOption = new ItemParticleOption(ParticleTypes.ITEM, stack);
                        serverWorld.sendParticles(particleOption, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.4, 3, 0.2, 0.1, 0, 0.1);
                    }
                }
            }

            if (crank <= 6) {
                level.setBlock(pos, state.setValue(CRANK, 10), Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEventRegistry.MINCER_CRANKING.get(), SoundSource.BLOCKS, 0.05f, 2.5F);
                return InteractionResult.SUCCESS;
            }

        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        VoxelShape shape = world.getBlockState(pos.below()).getShape(world, pos.below());
        Direction direction = Direction.UP;
        return Block.isFaceFull(shape, direction);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(world, pos)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(world, pos)) {
            world.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(blockstate, world, pos, oldState, moving);
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        return tileEntity instanceof MenuProvider ? (MenuProvider) tileEntity : null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MincerBlockEntity(pos, state);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MincerBlockEntity be) {
                for (int slot = 0; slot < be.getContainerSize(); slot++) {
                    ItemStack stack = be.removeItem(slot, be.getItem(slot).getCount());
                    if (!stack.isEmpty()) {
                        Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                    }
                }
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, EntityTypeRegistry.MINCER_BLOCK_ENTITY.get(), (world1, pos, state1, be) -> be.tick(world1, pos, state1, be));
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
    }
}
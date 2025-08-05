package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.CraftingBowlBlockEntity;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class CraftingBowlBlock extends BaseEntityBlock {
    public static final MapCodec<CraftingBowlBlock> CODEC = simpleCodec(CraftingBowlBlock::new);
    public static final int STIRS_NEEDED = 50;
    public static final IntegerProperty STIRRING = IntegerProperty.create("stirring", 0, 32);
    public static final IntegerProperty STIRRED = IntegerProperty.create("stirred", 0, 100);

    public CraftingBowlBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(STIRRING, 0));
        this.registerDefaultState(this.stateDefinition.any().setValue(STIRRED, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
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
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(world, pos);
        return (Shapes.or(box(3, 0, 3, 13, 8, 13), box(4, 3, 4, 12, 8, 12))).move(offset.x, offset.y, offset.z);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STIRRING);
        builder.add(STIRRED);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> dropsOriginal = super.getDrops(state, builder);
        if (!dropsOriginal.isEmpty())
            return dropsOriginal;
        return Collections.singletonList(new ItemStack(this, 1));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        InteractionHand hand = blockHitResult.getDirection() == Direction.UP ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        ItemStack itemStack = player.getItemInHand(hand);
        if (blockEntity instanceof CraftingBowlBlockEntity bowlEntity) {
            int stirring = blockState.getValue(STIRRING);
            int stirred = blockState.getValue(STIRRED);

            if (player.isShiftKeyDown() && itemStack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
                for (int i = 0; i < bowlEntity.getContainerSize(); i++) {
                    ItemStack stack = bowlEntity.getItem(i);
                    if (!stack.isEmpty()) {
                        popResource(world, pos, stack);
                        bowlEntity.setItem(i, ItemStack.EMPTY);
                    }
                }
                bowlEntity.setChanged();
                return InteractionResult.sidedSuccess(world.isClientSide);
            }

            if (!itemStack.isEmpty() && stirring == 0) {
                if (bowlEntity.canAddItem(itemStack)) {
                    bowlEntity.addItemStack(itemStack.copy());
                    if (!player.isCreative()) {
                        itemStack.setCount(0);
                    }
                    return InteractionResult.SUCCESS;
                }
            } else if (itemStack.isEmpty()) {
                if (stirred >= STIRS_NEEDED && stirring == 0) {
                    player.getInventory().add(bowlEntity.getItem(4));
                    bowlEntity.setItem(4, ItemStack.EMPTY);
                    world.setBlock(pos, blockState.setValue(STIRRED, 0), 3);
                    return InteractionResult.SUCCESS;
                }
                if (world instanceof ServerLevel serverWorld) {
                    RandomSource randomSource = serverWorld.random;
                    for (ItemStack stack : bowlEntity.getItems()) {
                        if (!stack.isEmpty() && bowlEntity.getItem(4) != stack) {
                            ItemParticleOption particleOption = new ItemParticleOption(ParticleTypes.ITEM, stack);
                            serverWorld.sendParticles(particleOption, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 1, randomSource.nextGaussian() * 0.15D, 0.05D, randomSource.nextGaussian() * 0.15D, 0.05D);                        }
                    }
                }
                if (stirring <= 6) {
                    world.setBlock(pos, blockState.setValue(STIRRING, 10), 3);
                    world.playSound(null, pos, SoundEventRegistry.CRAFTING_BOWL_STIRRING.get(), SoundSource.BLOCKS, 0.05f, 1.0F);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
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
        return new CraftingBowlBlockEntity(pos, state);
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
            if (blockEntity instanceof CraftingBowlBlockEntity be) {
                Containers.dropContents(world, pos, be);
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, EntityTypeRegistry.CRAFTING_BOWL_BLOCK_ENTITY.get(), (world1, pos, state1, be) -> be.tick(world1, pos, state1, be));
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

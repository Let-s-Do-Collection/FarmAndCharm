package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardAssemblyRecipe;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardRecipe;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import net.satisfy.farm_and_charm.core.block.entity.CuttingBoardBlockEntity;
import net.satisfy.farm_and_charm.core.util.Strippables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CuttingBoardBlock extends BaseEntityBlock {
    public static final MapCodec<CuttingBoardBlock> CODEC = simpleCodec(CuttingBoardBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final int CRUMBS = 8;
    private static final int TOOLTIP_LINES = 3;
    private static final int TOOLTIP_TEXT_COLOR = 0xFFD966;
    private static final int TOOLTIP_KEY_COLOR = 0xFFD700;
    public static final TagKey<Item> CLEAVERS = TagKey.create(Registries.ITEM, FarmAndCharm.identifier("cleavers"));
    public static final TagKey<Item> KNIVES = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/knife"));

    private static final VoxelShape SHAPE = Shapes.or(Block.box(1, 0, 3, 11, 1, 13), Block.box(11, 0, 6, 15, 1, 10));

    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            SHAPES.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, SHAPE));
        }
    }

    public CuttingBoardBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CuttingBoardBlockEntity(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.isEmpty() || !(level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity board)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (board.hasKnife()) {
            return autoCut(stack, level, pos, player, board);
        }
        if (Strippables.isAxe(stack) && board.isEmpty()) {
            return placeKnife(stack, level, pos, player, board);
        }
        if (stack.is(KNIVES)) {
            if (board.isEmpty()) {
                return placeKnife(stack, level, pos, player, board);
            }
            return chop(stack, level, pos, player, hand, board);
        }
        if (board.isFull() || !canPlace(level, board, stack)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        board.addItem(stack.copyWithCount(1));
        stack.consume(1, player);
        level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.7F, 1.2F);
        return ItemInteractionResult.CONSUME;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity board)) {
            return InteractionResult.PASS;
        }
        if (board.hasKnife()) {
            if (board.isCutting()) {
                if (!level.isClientSide) {
                    board.chopPending(level);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (!level.isClientSide) {
                giveToPlayer(player, board.takeKnife());
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (board.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Optional<RecipeHolder<CuttingBoardAssemblyRecipe>> assembly = player.isShiftKeyDown() ? Optional.empty() : findAssembly(level, board.getItems());
        if (assembly.isPresent()) {
            ItemStack result = assembly.get().value().assemble(new CuttingBoardAssemblyRecipe.Input(board.getItems()), level.registryAccess());
            board.setContents(ItemStack.EMPTY);
            CuttingBoardBlockEntity.output(level, pos, result);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.CONSUME;
        }
        giveToPlayer(player, board.removeLastItem());
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 1.0F);
        return InteractionResult.CONSUME;
    }

    private static void giveToPlayer(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static ItemInteractionResult placeKnife(ItemStack stack, Level level, BlockPos pos, Player player, CuttingBoardBlockEntity board) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        board.setKnife(stack.copyWithCount(1));
        stack.consume(1, player);
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.8F, 1.2F);
        return ItemInteractionResult.CONSUME;
    }

    private static ItemInteractionResult autoCut(ItemStack stack, Level level, BlockPos pos, Player player, CuttingBoardBlockEntity board) {
        if (board.isCutting()) {
            if (!level.isClientSide) {
                board.chopPending(level);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        boolean valid = Strippables.isAxe(board.getKnife()) ? Strippables.strip(stack).isPresent() : level.getRecipeManager().getRecipeFor(RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get(), new SingleRecipeInput(stack), level).isPresent();
        if (!valid) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            board.startCut(stack.copyWithCount(1));
            stack.consume(1, player);
            board.chopPending(level);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private static ItemInteractionResult chop(ItemStack knife, Level level, BlockPos pos, Player player, InteractionHand hand, CuttingBoardBlockEntity board) {
        if (board.getItems().size() != 1) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        SingleRecipeInput input = new SingleRecipeInput(board.getItems().getFirst());
        Optional<RecipeHolder<CuttingBoardRecipe>> recipe = level.getRecipeManager().getRecipeFor(RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get(), input, level);
        if (recipe.isEmpty()) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        CuttingBoardBlockEntity.playChopSound(level, pos, board.getItems().getFirst());
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, board.getItems().getFirst().copyWithCount(1)), pos.getX() + 0.5, pos.getY() + 0.15, pos.getZ() + 0.5, CRUMBS, 0.15, 0.05, 0.15, 0.08);
        }
        if (board.chop() >= CuttingBoardBlockEntity.requiredChops(knife, recipe.get().value())) {
            ItemStack ingredient = board.getItems().getFirst();
            ItemStack result = recipe.get().value().assemble(input, level.registryAccess());
            ingredient.shrink(1);
            board.setContents(ingredient.isEmpty() ? ItemStack.EMPTY : ingredient);
            CuttingBoardBlockEntity.output(level, pos, result);
            for (ItemStack byproduct : recipe.get().value().getByproducts()) {
                CuttingBoardBlockEntity.output(level, pos, byproduct.copy());
            }
            knife.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        }
        return ItemInteractionResult.CONSUME;
    }

    private static boolean canPlace(Level level, CuttingBoardBlockEntity board, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (board.isEmpty() && level.getRecipeManager().getRecipeFor(RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get(), new SingleRecipeInput(stack), level).isPresent()) {
            return true;
        }
        List<ItemStack> candidate = new ArrayList<>(board.getItems());
        candidate.add(stack);
        return level.getRecipeManager().getAllRecipesFor(RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get()).stream()
                .anyMatch(recipe -> recipe.value().accepts(candidate));
    }

    private static Optional<RecipeHolder<CuttingBoardAssemblyRecipe>> findAssembly(Level level, List<ItemStack> items) {
        return level.getRecipeManager().getRecipeFor(RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get(), new CuttingBoardAssemblyRecipe.Input(List.copyOf(items)), level);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        Style text = Style.EMPTY.withColor(TextColor.fromRgb(TOOLTIP_TEXT_COLOR));
        if (!Screen.hasShiftDown()) {
            Component key = Component.literal("[SHIFT]").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(TOOLTIP_KEY_COLOR)));
            tooltip.add(Component.translatable("tooltip.farm_and_charm.hold_shift", key).withStyle(text));
            return;
        }
        for (int i = 0; i < TOOLTIP_LINES; i++) {
            if (i > 0) {
                tooltip.add(Component.empty());
            }
            tooltip.add(Component.translatable("tooltip.farm_and_charm.cutting_board.info_" + i).withStyle(text));
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity cuttingBoard) {
                for (ItemStack stack : cuttingBoard.removeAllItems()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}

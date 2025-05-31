package net.satisfy.farm_and_charm.core.block;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.ScarecrowBlockEntity;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class ScarecrowBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty HAS_DUNGAREES = BooleanProperty.create("has_dungarees");

    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape,
                Shapes.box(0.4375, 0,    0.4375, 0.5625, 0.75,   0.5625),
                BooleanOp.OR
        );
        shape = Shapes.joinUnoptimized(shape,
                Shapes.box(0.25,   0.625, 0.375,  0.75,   1.4375, 0.6875),
                BooleanOp.OR
        );
        shape = Shapes.joinUnoptimized(shape,
                Shapes.box(0.25,   1.4375,0.3125,  0.75,   1.9375, 0.8125),
                BooleanOp.OR
        );
        shape = Shapes.joinUnoptimized(shape,
                Shapes.box(0.6875, 1.125, 0.375,  1.0,    1.5,    0.6875),
                BooleanOp.OR
        );
        shape = Shapes.joinUnoptimized(shape,
                Shapes.box(0.0,    1.125, 0.375,  0.3125, 1.5,    0.6875),
                BooleanOp.OR
        );
        return shape;
    };

    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new HashMap<>(), m -> {
        for (Direction d : Direction.Plane.HORIZONTAL) {
            m.put(d, GeneralUtil.rotateShape(Direction.NORTH, d, voxelShapeSupplier.get()));
        }
    });

    public ScarecrowBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(HAS_DUNGAREES, true)
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection())
                .setValue(HAS_DUNGAREES, true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        b.add(FACING, HAS_DUNGAREES);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter w, BlockPos pos, CollisionContext ctx) {
        return SHAPE.get(state.getValue(FACING));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader w, BlockPos pos) {
        var below = w.getBlockState(pos.below()).getShape(w, pos.below());
        return Block.isFaceFull(below, Direction.UP);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction d, BlockState ns, LevelAccessor w, BlockPos pos, BlockPos np) {
        if (!state.canSurvive(w, pos)) {
            w.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, d, ns, w, pos, np);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState st) {
        return new ScarecrowBlockEntity(pos, st);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level lvl, BlockState st, BlockEntityType<T> type) {
        return type == EntityTypeRegistry.SCARECROW_BLOCK_ENTITY.get()
                ? (level, pos, state, be) -> ScarecrowBlockEntity.tick(level, be)
                : null;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        boolean has = state.getValue(HAS_DUNGAREES);
        ItemStack held = player.getItemInHand(hand);

        if (!has && held.is(ObjectRegistry.DUNGAREES.get())) {
            level.setBlock(pos, state.setValue(HAS_DUNGAREES, true), 3);
            if (!player.isCreative()) held.shrink(1);
            return InteractionResult.SUCCESS;
        }

        if (has) {
            player.addItem(new ItemStack(ObjectRegistry.DUNGAREES.get()));
            level.setBlock(pos, state.setValue(HAS_DUNGAREES, false), 3);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter w, List<Component> tooltip, TooltipFlag f) {
        tooltip.add(Component.translatable("tooltip.farm_and_charm.thankyou_1")
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.farm_and_charm.thankyou_2")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.farm_and_charm.thankyou_4")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.farm_and_charm.thankyou_3")
                .withStyle(ChatFormatting.GOLD));
    }
}

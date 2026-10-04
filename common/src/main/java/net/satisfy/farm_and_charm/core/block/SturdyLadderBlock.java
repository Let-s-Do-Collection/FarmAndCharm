package net.satisfy.farm_and_charm.core.block;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.core.block.entity.SturdyLadderBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SturdyLadderBlock extends LadderBlock implements EntityBlock {
    public static final BooleanProperty SHAKING = BooleanProperty.create("shaking");
    private static final int SHAKE_TICKS = 10;
    private static final double MIN_CLIMB_SPEED = 0.02;

    public SturdyLadderBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SHAKING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHAKING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SturdyLadderBlockEntity(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return state.getValue(SHAKING) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.MODEL;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof LivingEntity living) || !living.onClimbable()) return;
        if (Math.abs(entity.getY() - entity.yo) < MIN_CLIMB_SPEED) return;
        if (!(level.getBlockEntity(pos) instanceof SturdyLadderBlockEntity ladder)) return;
        ladder.setLastClimbed(level.getGameTime());
        if (!state.getValue(SHAKING)) {
            level.setBlock(pos, state.setValue(SHAKING, true), Block.UPDATE_CLIENTS);
            level.scheduleTick(pos, this, SHAKE_TICKS);
        }
        if (level.random.nextInt(40) == 0) {
            level.playSound(null, pos, SoundEvents.WOODEN_DOOR_OPEN, SoundSource.BLOCKS, 0.12F, 0.5F + level.random.nextFloat() * 0.2F);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).isSource());
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos back = pos.relative(facing.getOpposite());
        if (level.getBlockState(back).isFaceSturdy(level, back, facing)) return true;
        BlockPos below = pos.below();
        if (level.getBlockState(below).is(this)) return true;
        if (level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) return true;
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos adj = pos.relative(dir);
            if (level.getBlockState(adj).isFaceSturdy(level, adj, dir.getOpposite())) return true;
        }
        return false;
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        if (state.getValue(SHAKING) && level.getBlockEntity(pos) instanceof SturdyLadderBlockEntity ladder) {
            long idle = level.getGameTime() - ladder.getLastClimbed();
            if (idle < SHAKE_TICKS) {
                level.scheduleTick(pos, this, (int) (SHAKE_TICKS - idle));
            } else {
                level.setBlock(pos, state.setValue(SHAKING, false), Block.UPDATE_CLIENTS);
            }
        }
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        if (!stack.is(this.asItem())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockPos top = pos;
        while (level.getBlockState(top.above()).is(this)) {
            top = top.above();
        }

        BlockPos target = top.above();
        BlockState placed = this.defaultBlockState()
                .setValue(FACING, state.getValue(FACING))
                .setValue(WATERLOGGED, false)
                .setValue(SHAKING, false);

        if (!level.getBlockState(target).isAir()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!this.canSurvive(placed, level, target)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        level.setBlock(target, placed, 3);
        level.playSound(null, target, SoundEvents.LADDER_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

        if (!player.isCreative()) {
            stack.shrink(1);
        }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        int earthy = 0xFFD966;
        int gold = 0xFFD700;

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.farm_and_charm.sturdy_ladder.info_0")
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
        } else {
            tooltip.add(Component.translatable(
                    "tooltip.farm_and_charm.tooltip_information.hold",
                    Component.literal("[SHIFT]").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(gold)))
            ).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
        }
    }
}
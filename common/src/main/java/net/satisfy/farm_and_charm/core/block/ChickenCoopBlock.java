package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.satisfy.farm_and_charm.core.block.entity.ChickenCoopBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class ChickenCoopBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty EGGS = IntegerProperty.create("eggs", 0, 3);

    public ChickenCoopBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(EGGS, 0));
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(EGGS, 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, EGGS);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new ChickenCoopBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> {
            if (be instanceof ChickenCoopBlockEntity coop) {
                ChickenCoopBlockEntity.tick(lvl, pos, coop);
                int eggCount = coop.getEggCount();
                int stage = eggCount >= 7 ? 3 : eggCount >= 4 ? 2 : eggCount >= 1 ? 1 : 0;
                if (lvl.getBlockState(pos).getValue(EGGS) != stage) {
                    lvl.setBlock(pos, lvl.getBlockState(pos).setValue(EGGS, stage), Block.UPDATE_CLIENTS);
                }
            }
        };
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ChickenCoopBlockEntity coop) {
                int eggCount = coop.getEggCount();
                if (eggCount > 0) {
                    player.addItem(Items.EGG.getDefaultInstance().copyWithCount(eggCount));
                    coop.clearEggs();
                    coop.setChanged();
                    level.setBlock(pos, state.setValue(EGGS, 0), Block.UPDATE_CLIENTS);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        VoxelShape base = Block.box(1.0, 0.0, 1.0, 15.0, 12.0, 15.0);
        VoxelShape top = Block.box(0.0, 12.0, 0.0, 16.0, 14.0, 16.0);
        return Shapes.or(base, top);
    }
}

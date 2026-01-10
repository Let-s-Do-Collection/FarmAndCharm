package net.satisfy.farm_and_charm.core.block;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.StorageTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ToolRackBlock extends StorageBlock {
    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.8125, 0.875, 1, 0.9375, 1), BooleanOp.OR);
        return shape;
    };
    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
        }
    });

    public ToolRackBlock(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }


    @Override
    public boolean canInsertStack(ItemStack stack) {
        return stack.getItem() instanceof TieredItem || stack.getItem() == Items.SHEARS || stack.getItem() == Items.FISHING_ROD || stack.getItem() == Items.SPYGLASS || stack.is(TagRegistry.HANGABLE);
    }

    @Override
    public ResourceLocation type() {
        return StorageTypeRegistry.TOOL_RACK;
    }

    @Override
    public Direction[] unAllowedDirections() {
        return new Direction[]{Direction.DOWN};
    }


    @Override
    public int getSection(Float f, Float y) {
        float oneS = 1.0f / 3;
        int nSection = (int) (f / oneS);
        return 2 - nSection;
    }

    @Override
    public int size() {
        return 3;
    }

    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        if (facing == Direction.DOWN) {
            BlockPos blockAbove = pos.above();
            return world.getBlockState(blockAbove).isFaceSturdy(world, blockAbove, Direction.DOWN);
        } else if (facing == Direction.UP) {
            return false;
        } else {
            BlockPos neighborPos = pos.relative(facing.getOpposite());
            BlockState neighborState = world.getBlockState(neighborPos);
            return neighborState.isFaceSturdy(world, neighborPos, facing);
        }
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
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
    }
}
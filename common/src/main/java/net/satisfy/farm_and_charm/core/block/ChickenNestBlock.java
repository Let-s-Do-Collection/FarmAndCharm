package net.satisfy.farm_and_charm.core.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.StorageTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("deprecation")
public class ChickenNestBlock extends StorageBlock {
    private static final VoxelShape CHICKEN_NEST = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

    public ChickenNestBlock(Properties settings) {
        super(settings);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return CHICKEN_NEST;
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean canInsertStack(ItemStack stack) {
        return stack.is(Items.EGG);
    }

    @Override
    public ResourceLocation type() {
        return StorageTypeRegistry.CHICKEN_NEST;
    }

    @Override
    public Direction[] unAllowedDirections() {
        return new Direction[]{Direction.DOWN};
    }

    @Override
    public int getSection(Float x, Float z) {
        int xIndex = x < 0.5f ? 0 : 1;
        int zIndex = z < 0.5f ? 0 : 1;
        return zIndex * 2 + xIndex;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(world, pos)) {
            world.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, BlockGetter world, List<Component> tooltip, TooltipFlag tooltipContext) {
        tooltip.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
    }
}

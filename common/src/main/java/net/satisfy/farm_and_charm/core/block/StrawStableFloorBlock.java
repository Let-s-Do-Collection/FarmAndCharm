package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class StrawStableFloorBlock extends Block {
    public static final MapCodec<StrawStableFloorBlock> CODEC = simpleCodec(StrawStableFloorBlock::new);
    private Supplier<Block> base;
    private Supplier<Item> strawItem;

    public StrawStableFloorBlock(Properties properties) {
        super(properties);
    }

    public void setBase(Supplier<Block> s) {
        base = s;
    }

    public void setStrawItem(Supplier<Item> s) {
        strawItem = s;
    }

    @Override
    public @NotNull MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.player.Player player, InteractionHand hand, BlockHitResult hit) {
        if ((stack.getItem() instanceof ShovelItem || stack.getItem() instanceof HoeItem) && base != null) {
            if (!level.isClientSide) {
                if (strawItem != null) popResource(level, pos, new ItemStack(strawItem.get()));
                level.setBlock(pos, base.get().defaultBlockState(), 3);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}

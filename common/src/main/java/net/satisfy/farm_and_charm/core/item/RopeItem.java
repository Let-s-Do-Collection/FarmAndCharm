package net.satisfy.farm_and_charm.core.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.entity.RopeKnotBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RopeItem extends BlockItem {
    public RopeItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState clicked = level.getBlockState(pos);

        if (clicked.getBlock() instanceof FenceBlock) {
            if (!level.isClientSide) {
                BlockState knot = ObjectRegistry.ROPE_KNOT.get().defaultBlockState();
                if (level.setBlock(pos, knot, 3)) {
                    level.playSound(ctx.getPlayer(), pos, SoundEvents.LEASH_KNOT_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (level.getBlockEntity(pos) instanceof RopeKnotBlockEntity be) {
                        be.setHeldBlock(clicked);
                        be.setChanged();
                    }
                    if (!Objects.requireNonNull(ctx.getPlayer()).getAbilities().instabuild) {
                        ctx.getItemInHand().shrink(1);
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return super.useOn(ctx);
    }

}

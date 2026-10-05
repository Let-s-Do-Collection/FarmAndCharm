package net.satisfy.farm_and_charm.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.core.block.entity.StoveBlockEntity;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StoveInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (hit == null || hit.getDirection() != Direction.UP || InfoTooltips.isHidden(true)) {
            return List.of();
        }
        if (!(level.getBlockEntity(pos) instanceof StoveBlockEntity stove) || !stove.isGrillFree()) {
            return List.of();
        }
        int slot = StoveBlockEntity.grillSlotAt(hit.getLocation(), pos);
        ItemStack stack = stove.getGrillItem(slot);
        if (stack.isEmpty()) {
            return List.of();
        }
        Component status;
        if (!stove.isLit()) {
            status = Component.translatable("hud.farm_and_charm.grill_cold").withStyle(ChatFormatting.GRAY);
        } else {
            int seconds = Mth.positiveCeilDiv(Math.max(0, stove.getGrillTotal(slot) - stove.getGrillProgress(slot)), 20);
            status = Component.translatable("hud.farm_and_charm.grill_time", seconds).withStyle(ChatFormatting.GOLD);
        }
        return List.of(
                InfoSection.rows(Component.translatable("hud.farm_and_charm.grill"), List.of(InfoSection.Row.item(stack, stack.getHoverName().copy().append(" ").append(status)))),
                InfoSection.title(Component.translatable("hud.farm_and_charm.grill_hint").withStyle(ChatFormatting.GRAY))
        );
    }
}

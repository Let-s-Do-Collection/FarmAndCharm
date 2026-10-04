package net.satisfy.farm_and_charm.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.core.block.WaterSprinklerBlock;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WaterSprinklerInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (InfoTooltips.isHidden(PlatformHelper.showWaterSprinklerInfo())) {
            return List.of();
        }
        if (!(state.getBlock() instanceof WaterSprinklerBlock)) {
            return List.of();
        }
        return List.of(InfoSection.lines(Component.translatable(state.getValue(WaterSprinklerBlock.PRESSURE).getTranslationKey()),
                List.of(Component.translatable("hud.farm_and_charm.sprinkler_hint").withStyle(ChatFormatting.GRAY))));
    }
}

package net.satisfy.farm_and_charm.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.core.block.MincerBlock;
import net.satisfy.farm_and_charm.core.block.entity.MincerBlockEntity;
import net.satisfy.farm_and_charm.core.recipe.MincerRecipe;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MincerInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (InfoTooltips.isHidden(PlatformHelper.showMincerInfo())) {
            return List.of();
        }
        if (!(level.getBlockEntity(pos) instanceof MincerBlockEntity mincer)) {
            return List.of();
        }
        ItemStack input = mincer.getItem(mincer.INPUT_SLOT);
        if (input.isEmpty()) {
            return List.of();
        }
        List<InfoSection> sections = new ArrayList<>();
        sections.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.mincer_input"), List.of(input), InfoSection.ROW_COLUMNS));
        MincerRecipe recipe = mincer.findRecipe(level);
        if (recipe != null) {
            sections.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.result").withStyle(ChatFormatting.GREEN), List.of(recipe.getResultItem(level.registryAccess())), InfoSection.ROW_COLUMNS));
        }
        int cranked = state.getValue(MincerBlock.CRANKED);
        sections.add(InfoSection.lines(Component.translatable("hud.farm_and_charm.mincer_hint").withStyle(ChatFormatting.GRAY),
                List.of(Component.translatable("hud.farm_and_charm.mincer_cranked", cranked, MincerBlock.CRANKS_NEEDED).withStyle(ChatFormatting.GRAY))));
        return sections;
    }
}

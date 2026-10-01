package net.satisfy.farm_and_charm.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.core.block.CraftingBowlBlock;
import net.satisfy.farm_and_charm.core.block.entity.CraftingBowlBlockEntity;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.overlay.BlockInfoProvider;
import net.satisfy.foundation.overlay.InfoSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CraftingBowlInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (InfoTooltips.isHidden(PlatformHelper.showCraftingBowlInfo())) {
            return List.of();
        }
        if (!(level.getBlockEntity(pos) instanceof CraftingBowlBlockEntity bowl)) {
            return List.of();
        }
        List<InfoSection> sections = new ArrayList<>();
        ItemStack result = bowl.getItem(4);
        if (!result.isEmpty()) {
            sections.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.result").withStyle(ChatFormatting.GREEN), List.of(result), InfoSection.ROW_COLUMNS));
            sections.add(InfoSection.title(Component.translatable("hud.farm_and_charm.take_hint").withStyle(ChatFormatting.GRAY)));
            return sections;
        }
        List<ItemStack> ingredients = new ArrayList<>();
        for (int slot = 0; slot < 4; slot++) {
            if (!bowl.getItem(slot).isEmpty()) {
                ingredients.add(bowl.getItem(slot));
            }
        }
        if (ingredients.isEmpty()) {
            return sections;
        }
        sections.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.in_bowl"), ingredients, InfoSection.ROW_COLUMNS));
        bowl.findRecipe(level).ifPresent(recipe -> {
            ItemStack output = recipe.getResultItem(level.registryAccess()).copyWithCount(recipe.getOutputCount());
            sections.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.result").withStyle(ChatFormatting.GREEN), List.of(output), InfoSection.ROW_COLUMNS));
            int stirred = Math.min(state.getValue(CraftingBowlBlock.STIRRED), CraftingBowlBlock.STIRS_NEEDED);
            sections.add(InfoSection.lines(Component.translatable("hud.farm_and_charm.stir_hint").withStyle(ChatFormatting.GRAY),
                    List.of(Component.translatable("hud.farm_and_charm.stirred", stirred, CraftingBowlBlock.STIRS_NEEDED).withStyle(ChatFormatting.GRAY))));
        });
        return sections;
    }
}

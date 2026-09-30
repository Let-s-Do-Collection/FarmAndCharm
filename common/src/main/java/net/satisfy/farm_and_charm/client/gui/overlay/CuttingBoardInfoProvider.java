package net.satisfy.farm_and_charm.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.farm_and_charm.core.block.entity.CuttingBoardBlockEntity;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardAssemblyRecipe;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardRecipe;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.util.Strippables;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CuttingBoardInfoProvider implements BlockInfoProvider {
    @Override
    public List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof CuttingBoardBlockEntity board)) {
            return List.of();
        }
        List<InfoSection> sections = new ArrayList<>();
        if (board.hasKnife()) {
            boolean axe = Strippables.isAxe(board.getKnife());
            String prefix = axe ? "hud.farm_and_charm.strip" : "hud.farm_and_charm.chop";
            sections.add(InfoSection.icons(Component.translatable(board.isCutting() ? prefix + "ping" : "hud.farm_and_charm.ready_to_" + (axe ? "strip" : "chop")), board.isCutting() ? List.of(board.getPending()) : List.of(board.getKnife()), InfoSection.ROW_COLUMNS));
            if (board.isCutting()) {
                if (axe) {
                    sections.add(InfoSection.title(Component.translatable("hud.farm_and_charm.stripped", board.getPendingChops(), Strippables.CHOPS).withStyle(ChatFormatting.GRAY)));
                } else {
                    level.getRecipeManager().getRecipeFor(RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get(), new SingleRecipeInput(board.getPending()), level)
                            .ifPresent(recipe -> sections.add(InfoSection.title(Component.translatable("hud.farm_and_charm.chop", board.getPendingChops(), CuttingBoardBlockEntity.requiredChops(board.getKnife(), recipe.value())).withStyle(ChatFormatting.GRAY))));
                }
            }
            return sections;
        }
        List<ItemStack> items = board.getItems();
        if (items.isEmpty()) {
            return sections;
        }
        Optional<RecipeHolder<CuttingBoardAssemblyRecipe>> assembled = level.getRecipeManager().getRecipeFor(RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get(), new CuttingBoardAssemblyRecipe.Input(List.copyOf(items)), level);
        if (assembled.isPresent()) {
            sections.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.assembly_ready").withStyle(ChatFormatting.GREEN), List.of(assembled.get().value().getResult()), InfoSection.ROW_COLUMNS));
            sections.add(InfoSection.title(Component.translatable("hud.farm_and_charm.assemble_hint").withStyle(ChatFormatting.GRAY)));
        }
        sections.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.on_board"), items, InfoSection.ROW_COLUMNS));
        if (items.size() == 1) {
            Optional<RecipeHolder<CuttingBoardRecipe>> chop = level.getRecipeManager().getRecipeFor(RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get(), new SingleRecipeInput(items.getFirst()), level);
            chop.ifPresent(recipe -> sections.add(InfoSection.title(Component.translatable("hud.farm_and_charm.chop", board.getChops(), CuttingBoardBlockEntity.requiredChops(Minecraft.getInstance().player.getMainHandItem(), recipe.value())).withStyle(ChatFormatting.GRAY))));
        }
        List<ItemStack> additions = new ArrayList<>();
        for (RecipeHolder<CuttingBoardAssemblyRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get())) {
            CuttingBoardAssemblyRecipe recipe = holder.value();
            if (!recipe.accepts(items)) {
                continue;
            }
            for (Ingredient ingredient : recipe.getIngredients()) {
                ItemStack[] choices = ingredient.getItems();
                if (choices.length == 0) {
                    continue;
                }
                List<ItemStack> candidate = new ArrayList<>(items);
                candidate.add(choices[0]);
                if (recipe.accepts(candidate) && additions.stream().noneMatch(stack -> ItemStack.isSameItem(stack, choices[0]))) {
                    additions.add(choices[0]);
                }
            }
        }
        if (!additions.isEmpty()) {
            sections.add(InfoSection.icons(Component.translatable("hud.farm_and_charm.add"), additions, InfoSection.GRID_COLUMNS));
        }
        return sections;
    }
}

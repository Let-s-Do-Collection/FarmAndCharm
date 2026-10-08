package net.satisfy.farm_and_charm.core.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.gui.CookingPotGui;
import net.satisfy.farm_and_charm.client.gui.RoasterGui;
import net.satisfy.farm_and_charm.client.gui.StoveGui;
import net.satisfy.farm_and_charm.core.block.entity.CookingPotBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.RoasterBlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.StoveBlockEntity;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.recipe.CraftingBowlRecipe;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardAssemblyRecipe;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardRecipe;
import net.satisfy.farm_and_charm.core.recipe.MincerRecipe;
import net.satisfy.farm_and_charm.core.recipe.RoasterRecipe;
import net.satisfy.farm_and_charm.core.recipe.SiloRecipe;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.farm_and_charm.core.util.Strippables;
import net.satisfy.foundation.compat.RecipeViewerLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EmiEntrypoint
public class FarmAndCharmEmiPlugin implements EmiPlugin {
    public static final FarmAndCharmEmiCategory COOKING_POT = new FarmAndCharmEmiCategory("cooking_pot", ObjectRegistry.COOKING_POT.get(), "rei.farm_and_charm.cooking_pot_category");
    public static final FarmAndCharmEmiCategory STOVE = new FarmAndCharmEmiCategory("stove", ObjectRegistry.STOVE.get(), "rei.farm_and_charm.stove_category");
    public static final FarmAndCharmEmiCategory DOUGHING = new FarmAndCharmEmiCategory("doughing", ObjectRegistry.CRAFTING_BOWL.get(), "rei.farm_and_charm.bowl_category");
    public static final FarmAndCharmEmiCategory ROASTER = new FarmAndCharmEmiCategory("roaster", ObjectRegistry.ROASTER.get(), "rei.farm_and_charm.roaster_category");
    public static final FarmAndCharmEmiCategory DRYING = new FarmAndCharmEmiCategory("drying", ObjectRegistry.SILO_WOOD.get(), "rei.farm_and_charm.silo_category");
    public static final FarmAndCharmEmiCategory MINCING = new FarmAndCharmEmiCategory("mincer", ObjectRegistry.MINCER.get(), "rei.farm_and_charm.mincer_category");
    public static final FarmAndCharmEmiCategory CUTTING_BOARD = new FarmAndCharmEmiCategory("cutting_board", ObjectRegistry.CUTTING_BOARD.get(), "category.farm_and_charm.cutting_board");
    public static final FarmAndCharmEmiCategory ASSEMBLY = new FarmAndCharmEmiCategory("cutting_board_assembly", ObjectRegistry.CUTTING_BOARD.get(), "category.farm_and_charm.assembly");
    public static final FarmAndCharmEmiCategory LOG_STRIPPING = new FarmAndCharmEmiCategory("log_stripping", Items.IRON_AXE, "category.farm_and_charm.log_stripping");

    private static final int MACHINE_WIDTH = 124;
    private static final int MACHINE_HEIGHT = 60;
    private static final int OFFSET_X = 26;
    private static final int OFFSET_Y = 13;
    private static final int SIMPLE_WIDTH = 150;
    private static final int SIMPLE_HEIGHT = 50;
    private static final int SIMPLE_INPUT_X = (SIMPLE_WIDTH - 72) / 2;
    private static final int SIMPLE_ROW_Y = (SIMPLE_HEIGHT - RecipeViewerLayout.SLOT) / 2;
    private static final int ROW_INPUT_X = 10;
    private static final int ROW_ARROW_X = 34;
    private static final int ROW_OUTPUT_X = 64;
    private static final int ROW_Y = 6;
    private static final ResourceLocation CRAFTING_BOWL_TEXTURE = FarmAndCharm.identifier("textures/gui/crafting_bowl.png");

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(COOKING_POT);
        registry.addCategory(STOVE);
        registry.addCategory(DOUGHING);
        registry.addCategory(ROASTER);
        registry.addCategory(DRYING);
        registry.addCategory(MINCING);
        registry.addCategory(CUTTING_BOARD);
        registry.addCategory(ASSEMBLY);
        registry.addCategory(LOG_STRIPPING);

        registry.addWorkstation(COOKING_POT, EmiStack.of(ObjectRegistry.COOKING_POT.get()));
        registry.addWorkstation(STOVE, EmiStack.of(ObjectRegistry.STOVE.get()));
        registry.addWorkstation(DOUGHING, EmiStack.of(ObjectRegistry.CRAFTING_BOWL.get()));
        registry.addWorkstation(ROASTER, EmiStack.of(ObjectRegistry.ROASTER.get()));
        registry.addWorkstation(DRYING, EmiStack.of(ObjectRegistry.SILO_WOOD.get()));
        registry.addWorkstation(DRYING, EmiStack.of(ObjectRegistry.SILO_COPPER.get()));
        registry.addWorkstation(MINCING, EmiStack.of(ObjectRegistry.MINCER.get()));
        registry.addWorkstation(CUTTING_BOARD, EmiStack.of(ObjectRegistry.CUTTING_BOARD.get()));
        registry.addWorkstation(CUTTING_BOARD, EmiStack.of(ObjectRegistry.IRON_CLEAVER.get()));
        registry.addWorkstation(CUTTING_BOARD, EmiStack.of(ObjectRegistry.DIAMOND_CLEAVER.get()));
        registry.addWorkstation(CUTTING_BOARD, EmiStack.of(ObjectRegistry.NETHERITE_CLEAVER.get()));
        registry.addWorkstation(ASSEMBLY, EmiStack.of(ObjectRegistry.CUTTING_BOARD.get()));
        registry.addWorkstation(LOG_STRIPPING, EmiStack.of(ObjectRegistry.CUTTING_BOARD.get()));
        registry.addWorkstation(LOG_STRIPPING, EmiStack.of(Items.IRON_AXE));

        registry.addRecipeHandler(ScreenhandlerTypeRegistry.COOKING_POT_SCREEN_HANDLER.get(), new FarmAndCharmEmiRecipeHandler<>(COOKING_POT, 1, 6, 0, 8));
        registry.addRecipeHandler(ScreenhandlerTypeRegistry.ROASTER_SCREEN_HANDLER.get(), new FarmAndCharmEmiRecipeHandler<>(ROASTER, 1, 6, 0, 8));
        registry.addRecipeHandler(ScreenhandlerTypeRegistry.STOVE_SCREEN_HANDLER.get(), new FarmAndCharmEmiRecipeHandler<>(STOVE, 1, 3, -1, 5));

        RecipeManager manager = registry.getRecipeManager();
        RegistryAccess access = Objects.requireNonNull(Minecraft.getInstance().level).registryAccess();

        for (RecipeHolder<CookingPotRecipe> holder : manager.getAllRecipesFor(RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get())) {
            registry.addRecipe(cookingPot(holder, access));
        }
        for (RecipeHolder<RoasterRecipe> holder : manager.getAllRecipesFor(RecipeTypeRegistry.ROASTER_RECIPE_TYPE.get())) {
            registry.addRecipe(roaster(holder, access));
        }
        for (RecipeHolder<StoveRecipe> holder : manager.getAllRecipesFor(RecipeTypeRegistry.STOVE_RECIPE_TYPE.get())) {
            registry.addRecipe(stove(holder, access));
        }
        for (RecipeHolder<CraftingBowlRecipe> holder : manager.getAllRecipesFor(RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.get())) {
            registry.addRecipe(craftingBowl(holder, access));
        }
        for (RecipeHolder<SiloRecipe> holder : manager.getAllRecipesFor(RecipeTypeRegistry.SILO_RECIPE_TYPE.get())) {
            registry.addRecipe(simple(DRYING, holder.id(), holder.value().getIngredients(), holder.value().getOutput()));
        }
        for (RecipeHolder<MincerRecipe> holder : manager.getAllRecipesFor(RecipeTypeRegistry.MINCER_RECIPE_TYPE.get())) {
            registry.addRecipe(simple(MINCING, holder.id(), holder.value().getIngredients(), holder.value().getOutput()));
        }
        for (RecipeHolder<CuttingBoardRecipe> holder : manager.getAllRecipesFor(RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get())) {
            registry.addRecipe(cuttingBoard(holder));
        }
        for (RecipeHolder<CuttingBoardAssemblyRecipe> holder : manager.getAllRecipesFor(RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get())) {
            registry.addRecipe(assembly(holder));
        }
        for (Strippables.Entry entry : Strippables.all()) {
            registry.addRecipe(stripping(entry));
        }

        registry.removeEmiStacks(stack -> stack.getItemStack().is(TagRegistry.CAMPFIRE_MEATS));
        registry.removeRecipes(recipe -> recipe.getCategory() == VanillaEmiRecipeCategories.CAMPFIRE_COOKING && recipe.getOutputs().stream().anyMatch(output -> output.getItemStack().is(TagRegistry.CAMPFIRE_MEATS)));
    }

    private static FarmAndCharmEmiRecipe cookingPot(RecipeHolder<CookingPotRecipe> holder, RegistryAccess access) {
        CookingPotRecipe recipe = holder.value();
        List<EmiIngredient> inputs = ingredients(recipe.getIngredients());
        EmiStack container = recipe.isContainerRequired() ? EmiStack.of(recipe.getContainerItem()) : EmiStack.EMPTY;
        List<EmiIngredient> all = new ArrayList<>(inputs);
        if (!container.isEmpty()) {
            all.add(container);
        }
        EmiStack output = EmiStack.of(recipe.getResultItem(access));
        return new FarmAndCharmEmiRecipe(COOKING_POT, holder.id(), MACHINE_WIDTH, MACHINE_HEIGHT, all, inputs.size(), List.of(output), (self, widgets) -> {
            widgets.addTexture(CookingPotGui.BACKGROUND, 0, 0, MACHINE_WIDTH, MACHINE_HEIGHT, OFFSET_X, OFFSET_Y);
            widgets.addAnimatedTexture(CookingPotGui.BACKGROUND, CookingPotGui.ARROW_X - OFFSET_X, CookingPotGui.ARROW_Y - OFFSET_Y, 18, 30, 178, 15, CookingPotBlockEntity.getMaxCookingTime() * 50, true, false, false);
            potGrid(widgets, inputs);
            if (!container.isEmpty()) {
                widgets.addSlot(container, 95 - OFFSET_X - 1, 55 - OFFSET_Y - 1).drawBack(false);
            }
            widgets.addSlot(output, 124 - OFFSET_X - 1, 28 - OFFSET_Y - 1).drawBack(false).recipeContext(self);
        });
    }

    private static FarmAndCharmEmiRecipe roaster(RecipeHolder<RoasterRecipe> holder, RegistryAccess access) {
        RoasterRecipe recipe = holder.value();
        List<EmiIngredient> inputs = ingredients(recipe.getIngredients());
        EmiStack container = EmiStack.of(recipe.getContainer());
        List<EmiIngredient> all = new ArrayList<>(inputs);
        all.add(container);
        EmiStack output = EmiStack.of(recipe.getResultItem(access));
        return new FarmAndCharmEmiRecipe(ROASTER, holder.id(), MACHINE_WIDTH, MACHINE_HEIGHT, all, inputs.size(), List.of(output), (self, widgets) -> {
            widgets.addTexture(RoasterGui.BACKGROUND, 0, 0, MACHINE_WIDTH, MACHINE_HEIGHT, OFFSET_X, OFFSET_Y);
            widgets.addAnimatedTexture(RoasterGui.BACKGROUND, RoasterGui.ARROW_X - OFFSET_X, RoasterGui.ARROW_Y - OFFSET_Y, 18, 30, 178, 15, RoasterBlockEntity.getMaxRoastingTime() * 50, true, false, false);
            potGrid(widgets, inputs);
            widgets.addSlot(container, 95 - OFFSET_X - 1, 55 - OFFSET_Y - 1).drawBack(false);
            widgets.addSlot(output, 124 - OFFSET_X - 1, 28 - OFFSET_Y - 1).drawBack(false).recipeContext(self);
        });
    }

    private static void potGrid(WidgetHolder widgets, List<EmiIngredient> inputs) {
        for (int i = 0; i < inputs.size() && i < 6; i++) {
            int x = 30 + (i % 3) * 18 - OFFSET_X - 1;
            int y = 17 + (i / 3) * 18 - OFFSET_Y - 1;
            widgets.addSlot(inputs.get(i), x, y).drawBack(false);
        }
    }

    private static FarmAndCharmEmiRecipe stove(RecipeHolder<StoveRecipe> holder, RegistryAccess access) {
        StoveRecipe recipe = holder.value();
        List<EmiIngredient> inputs = ingredients(recipe.getIngredients());
        EmiStack output = EmiStack.of(recipe.getResultItem(access));
        return new FarmAndCharmEmiRecipe(STOVE, holder.id(), MACHINE_WIDTH, MACHINE_HEIGHT, inputs, List.of(output), (self, widgets) -> {
            widgets.addTexture(StoveGui.BACKGROUND, 0, 0, MACHINE_WIDTH, MACHINE_HEIGHT, OFFSET_X, OFFSET_Y);
            widgets.addAnimatedTexture(StoveGui.BACKGROUND, StoveGui.ARROW_X - OFFSET_X, StoveGui.ARROW_Y - OFFSET_Y, 18, 25, 178, 20, StoveBlockEntity.TOTAL_COOKING_TIME * 50, true, false, false);
            widgets.addTexture(StoveGui.BACKGROUND, 62 - OFFSET_X, 49 - OFFSET_Y, 17, 15, 176, 0);
            for (int i = 0; i < inputs.size() && i < 3; i++) {
                widgets.addSlot(inputs.get(i), 29 + i * 18 - OFFSET_X - 1, 18 - OFFSET_Y - 1).drawBack(false);
            }
            widgets.addSlot(output, 126 - OFFSET_X - 1, 42 - OFFSET_Y - 1).drawBack(false).recipeContext(self);
            float experience = recipe.getExperience();
            if (experience > 0) {
                Component text = Component.translatable("emi.cooking.experience", experience);
                widgets.addText(text, MACHINE_WIDTH - Minecraft.getInstance().font.width(text), 0, 0xFF808080, false);
            }
        });
    }

    private static FarmAndCharmEmiRecipe craftingBowl(RecipeHolder<CraftingBowlRecipe> holder, RegistryAccess access) {
        CraftingBowlRecipe recipe = holder.value();
        List<EmiIngredient> inputs = ingredients(recipe.getIngredients());
        EmiStack output = EmiStack.of(recipe.getResultItem(access));
        int[][] positions = {{50, 25}, {50, 43}, {32, 25}, {32, 43}};
        return new FarmAndCharmEmiRecipe(DOUGHING, holder.id(), 176, 85, inputs, List.of(output), (self, widgets) -> {
            widgets.addTexture(CRAFTING_BOWL_TEXTURE, 0, 0, 176, 85, 0, 0);
            for (int i = 0; i < inputs.size() && i < positions.length; i++) {
                widgets.addSlot(inputs.get(i), positions[i][0] - 1, positions[i][1] - 1).drawBack(false);
            }
            widgets.addSlot(output, 109, 34).drawBack(false).recipeContext(self);
        });
    }

    private static FarmAndCharmEmiRecipe simple(FarmAndCharmEmiCategory category, ResourceLocation id, List<Ingredient> ingredients, ItemStack result) {
        List<EmiIngredient> inputs = ingredients.isEmpty() ? List.of() : List.of(EmiIngredient.of(ingredients.get(0)));
        EmiStack output = EmiStack.of(result);
        return new FarmAndCharmEmiRecipe(category, id, SIMPLE_WIDTH, SIMPLE_HEIGHT, inputs, List.of(output), (self, widgets) -> {
            widgets.addSlot(inputs.isEmpty() ? EmiStack.EMPTY : inputs.get(0), SIMPLE_INPUT_X, SIMPLE_ROW_Y);
            arrow(widgets, SIMPLE_INPUT_X + 24, SIMPLE_ROW_Y + 1);
            widgets.addSlot(output, SIMPLE_INPUT_X + 54, SIMPLE_ROW_Y).recipeContext(self);
        });
    }

    private static FarmAndCharmEmiRecipe cuttingBoard(RecipeHolder<CuttingBoardRecipe> holder) {
        CuttingBoardRecipe recipe = holder.value();
        EmiIngredient input = EmiIngredient.of(recipe.getIngredient());
        List<EmiStack> outputs = new ArrayList<>();
        outputs.add(EmiStack.of(recipe.getResult()));
        recipe.getByproducts().forEach(stack -> outputs.add(EmiStack.of(stack)));
        int width = ROW_OUTPUT_X - ROW_INPUT_X + outputs.size() * RecipeViewerLayout.SLOT;
        int offset = (RecipeViewerLayout.ROW_WIDTH - width) / 2 - ROW_INPUT_X;
        return new FarmAndCharmEmiRecipe(CUTTING_BOARD, holder.id(), RecipeViewerLayout.ROW_WIDTH, RecipeViewerLayout.ROW_HEIGHT, List.of(input), outputs, (self, widgets) -> {
            widgets.addSlot(input, offset + ROW_INPUT_X, ROW_Y);
            arrow(widgets, offset + ROW_ARROW_X, ROW_Y + 1);
            int x = offset + ROW_OUTPUT_X;
            for (EmiStack output : outputs) {
                widgets.addSlot(output, x, ROW_Y).recipeContext(self);
                x += RecipeViewerLayout.SLOT;
            }
        });
    }

    private static FarmAndCharmEmiRecipe assembly(RecipeHolder<CuttingBoardAssemblyRecipe> holder) {
        CuttingBoardAssemblyRecipe recipe = holder.value();
        List<EmiIngredient> inputs = ingredients(recipe.getIngredients());
        EmiStack output = EmiStack.of(recipe.getResult());
        RecipeViewerLayout.AssemblyLayout layout = RecipeViewerLayout.assembly(inputs.size(), recipe.isOrdered(), CuttingBoardAssemblyRecipe.MAX_ORDERED_ITEMS);
        return new FarmAndCharmEmiRecipe(ASSEMBLY, holder.id(), RecipeViewerLayout.assemblyWidth(CuttingBoardAssemblyRecipe.MAX_ORDERED_ITEMS), RecipeViewerLayout.ASSEMBLY_HEIGHT, inputs, List.of(output), (self, widgets) -> {
            for (int i = 0; i < layout.slots().size(); i++) {
                RecipeViewerLayout.Pos pos = layout.slots().get(i);
                widgets.addSlot(i < inputs.size() ? inputs.get(i) : EmiStack.EMPTY, pos.x(), pos.y());
            }
            for (RecipeViewerLayout.Pos pos : layout.arrows()) {
                arrow(widgets, pos.x(), pos.y());
            }
            widgets.addSlot(output, layout.output().x(), layout.output().y()).recipeContext(self);
        });
    }

    private static FarmAndCharmEmiRecipe stripping(Strippables.Entry entry) {
        EmiStack input = EmiStack.of(entry.log());
        EmiStack output = EmiStack.of(entry.stripped());
        ResourceLocation logId = entry.log().getItem().builtInRegistryHolder().key().location();
        ResourceLocation id = FarmAndCharm.identifier("/log_stripping/" + logId.getNamespace() + "/" + logId.getPath());
        return new FarmAndCharmEmiRecipe(LOG_STRIPPING, id, RecipeViewerLayout.ROW_WIDTH, RecipeViewerLayout.ROW_HEIGHT, List.of(input), List.of(output), (self, widgets) -> {
            widgets.addSlot(input, ROW_INPUT_X, ROW_Y);
            arrow(widgets, ROW_ARROW_X, ROW_Y + 1);
            widgets.addSlot(output, ROW_OUTPUT_X, ROW_Y).recipeContext(self);
        });
    }

    private static void arrow(WidgetHolder widgets, int x, int y) {
        widgets.addDrawable(x, y, RecipeViewerLayout.ARROW_LENGTH, RecipeViewerLayout.ARROW_THICKNESS, (graphics, mouseX, mouseY, delta) -> RecipeViewerLayout.drawRightArrow(graphics, 0, 0));
    }

    private static List<EmiIngredient> ingredients(List<Ingredient> ingredients) {
        return ingredients.stream().map(EmiIngredient::of).toList();
    }
}

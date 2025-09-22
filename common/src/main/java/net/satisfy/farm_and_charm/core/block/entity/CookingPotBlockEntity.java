package net.satisfy.farm_and_charm.core.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.client.gui.handler.CookingPotGuiHandler;
import net.satisfy.farm_and_charm.core.block.CookingPotBlock;
import net.satisfy.farm_and_charm.core.item.food.EffectFood;
import net.satisfy.farm_and_charm.core.item.food.EffectFoodHelper;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.recipe.RecipeUnlockManager;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.farm_and_charm.core.world.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CookingPotBlockEntity extends BlockEntity implements BlockEntityTicker<CookingPotBlockEntity>, ImplementedInventory, MenuProvider, Container {
    private static final int FIRST_INGREDIENT_SLOT = 0;
    private static final int LAST_INGREDIENT_SLOT = 5;
    private static final int CONTAINER_SLOT = 6;
    private static final int OUTPUT_SLOT = 7;
    private static final int MAX_CAPACITY = 8;
    private static final int MAX_COOKING_TIME = 900;
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(MAX_CAPACITY, ItemStack.EMPTY);
    private int cookingTime;
    private boolean isBeingBurned;
    private UUID ownerUuid;
    private final ContainerData delegate = new ContainerData() {
        public int get(int index) {
            return switch (index) {
                case 0 -> cookingTime;
                case 1 -> isBeingBurned ? 1 : 0;
                default -> 0;
            };
        }
        public void set(int index, int value) {
            switch (index) {
                case 0 -> cookingTime = value;
                case 1 -> isBeingBurned = value != 0;
            }
        }
        public int getCount() {
            return 2;
        }
    };

    public CookingPotBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.COOKING_POT_BLOCK_ENTITY.get(), pos, state);
    }

    public static int getMaxCookingTime() {
        return MAX_COOKING_TIME;
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        return switch (side) {
            case UP -> new int[]{0, 1, 2, 3, 4, 5};
            case DOWN -> new int[]{OUTPUT_SLOT};
            default -> new int[]{CONTAINER_SLOT};
        };
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        NonNullList<ItemStack> loaded = NonNullList.withSize(MAX_CAPACITY, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, loaded, provider);
        for (int i = 0; i < MAX_CAPACITY; i++) {
            this.inventory.set(i, loaded.get(i));
        }
        cookingTime = tag.getInt("CookingTime");
        if (tag.hasUUID("OwnerUUID")) {
            ownerUuid = tag.getUUID("OwnerUUID");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        ContainerHelper.saveAllItems(tag, inventory, provider);
        tag.putInt("CookingTime", cookingTime);
        if (ownerUuid != null) {
            tag.putUUID("OwnerUUID", ownerUuid);
        }
    }

    public boolean isBeingBurned() {
        if (level == null) throw new IllegalStateException("Null world not allowed");
        BlockState belowState = level.getBlockState(worldPosition.below());
        return belowState.is(TagRegistry.ALLOWS_COOKING);
    }

    private boolean canCraft(Recipe<?> recipe, RegistryAccess access) {
        if (recipe == null || recipe.getResultItem(access).isEmpty()) return false;
        if (recipe instanceof CookingPotRecipe cookingRecipe) {
            if (cookingRecipe.isContainerRequired()) {
                ItemStack containerSlotStack = getItem(CONTAINER_SLOT);
                if (!containerSlotStack.is(cookingRecipe.getContainerItem().getItem())) return false;
            }
            ItemStack outputSlotStack = getItem(OUTPUT_SLOT);
            ItemStack expected = generateOutputItem(recipe, access);
            return outputSlotStack.isEmpty() || ItemStack.isSameItemSameComponents(outputSlotStack, expected) && outputSlotStack.getCount() < outputSlotStack.getMaxStackSize();
        }
        return false;
    }

    private void craft(Recipe<?> recipe, RegistryAccess access) {
        if (!canCraft(recipe, access)) return;
        ItemStack recipeOutput = generateOutputItem(recipe, access);
        ItemStack outputSlotStack = getItem(OUTPUT_SLOT);
        if (outputSlotStack.isEmpty()) {
            setItem(OUTPUT_SLOT, recipeOutput);
        } else {
            outputSlotStack.grow(recipeOutput.getCount());
        }
        recipe.getIngredients().forEach(ingredient -> {
            for (int slot = FIRST_INGREDIENT_SLOT; slot <= LAST_INGREDIENT_SLOT; slot++) {
                ItemStack stack = getItem(slot);
                if (ingredient.test(stack)) {
                    ItemStack remainderStack = stack.getItem().hasCraftingRemainingItem() ? new ItemStack(Objects.requireNonNull(stack.getItem().getCraftingRemainingItem())) : ItemStack.EMPTY;
                    stack.shrink(1);
                    if (!remainderStack.isEmpty()) setItem(slot, remainderStack);
                    break;
                }
            }
        });
        if (recipe instanceof CookingPotRecipe cookingPotRecipe && cookingPotRecipe.isContainerRequired()) {
            ItemStack containerSlotStack = getItem(CONTAINER_SLOT);
            if (!containerSlotStack.isEmpty()) {
                containerSlotStack.shrink(1);
                if (containerSlotStack.isEmpty()) setItem(CONTAINER_SLOT, ItemStack.EMPTY);
            }
        }
        setChanged();
    }

    private ItemStack generateOutputItem(Recipe<?> recipe, RegistryAccess access) {
        ItemStack outputStack = recipe.getResultItem(access).copy();
        if (outputStack.getItem() instanceof EffectFood) {
            for (MobEffectInstance inst : EffectFoodHelper.collectMergedSortedEffects(this, FIRST_INGREDIENT_SLOT, LAST_INGREDIENT_SLOT)) {
                EffectFoodHelper.addEffect(outputStack, new Pair<>(inst, 1.0f));
            }
        }
        return outputStack;
    }

    public void tick(Level world, BlockPos pos, BlockState state, CookingPotBlockEntity blockEntity) {
        if (world.isClientSide()) return;
        boolean wasBeingBurned = isBeingBurned;
        isBeingBurned = isBeingBurned();
        if (wasBeingBurned != isBeingBurned || state.getValue(CookingPotBlock.LIT) != isBeingBurned) {
            world.setBlock(pos, state.setValue(CookingPotBlock.LIT, isBeingBurned), Block.UPDATE_ALL);
        }
        if (!isBeingBurned) {
            return;
        }
        RecipeManager recipeManager = world.getRecipeManager();
        List<RecipeHolder<CookingPotRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get());
        Optional<CookingPotRecipe> recipe = Optional.ofNullable(getRecipe(recipes, inventory));
        if (recipe.isPresent() && recipe.get() instanceof CookingPotRecipe cookingRecipe) {
            if (cookingRecipe.requiresLearning()) {
                ServerPlayer owner = Objects.requireNonNull(world.getServer()).getPlayerList().getPlayer(ownerUuid);
                if (owner == null || RecipeUnlockManager.isRecipeLocked(owner, BuiltInRegistries.RECIPE_TYPE.getKey(recipe.get().getType()))) {
                    cookingTime = 0;
                    if (state.getValue(CookingPotBlock.COOKING)) {
                        world.setBlock(pos, state.setValue(CookingPotBlock.COOKING, false), Block.UPDATE_ALL);
                    }
                    return;
                }
            }
        }
        if (level == null) throw new IllegalStateException("Null world not allowed");
        RegistryAccess access = level.registryAccess();
        if (recipe.isPresent() && canCraft(recipe.get(), access)) {
            if (++cookingTime >= MAX_COOKING_TIME) {
                cookingTime = 0;
                craft(recipe.get(), access);
            }
            if (!state.getValue(CookingPotBlock.COOKING)) {
                world.setBlock(pos, state.setValue(CookingPotBlock.COOKING, true), Block.UPDATE_ALL);
            }
        } else {
            cookingTime = 0;
            if (state.getValue(CookingPotBlock.COOKING)) {
                world.setBlock(pos, state.setValue(CookingPotBlock.COOKING, false), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        setChanged();
    }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    public @NotNull Component getDisplayName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    private CookingPotRecipe getRecipe(List<RecipeHolder<CookingPotRecipe>> recipes, NonNullList<ItemStack> inventory) {
        for (RecipeHolder<CookingPotRecipe> recipeHolder : recipes) {
            CookingPotRecipe recipe = recipeHolder.value();
            if (matchesInventory(recipe, inventory)) {
                return recipe;
            }
        }
        return null;
    }

    private boolean matchesInventory(CookingPotRecipe recipe, NonNullList<ItemStack> inventory) {
        List<Ingredient> ingredients = recipe.getIngredients();
        NonNullList<ItemStack> invCopy = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);
        for (int i = FIRST_INGREDIENT_SLOT; i <= LAST_INGREDIENT_SLOT; i++) {
            invCopy.set(i, inventory.get(i).copy());
        }

        for (Ingredient ingredient : ingredients) {
            boolean matched = false;
            for (int i = FIRST_INGREDIENT_SLOT; i <= LAST_INGREDIENT_SLOT; i++) {
                ItemStack stack = invCopy.get(i);
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    stack.shrink(1);
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }

        outer:
        for (int i = FIRST_INGREDIENT_SLOT; i <= LAST_INGREDIENT_SLOT; i++) {
            ItemStack remaining = invCopy.get(i);
            if (!remaining.isEmpty()) {
                for (Ingredient ingredient : ingredients) {
                    if (ingredient.test(remaining)) {
                        continue outer;
                    }
                }
                return false;
            }
        }

        return true;
    }

    @Nullable
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        ownerUuid = player.getUUID();
        return new CookingPotGuiHandler(syncId, inv, this, delegate);
    }
}

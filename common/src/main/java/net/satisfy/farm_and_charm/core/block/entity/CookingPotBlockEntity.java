package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class CookingPotBlockEntity extends BlockEntity implements BlockEntityTicker<CookingPotBlockEntity>, ImplementedInventory, MenuProvider {
    private static final int MAX_CAPACITY = 8, CONTAINER_SLOT = 6, OUTPUT_SLOT = 7, INGREDIENTS_AREA = 2 * 3;
    private static final int[] SLOTS_FOR_UP = new int[]{0, 1, 2, 3, 4, 5};
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

    public int @NotNull [] getSlotsForFace(Direction side) {
        return switch (side) {
            case UP -> SLOTS_FOR_UP;
            case DOWN -> new int[]{OUTPUT_SLOT};
            default -> new int[]{CONTAINER_SLOT};
        };
    }

    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        ContainerHelper.loadAllItems(nbt, inventory, provider);
        cookingTime = nbt.getInt("CookingTime");
        if (nbt.hasUUID("OwnerUUID")) {
            ownerUuid = nbt.getUUID("OwnerUUID");
        }
    }

    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);
        ContainerHelper.saveAllItems(nbt, inventory, provider);
        nbt.putInt("CookingTime", cookingTime);
        if (ownerUuid != null) {
            nbt.putUUID("OwnerUUID", ownerUuid);
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
            ItemStack recipeOutput = generateOutputItem(recipe, access);
            if (!outputSlotStack.isEmpty() && (!ItemStack.isSameItemSameComponents(outputSlotStack, recipeOutput) || outputSlotStack.getCount() >= outputSlotStack.getMaxStackSize()))
                return false;
            NonNullList<ItemStack> temp = NonNullList.create();
            for (int i = 0; i < INGREDIENTS_AREA; i++) {
                temp.add(getItem(i).copy());
            }
            for (var ingredient : cookingRecipe.getIngredients()) {
                boolean found = false;
                for (ItemStack stack : temp) {
                    if (ingredient.test(stack) && !stack.isEmpty()) {
                        stack.shrink(1);
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
            return true;
        }
        return false;
    }

    private void craft(Recipe<?> recipe, RegistryAccess access) {
        if (!canCraft(recipe, access)) return;
        ItemStack recipeOutput = generateOutputItem(recipe, access);
        ItemStack outputSlotStack = getItem(OUTPUT_SLOT);
        if (outputSlotStack.isEmpty()) {
            setItem(OUTPUT_SLOT, recipeOutput);
        } else if (ItemStack.isSameItemSameComponents(outputSlotStack, recipeOutput)) {
            outputSlotStack.grow(recipeOutput.getCount());
        }
        if (recipe instanceof CookingPotRecipe cookingRecipe) {
            for (var ingredient : cookingRecipe.getIngredients()) {
                for (int slot = 0; slot < INGREDIENTS_AREA; slot++) {
                    ItemStack stack = getItem(slot);
                    if (ingredient.test(stack) && !stack.isEmpty()) {
                        ItemStack remainderStack = stack.getItem().hasCraftingRemainingItem() ? new ItemStack(Objects.requireNonNull(stack.getItem().getCraftingRemainingItem())) : ItemStack.EMPTY;
                        stack.shrink(1);
                        if (!remainderStack.isEmpty()) {
                            if (getItem(slot).isEmpty()) {
                                setItem(slot, remainderStack);
                            } else {
                                boolean added = false;
                                for (int i = 0; i < INGREDIENTS_AREA; i++) {
                                    ItemStack is = getItem(i);
                                    if (is.isEmpty()) {
                                        setItem(i, remainderStack.copy());
                                        added = true;
                                        break;
                                    } else if (ItemStack.isSameItemSameComponents(is, remainderStack) && is.getCount() < is.getMaxStackSize()) {
                                        is.grow(1);
                                        added = true;
                                        break;
                                    }
                                }
                                if (!added) {
                                    if (this.level != null) {
                                        Block.popResource(this.level, this.worldPosition, remainderStack);
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            }
            if (cookingRecipe.isContainerRequired()) {
                ItemStack containerSlotStack = getItem(CONTAINER_SLOT);
                if (!containerSlotStack.isEmpty()) {
                    containerSlotStack.shrink(1);
                    if (containerSlotStack.isEmpty()) setItem(CONTAINER_SLOT, ItemStack.EMPTY);
                }
            }
        }
        for (int slot = 0; slot < INGREDIENTS_AREA; slot++) {
            ItemStack ingredientStack = this.getItem(slot);
            if (!ingredientStack.isEmpty() && ingredientStack.getItem() instanceof EffectFood) {
                DataComponentMap ingredientComponents = ingredientStack.getComponents();
                if (ingredientComponents.has(EffectFoodHelper.STORED_EFFECTS)) {
                    DataComponentMap recipeOutputComponents = recipeOutput.getComponents();
                    recipeOutputComponents = recipeOutputComponents.set(EffectFoodHelper.STORED_EFFECTS, ingredientComponents.get(EffectFoodHelper.STORED_EFFECTS));
                    recipeOutput.applyComponents(recipeOutputComponents);
                }
            }
        }

    }

    private ItemStack generateOutputItem(Recipe<?> recipe, RegistryAccess access) {
        ItemStack outputStack = recipe.getResultItem(access).copy();
        if (outputStack.getItem() instanceof EffectFood) {
            recipe.getIngredients().forEach(ingredient -> {
                for (int slot = 0; slot < INGREDIENTS_AREA; slot++) {
                    ItemStack stack = getItem(slot);
                    if (ingredient.test(stack)) {
                        EffectFoodHelper.getEffects(stack).forEach(effect -> EffectFoodHelper.addEffect(outputStack, List.of(effect)));
                        break;
                    }
                }
            });
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

        RecipeInput recipeInput = CraftingInput.of(3, 2, blockEntity.getItems().subList(0, 6));
        Optional<RecipeHolder<CookingPotRecipe>> recipeHolder = world.getRecipeManager().getRecipeFor(
                RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get(), recipeInput, level
        );

        if (level == null) throw new IllegalStateException("Null world not allowed");
        RegistryAccess access = level.registryAccess();

        if(recipeHolder.isPresent() && canCraft(recipeHolder.get().value(), access)){
            if(++cookingTime >= MAX_COOKING_TIME){
                cookingTime = 0;
                craft(recipeHolder.get().value(), access);
            }
            if(!state.getValue(CookingPotBlock.COOKING)){
                world.setBlock(pos, state.setValue(CookingPotBlock.COOKING, true), Block.UPDATE_ALL);
            }
        } else {
            cookingTime = 0;
            if(state.getValue(CookingPotBlock.COOKING)){
                world.setBlock(pos, state.setValue(CookingPotBlock.COOKING, false), Block.UPDATE_ALL);
            }
        }
    }

    private CookingPotRecipe getRecipe(List<RecipeHolder<CookingPotRecipe>> recipes, NonNullList<ItemStack> inventory) {
        recipeLoop:
        for (RecipeHolder<CookingPotRecipe> recipeHolder : recipes) {
            CookingPotRecipe recipe = recipeHolder.value();
            for (Ingredient ingredient : recipe.getIngredients()) {
                boolean ingredientFound = false;
                for (int slotIndex = 1; slotIndex < inventory.size(); slotIndex++) {
                    ItemStack slotItem = inventory.get(slotIndex);
                    if (ingredient.test(slotItem)) {
                        ingredientFound = true;
                        break;
                    }
                }
                if (!ingredientFound) {
                    continue recipeLoop;
                }
            }
            return recipe;
        }
        return null;
    }

    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    public @NotNull net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    public @Nullable AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        ownerUuid = player.getUUID();
        return new CookingPotGuiHandler(syncId, inv, this, delegate);
    }
}

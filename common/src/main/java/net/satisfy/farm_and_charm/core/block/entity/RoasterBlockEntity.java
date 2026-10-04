package net.satisfy.farm_and_charm.core.block.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.satisfy.farm_and_charm.client.gui.handler.RoasterGuiHandler;
import net.satisfy.farm_and_charm.core.block.RoasterBlock;
import net.satisfy.foundation.food.IngredientEffectCarrier;
import net.satisfy.foundation.food.IngredientEffects;
import net.satisfy.foundation.recipe.RecipeUnlockManager;
import net.satisfy.farm_and_charm.core.recipe.RoasterRecipe;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.foundation.util.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class RoasterBlockEntity extends BlockEntity implements BlockEntityTicker<RoasterBlockEntity>, ImplementedInventory, MenuProvider {
    private static final int FIRST_INGREDIENT_SLOT = 0;
    private static final int LAST_INGREDIENT_SLOT = 5;
    private static final int CONTAINER_SLOT = 6;
    private static final int OUTPUT_SLOT = 7;
    private static final int MAX_CAPACITY = 8;
    private static final int MAX_ROASTING_TIME = 900;
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(MAX_CAPACITY, ItemStack.EMPTY);
    private int roastingTime;
    private boolean isBeingBurned;
    private UUID ownerUuid;
    private final ContainerData delegate = new ContainerData() {
        public int get(int index) {
            return switch (index) {
                case 0 -> roastingTime;
                case 1 -> isBeingBurned ? 1 : 0;
                default -> 0;
            };
        }

        public void set(int index, int value) {
            switch (index) {
                case 0 -> roastingTime = value;
                case 1 -> isBeingBurned = value != 0;
            }
        }

        public int getCount() {
            return 2;
        }
    };

    public RoasterBlockEntity(BlockPos pos, BlockState state) {
        this(EntityTypeRegistry.ROASTER_BLOCK_ENTITY.get(), pos, state);
    }

    protected RoasterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected BooleanProperty getLitProperty() {
        return RoasterBlock.LIT;
    }

    protected BooleanProperty getRoastingProperty() {
        return RoasterBlock.ROASTING;
    }

    public static int getMaxRoastingTime() {
        return MAX_ROASTING_TIME;
    }

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
        roastingTime = tag.getInt("RoastingTime");
        if (tag.hasUUID("OwnerUUID")) {
            ownerUuid = tag.getUUID("OwnerUUID");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        ContainerHelper.saveAllItems(tag, inventory, provider);
        tag.putInt("RoastingTime", roastingTime);
        if (ownerUuid != null) {
            tag.putUUID("OwnerUUID", ownerUuid);
        }
    }

    public boolean hasOutputItem() {
        return !getItem(OUTPUT_SLOT).isEmpty();
    }

    public boolean isBeingBurned() {
        if (level == null) throw new IllegalStateException("Null world not allowed");
        BlockState belowState = level.getBlockState(worldPosition.below());
        return belowState.is(TagRegistry.ALLOWS_COOKING);
    }

    private boolean canCraft(Recipe<?> recipe, RegistryAccess access) {
        if (recipe == null || recipe.getResultItem(access).isEmpty()) return false;
        if (recipe instanceof RoasterRecipe roastingRecipe) {
            ItemStack containerSlotStack = getItem(CONTAINER_SLOT);
            if (!containerSlotStack.is(roastingRecipe.getContainer().getItem())) return false;
            ItemStack outputSlotStack = getItem(OUTPUT_SLOT);
            ItemStack expected = generateOutputItem(recipe, access);
            return outputSlotStack.isEmpty() || ItemStack.isSameItemSameComponents(outputSlotStack, expected) && outputSlotStack.getCount() < outputSlotStack.getMaxStackSize();
        }
        return false;
    }

    protected int getExtraOutputCount(ItemStack output) {
        return 0;
    }

    @Nullable
    protected Player getOwner() {
        if (ownerUuid == null || level == null || level.getServer() == null) return null;
        return level.getServer().getPlayerList().getPlayer(ownerUuid);
    }

    private void craft(Recipe<?> recipe, RegistryAccess access) {
        if (!canCraft(recipe, access)) return;

        ItemStack recipeOutput = generateOutputItem(recipe, access);
        ItemStack outputSlotStack = getItem(OUTPUT_SLOT);
        int room = recipeOutput.getMaxStackSize() - outputSlotStack.getCount() - recipeOutput.getCount();
        recipeOutput.grow(Math.max(0, Math.min(getExtraOutputCount(recipeOutput), room)));

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
                    if (!remainderStack.isEmpty()) {
                        if (stack.isEmpty()) {
                            setItem(slot, remainderStack);
                        } else {
                            tryInsertRemainder(remainderStack);
                        }
                    }
                    break;
                }
            }
        });

        if (recipe instanceof RoasterRecipe roasterRecipe && !roasterRecipe.getContainer().isEmpty()) {
            ItemStack containerSlotStack = getItem(CONTAINER_SLOT);
            if (!containerSlotStack.isEmpty()) {
                ItemStack containerRemainder = containerSlotStack.getItem().hasCraftingRemainingItem()
                        ? new ItemStack(Objects.requireNonNull(containerSlotStack.getItem().getCraftingRemainingItem()))
                        : ItemStack.EMPTY;

                containerSlotStack.shrink(1);

                if (!containerRemainder.isEmpty()) {
                    if (containerSlotStack.isEmpty()) {
                        setItem(CONTAINER_SLOT, containerRemainder);
                    } else {
                        if (!tryInsertRemainder(containerRemainder)) {
                            if (level != null) {
                                Block.popResource(level, worldPosition, containerRemainder);
                            }
                        }
                    }
                }
            }
        }

        setChanged();
    }

    private boolean tryInsertRemainder(ItemStack remainderStack) {
        if (remainderStack.isEmpty()) return true;

        for (int slot = FIRST_INGREDIENT_SLOT; slot <= LAST_INGREDIENT_SLOT; slot++) {
            ItemStack existingStack = getItem(slot);
            if (existingStack.isEmpty()) {
                setItem(slot, remainderStack);
                return true;
            }
            if (ItemStack.isSameItemSameComponents(existingStack, remainderStack) && existingStack.getCount() < existingStack.getMaxStackSize()) {
                int transferableAmount = Math.min(remainderStack.getCount(), existingStack.getMaxStackSize() - existingStack.getCount());
                if (transferableAmount > 0) {
                    existingStack.grow(transferableAmount);
                    remainderStack.shrink(transferableAmount);
                    if (remainderStack.isEmpty()) {
                        setChanged();
                        return true;
                    }
                }
            }
        }

        ItemStack containerSlotStack = getItem(CONTAINER_SLOT);
        if (containerSlotStack.isEmpty()) {
            setItem(CONTAINER_SLOT, remainderStack);
            return true;
        }
        if (ItemStack.isSameItemSameComponents(containerSlotStack, remainderStack) && containerSlotStack.getCount() < containerSlotStack.getMaxStackSize()) {
            int transferableAmount = Math.min(remainderStack.getCount(), containerSlotStack.getMaxStackSize() - containerSlotStack.getCount());
            if (transferableAmount > 0) {
                containerSlotStack.grow(transferableAmount);
                remainderStack.shrink(transferableAmount);
                setChanged();
                return remainderStack.isEmpty();
            }
        }

        return remainderStack.isEmpty();
    }

    private ItemStack generateOutputItem(Recipe<?> recipe, RegistryAccess access) {
        ItemStack outputStack = recipe.getResultItem(access).copy();
        if (outputStack.getItem() instanceof IngredientEffectCarrier) {
            for (MobEffectInstance inst : IngredientEffects.collectMergedSortedEffects(this, FIRST_INGREDIENT_SLOT, LAST_INGREDIENT_SLOT)) {
                IngredientEffects.addEffect(outputStack, new Pair<>(inst, 1.0f));
            }
        }
        return outputStack;
    }

    public void tick(Level world, BlockPos pos, BlockState state, RoasterBlockEntity blockEntity) {
        if (world.isClientSide()) return;
        boolean wasBeingBurned = isBeingBurned;
        isBeingBurned = isBeingBurned();
        if (wasBeingBurned != isBeingBurned || state.getValue(getLitProperty()) != isBeingBurned) {
            world.setBlock(pos, state.setValue(getLitProperty(), isBeingBurned), Block.UPDATE_ALL);
        }
        if (!isBeingBurned) {
            return;
        }
        if (level == null) throw new IllegalStateException("Null world not allowed");
        RecipeManager recipeManager = level.getRecipeManager();
        List<RecipeHolder<RoasterRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeTypeRegistry.ROASTER_RECIPE_TYPE.get());
        Optional<RoasterRecipe> recipe = Optional.ofNullable(getRecipe(recipes, inventory));
        RegistryAccess access = level.registryAccess();
        if (recipe.isPresent() && recipe.get() instanceof RoasterRecipe roastingRecipe) {
            if (roastingRecipe.requiresLearning()) {
                ServerPlayer owner = Objects.requireNonNull(world.getServer()).getPlayerList().getPlayer(ownerUuid);
                if (owner == null || RecipeUnlockManager.isRecipeLocked(owner, BuiltInRegistries.RECIPE_TYPE.getKey(recipe.get().getType()))) {
                    roastingTime = 0;
                    if (state.getValue(getRoastingProperty())) {
                        world.setBlock(pos, state.setValue(getRoastingProperty(), false), Block.UPDATE_ALL);
                    }
                    return;
                }
            }
        }
        if (recipe.isPresent() && canCraft(recipe.get(), access)) {
            if (++roastingTime >= MAX_ROASTING_TIME) {
                roastingTime = 0;
                craft(recipe.get(), access);
            }
            if (!state.getValue(getRoastingProperty())) {
                world.setBlock(pos, state.setValue(getRoastingProperty(), true), Block.UPDATE_ALL);
            }
        } else {
            roastingTime = 0;
            if (state.getValue(getRoastingProperty())) {
                world.setBlock(pos, state.setValue(getRoastingProperty(), false), Block.UPDATE_ALL);
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

    @Nullable
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        ownerUuid = player.getUUID();
        return new RoasterGuiHandler(syncId, inv, this, delegate);
    }

    private RoasterRecipe getRecipe(List<RecipeHolder<RoasterRecipe>> recipes, NonNullList<ItemStack> inventory) {
        for (RecipeHolder<RoasterRecipe> recipeHolder : recipes) {
            RoasterRecipe recipe = recipeHolder.value();
            if (matchesInventory(recipe, inventory)) {
                return recipe;
            }
        }
        return null;
    }

    private boolean matchesInventory(RoasterRecipe recipe, NonNullList<ItemStack> inventory) {
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
}

package net.satisfy.farm_and_charm.block.entity;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.block.StoveBlock;
import net.satisfy.farm_and_charm.client.gui.handler.StoveGuiHandler;
import net.satisfy.farm_and_charm.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.world.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class StoveBlockEntity extends BlockEntity implements BlockEntityTicker<StoveBlockEntity>, ImplementedInventory, MenuProvider {

    public static final int TOTAL_COOKING_TIME = 240;
    protected static final int FUEL_SLOT = StoveGuiHandler.FUEL_SLOT;
    protected static final int[] INGREDIENT_SLOTS = {1, 2, 3};
    protected static final int OUTPUT_SLOT = StoveGuiHandler.OUTPUT_SLOT;
    protected int burnTime;
    protected int burnTimeTotal;
    protected int cookTime;
    protected int cookTimeTotal;
    private final ContainerData propertyDelegate = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> StoveBlockEntity.this.burnTime;
                case 1 -> StoveBlockEntity.this.burnTimeTotal;
                case 2 -> StoveBlockEntity.this.cookTime;
                case 3 -> StoveBlockEntity.this.cookTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> StoveBlockEntity.this.burnTime = value;
                case 1 -> StoveBlockEntity.this.burnTimeTotal = value;
                case 2 -> StoveBlockEntity.this.cookTime = value;
                case 3 -> StoveBlockEntity.this.cookTimeTotal = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    protected float experience;
    private NonNullList<ItemStack> inventory;

    public StoveBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.STOVE_BLOCK_ENTITY.get(), pos, state);
        this.inventory = NonNullList.withSize(5, ItemStack.EMPTY);
    }

    public int[] getIngredientSlots() {
        return INGREDIENT_SLOTS;
    }

    public int getOutputSlot() {
        return OUTPUT_SLOT;
    }

    public void dropExperience(ServerLevel world, Vec3 pos) {
        ExperienceOrb.award(world, pos, (int) experience);
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        if (side.equals(Direction.UP)) {
            return INGREDIENT_SLOTS;
        } else if (side.equals(Direction.DOWN)) {
            return new int[]{OUTPUT_SLOT};
        } else return new int[]{FUEL_SLOT};
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, this.inventory, provider);
        this.burnTime = compoundTag.getShort("BurnTime");
        this.cookTime = compoundTag.getShort("CookTime");
        this.cookTimeTotal = compoundTag.getShort("CookTimeTotal");
        this.burnTimeTotal = this.getTotalBurnTime(this.getItem(FUEL_SLOT));
        this.experience = compoundTag.getFloat("Experience");

    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        compoundTag.putShort("BurnTime", (short) this.burnTime);
        compoundTag.putShort("CookTime", (short) this.cookTime);
        compoundTag.putShort("CookTimeTotal", (short) this.cookTimeTotal);
        compoundTag.putFloat("Experience", this.experience);
        ContainerHelper.saveAllItems(compoundTag, this.inventory, provider);
    }

    protected boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public void tick(Level world, BlockPos pos, BlockState state, StoveBlockEntity blockEntity) {
        if (world.isClientSide) {
            return;
        }
        boolean initialBurningState = blockEntity.isBurning();
        boolean dirty = false;
        if (initialBurningState) {
            --this.burnTime;
        }

        assert level != null;

        RecipeManager recipeManager = world.getRecipeManager();
        List<RecipeHolder<StoveRecipe>> recipes = recipeManager.getAllRecipesFor(RecipeTypeRegistry.STOVE_RECIPE_TYPE.get());
        Optional<StoveRecipe> recipe = Optional.ofNullable(getRecipe(recipes, inventory));

        RegistryAccess access = level.registryAccess();
        if (!initialBurningState && recipe.isPresent() && canCraft(recipe.get(), access)) {
            this.burnTime = this.burnTimeTotal = this.getTotalBurnTime(this.getItem(FUEL_SLOT));
            if (burnTime > 0) {
                dirty = true;
                final ItemStack fuelStack = this.getItem(FUEL_SLOT);
                if (fuelStack.getItem().hasCraftingRemainingItem()) {
                    setItem(FUEL_SLOT, new ItemStack(Objects.requireNonNull(fuelStack.getItem().getCraftingRemainingItem())));
                } else if (fuelStack.getCount() > 1) {
                    removeItem(FUEL_SLOT, 1);
                } else if (fuelStack.getCount() == 1) {
                    setItem(FUEL_SLOT, ItemStack.EMPTY);
                }
            }
        }
        if ((isBurning() || initialBurningState) && recipe.isPresent() && canCraft(recipe.get(), access)) {
            ++this.cookTime;
            if (this.cookTime == cookTimeTotal) {
                this.cookTime = 0;
                craft(recipe.get(), access);
                dirty = true;
            }
        } else if (recipe.isPresent() && !canCraft(recipe.get(), access)) {
            this.cookTime = 0;
        }
        if (initialBurningState != isBurning()) {
            if (state.getValue(StoveBlock.LIT) != (burnTime > 0)) {
                world.setBlock(pos, state.setValue(StoveBlock.LIT, burnTime > 0), Block.UPDATE_ALL);
                dirty = true;
            }
        }
        if (dirty) {
            setChanged();
        }

    }

    protected boolean canCraft(StoveRecipe recipe, RegistryAccess access) {
        if (recipe == null || recipe.getResultItem(access).isEmpty()) {
            return false;
        }
        for (Ingredient ingredient : recipe.getIngredients()) {
            boolean found = false;
            for (int slot : INGREDIENT_SLOTS) {
                if (ingredient.test(this.getItem(slot))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        final ItemStack recipeOutput = recipe.getResultItem(access);
        final ItemStack outputSlotStack = this.getItem(OUTPUT_SLOT);
        if (outputSlotStack.isEmpty()) {
            return true;
        } else if (!ItemStack.isSameItemSameComponents(outputSlotStack, recipeOutput)) {
            return false;
        } else {
            return outputSlotStack.getCount() + recipeOutput.getCount() <= outputSlotStack.getMaxStackSize();
        }
    }

    protected void craft(StoveRecipe recipe, RegistryAccess access) {
        if (recipe == null || !canCraft(recipe, access)) {
            return;
        }
        final ItemStack recipeOutput = recipe.getResultItem(access).copy();
        final ItemStack outputSlotStack = this.getItem(OUTPUT_SLOT);
        if (outputSlotStack.isEmpty()) {
            setItem(OUTPUT_SLOT, recipeOutput);
        } else if (ItemStack.isSameItemSameComponents(outputSlotStack, recipeOutput)) {
            outputSlotStack.grow(recipeOutput.getCount());
        }

        for (int slot : INGREDIENT_SLOTS) {
            ItemStack stackInSlot = this.getItem(slot);
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (ingredient.test(stackInSlot)) {
                    ItemStack remainderStack = getRemainderItem(stackInSlot);
                    stackInSlot.shrink(1);
                    if (!remainderStack.isEmpty()) {
                        if (stackInSlot.isEmpty()) {
                            setItem(slot, remainderStack);
                        } else {
                            boolean added = false;
                            for (int i : INGREDIENT_SLOTS) {
                                ItemStack is = this.getItem(i);
                                if (is.isEmpty()) {
                                    this.setItem(i, remainderStack.copy());
                                    added = true;
                                    break;
                                } else if (ItemStack.isSameItemSameComponents(is, remainderStack) && is.getCount() < is.getMaxStackSize()) {
                                    is.grow(1);
                                    added = true;
                                    break;
                                }
                            }
                            if (!added) {
                                assert this.level != null;
                                Block.popResource(this.level, this.worldPosition, remainderStack);
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    protected int getTotalBurnTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            Map<Item, Integer> fuelBurnTimes = new HashMap<>();
            fuelBurnTimes.put(Items.COAL, 1600);
            fuelBurnTimes.put(Items.CHARCOAL, 1600);
            fuelBurnTimes.put(Items.LAVA_BUCKET, 20000);
            fuelBurnTimes.put(Items.BLAZE_ROD, 2400);
            return fuelBurnTimes.getOrDefault(item, 0);
        }
    }

    private ItemStack getRemainderItem(ItemStack stack) {
        if (stack.getItem().hasCraftingRemainingItem()) {
            return new ItemStack(Objects.requireNonNull(stack.getItem().getCraftingRemainingItem()));
        }
        return ItemStack.EMPTY;
    }


    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }


    @Override
    public void setItem(int slot, ItemStack stack) {
        final ItemStack stackInSlot = this.inventory.get(slot);
        boolean dirty = !stack.isEmpty() && ItemStack.isSameItem(stack, stackInSlot) && ItemStack.matches(stack, stackInSlot);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        boolean hasIngredientChange = false;
        for (int ingredientSlot : INGREDIENT_SLOTS) {
            if (!ItemStack.isSameItemSameComponents(this.getItem(ingredientSlot), stackInSlot)) {
                hasIngredientChange = true;
                break;
            }
        }
        if (hasIngredientChange && !dirty) {
            this.cookTimeTotal = TOTAL_COOKING_TIME;
            this.cookTime = 0;
            this.setChanged();
        }
    }

    @Override
    public boolean stillValid(Player player) {
        assert this.level != null;
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double) this.worldPosition.getX() + 0.5, (double) this.worldPosition.getY() + 0.5, (double) this.worldPosition.getZ() + 0.5) <= 64.0;
        }
    }


    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
        return new StoveGuiHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null)
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag, provider);
        return compoundTag;
    }

    private StoveRecipe getRecipe(List<RecipeHolder<StoveRecipe>> recipes, NonNullList<ItemStack> inventory) {
        recipeLoop:
        for (RecipeHolder<StoveRecipe> recipeHolder : recipes) {
            StoveRecipe recipe = recipeHolder.value();
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
}
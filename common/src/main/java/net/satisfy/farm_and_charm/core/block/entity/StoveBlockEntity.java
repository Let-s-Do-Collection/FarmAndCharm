package net.satisfy.farm_and_charm.core.block.entity;

import com.mojang.datafixers.util.Pair;
import dev.architectury.registry.fuel.FuelRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.client.gui.handler.StoveGuiHandler;
import net.satisfy.farm_and_charm.core.block.StoveBlock;
import net.satisfy.farm_and_charm.core.item.food.EffectFood;
import net.satisfy.farm_and_charm.core.item.food.EffectFoodBlockItem;
import net.satisfy.farm_and_charm.core.item.food.EffectFoodHelper;
import net.satisfy.farm_and_charm.core.recipe.RecipeUnlockManager;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.world.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class StoveBlockEntity extends BlockEntity implements BlockEntityTicker<StoveBlockEntity>, ImplementedInventory, MenuProvider {

    public static final int TOTAL_COOKING_TIME = 240;
    protected static final int[] INGREDIENT_SLOTS = {1, 2, 3};
    protected int burnTime;
    protected int burnTimeTotal;
    protected int cookTime;
    protected int cookTimeTotal;
    private UUID ownerUuid;
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
        return 0;
    }

    public void dropExperience(ServerLevel world, Vec3 pos) {
        ExperienceOrb.award(world, pos, (int) experience);
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        if (side.equals(Direction.UP)) {
            return INGREDIENT_SLOTS;
        } else if (side.equals(Direction.DOWN)) {
            return new int[]{0};
        } else {
            return new int[]{4};
        }
    }

    @Override
    protected void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);
        this.inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compoundTag, this.inventory, provider);
        this.burnTime = compoundTag.getShort("BurnTime");
        this.cookTime = compoundTag.getShort("CookTime");
        this.cookTimeTotal = compoundTag.getShort("CookTimeTotal");
        this.burnTimeTotal = this.getTotalBurnTime(this.getItem(4));
        this.experience = compoundTag.getFloat("Experience");
        if (compoundTag.hasUUID("Owner")) {
            this.ownerUuid = compoundTag.getUUID("Owner");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);
        compoundTag.putShort("BurnTime", (short) this.burnTime);
        compoundTag.putShort("CookTime", (short) this.cookTime);
        compoundTag.putShort("CookTimeTotal", (short) this.cookTimeTotal);
        compoundTag.putFloat("Experience", this.experience);
        if (this.ownerUuid != null) {
            compoundTag.putUUID("Owner", this.ownerUuid);
        }
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
        if (recipe.isPresent() && recipe.get().requiresLearning()) {
            ServerPlayer owner = Objects.requireNonNull(world.getServer()).getPlayerList().getPlayer(ownerUuid);
            if (owner == null || RecipeUnlockManager.isRecipeLocked(owner, BuiltInRegistries.RECIPE_TYPE.getKey(recipe.get().getType()))) {
                this.cookTime = 0;
                if (state.getValue(StoveBlock.LIT)) {
                    world.setBlock(pos, state.setValue(StoveBlock.LIT, false), Block.UPDATE_ALL);
                    setChanged();
                }
                return;
            }
        }
        if (!initialBurningState && recipe.isPresent() && canCraft(recipe.get(), access)) {
            this.burnTime = this.burnTimeTotal = this.getTotalBurnTime(this.getItem(4));
            if (burnTime > 0) {
                dirty = true;
                ItemStack fuelStack = this.getItem(4);
                if (fuelStack.getItem().hasCraftingRemainingItem()) {
                    setItem(4, new ItemStack(Objects.requireNonNull(fuelStack.getItem().getCraftingRemainingItem())));
                } else if (fuelStack.getCount() > 1) {
                    removeItem(4, 1);
                } else if (fuelStack.getCount() == 1) {
                    setItem(4, ItemStack.EMPTY);
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

        if (state.getValue(StoveBlock.LIT) != isBurning()) {
            world.setBlock(pos, state.setValue(StoveBlock.LIT, isBurning()), Block.UPDATE_ALL);
            dirty = true;
        }

        if (dirty) {
            setChanged();
        }

    }

    protected boolean canCraft(StoveRecipe recipe, RegistryAccess access) {
        if (recipe == null || recipe.getResultItem(access).isEmpty()) return false;
        if (!matchesInventory(recipe, this.inventory)) return false;
        ItemStack expected = generateOutputItem(recipe, access);
        ItemStack output = this.getItem(0);
        if (output.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(output, expected)) return false;
        return output.getCount() + expected.getCount() <= output.getMaxStackSize();
    }

    private StoveRecipe getRecipe(List<RecipeHolder<StoveRecipe>> recipes, NonNullList<ItemStack> inventory) {
        for (RecipeHolder<StoveRecipe> holder : recipes) {
            StoveRecipe r = holder.value();
            if (matchesInventory(r, inventory)) {
                return r;
            }
        }
        return null;
    }

    private boolean matchesInventory(StoveRecipe recipe, NonNullList<ItemStack> inventory) {
        NonNullList<ItemStack> invCopy = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);
        for (int i : INGREDIENT_SLOTS) {
            invCopy.set(i, inventory.get(i).copy());
        }
        for (Ingredient ingredient : recipe.getIngredients()) {
            boolean matched = false;
            for (int i : INGREDIENT_SLOTS) {
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
        for (int i : INGREDIENT_SLOTS) {
            ItemStack remaining = invCopy.get(i);
            if (!remaining.isEmpty()) {
                boolean stillIngredient = false;
                for (Ingredient ing : recipe.getIngredients()) {
                    if (ing.test(remaining)) {
                        stillIngredient = true;
                        break;
                    }
                }
                if (!stillIngredient) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void craft(StoveRecipe recipe, RegistryAccess access) {
        if (recipe == null || !canCraft(recipe, access)) return;

        ItemStack recipeOutput = generateOutputItem(recipe, access);
        ItemStack outputSlotStack = this.getItem(0);

        if (outputSlotStack.isEmpty()) {
            setItem(0, recipeOutput);
        } else if (ItemStack.isSameItemSameComponents(outputSlotStack, recipeOutput)) {
            outputSlotStack.grow(recipeOutput.getCount());
        }

        for (Ingredient ingredient : recipe.getIngredients()) {
            for (int slot : INGREDIENT_SLOTS) {
                ItemStack stackInSlot = this.getItem(slot);
                if (stackInSlot.isEmpty()) continue;
                if (!ingredient.test(stackInSlot)) continue;

                ItemStack remainderStack = getRemainderItem(stackInSlot);
                stackInSlot.shrink(1);

                if (!remainderStack.isEmpty()) {
                    if (stackInSlot.isEmpty()) {
                        setItem(slot, remainderStack);
                    } else {
                        if (!tryInsertRemainder(remainderStack)) {
                            if (this.level != null) {
                                Block.popResource(this.level, this.worldPosition, remainderStack);
                            }
                        }
                    }
                }
                break;
            }
        }

        setChanged();
    }

    private boolean tryInsertRemainder(ItemStack remainderStack) {
        if (remainderStack.isEmpty()) return true;

        for (int slot : INGREDIENT_SLOTS) {
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

        ItemStack fuelSlotStack = getItem(4);
        if (fuelSlotStack.isEmpty()) {
            setItem(4, remainderStack);
            return true;
        }
        if (ItemStack.isSameItemSameComponents(fuelSlotStack, remainderStack) && fuelSlotStack.getCount() < fuelSlotStack.getMaxStackSize()) {
            int transferableAmount = Math.min(remainderStack.getCount(), fuelSlotStack.getMaxStackSize() - fuelSlotStack.getCount());
            if (transferableAmount > 0) {
                fuelSlotStack.grow(transferableAmount);
                remainderStack.shrink(transferableAmount);
                setChanged();
                return remainderStack.isEmpty();
            }
        }

        return remainderStack.isEmpty();
    }

    private ItemStack generateOutputItem(StoveRecipe recipe, RegistryAccess access) {
        ItemStack outputStack = recipe.getResultItem(access).copy();
        if (outputStack.getItem() instanceof EffectFood || outputStack.getItem() instanceof EffectFoodBlockItem) {
            List<ItemStack> stacks = new ArrayList<>();
            for (int i : INGREDIENT_SLOTS) {
                ItemStack s = this.getItem(i);
                if (!s.isEmpty()) stacks.add(s);
            }
            for (MobEffectInstance inst : EffectFoodHelper.collectMergedSortedEffects(stacks)) {
                EffectFoodHelper.addEffect(outputStack, new Pair<>(inst, 1.0f));
            }
        }
        return outputStack;
    }

    protected int getTotalBurnTime(ItemStack fuel) {
        if (fuel.isEmpty()) return 0;
        return FuelRegistry.get(fuel);
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

    public void setOwner(UUID uuid) {
        this.ownerUuid = uuid;
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
}

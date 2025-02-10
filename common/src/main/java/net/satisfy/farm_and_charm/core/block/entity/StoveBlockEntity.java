package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.client.gui.handler.StoveGuiHandler;
import net.satisfy.farm_and_charm.core.block.StoveBlock;
import net.satisfy.farm_and_charm.core.item.food.EffectFood;
import net.satisfy.farm_and_charm.core.item.food.EffectFoodHelper;
import net.satisfy.farm_and_charm.core.item.food.EffectBlockItem;
import net.satisfy.farm_and_charm.core.item.food.EffectFoodBlockItem;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.core.recipe.RecipeUnlockManager;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.world.ImplementedInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class StoveBlockEntity extends BlockEntity implements BlockEntityTicker<StoveBlockEntity>, ImplementedInventory, MenuProvider {
    public static final int TOTAL_COOKING_TIME = 240;
    protected static final int[] INGREDIENT_SLOTS = {1, 2, 3};
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
    private UUID ownerUuid;

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
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.inventory = NonNullList.withSize(5, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, this.inventory);
        this.burnTime = nbt.getShort("BurnTime");
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
        this.burnTimeTotal = this.getTotalBurnTime(this.getItem(4));
        this.experience = nbt.getFloat("Experience");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putShort("BurnTime", (short) this.burnTime);
        nbt.putShort("CookTime", (short) this.cookTime);
        nbt.putShort("CookTimeTotal", (short) this.cookTimeTotal);
        nbt.putFloat("Experience", this.experience);
        ContainerHelper.saveAllItems(nbt, this.inventory);
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
        StoveRecipe recipe = world.getRecipeManager().getRecipeFor(RecipeTypeRegistry.STOVE_RECIPE_TYPE.get(), blockEntity, world).orElse(null);
        assert level != null;
        RegistryAccess access = level.registryAccess();
        if (recipe != null && recipe.requiresLearning()) {
            ServerPlayer owner = Objects.requireNonNull(world.getServer()).getPlayerList().getPlayer(ownerUuid);
            if (owner == null || RecipeUnlockManager.isRecipeLocked(owner, recipe.getId())) {
                this.cookTime = 0;
                if (state.getValue(StoveBlock.LIT)) {
                    world.setBlock(pos, state.setValue(StoveBlock.LIT, false), Block.UPDATE_ALL);
                }
                return;
            }
        }
        if (!initialBurningState && canCraft(recipe, access)) {
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
        if ((isBurning() || initialBurningState) && canCraft(recipe, access)) {
            ++this.cookTime;
            if (this.cookTime == cookTimeTotal) {
                this.cookTime = 0;
                craft(recipe, access);
                dirty = true;
            }
        } else if (!canCraft(recipe, access)) {
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
        if (recipe == null || recipe.getResultItem(access).isEmpty()) return false;
        NonNullList<ItemStack> temp = NonNullList.create();
        for (int slot : INGREDIENT_SLOTS) {
            temp.add(this.getItem(slot).copy());
        }
        for (Ingredient ingredient : recipe.getIngredients()) {
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
        ItemStack recipeOutput = recipe.getResultItem(access);
        ItemStack outputSlotStack = this.getItem(0);
        if (outputSlotStack.isEmpty()) return true;
        if (!ItemStack.isSameItemSameTags(outputSlotStack, recipeOutput)) return false;
        return outputSlotStack.getCount() + recipeOutput.getCount() <= outputSlotStack.getMaxStackSize();
    }

    protected void craft(StoveRecipe recipe, RegistryAccess access) {
        if (recipe == null || !canCraft(recipe, access)) return;
        ItemStack recipeOutput = recipe.getResultItem(access).copy();
        ItemStack outputSlotStack = this.getItem(0);
        for (int slot : INGREDIENT_SLOTS) {
            ItemStack ingredientStack = this.getItem(slot);
            if (!ingredientStack.isEmpty() && (ingredientStack.getItem() instanceof EffectFood || ingredientStack.getItem() instanceof EffectBlockItem)) {
                CompoundTag ingredientTag = ingredientStack.getTag();
                if (ingredientTag != null && ingredientTag.contains(EffectFoodHelper.STORED_EFFECTS_KEY)) {
                    CompoundTag resultTag = recipeOutput.getOrCreateTag();
                    resultTag.put(EffectFoodHelper.STORED_EFFECTS_KEY, ingredientTag.get(EffectFoodHelper.STORED_EFFECTS_KEY));
                    recipeOutput.setTag(resultTag);
                }
            }
        }
        if (recipeOutput.getItem() instanceof EffectFood || recipeOutput.getItem() instanceof EffectFoodBlockItem || recipeOutput.getItem() instanceof EffectFoodBlockItem) {
            EffectFoodHelper.applyEffects(recipeOutput);
        }
        if (outputSlotStack.isEmpty()) {
            setItem(0, recipeOutput);
        } else if (ItemStack.isSameItemSameTags(outputSlotStack, recipeOutput)) {
            outputSlotStack.grow(recipeOutput.getCount());
        }
        for (Ingredient ingredient : recipe.getIngredients()) {
            for (int slot : INGREDIENT_SLOTS) {
                ItemStack stackInSlot = this.getItem(slot);
                if (ingredient.test(stackInSlot) && !stackInSlot.isEmpty()) {
                    ItemStack remainderStack = getRemainderItem(stackInSlot);
                    stackInSlot.shrink(1);
                    if (!remainderStack.isEmpty()) {
                        if (this.getItem(slot).isEmpty()) {
                            setItem(slot, remainderStack);
                        } else {
                            boolean added = false;
                            for (int i : INGREDIENT_SLOTS) {
                                ItemStack is = this.getItem(i);
                                if (is.isEmpty()) {
                                    this.setItem(i, remainderStack.copy());
                                    added = true;
                                    break;
                                } else if (ItemStack.isSameItemSameTags(is, remainderStack) && is.getCount() < is.getMaxStackSize()) {
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
            if (!ItemStack.isSameItemSameTags(this.getItem(ingredientSlot), stackInSlot)) {
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
        ownerUuid = player.getUUID();
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
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }
}

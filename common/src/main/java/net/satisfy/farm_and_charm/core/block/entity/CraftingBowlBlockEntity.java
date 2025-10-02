package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.CraftingBowlBlock;
import net.satisfy.farm_and_charm.core.recipe.CraftingBowlRecipe;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class CraftingBowlBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer, BlockEntityTicker<CraftingBowlBlockEntity> {
    private NonNullList<ItemStack> stacks = NonNullList.withSize(5, ItemStack.EMPTY);
    private float whiskAngle;
    private float whiskAnglePrev;
    private float whiskSpeed;
    private float whiskTargetSpeed;

    public CraftingBowlBlockEntity(BlockPos position, BlockState state) {
        super(EntityTypeRegistry.CRAFTING_BOWL_BLOCK_ENTITY.get(), position, state);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (!this.tryLoadLootTable(tag)) this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.stacks, provider);
        this.whiskAngle = tag.getFloat("WhiskAngle");
        this.whiskSpeed = tag.getFloat("WhiskSpeed");
        this.whiskTargetSpeed = tag.getFloat("WhiskTargetSpeed");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!this.trySaveLootTable(tag)) ContainerHelper.saveAllItems(tag, this.stacks, provider);
        tag.putFloat("WhiskAngle", this.whiskAngle);
        tag.putFloat("WhiskSpeed", this.whiskSpeed);
        tag.putFloat("WhiskTargetSpeed", this.whiskTargetSpeed);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public int getContainerSize() {
        return stacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.stacks) if (!itemstack.isEmpty()) return false;
        return true;
    }

    @Override
    public @NotNull Component getDefaultName() {
        return Component.literal("crafting_bowl");
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return ChestMenu.threeRows(id, inventory);
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return index >= 0 && index < 4;
    }

    @Override
    public @NotNull NonNullList<ItemStack> getItems() {
        return this.stacks;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public boolean canAddItem() {
        for (int i = 0; i < 4; i++) if (this.getItem(i).isEmpty()) return true;
        return false;
    }

    public void addItemStack(ItemStack stack) {
        for (int j = 0; j < 4; ++j) {
            if (this.getItem(j).isEmpty()) {
                ItemStack one = stack.copy();
                one.setCount(1);
                this.setItem(j, one);
                setChanged();
                return;
            }
        }
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        return IntStream.range(0, this.getContainerSize()).toArray();
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return true;
    }

    private ItemStack getRemainderItem(ItemStack stack) {
        if (stack.getItem().hasCraftingRemainingItem()) {
            return new ItemStack(Objects.requireNonNull(stack.getItem().getCraftingRemainingItem()));
        }
        return ItemStack.EMPTY;
    }

    public int getStirringProgress() {
        return this.getBlockState().getValue(CraftingBowlBlock.STIRRED);
    }

    public Optional<CraftingBowlRecipe> findRecipe(Level level) {
        if (!this.getItem(4).isEmpty()) return Optional.empty();
        List<RecipeHolder<CraftingBowlRecipe>> all = level.getRecipeManager().getAllRecipesFor(RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.get());
        return Optional.ofNullable(matchExact(all));
    }

    private CraftingBowlRecipe matchExact(List<RecipeHolder<CraftingBowlRecipe>> recipes) {
        ItemStack[] inputs = new ItemStack[4];
        int present = 0;
        for (int i = 0; i < 4; i++) {
            inputs[i] = this.getItem(i);
            if (!inputs[i].isEmpty()) present++;
        }
        for (RecipeHolder<CraftingBowlRecipe> holder : recipes) {
            CraftingBowlRecipe r = holder.value();
            int needed = 0;
            for (Ingredient ing : r.getIngredients()) if (!ing.isEmpty()) needed++;
            if (present != needed) continue;
            boolean[] used = new boolean[4];
            boolean ok = true;
            for (Ingredient ing : r.getIngredients()) {
                if (ing.isEmpty()) continue;
                boolean matched = false;
                for (int i = 0; i < 4; i++) {
                    if (!used[i] && !inputs[i].isEmpty() && ing.test(inputs[i])) {
                        used[i] = true;
                        matched = true;
                        break;
                    }
                }
                if (!matched) { ok = false; break; }
            }
            if (ok) return r;
        }
        return null;
    }

    public float getInterpolatedWhiskAngle(float partial) {
        float a0 = this.whiskAnglePrev;
        float a1 = this.whiskAngle;
        float da = a1 - a0;
        float tau = (float) (Math.PI * 2D);
        if (da > Math.PI) da -= tau;
        if (da < -Math.PI) da += tau;
        return a0 + da * partial;
    }

    public void addWhiskImpulse(float v) {
        this.whiskTargetSpeed = Math.min(0.5F, this.whiskTargetSpeed + v);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state, CraftingBowlBlockEntity be) {
        this.whiskAnglePrev = this.whiskAngle;
        int stirring = state.getValue(CraftingBowlBlock.STIRRING);
        if (stirring > 0) {
            this.whiskTargetSpeed = 0.5F;
        } else {
            this.whiskTargetSpeed = 0F;
        }
        float k = 0.22F;
        this.whiskSpeed += (this.whiskTargetSpeed - this.whiskSpeed) * k;
        if (stirring == 0) this.whiskSpeed *= 0.96F;
        this.whiskAngle += this.whiskSpeed;
        float tau = (float) (Math.PI * 2D);
        if (this.whiskAngle > tau) this.whiskAngle -= tau;
        if (this.whiskAngle < 0F) this.whiskAngle += tau;

        if (!level.isClientSide && state.getBlock() instanceof CraftingBowlBlock) {
            int stirred = state.getValue(CraftingBowlBlock.STIRRED);
            if (stirring > 0) {
                Optional<CraftingBowlRecipe> recipe = be.findRecipe(level);
                if (recipe.isPresent() && stirred < CraftingBowlBlock.STIRS_NEEDED) {
                    stirred++;
                    if (stirred == CraftingBowlBlock.STIRS_NEEDED) {
                        NonNullList<Ingredient> ings = recipe.get().getIngredients();
                        boolean[] used = new boolean[4];
                        for (Ingredient ing : ings) {
                            if (ing.isEmpty()) continue;
                            for (int i = 0; i < 4; i++) {
                                if (!used[i] && ing.test(be.getItem(i))) {
                                    ItemStack stack = be.getItem(i);
                                    ItemStack remainder = getRemainderItem(stack);
                                    stack.shrink(1);
                                    if (stack.isEmpty()) be.setItem(i, ItemStack.EMPTY);
                                    if (!remainder.isEmpty()) {
                                        double ox = level.random.nextDouble() * 0.7D + 0.15D;
                                        double oy = level.random.nextDouble() * 0.7D + 0.15D;
                                        double oz = level.random.nextDouble() * 0.7D + 0.15D;
                                        level.addFreshEntity(new ItemEntity(level, pos.getX() + ox, pos.getY() + oy, pos.getZ() + oz, remainder));
                                    }
                                    used[i] = true;
                                    break;
                                }
                            }
                        }
                        ItemStack resultItem = recipe.get().getResultItem(level.registryAccess()).copy();
                        resultItem.setCount(recipe.get().getOutputCount());
                        be.setItem(4, resultItem);
                    }
                }
                stirring -= 1;
                level.setBlock(pos, state.setValue(CraftingBowlBlock.STIRRING, stirring).setValue(CraftingBowlBlock.STIRRED, stirred), 3);
            } else {
                int stirredNow = state.getValue(CraftingBowlBlock.STIRRED);
                if (stirredNow > 0 && stirredNow < CraftingBowlBlock.STIRS_NEEDED) {
                    level.setBlock(pos, state.setValue(CraftingBowlBlock.STIRRED, 0), 3);
                }
            }
            if (level.getGameTime() % 5L == 0L) setChanged();
        }
    }
}

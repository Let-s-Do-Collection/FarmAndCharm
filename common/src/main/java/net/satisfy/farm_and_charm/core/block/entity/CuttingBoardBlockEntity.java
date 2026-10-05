package net.satisfy.farm_and_charm.core.block.entity;

import net.satisfy.farm_and_charm.core.util.StoredExperience;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardRecipe;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.item.CleaverItem;
import net.satisfy.farm_and_charm.core.util.Strippables;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CuttingBoardBlockEntity extends BlockEntity {
    public static final int MAX_ITEMS = 5;

    private static final String ITEMS_KEY = "Items";
    private static final String CHOPS_KEY = "Chops";
    private static final String KNIFE_KEY = "Knife";
    private static final String PENDING_KEY = "Pending";
    private static final String CUT_START_KEY = "CutStart";
    private static final String PENDING_CHOPS_KEY = "PendingChops";
    public static final int CHOP_ANIMATION_TICKS = 4;
    private static final float STRIP_EXPERIENCE = 0.05F;
    private static final TagKey<Item> MEAT_SOUND = TagKey.create(Registries.ITEM, FarmAndCharm.identifier("chop_sounds/meat"));
    private static final TagKey<Item> FISH_SOUND = TagKey.create(Registries.ITEM, FarmAndCharm.identifier("chop_sounds/fish"));
    private static final TagKey<Item> VEGETABLE_SOUND = TagKey.create(Registries.ITEM, FarmAndCharm.identifier("chop_sounds/vegetable"));
    private static final int CRUMBS = 10;

    private static final List<OutputHandler> OUTPUT_HANDLERS = new ArrayList<>();

    private final List<ItemStack> items = new ArrayList<>();
    private int chops;
    private ItemStack knife = ItemStack.EMPTY;
    private ItemStack pending = ItemStack.EMPTY;
    private long cutStart;
    private int pendingChops;
    private final StoredExperience experience = new StoredExperience();

    public CuttingBoardBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.CUTTING_BOARD_BLOCK_ENTITY.get(), pos, state);
    }

    public List<ItemStack> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public boolean isFull() {
        return this.items.size() >= MAX_ITEMS;
    }

    public int getChops() {
        return this.chops;
    }

    public void addItem(ItemStack stack) {
        this.items.add(stack);
        this.chops = 0;
        this.markUpdated();
    }

    public int chop() {
        this.chops++;
        if (this.level != null) {
            this.cutStart = this.level.getGameTime();
        }
        this.markUpdated();
        return this.chops;
    }

    public void setContents(ItemStack stack) {
        this.items.clear();
        if (!stack.isEmpty()) {
            this.items.add(stack);
        }
        this.chops = 0;
        this.markUpdated();
    }

    public ItemStack removeLastItem() {
        if (this.items.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = this.items.removeLast();
        this.chops = 0;
        this.markUpdated();
        return removed;
    }

    public boolean hasKnife() {
        return !this.knife.isEmpty();
    }

    public ItemStack getKnife() {
        return this.knife;
    }

    public void setKnife(ItemStack knife) {
        this.knife = knife;
        this.markUpdated();
    }

    public ItemStack takeKnife() {
        ItemStack removed = this.knife;
        this.knife = ItemStack.EMPTY;
        this.markUpdated();
        return removed;
    }

    public boolean isCutting() {
        return !this.pending.isEmpty();
    }

    public ItemStack getPending() {
        return this.pending;
    }

    public long getCutStart() {
        return this.cutStart;
    }

    public int getPendingChops() {
        return this.pendingChops;
    }

    public void startCut(ItemStack stack) {
        this.pending = stack;
        this.pendingChops = 0;
        this.markUpdated();
    }

    public void chopPending(Level level) {
        Optional<RecipeHolder<CuttingBoardRecipe>> recipe = Strippables.isAxe(this.knife) ? Optional.empty() : level.getRecipeManager().getRecipeFor(RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get(), new SingleRecipeInput(this.pending), level);
        this.cutStart = level.getGameTime();
        this.pendingChops++;
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, this.pending.copyWithCount(1)), this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.15, this.worldPosition.getZ() + 0.5, CRUMBS, 0.15, 0.05, 0.15, 0.08);
        }
        if (Strippables.isAxe(this.knife)) {
            level.playSound(null, this.worldPosition, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 0.6F, 1.3F + level.random.nextFloat() * 0.2F);
        } else {
            playChopSound(level, this.worldPosition, this.pending);
        }
        Optional<ItemStack> stripped = Strippables.isAxe(this.knife) ? Strippables.strip(this.pending) : Optional.empty();
        if (stripped.isPresent()) {
            if (this.pendingChops >= Strippables.CHOPS) {
                output(level, this.worldPosition, stripped.get());
                this.addExperience(level, STRIP_EXPERIENCE);
                this.pending = ItemStack.EMPTY;
                this.pendingChops = 0;
                this.wearKnife(level);
            }
        } else if (recipe.isEmpty()) {
            output(level, this.worldPosition, this.pending);
            this.pending = ItemStack.EMPTY;
        } else if (this.pendingChops >= requiredChops(this.knife, recipe.get().value())) {
            output(level, this.worldPosition, recipe.get().value().assemble(new SingleRecipeInput(this.pending), level.registryAccess()));
            for (ItemStack byproduct : recipe.get().value().getByproducts()) {
                output(level, this.worldPosition, byproduct.copy());
            }
            this.addExperience(level, recipe.get().value().getExperience());
            this.pending = ItemStack.EMPTY;
            this.pendingChops = 0;
            this.wearKnife(level);
        }
        this.markUpdated();
    }

    private void addExperience(Level level, float amount) {
        if (level instanceof ServerLevel serverLevel) {
            this.experience.add(amount);
            this.experience.award(serverLevel, Vec3.atCenterOf(this.worldPosition));
        }
    }

    public static void registerOutputHandler(OutputHandler handler) {
        OUTPUT_HANDLERS.add(handler);
    }

    public static void output(Level level, BlockPos pos, ItemStack stack) {
        ItemStack remaining = stack;
        for (OutputHandler handler : OUTPUT_HANDLERS) {
            remaining = handler.accept(level, pos, remaining);
            if (remaining.isEmpty()) {
                return;
            }
        }
        launch(level, pos, remaining);
    }

    private static void launch(Level level, BlockPos pos, ItemStack stack) {
        if (level.isClientSide || stack.isEmpty()) {
            return;
        }
        RandomSource random = level.random;
        ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, stack);
        float angle = random.nextFloat() * Mth.TWO_PI;
        double spread = 0.08 + random.nextDouble() * 0.06;
        item.setDeltaMovement(Mth.cos(angle) * spread, 0.3 + random.nextDouble() * 0.1, Mth.sin(angle) * spread);
        item.setPickUpDelay(15);
        level.addFreshEntity(item);
    }

    public static void playChopSound(Level level, BlockPos pos, ItemStack stack) {
        float pitch = 0.9F + level.random.nextFloat() * 0.3F;
        level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.35F, 1.4F + level.random.nextFloat() * 0.2F);
        if (stack.is(MEAT_SOUND)) {
            level.playSound(null, pos, SoundEvents.MUD_HIT, SoundSource.BLOCKS, 0.7F, pitch);
        } else if (stack.is(FISH_SOUND)) {
            level.playSound(null, pos, SoundEvents.COD_FLOP, SoundSource.BLOCKS, 0.5F, pitch + 0.2F);
        } else if (stack.is(VEGETABLE_SOUND)) {
            level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 0.6F, pitch + 0.3F);
        } else {
            level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 0.5F, 1.3F + level.random.nextFloat() * 0.2F);
        }
    }

    public static int requiredChops(ItemStack tool, CuttingBoardRecipe recipe) {
        return tool.getItem() instanceof CleaverItem cleaver ? cleaver.getChops() : recipe.getChops();
    }

    private void wearKnife(Level level) {
        if (!this.knife.isDamageableItem()) {
            return;
        }
        this.knife.setDamageValue(this.knife.getDamageValue() + 1);
        if (this.knife.getDamageValue() >= this.knife.getMaxDamage()) {
            this.knife = ItemStack.EMPTY;
            level.playSound(null, this.worldPosition, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.8F, 1.0F);
        }
    }

    public List<ItemStack> removeAllItems() {
        List<ItemStack> removed = new ArrayList<>(this.items);
        if (!this.knife.isEmpty()) {
            removed.add(this.knife);
        }
        if (!this.pending.isEmpty()) {
            removed.add(this.pending);
        }
        this.knife = ItemStack.EMPTY;
        this.pending = ItemStack.EMPTY;
        this.items.clear();
        this.chops = 0;
        this.setChanged();
        return removed;
    }

    private void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        ListTag itemsTag = new ListTag();
        for (ItemStack stack : this.items) {
            itemsTag.add(stack.save(provider));
        }
        tag.put(ITEMS_KEY, itemsTag);
        tag.putInt(CHOPS_KEY, this.chops);
        if (!this.knife.isEmpty()) {
            tag.put(KNIFE_KEY, this.knife.save(provider));
        }
        if (!this.pending.isEmpty()) {
            tag.put(PENDING_KEY, this.pending.save(provider));
        }
        tag.putLong(CUT_START_KEY, this.cutStart);
        tag.putInt(PENDING_CHOPS_KEY, this.pendingChops);
        this.experience.save(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.items.clear();
        for (Tag itemTag : tag.getList(ITEMS_KEY, Tag.TAG_COMPOUND)) {
            ItemStack stack = ItemStack.parseOptional(provider, (CompoundTag) itemTag);
            if (!stack.isEmpty()) {
                this.items.add(stack);
            }
        }
        this.chops = tag.getInt(CHOPS_KEY);
        this.knife = tag.contains(KNIFE_KEY) ? ItemStack.parseOptional(provider, tag.getCompound(KNIFE_KEY)) : ItemStack.EMPTY;
        this.pending = tag.contains(PENDING_KEY) ? ItemStack.parseOptional(provider, tag.getCompound(PENDING_KEY)) : ItemStack.EMPTY;
        this.cutStart = tag.getLong(CUT_START_KEY);
        this.pendingChops = tag.getInt(PENDING_CHOPS_KEY);
        this.experience.load(tag);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @FunctionalInterface
    public interface OutputHandler {
        ItemStack accept(Level level, BlockPos pos, ItemStack stack);
    }
}

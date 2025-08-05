package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.entity.ChickenCoopAccess;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChickenCoopBlockEntity extends BlockEntity {
    private static final int MAX_CHICKENS = 3;
    private static final int MAX_EGGS = 9;
    private final List<CompoundTag> storedChickens = new ArrayList<>();
    private int eggCount = 0;

    public ChickenCoopBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.CHICKEN_COOP_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, ChickenCoopBlockEntity coop) {
        if (level.isClientSide) return;

        Iterator<CompoundTag> iterator = coop.storedChickens.iterator();
        while (iterator.hasNext()) {
            CompoundTag chickenTag = iterator.next();
            int ticks = chickenTag.getInt("CoopTime") - 1;

            if (ticks <= 0) {
                chickenTag.remove("CoopTime");
                Entity chicken = EntityType.CHICKEN.create(level);
                if (chicken instanceof Chicken spawned) {
                    int coopCooldown = 20 * 60 * (4 + level.getRandom().nextInt(2));
                    spawned.load(chickenTag);
                    spawned.setHealth(spawned.getMaxHealth());
                    spawned.setInvisible(false);
                    spawned.setNoAi(false);
                    spawned.setSilent(false);
                    if (spawned instanceof ChickenCoopAccess coopChicken) {
                        coopChicken.farmAndCharm$setCoopCooldown(coopCooldown);
                    }

                    BlockPos spawnPos = findSafeSpawnPosition(level, pos);
                    spawned.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, spawned.getYRot(), spawned.getXRot());
                    level.addFreshEntity(spawned);
                    iterator.remove();
                    coop.setChanged();
                    level.sendBlockUpdated(pos, coop.getBlockState(), coop.getBlockState(), 3);
                }
            } else {
                chickenTag.putInt("CoopTime", ticks);
            }
        }
    }

    private static BlockPos findSafeSpawnPosition(Level level, BlockPos center) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos offset = center.relative(dir);
            if (level.getBlockState(offset).isAir() && level.getBlockState(offset.above()).isAir()) {
                return offset;
            }
        }
        return center.above();
    }

    public boolean hasSpaceForChicken() {
        return storedChickens.size() < MAX_CHICKENS;
    }

    public void addChicken(Chicken chicken) {
        if (this.level == null || this.level.isClientSide) return;
        if (!hasSpaceForChicken()) return;

        CompoundTag tag = new CompoundTag();
        chicken.save(tag);
        tag.remove("Leash");
        tag.putInt("CoopTime", 200 + chicken.getRandom().nextInt(200));
        storedChickens.add(tag);

        if (chicken.getLeashHolder() != null) {
            chicken.dropLeash(true, false);
            chicken.spawnAtLocation(Items.LEAD);
        }

        chicken.stopRiding();
        chicken.ejectPassengers();
        chicken.setNoAi(true);
        chicken.setSilent(true);
        chicken.setInvisible(true);
        chicken.discard();

        addEgg();
        this.setChanged();
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
    }

    public void releaseAllChickens() {
        if (this.level == null || this.level.isClientSide) return;

        for (CompoundTag tag : new ArrayList<>(storedChickens)) {
            tag.remove("CoopTime");
            Entity chicken = EntityType.CHICKEN.create(level);
            if (chicken instanceof Chicken spawned) {
                int coopCooldown = 20 * 60 * (4 + level.getRandom().nextInt(2));
                spawned.load(tag);
                spawned.setHealth(spawned.getMaxHealth());
                spawned.setInvisible(false);
                spawned.setNoAi(false);
                spawned.setSilent(false);
                if (spawned instanceof ChickenCoopAccess coopChicken) {
                    coopChicken.farmAndCharm$setCoopCooldown(coopCooldown);
                }

                BlockPos spawnPos = findSafeSpawnPosition(level, worldPosition);
                spawned.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, spawned.getYRot(), spawned.getXRot());
                level.addFreshEntity(spawned);
            }
        }

        storedChickens.clear();
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean containsChicken(Chicken chicken) {
        for (CompoundTag tag : storedChickens) {
            if (tag.getUUID("UUID").equals(chicken.getUUID())) return true;
        }
        return false;
    }

    public void addEgg() {
        if (eggCount < MAX_EGGS) {
            eggCount++;
            setChanged();
        }
    }

    public int getEggCount() {
        return eggCount;
    }

    public void clearEggs() {
        eggCount = 0;
        setChanged();
    }

    public List<CompoundTag> getStoredChickens() {
        return this.storedChickens;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        ListTag chickenList = new ListTag();
        for (CompoundTag chickenTag : storedChickens) {
            chickenList.add(chickenTag.copy());
        }
        tag.put("Chickens", chickenList);
        tag.putInt("EggCount", eggCount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        storedChickens.clear();
        ListTag chickenList = tag.getList("Chickens", 10);
        for (int i = 0; i < chickenList.size(); i++) {
            CompoundTag chickenTag = chickenList.getCompound(i);
            storedChickens.add(chickenTag);
        }
        eggCount = tag.getInt("EggCount");
    }
}

package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
                Entity chicken = EntityType.CHICKEN.create((ServerLevel) level);
                if (chicken != null) {
                    chicken.load(chickenTag);
                    chicken.moveTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, chicken.getYRot(), chicken.getXRot());
                    level.addFreshEntity(chicken);
                }
                iterator.remove();
            } else {
                chickenTag.putInt("CoopTime", ticks);
            }
        }
    }

    public boolean hasSpaceForChicken() {
        return storedChickens.size() < MAX_CHICKENS;
    }

    public void addChicken(Chicken chicken) {
        if (hasSpaceForChicken()) {
            CompoundTag tag = new CompoundTag();
            chicken.save(tag);
            tag.putInt("CoopTime", 200);
            storedChickens.add(tag);
            chicken.discard();
            addEgg();
        }
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
        }
    }

    public int getEggCount() {
        return eggCount;
    }

    public void addEggCount(int count) {
        eggCount = Math.min(eggCount + count, MAX_EGGS);
    }

    public void clearEggs() {
        eggCount = 0;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag chickenList = new ListTag();
        for (CompoundTag chickenTag : storedChickens) {
            chickenList.add(chickenTag.copy());
        }
        tag.put("Chickens", chickenList);
        tag.putInt("EggCount", eggCount);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        storedChickens.clear();
        ListTag chickenList = tag.getList("Chickens", 10);
        for (int i = 0; i < chickenList.size(); i++) {
            CompoundTag chickenTag = chickenList.getCompound(i);
            storedChickens.add(chickenTag);
        }
        eggCount = tag.getInt("EggCount");
    }
}

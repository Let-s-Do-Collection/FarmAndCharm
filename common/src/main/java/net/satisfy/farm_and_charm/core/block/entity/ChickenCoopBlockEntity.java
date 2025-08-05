package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ChickenCoopBlockEntity extends BlockEntity {
    private static final int MAX_CHICKENS = 3;
    private static final int STAY_TICKS = 200;
    private static final int MAX_EGGS = 9;

    private final Map<UUID, Integer> chickensInside = new HashMap<>();
    private int eggCount = 0;

    public ChickenCoopBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.CHICKEN_COOP_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, ChickenCoopBlockEntity coop) {
        if (level.isClientSide) return;

        Iterator<Map.Entry<UUID, Integer>> iterator = coop.chickensInside.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            int time = entry.getValue() - 1;

            if (time <= 0) {
                UUID chickenId = entry.getKey();
                Entity entity = ((ServerLevel) level).getEntity(chickenId);

                if (entity instanceof Chicken chicken) {
                    coop.removeChicken(chicken);
                    chicken.moveTo(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, level.random.nextFloat() * 360F, 0);
                    level.addFreshEntity(chicken);
                    coop.addEgg();
                }

                iterator.remove();
            } else {
                entry.setValue(time);
            }
        }
    }


    public boolean hasSpaceForChicken() {
        return chickensInside.size() < MAX_CHICKENS;
    }

    public void addChicken(Chicken chicken) {
        if (hasSpaceForChicken()) {
            chicken.discard();
            chickensInside.put(chicken.getUUID(), STAY_TICKS);
            addEgg();
        }
    }

    public void removeChicken(Chicken chicken) {
        chickensInside.remove(chicken.getUUID());
    }

    public boolean containsChicken(Chicken chicken) {
        return chickensInside.containsKey(chicken.getUUID());
    }

    public void addEgg() {
        if (eggCount < MAX_EGGS) {
            eggCount++;
        }
    }

    public int getEggCount() {
        return eggCount;
    }

    public void clearEggs() {
        eggCount = 0;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        ListTag chickenList = new ListTag();
        for (Map.Entry<UUID, Integer> entry : chickensInside.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("Id", entry.getKey());
            entryTag.putInt("Time", entry.getValue());
            chickenList.add(entryTag);
        }
        tag.put("ChickensInside", chickenList);
        tag.putInt("EggCount", eggCount);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        chickensInside.clear();
        ListTag chickenList = tag.getList("ChickensInside", 10);
        for (int i = 0; i < chickenList.size(); i++) {
            CompoundTag entryTag = chickenList.getCompound(i);
            UUID id = entryTag.getUUID("Id");
            int time = entryTag.getInt("Time");
            chickensInside.put(id, time);
        }
        eggCount = tag.getInt("EggCount");
    }
}
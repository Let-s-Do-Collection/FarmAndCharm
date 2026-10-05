package net.satisfy.farm_and_charm.core.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.phys.Vec3;

public class StoredExperience {
    private static final String KEY = "Experience";
    private float amount;

    public void add(float experience) {
        if (experience > 0) {
            amount += experience;
        }
    }

    public void award(ServerLevel level, Vec3 pos) {
        int whole = Mth.floor(amount);
        if (whole > 0) {
            ExperienceOrb.award(level, pos, whole);
            amount -= whole;
        }
    }

    public void save(CompoundTag tag) {
        tag.putFloat(KEY, amount);
    }

    public void load(CompoundTag tag) {
        amount = tag.getFloat(KEY);
    }
}

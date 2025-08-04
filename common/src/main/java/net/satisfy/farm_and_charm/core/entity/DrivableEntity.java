package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class DrivableEntity extends Entity {
    public static final String DRIVER_TAG = "Driver";
    @Nullable
    private Entity driver;

    protected DrivableEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public boolean hasDriver() {
        return this.driver != null;
    }

    public final @Nullable Entity getDriver() {
        return this.driver;
    }

    public boolean addDriver(Entity entity) {
        if (entity instanceof Player) {
            List<Entity> entities = this.level().getEntitiesOfClass(Entity.class, entity.getBoundingBox().inflate(100)); // angenommene Methode mit Bereich
            for (Entity ent : entities) {
                if (ent instanceof DrivableEntity drivable) {
                    if (drivable.hasDriver()) {
                        assert drivable.getDriver() != null;
                        if (drivable.getDriver().equals(entity)) {
                            return false;
                        }
                    }
                }
            }
        }

        if (!this.hasDriver() && this.canAddDriver()) {
            this.driver = entity;
            return true;
        }
        return false;
    }

    protected void removeDriver() {
        this.driver = null;
    }

    public boolean canAddDriver() {
        return !this.hasDriver();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.hasDriver()) {
            assert this.getDriver() != null;
            compoundTag.putInt(DRIVER_TAG, this.getDriver().getId());
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains(DRIVER_TAG)) {
            this.driver = this.level().getEntity(compoundTag.getInt(DRIVER_TAG));
        }
    }
}

package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import net.satisfy.farm_and_charm.core.util.CartWheel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractTowableEntity extends Entity {
    public static final String DRIVER = "Driver";
    @Nullable
    public Entity driver;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;
    private int soundCooldownTicks = 0;
    final CartWheel leftWheel;
    final CartWheel rightWheel;
    private float health = 10.0F;
    private int wobbleTicks = 0;
    private float wobbleDirection = 0.0F;

    public AbstractTowableEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.leftWheel = new CartWheel(this, -1.5F);
        this.rightWheel = new CartWheel(this, 1.5F);
    }

    public boolean hasDriver() {
        return this.driver != null;
    }

    public @Nullable Entity getDriver() {
        return this.driver;
    }

    protected void removeDriver() {
        this.driver = null;
    }

    public boolean canAddDriver() {
        return !this.hasDriver();
    }

    public boolean addDriver(Entity entity) {
        if (entity instanceof Player) {
            List<Entity> entities = this.level().getEntitiesOfClass(Entity.class, entity.getBoundingBox().inflate(100));
            for (Entity ent : entities) {
                if (ent instanceof DrivableEntity drivable) {
                    if (drivable.hasDriver()) {
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

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isRemoved();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        if (this.hasDriver()) {
            compoundTag.putInt(DRIVER, this.getDriver().getId());
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains(DRIVER)) {
            this.driver = this.level().getEntity(compoundTag.getInt(DRIVER));
        }
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand interactionHand) {
        if (this.hasDriver()) {
            this.removeDriver();
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WOOD_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResult.SUCCESS;
        } else {
            boolean added = this.addDriver(player);
            if (added) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WOOD_FALL, SoundSource.PLAYERS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public Vec3 getRelativeTargetVec(final float delta) {
        final double x;
        final double y;
        final double z;
        if (this.driver == null) {
            return Vec3.ZERO;
        }
        if (delta == 1.0F) {
            x = this.driver.getX() - this.getX();
            y = this.driver.getY() - this.getY();
            z = this.driver.getZ() - this.getZ();
        } else {
            x = Mth.lerp(delta, this.driver.xOld, this.driver.getX()) - Mth.lerp(delta, this.xOld, this.getX());
            y = Mth.lerp(delta, this.driver.yOld, this.driver.getY()) - Mth.lerp(delta, this.yOld, this.getY());
            z = Mth.lerp(delta, this.driver.zOld, this.driver.getZ()) - Mth.lerp(delta, this.zOld, this.getZ());
        }
        final float yaw = (float) Math.toRadians(this.driver.getYRot());
        final float nx = -Mth.sin(yaw);
        final float nz = Mth.cos(yaw);
        final double r = getOffsetRadius();
        return new Vec3(x + nx * r, -y, z + nz * r);
    }

    public void handleRotation(final Vec3 target) {
        this.setYRot(getYaw(target));
        this.setXRot(getPitch(target));
    }

    public static float getYaw(final Vec3 vec) {
        return Mth.wrapDegrees((float) Math.toDegrees(-Mth.atan2(vec.x, vec.z)));
    }

    public static float getPitch(final Vec3 vec) {
        return Mth.wrapDegrees((float) Math.toDegrees(Mth.atan2(vec.y, Mth.sqrt((float) (vec.x * vec.x + vec.z * vec.z)))));
    }

    public void pulledTick() {
        if (this.driver == null) {
            return;
        }
        Vec3 targetVec = this.getRelativeTargetVec(1.0F);
        this.handleRotation(targetVec);
        while (this.getYRot() - this.yRotO < -180.0F) {
            this.yRotO -= 360.0F;
        }
        while (this.getYRot() - this.yRotO >= 180.0F) {
            this.yRotO += 360.0F;
        }
        if (this.driver.onGround()) {
            targetVec = new Vec3(targetVec.x, 0.0D, targetVec.z);
        }
        final double targetVecLength = targetVec.length();
        final double r = getOffsetRadius();
        final double relativeSpacing = getRelativeSpacing();
        final double diff = targetVecLength - relativeSpacing;
        final Vec3 move = Math.abs(diff) < r ? this.getDeltaMovement() : this.getDeltaMovement().add(targetVec.subtract(targetVec.normalize().scale(relativeSpacing + r * Math.signum(diff))));
        this.setOnGround(true);
        this.move(MoverType.SELF, move);
        if (!this.isAlive()) {
            return;
        }
        this.addStats();
        if (!this.level().isClientSide) {
            targetVec = this.getRelativeTargetVec(1.0F);
            if (targetVec.length() > relativeSpacing + 1.0D) {
                this.driver = null;
            }
        }
        this.updatePassengers();
    }

    public void updatePassengers() {
        for (final Entity passenger : this.getPassengers()) {
            this.positionRider(passenger);
        }
    }

    private void addStats() {
    }

    void tickLerp() {
        if (this.lerpSteps > 0) {
            final double dx = (this.lerpX - this.getX()) / this.lerpSteps;
            final double dy = (this.lerpY - this.getY()) / this.lerpSteps;
            final double dz = (this.lerpZ - this.getZ()) / this.lerpSteps;
            this.setYRot((float) (this.getYRot() + Mth.wrapDegrees(this.lerpYaw - this.getYRot()) / this.lerpSteps));
            this.setXRot((float) (this.getXRot() + (this.lerpPitch - this.getXRot()) / this.lerpSteps));
            this.lerpSteps--;
            this.setOnGround(true);
            this.move(MoverType.SELF, new Vec3(dx, dy, dz));
            this.setRot(this.getYRot(), this.getXRot());
        }
    }

    @Override
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int posRotationIncrements) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = yaw;
        this.lerpPitch = pitch;
        this.lerpSteps = posRotationIncrements;
    }

    @Override
    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.08, 0.0));
        }
        super.tick();
        this.tickLerp();
        if (this.driver != null) {
            this.pulledTick();
        }
        this.leftWheel.tick();
        this.rightWheel.tick();
        this.move(MoverType.SELF, this.getDeltaMovement());

        double dx = this.getX() - this.xOld;
        double dz = this.getZ() - this.zOld;
        double distanceTravelled = Math.sqrt(dx * dx + dz * dz);

        if (distanceTravelled > 0.2 && soundCooldownTicks <= 0 && !this.level().isClientSide()) {
            this.level().playSound(null, this.blockPosition(), SoundEventRegistry.CART_MOVING.get(), SoundSource.BLOCKS, 0.7F, 1.0F);
            soundCooldownTicks = 45;
        }

        if (soundCooldownTicks > 0) {
            soundCooldownTicks--;
        }

        if (this.wobbleTicks > 0) {
            float wobbleAmount = (float) Math.sin((10 - this.wobbleTicks) * Math.PI / 10) * wobbleDirection;
            this.setYRot(this.getYRot() + wobbleAmount);
            this.wobbleTicks--;
            if (this.wobbleTicks == 0) {
                this.setYRot(this.getYRot() - wobbleDirection);
            }
        }
    }

    public float getWheelRotation() {
        return this.leftWheel.getRotation();
    }

    protected double getRelativeSpacing() {
        return 2.0;
    }

    protected double getOffsetRadius() {
        return 0D;
    }

    @Override
    public boolean hurt(DamageSource source, float damage) {
        if (this.isRemoved() || !this.isAlive()) {
            return false;
        }
        if (source.getEntity() instanceof Player) {
            this.setHealth(this.getHealth() - damage);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WOOD_HIT, SoundSource.PLAYERS, 1.0F, 1.0F);
            this.wobbleTicks = 10;
            this.wobbleDirection = this.random.nextBoolean() ? 5.0F : -5.0F;
            if (this.getHealth() <= 0.0F) {
                this.destroy();
            }
            return true;
        }
        return false;
    }

    public float getHealth() {
        return this.health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    private void destroy() {
        if (!this.level().isClientSide) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WOOD_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
            this.spawnAtLocation(this.getDropItem());
            this.remove(RemovalReason.KILLED);
        }
    }

    protected ItemStack getDropItem() {
        return null;
    }
}
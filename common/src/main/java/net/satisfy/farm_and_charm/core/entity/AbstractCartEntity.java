package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.farm_and_charm.core.util.CartWorldData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class AbstractCartEntity extends Entity {
    private static final EntityDataAccessor<Float> DATA_WHEEL_ROTATION = SynchedEntityData.defineId(AbstractCartEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_SPEED = SynchedEntityData.defineId(AbstractCartEntity.class, EntityDataSerializers.FLOAT);

    private float prevWheelRotation;
    private float wheelRotation;
    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;

    private int pullingId = -1;
    private UUID pullingUuid;
    protected double spacing = 1.7D;

    protected Entity pulling;

    protected AbstractCartEntity(EntityType<? extends AbstractCartEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
    }

    @Override
    public float maxUpStep() {
        return 1.2F;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3.0D, 3.0D, 3.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_WHEEL_ROTATION, 0.0F);
        builder.define(DATA_SPEED, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("PullingUUID")) {
            this.pullingUuid = compound.getUUID("PullingUUID");
        } else {
            this.pullingUuid = null;
        }
        if (compound.contains("CartSpeed")) {
            this.entityData.set(DATA_SPEED, compound.getFloat("CartSpeed"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.pulling != null && this.pullingUuid != null) {
            compound.putUUID("PullingUUID", this.pullingUuid);
        }
        compound.putFloat("CartSpeed", this.entityData.get(DATA_SPEED));
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        if (this.getPassengers().isEmpty()) {
            return null;
        }
        Entity passenger = this.getPassengers().get(0);
        return passenger instanceof LivingEntity livingEntity ? livingEntity : null;
    }

    protected boolean isPlayerDrivingPulledByHorse() {
        return this.pulling instanceof AbstractHorse && this.getControllingPassenger() instanceof Player;
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        if (!(passenger instanceof Player)) {
            return false;
        }
        if (!(this.pulling instanceof AbstractHorse)) {
            return false;
        }
        return this.getPassengers().isEmpty();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public ItemStack getPickResult() {
        return this.getCartItemStack().copy();
    }

    @Override
    public @NotNull InteractionResult interact(Player player, InteractionHand hand) {
        return this.onPrimaryUse(player, hand);
    }

    protected InteractionResult onSecondaryUse(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    protected InteractionResult onPrimaryUse(Player player, InteractionHand hand) {
        if (!player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!this.level().isClientSide) {
            if (this.pulling == player) {
                this.setPulling(null);
            } else if (this.pulling == null) {
                this.setPulling(player);
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        this.prevWheelRotation = this.wheelRotation;
        this.wheelRotation = this.entityData.get(DATA_WHEEL_ROTATION);

        if (!this.isNoGravity()) {
            this.setDeltaMovement(0.0D, this.getDeltaMovement().y - 0.08D, 0.0D);
        }

        if (this.isVehicle()) {
            Vec3 current = this.getDeltaMovement();
            this.setDeltaMovement(0.0D, current.y, 0.0D);
        }

        super.tick();

        this.tickLerp();

        if (this.level().isClientSide) {
            this.clientResolvePullingIfPossible();
        }

        if (this.pulling == null) {
            this.setXRot(25.0F);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.attemptReattach();
        }

        for (Entity entity : this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this))) {
            this.push(entity);
        }
    }

    public @Nullable Player getCartDriver() {
        if (this.getPassengers().isEmpty()) {
            return null;
        }
        Entity passenger = this.getPassengers().get(0);
        return passenger instanceof Player player ? player : null;
    }

    public float getWheelRotation(float partialTick) {
        return Mth.lerp(partialTick, this.prevWheelRotation, this.wheelRotation);
    }

    protected float getWheelRadius() {
        return 0.5F;
    }

    public float getCartSpeed() {
        return this.entityData.get(DATA_SPEED);
    }

    public void setCartSpeed(float speed) {
        this.entityData.set(DATA_SPEED, speed);
    }

    public void pulledPostTick() {
        if (this.pulling == null) {
            return;
        }

        Vec3 targetVec = this.getRelativeTargetVec();
        this.handleRotation(targetVec);

        while (this.getYRot() - this.yRotO < -180.0F) {
            this.yRotO -= 360.0F;
        }
        while (this.getYRot() - this.yRotO >= 180.0F) {
            this.yRotO += 360.0F;
        }

        if (this.pulling.onGround()) {
            targetVec = new Vec3(targetVec.x, 0.0D, targetVec.z);
        }

        double targetVecLength = targetVec.length();
        double r = 0.2D;
        double relativeSpacing = Math.max(this.spacing + 0.5D * this.pulling.getBbWidth(), 1.0D);
        double diff = targetVecLength - relativeSpacing;

        Vec3 move;
        if (Math.abs(diff) < r) {
            move = this.getDeltaMovement();
        } else {
            move = this.getDeltaMovement().add(targetVec.subtract(targetVec.normalize().scale(relativeSpacing + r * Math.signum(diff))));
        }

        this.setOnGround(true);
        this.move(MoverType.SELF, move);

        if (!this.isAlive()) {
            return;
        }

        if (!this.level().isClientSide) {
            targetVec = this.getRelativeTargetVec();
            if (targetVec.length() > relativeSpacing + 1.0D) {
                this.setPulling(null);
                return;
            }
            this.updateWheelRotationFromMovement();
        }
    }

    public boolean shouldStopPulledTick() {
        if (!this.isAlive() || this.getPulling() == null || !this.getPulling().isAlive() || this.getPulling().isPassenger()) {
            if (this.pulling instanceof Player) {
                this.setPulling(null);
            } else {
                this.pulling = null;
            }
            return true;
        }

        if (!this.level().isClientSide && this.shouldRemovePulling()) {
            this.setPulling(null);
            return true;
        }

        return false;
    }

    protected boolean shouldRemovePulling() {
        if (this.horizontalCollision && this.pulling != null) {
            Vec3 start = new Vec3(this.getX(), this.getY() + this.getBbHeight(), this.getZ());
            Vec3 end = new Vec3(this.pulling.getX(), this.pulling.getY() + this.pulling.getBbHeight() / 2.0F, this.pulling.getZ());
            BlockHitResult result = this.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            return result.getType() == HitResult.Type.BLOCK;
        }
        return false;
    }

    protected Vec3 getRelativeTargetVec() {
        double x = this.pulling.getX() - this.getX();
        double y = this.pulling.getY() - this.getY();
        double z = this.pulling.getZ() - this.getZ();

        float yaw = (float) Math.toRadians(this.pulling.getYRot());
        float nx = -Mth.sin(yaw);
        float nz = Mth.cos(yaw);
        double r = 0.2D;

        return new Vec3(x + nx * r, y, z + nz * r);
    }

    protected void handleRotation(Vec3 target) {
        this.setYRot(Mth.wrapDegrees((float) Math.toDegrees(-Mth.atan2(target.x, target.z))));
        this.setXRot(Mth.wrapDegrees((float) Math.toDegrees(-Mth.atan2(target.y, Mth.sqrt((float) (target.x * target.x + target.z * target.z))))));
    }

    private void attemptReattach() {
        if (this.level().isClientSide) {
            if (this.pullingId != -1) {
                Entity entity = this.level().getEntity(this.pullingId);
                if (entity != null && entity.isAlive()) {
                    this.setPulling(entity);
                }
            }
            return;
        }

        if (this.pullingUuid != null && this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(this.pullingUuid);
            if (entity != null && entity.isAlive()) {
                this.setPulling(entity);
            }
        }
    }

    private void tickLerp() {
        if (this.lerpSteps > 0) {
            double dx = (this.lerpX - this.getX()) / this.lerpSteps;
            double dy = (this.lerpY - this.getY()) / this.lerpSteps;
            double dz = (this.lerpZ - this.getZ()) / this.lerpSteps;

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

    public Entity getPulling() {
        return this.pulling;
    }

    public void setPulling(Entity entity) {
        if (this.level().isClientSide) {
            if (entity == null) {
                this.pulling = null;
                this.pullingId = -1;
                this.pullingUuid = null;
                CartWorldData.get(this.level()).removePullingByCart(this);
            } else {
                this.pulling = entity;
                this.pullingId = entity.getId();
                this.pullingUuid = entity.getUUID();
                CartWorldData.get(this.level()).addPulling(this);
            }
            return;
        }

        CartWorldData worldData = CartWorldData.get(this.level());

        if (this.pulling != null) {
            worldData.removePullingByCart(this);
        }

        if (entity == null) {
            this.pulling = null;
            this.pullingUuid = null;
            this.setCartSpeed(0.0F);
            PacketHandler.sendCartPullingSync(this, -1);
            return;
        }

        if (!worldData.canAttach(entity, this)) {
            this.pulling = null;
            this.pullingUuid = null;
            this.setCartSpeed(0.0F);
            PacketHandler.sendCartPullingSync(this, -1);
            return;
        }

        this.pulling = entity;
        this.pullingUuid = entity.getUUID();
        worldData.addPulling(this);

        PacketHandler.sendCartPullingSync(this, entity.getId());
    }

    protected void clientResolvePullingIfPossible() {
        if (!this.level().isClientSide) {
            return;
        }
        if (this.pulling != null) {
            return;
        }
        if (this.pullingId < 0) {
            return;
        }

        Entity entity = this.level().getEntity(this.pullingId);
        if (entity != null && entity.isAlive()) {
            this.pulling = entity;
        }
    }

    private void updateWheelRotationFromMovement() {
        double deltaX;
        double deltaZ;

        if (this.pulling instanceof AbstractHorse) {
            Vec3 pullerDelta = this.pulling.getDeltaMovement();
            float pullerYawRad = this.pulling.getYRot() * ((float) Math.PI / 180.0F);
            float pullerForwardX = -Mth.sin(pullerYawRad);
            float pullerForwardZ = Mth.cos(pullerYawRad);
            double forwardSpeed = pullerDelta.x * pullerForwardX + pullerDelta.z * pullerForwardZ;
            float cartYawRad = this.getYRot() * ((float) Math.PI / 180.0F);
            deltaX = -Mth.sin(cartYawRad) * forwardSpeed;
            deltaZ = Mth.cos(cartYawRad) * forwardSpeed;
        } else {
            deltaX = this.getX() - this.xOld;
            deltaZ = this.getZ() - this.zOld;
        }

        float distance = Mth.sqrt((float) (deltaX * deltaX + deltaZ * deltaZ));
        if (distance < 1.0E-5F || distance > 2.0F) {
            return;
        }

        float clampedDistance = Math.min(distance, 0.35F);

        float cartYawRadians = (float) Math.toRadians(this.getYRot());
        float forwardX = -Mth.sin(cartYawRadians);
        float forwardZ = Mth.cos(cartYawRadians);

        float forwardDot = (float) (deltaX * forwardX + deltaZ * forwardZ);
        float direction = forwardDot >= 0.0F ? -1.0F : 1.0F;

        float wheelRadius = this.getWheelRadius();
        float deltaRotation = direction * (clampedDistance / wheelRadius);

        this.wheelRotation += deltaRotation;
        this.entityData.set(DATA_WHEEL_ROTATION, this.wheelRotation);
    }

    protected abstract ItemStack getCartItemStack();
}


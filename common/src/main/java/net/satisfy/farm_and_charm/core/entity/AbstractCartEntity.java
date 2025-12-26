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
import net.minecraft.world.entity.MoverType;
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

import java.util.UUID;

public abstract class AbstractCartEntity extends Entity {
    private static final EntityDataAccessor<Float> DATA_WHEEL_ROTATION = SynchedEntityData.defineId(AbstractCartEntity.class, EntityDataSerializers.FLOAT);
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
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.hasUUID("PullingUUID")) {
            this.pullingUuid = compound.getUUID("PullingUUID");
        } else {
            this.pullingUuid = null;
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        if (this.pulling != null && this.pullingUuid != null) {
            compound.putUUID("PullingUUID", this.pullingUuid);
        }
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
        return false;
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
        if (player.isShiftKeyDown()) {
            return this.onSecondaryUse(player, hand);
        }
        return this.onPrimaryUse(player, hand);
    }

    protected boolean canBePickedUpBy() {
        return this.pulling == null && this.isAlive() && this.getPassengers().isEmpty();
    }

    protected InteractionResult onSecondaryUse(Player player, InteractionHand hand) {
        if (!player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }

        if (!this.level().isClientSide) {
            if (!this.canBePickedUpBy()) {
                return InteractionResult.PASS;
            }

            ItemStack drop = this.getCartItemStack();
            if (!drop.isEmpty()) {
                if (!player.getInventory().add(drop)) {
                    player.drop(drop, false);
                }
            }
            this.setPulling(null);
            this.discard();
            return InteractionResult.CONSUME;
        }

        return InteractionResult.SUCCESS;
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

    public float getWheelRotation(float partialTick) {
        return Mth.lerp(partialTick, this.prevWheelRotation, this.wheelRotation);
    }



    protected float getWheelRadius() {
        return 0.5F;
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
            }
        }

        this.updateWheelRotationFromMovement();
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
        double x;
        double y;
        double z;

        x = this.pulling.getX() - this.getX();
        y = this.pulling.getY() - this.getY();
        z = this.pulling.getZ() - this.getZ();

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
            } else {
                this.pulling = entity;
                this.pullingId = entity.getId();
                this.pullingUuid = entity.getUUID();
            }
            CartWorldData.get(this.level()).addPulling(this);
            return;
        }

        this.pulling = entity;
        this.pullingUuid = entity == null ? null : entity.getUUID();

        int newPullingId = entity == null ? -1 : entity.getId();
        PacketHandler.sendCartPullingSync(this, newPullingId);

        if (entity == null) {
            CartWorldData.get(this.level()).removePullingByCart(this);
        } else {
            CartWorldData.get(this.level()).addPulling(this);
        }
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
        double deltaX = this.getX() - this.xOld;
        double deltaZ = this.getZ() - this.zOld;

        float distance = Mth.sqrt((float) (deltaX * deltaX + deltaZ * deltaZ));
        if (distance < 1.0E-5F) {
            return;
        }

        float yawRadians = (float) Math.toRadians(this.getYRot());
        float forwardX = -Mth.sin(yawRadians);
        float forwardZ = Mth.cos(yawRadians);

        float forwardDot = (float) (deltaX * forwardX + deltaZ * forwardZ);
        float direction = forwardDot >= 0.0F ? -1.0F : 1.0F;

        float wheelRadius = this.getWheelRadius();
        float deltaRotation = direction * (distance / wheelRadius);

        this.wheelRotation += deltaRotation;

        if (!this.level().isClientSide) {
            this.entityData.set(DATA_WHEEL_ROTATION, this.wheelRotation);
        }
    }
    protected abstract ItemStack getCartItemStack();
}
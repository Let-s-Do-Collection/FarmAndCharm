package net.satisfy.farm_and_charm.core.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import org.jetbrains.annotations.NotNull;

public abstract class CartEntity extends DrivableEntity {
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE;

    static {
        DATA_ID_DAMAGE = SynchedEntityData.defineId(CartEntity.class, EntityDataSerializers.FLOAT);
    }

    private float originalYRot = 0.0F;
    private boolean shouldResetRot = false;
    private float wheelRot;
    private int rollOut;
    private int soundCooldown = 0;
    private double lastDriverX, lastDriverY, lastDriverZ;

    protected CartEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    protected float wheelYOffset() {
        return this.wheelRadius() / 2.0F;
    }

    protected abstract float firstPoint();

    protected abstract float lastPoint();

    protected abstract float wheelRadius();

    protected float holdOffset() {
        return 2.0F;
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


    @Override
    public void tick() {
        super.tick();

        if (soundCooldown > 0) {
            soundCooldown--;
        }

        Vec3 currentPos = this.position();
        double distanceMoved = Math.sqrt(Math.pow(currentPos.x - this.lastDriverX, 1.5) + Math.pow(currentPos.y - this.lastDriverY, 1.5) + Math.pow(currentPos.z - this.lastDriverZ, 1.5));
        final double MIN_MOVEMENT_THRESHOLD = 0.05;

        if (distanceMoved > MIN_MOVEMENT_THRESHOLD) {
            spawnWheelParticles();
            playMovementSound();
        }

        this.lastDriverX = currentPos.x;
        this.lastDriverY = currentPos.y;
        this.lastDriverZ = currentPos.z;

        this.setupMovement();
        this.setupWheels();
        this.setupRotation();

        if (shouldResetRot) {
            this.setYRot(originalYRot);
            shouldResetRot = false;
        }
    }


    private void setupMovement() {
        if (this.hasDriver()) {
            Vec3 lastMoveVec = this.position().subtract(this.lastDriverX, this.lastDriverY, this.lastDriverZ).scale(0.5D);
            assert this.getDriver() != null;
            Vec3 driverMoveVec = this.getDriver().position().subtract(this.lastDriverX, this.lastDriverY, this.lastDriverZ).reverse().scale(0.5D);
            Vec3 newPosVec = driverMoveVec.add(lastMoveVec).normalize().scale(this.holdOffset());
            Vec3 desiredPos = this.getDriver().position().add(newPosVec);
            Vec3 movVec = desiredPos.subtract(this.position());
            if (this.getDeltaMovement().length() + movVec.scale(0.2D).length() < movVec.length()) {
                this.setDeltaMovement(this.getDeltaMovement().add(movVec).scale(0.2D));
            }
        }

        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.08, 0.0));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    private void setupRotation() {
        if (this.hasDriver()) {
            assert this.getDriver() != null;
            Vec3 lookAtVec = this.getDriver().position().subtract(this.position()).normalize();
            double yRot = Math.atan2(-lookAtVec.x, lookAtVec.z);
            double xRot = Math.atan2(-lookAtVec.y, Math.sqrt(lookAtVec.x * lookAtVec.x + lookAtVec.z * lookAtVec.z));
            this.setYRot((float) Math.toDegrees(yRot));
            this.setXRot((float) Math.toDegrees(xRot));
        }
    }

    private void setupWheels() {
        Vec3 velocity = this.getDeltaMovement();
        float xzDist = (float) Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        if (0.01F < xzDist && 10 >= this.rollOut) {
            float anglePerTick = (xzDist / this.wheelRadius()) / ((float) this.rollOut);
            this.wheelRot -= anglePerTick;
            this.wheelRot %= (float) (Math.PI * 2);
        }

        if (!this.onGround()) {
            if (10 > this.rollOut) {
                this.rollOut++;
            }
        } else {
            this.rollOut = 1;
        }
    }

    public float wheelRot() {
        return this.wheelRot;
    }


    private void playMovementSound() {
        if (soundCooldown <= 0) {
            SoundEvent sound = SoundEventRegistry.CART_MOVING.get();
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 0.1F, 1.0F);
            soundCooldown = 55;
        }
    }

    private void spawnWheelParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockPos blockPosUnder = new BlockPos((int) this.getX(), (int) (this.getY() - 0.5), (int) this.getZ());
            BlockState blockState = serverLevel.getBlockState(blockPosUnder);

            if (!blockState.isAir()) {
                for (int i = 0; i < 4; ++i) {
                    double wheelParticleX = this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 0.5;
                    double wheelParticleY = this.getY() + 0.1;
                    double wheelParticleZ = this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 0.5;
                    serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), wheelParticleX, wheelParticleY, wheelParticleZ, 1, 0.0D, 0.05D, 0.0D, 0.1D);
                }
            }
        }
    }

    public float balance() {
        double maxFrontX = Math.sqrt(this.firstPoint() * this.firstPoint() - this.wheelYOffset() * this.wheelYOffset());
        double maxFrontSlope = Math.atan2(-this.wheelYOffset(), maxFrontX);
        if (!this.hasDriver()) {
            return (float) maxFrontSlope;
        }
        double maxBackX = Math.sqrt(this.lastPoint() * this.lastPoint() - this.wheelYOffset() * this.wheelYOffset());
        double maxBackSlope = Math.atan2(this.wheelYOffset(), maxBackX);

        double desiredXRot = Math.toRadians(-this.getXRot());
        return this.onGround() ? (float) Math.max(Math.min(desiredXRot, maxBackSlope), maxFrontSlope) : (float) desiredXRot;
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damageAmount) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        } else if (!this.level().isClientSide && !this.isRemoved()) {
            this.setDamage(this.getDamage() + damageAmount);
            this.markHurt();
            this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());

            if (this.level() instanceof ServerLevel serverLevel) {
                BlockState blockState = serverLevel.getBlockState(this.blockPosition().below());
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState),
                        this.getX(), this.getY() + 0.1, this.getZ(), 10, 0.1D, 0.1D, 0.1D, 0.0D);

                originalYRot = this.getYRot();
                this.setYRot(originalYRot + this.random.nextFloat() * 10.0F - 5.0F);
                shouldResetRot = true;
            }

            if (this.getDamage() > 5.0F) {
                if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.destroy(damageSource);
                }
                this.discard();
            }

            return true;
        } else {
            return true;
        }
    }


    protected void destroy(DamageSource damageSource) {
        this.spawnAtLocation(ObjectRegistry.SUPPLY_CART.get());
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DATA_ID_DAMAGE, Math.min(damage, 40.0F));
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_ID_DAMAGE, 0.0F);
    }
}

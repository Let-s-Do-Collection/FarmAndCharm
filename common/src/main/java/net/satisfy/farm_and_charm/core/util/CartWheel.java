package net.satisfy.farm_and_charm.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.entity.AbstractCartEntity;

public final class CartWheel {
    private float rotation;
    private float rotationIncrement;
    private final float offsetX;
    private final float offsetZ;
    private final float circumference;
    private double posX;
    private double posZ;
    private double prevPosX;
    private double prevPosZ;
    private final AbstractCartEntity cart;

    public CartWheel(final AbstractCartEntity cartIn, final float offsetXIn, final float offsetZIn, final float circumferenceIn) {
        this.cart = cartIn;
        this.offsetX = offsetXIn;
        this.offsetZ = offsetZIn;
        this.circumference = circumferenceIn;
        this.posX = this.prevPosX = cartIn.getX();
        this.posZ = this.prevPosZ = cartIn.getZ();
    }

    public CartWheel(final AbstractCartEntity cartIn, final float offsetX) {
        this(cartIn, offsetX, 0.0F, (float) (10 * Math.PI * 2 / 16));
    }

    public void tick() {
        this.rotation += this.rotationIncrement;
        this.prevPosX = this.posX;
        this.prevPosZ = this.posZ;

        final float yaw = (float) Math.toRadians(this.cart.getYRot());
        final float nx = -Mth.sin(yaw);
        final float nz = Mth.cos(yaw);

        this.posX = this.cart.getX() + nx * this.offsetZ - nz * this.offsetX;
        this.posZ = this.cart.getZ() + nz * this.offsetZ + nx * this.offsetX;

        final double dx = this.posX - this.prevPosX;
        final double dz = this.posZ - this.prevPosZ;
        final float distanceTravelled = (float) Math.sqrt(dx * dx + dz * dz);

        final double dxNormalized = distanceTravelled > 0 ? dx / distanceTravelled : 0;
        final double dzNormalized = distanceTravelled > 0 ? dz / distanceTravelled : 0;
        final float travelledForward = Mth.sign((float) (dxNormalized * nx + dzNormalized * nz));

        if (distanceTravelled > 0.05F && this.cart.level().isClientSide) {
            final BlockPos blockpos = new BlockPos(Mth.floor(this.posX), Mth.floor(this.cart.getY() - 0.2F), Mth.floor(this.posZ));
            final BlockState blockstate = this.cart.level().getBlockState(blockpos);

            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                int particleCount = 8;
                final float sideYaw = (float) Math.toRadians(this.cart.getYRot());
                final double sideX = -Mth.cos(sideYaw);
                final double sideZ = -Mth.sin(sideYaw);

                double upwardVelocity = Math.min(distanceTravelled * 0.1, 0.15);

                for (int i = 0; i < particleCount; i++) {
                    double centerShiftFactor = 0.3;
                    double px = this.posX - sideX * this.offsetX * centerShiftFactor + (this.cart.getRandom().nextDouble() - 0.5) * 0.02;
                    double py = blockpos.getY() + 1;
                    double pz = this.posZ - sideZ * this.offsetX * centerShiftFactor + (this.cart.getRandom().nextDouble() - 0.5) * 0.02;
                    this.cart.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), px, py, pz, dx * 0.05, upwardVelocity, dz * 0.05);
                }
            }
        }

        this.rotationIncrement = travelledForward * distanceTravelled * this.circumference * 0.2F;
    }

    public float getRotation() {
        return this.rotation;
    }
}
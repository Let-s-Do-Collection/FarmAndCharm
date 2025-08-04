package net.satisfy.farm_and_charm.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.entity.AbstractTowableEntity;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;

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
    private final AbstractTowableEntity cart;

    public CartWheel(final AbstractTowableEntity cartIn, final float offsetXIn, final float offsetZIn, final float circumferenceIn) {
        this.cart = cartIn;
        this.offsetX = offsetXIn;
        this.offsetZ = offsetZIn;
        this.circumference = circumferenceIn;
        this.posX = this.prevPosX = cartIn.getX();
        this.posZ = this.prevPosZ = cartIn.getZ();
    }

    public CartWheel(final AbstractTowableEntity cartIn, final float offsetX) {
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
        final double dxNormalized = dx / distanceTravelled;
        final double dzNormalized = dz / distanceTravelled;
        final float travelledForward = Mth.sign(dxNormalized * nx + dzNormalized * nz);

        if (distanceTravelled > 0.2) {
            final BlockPos blockpos = new BlockPos(Mth.floor(this.posX), Mth.floor(this.cart.getY() - 0.2F), Mth.floor(this.posZ));
            final BlockState blockstate = this.cart.level().getBlockState(blockpos);
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                int particleCount = 15;
                float zFactor = 0.5F;
                for (int i = 0; i < particleCount; i++) {
                    double adjustedPosZ = Mth.lerp(zFactor, this.cart.getZ(), this.posZ) + (Math.random() - 0.5) * 0.1;
                    double offsetX = this.posX + (Math.random() - 0.5) * 0.1;
                    this.cart.level().addParticle(
                            new BlockParticleOption(ParticleTypes.BLOCK, blockstate),
                            offsetX,
                            this.cart.getY() + (Math.random() - 0.5) * 0.1,
                            adjustedPosZ,
                            dx,
                            distanceTravelled,
                            dz
                    );
                }
            }
        }
        this.rotationIncrement = travelledForward * distanceTravelled * this.circumference * 0.2F;
    }


    public float getRotation() {
        return this.rotation;
    }
}
package net.satisfy.farm_and_charm.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class WaterDripParticle extends TextureSheetParticle {
    private static final int HANG = 0;
    private static final int FALL = 1;
    private static final int LAND = 2;

    private final SpriteSet sprites;
    private int stage;
    private int stageAge;

    protected WaterDripParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        WaterTint.apply(this, level, x, y, z);
        this.setSize(0.01F, 0.01F);
        this.hasPhysics = true;
        this.friction = 0.98F;
        this.lifetime = 200;
        if (ySpeed != 0.0 || xSpeed != 0.0 || zSpeed != 0.0) {
            this.xd = xSpeed;
            this.yd = ySpeed;
            this.zd = zSpeed;
            setStage(FALL);
        } else {
            this.xd = 0.0;
            this.yd = 0.0;
            this.zd = 0.0;
            setStage(HANG);
        }
    }

    private void setStage(int stage) {
        this.stage = stage;
        this.stageAge = 0;
        this.setSprite(this.sprites.get(stage, LAND));
        this.gravity = stage == FALL ? 0.06F : 0.0F;
        if (stage == LAND) {
            this.xd = 0.0;
            this.yd = 0.0;
            this.zd = 0.0;
        }
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.stageAge++;

        switch (this.stage) {
            case HANG -> {
                if (this.stageAge > 40) {
                    setStage(FALL);
                }
            }
            case FALL -> {
                this.yd -= this.gravity;
                this.move(this.xd, this.yd, this.zd);
                this.xd *= this.friction;
                this.zd *= this.friction;
                if (this.onGround) {
                    setStage(LAND);
                }
            }
            case LAND -> {
                if (this.stageAge > 8 + this.random.nextInt(8)) {
                    this.remove();
                }
            }
            default -> this.remove();
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new WaterDripParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}

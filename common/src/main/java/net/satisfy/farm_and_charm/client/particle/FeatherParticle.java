package net.satisfy.farm_and_charm.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class FeatherParticle extends TextureSheetParticle {
    private static final double POP = 0.12;
    private static final float FALL_SPEED = 0.025F;
    private static final float SWAY = 0.04F;

    private final float swayPhase;
    private final float spin;

    protected FeatherParticle(ClientLevel level, double x, double y, double z, double xSpeed, double zSpeed, ColorParticleOption color) {
        super(level, x, y, z);
        this.xd = xSpeed;
        this.yd = POP + this.random.nextDouble() * 0.06;
        this.zd = zSpeed;
        this.lifetime = 40 + this.random.nextInt(25);
        this.gravity = 0.0F;
        this.friction = 0.9F;
        this.quadSize = 0.07F + this.random.nextFloat() * 0.03F;
        this.swayPhase = this.random.nextFloat() * Mth.TWO_PI;
        this.spin = (this.random.nextFloat() - 0.5F) * 0.2F;
        this.roll = this.random.nextFloat() * Mth.TWO_PI;
        this.rCol = color.getRed();
        this.gCol = color.getGreen();
        this.bCol = color.getBlue();
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.oRoll = this.roll;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.yd = Math.max(this.yd * 0.8 - 0.01, -FALL_SPEED);
        this.xd = this.xd * this.friction + Mth.sin(this.age * 0.25F + this.swayPhase) * SWAY * 0.1;
        this.zd = this.zd * this.friction + Mth.cos(this.age * 0.2F + this.swayPhase) * SWAY * 0.1;
        this.move(this.xd, this.yd, this.zd);
        if (!this.onGround) {
            this.roll += this.spin;
        }
        if (this.lifetime - this.age < 10) {
            this.alpha = (this.lifetime - this.age) / 10.0F;
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<ColorParticleOption> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ColorParticleOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            FeatherParticle particle = new FeatherParticle(level, x, y, z, xSpeed, zSpeed, type);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}

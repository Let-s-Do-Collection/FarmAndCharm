package net.satisfy.farm_and_charm.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ColorParticleOption;
import org.jetbrains.annotations.NotNull;

public class DyeSplashParticle extends TextureSheetParticle {
    private static final float GRAVITY = 0.9F;
    private static final double UPWARD_BOOST = 0.12;

    protected DyeSplashParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, ColorParticleOption color) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.xd = xSpeed + (this.random.nextDouble() - 0.5) * 0.1;
        this.yd = Math.abs(ySpeed) + UPWARD_BOOST + this.random.nextDouble() * 0.08;
        this.zd = zSpeed + (this.random.nextDouble() - 0.5) * 0.1;
        this.gravity = GRAVITY;
        this.friction = 0.96F;
        this.lifetime = 10 + this.random.nextInt(8);
        this.quadSize *= 0.8F;
        this.rCol = color.getRed();
        this.gCol = color.getGreen();
        this.bCol = color.getBlue();
        this.hasPhysics = true;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<ColorParticleOption> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(ColorParticleOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            DyeSplashParticle particle = new DyeSplashParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, type);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}

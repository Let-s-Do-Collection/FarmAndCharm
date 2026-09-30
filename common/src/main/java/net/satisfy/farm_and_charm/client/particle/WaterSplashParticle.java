package net.satisfy.farm_and_charm.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class WaterSplashParticle extends TextureSheetParticle {
    protected WaterSplashParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.xd *= 0.3F;
        this.yd = this.random.nextFloat() * 0.2F + 0.1F;
        this.zd *= 0.3F;
        if (ySpeed == 0.0 && (xSpeed != 0.0 || zSpeed != 0.0)) {
            this.xd = xSpeed;
            this.yd = 0.1;
            this.zd = zSpeed;
        }
        this.setSize(0.01F, 0.01F);
        this.gravity = 0.06F;
        this.lifetime = (int) (8.0 / (this.random.nextFloat() * 0.8 + 0.2));
        WaterTint.apply(this, level, x, y, z);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.lifetime-- <= 0) {
            this.remove();
            return;
        }
        this.yd -= this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98F;
        this.yd *= 0.98F;
        this.zd *= 0.98F;
        if (this.onGround) {
            if (this.random.nextFloat() < 0.5F) {
                this.remove();
            }
            this.xd *= 0.7F;
            this.zd *= 0.7F;
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
            WaterSplashParticle particle = new WaterSplashParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}

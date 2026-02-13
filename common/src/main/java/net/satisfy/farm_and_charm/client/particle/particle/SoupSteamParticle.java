package net.satisfy.farm_and_charm.client.particle.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class SoupSteamParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected SoupSteamParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;

        this.lifetime = 60 + this.random.nextInt(30);
        this.gravity = -0.0008F;

        this.xd = velocityX * 0.2;
        this.yd = 0.02 + this.random.nextDouble() * 0.01;
        this.zd = velocityZ * 0.2;

        this.quadSize = 0.12F + this.random.nextFloat() * 0.04F;
        this.alpha = 0.6F;

        this.setSpriteFromAge(sprites);
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

        this.xd *= 0.96;
        this.zd *= 0.96;

        this.move(this.xd, this.yd, this.zd);

        if (this.age > this.lifetime * 0.6) {
            this.alpha *= 0.94F;
        }

        this.quadSize *= 1.002F;

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new SoupSteamParticle(level, x, y + 0.25, z, velocityX, velocityY, velocityZ, this.sprites);
        }
    }
}
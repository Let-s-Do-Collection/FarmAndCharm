package net.satisfy.farm_and_charm.client.particle.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class SoupCookingBubbleParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final double baseY;

    protected SoupCookingBubbleParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);

        this.sprites = sprites;
        this.baseY = y;

        this.setSize(0.04F, 0.04F);
        this.quadSize *= 0.6F + this.random.nextFloat() * 0.4F;

        this.xd = (this.random.nextDouble() - 0.5) * 0.04;
        this.zd = (this.random.nextDouble() - 0.5) * 0.04;
        this.yd = 0.0;

        this.lifetime = 6 + this.random.nextInt(4);

        this.setSpriteFromAge(this.sprites);
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

        this.xd += (this.random.nextDouble() - 0.5) * 0.01;
        this.zd += (this.random.nextDouble() - 0.5) * 0.01;

        this.move(this.xd, 0.0, this.zd);

        this.xd *= 0.7;
        this.zd *= 0.7;

        this.y = this.baseY + Math.sin(this.age * 0.8) * 0.01;

        this.setSpriteFromAge(this.sprites);
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
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new SoupCookingBubbleParticle(level, x, y, z,
                    this.sprites);
        }
    }
}
package net.satisfy.farm_and_charm.client.util;

import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.entity.AbstractCartEntity;

import java.util.Arrays;
import java.util.List;

public final class CartWheelUtil {

    private static final Int2ObjectOpenHashMap<PullSoundInstance> ACTIVE_SOUNDS = new Int2ObjectOpenHashMap<>();
    private static final Int2FloatOpenHashMap MOVE_ACCUMULATOR = new Int2FloatOpenHashMap();

    private CartWheelUtil() {
    }

    public static List<CartWheel> createDefaultWheels(AbstractCartEntity cart) {
        return Arrays.asList(
                new CartWheel(cart, 0.9F),
                new CartWheel(cart, -0.9F)
        );
    }

    public static void tickCart(AbstractCartEntity cart, List<CartWheel> wheels, SoundEvent pullSound) {
        if (!cart.level().isClientSide) {
            return;
        }

        tickPullSound(cart, pullSound);

        for (CartWheel wheel : wheels) {
            wheel.tickClient();
        }
    }

    public static void stopCart(AbstractCartEntity cart) {
        PullSoundInstance sound = ACTIVE_SOUNDS.get(cart.getId());
        if (sound != null) {
            sound.requestFadeOut();
        }
        MOVE_ACCUMULATOR.remove(cart.getId());
    }

    private static void tickPullSound(AbstractCartEntity cart, SoundEvent soundEvent) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || !cart.isAlive()) {
            stopCart(cart);
            return;
        }

        PullSoundInstance existing = ACTIVE_SOUNDS.get(cart.getId());
        if (cart.getPulling() == null) {
            if (existing != null) {
                existing.requestFadeOut();
            }
            MOVE_ACCUMULATOR.remove(cart.getId());
            return;
        }

        double dx = cart.getX() - cart.xOld;
        double dz = cart.getZ() - cart.zOld;
        float step = Mth.sqrt((float) (dx * dx + dz * dz));

        if (step < 1.0E-4F) {
            if (existing != null) {
                existing.requestFadeOut();
            }
            return;
        }

        float moved = MOVE_ACCUMULATOR.get(cart.getId());
        moved += step;
        MOVE_ACCUMULATOR.put(cart.getId(), moved);

        if (moved < 1.0F) {
            if (existing != null) {
                existing.requestFadeOut();
            }
            return;
        }

        if (existing == null || existing.isStopped()) {
            PullSoundInstance sound = new PullSoundInstance(cart, soundEvent);
            ACTIVE_SOUNDS.put(cart.getId(), sound);
            minecraft.getSoundManager().play(sound);
        } else {
            existing.ensureActive();
        }
    }

    public static final class CartWheel {
        private final AbstractCartEntity cart;
        private final float offsetX;
        private final float offsetZ;
        private final float circumference;

        private float rotation;
        private float rotationIncrement;

        private double x;
        private double z;
        private double prevX;
        private double prevZ;

        public CartWheel(AbstractCartEntity cart, float offsetX) {
            this(cart, offsetX, 0.0F, (float) (10 * Math.PI * 2 / 16));
        }

        public CartWheel(AbstractCartEntity cart, float offsetX, float offsetZ, float circumference) {
            this.cart = cart;
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
            this.circumference = circumference;
            this.x = this.prevX = cart.getX();
            this.z = this.prevZ = cart.getZ();
        }

        public void tickClient() {
            this.rotation += this.rotationIncrement;

            this.prevX = this.x;
            this.prevZ = this.z;

            float yaw = (float) Math.toRadians(this.cart.getYRot());
            float forwardX = -Mth.sin(yaw);
            float forwardZ = Mth.cos(yaw);

            this.x = this.cart.getX() + forwardX * this.offsetZ - forwardZ * this.offsetX;
            this.z = this.cart.getZ() + forwardZ * this.offsetZ + forwardX * this.offsetX;

            double dx = this.x - this.prevX;
            double dz = this.z - this.prevZ;
            float distance = Mth.sqrt((float) (dx * dx + dz * dz));

            if (distance > 0.02F) {
                spawnWheelParticles(this.cart, this.x, this.z, dx, dz, distance);
            }

            float forward = Mth.sign((float) (dx * forwardX + dz * forwardZ));
            this.rotationIncrement = forward * distance * this.circumference * 0.2F;
        }

        public float getRotation() {
            return this.rotation;
        }
    }

    private static void spawnWheelParticles(AbstractCartEntity cart, double wheelX, double wheelZ, double velocityX, double velocityZ, float distanceTravelled) {
        if (distanceTravelled < 0.02F) {
            return;
        }

        double groundY = cart.getBoundingBox().minY - 0.05D;
        BlockPos blockPos = BlockPos.containing(wheelX, groundY, wheelZ);
        BlockState blockState = cart.level().getBlockState(blockPos);

        if (blockState.getRenderShape() == RenderShape.INVISIBLE) {
            return;
        }

        int count = Mth.clamp((int) (distanceTravelled * 70.0F), 1, 5);
        double particleY = cart.getBoundingBox().minY + 0.02D;

        for (int i = 0; i < count; i++) {
            double px = wheelX + (cart.getRandom().nextDouble() - 0.5D) * 0.08D;
            double pz = wheelZ + (cart.getRandom().nextDouble() - 0.5D) * 0.08D;

            double vx = velocityX * 0.12D;
            double vz = velocityZ * 0.12D;
            double vy = 0.02D + cart.getRandom().nextDouble() * 0.03D;

            cart.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), px, particleY, pz, vx, vy, vz);
        }
    }

    private static final class PullSoundInstance extends AbstractTickableSoundInstance {
        private static final int FADE_TICKS = 15;

        private final AbstractCartEntity cart;
        private boolean fadingOut;
        private int fadeTicksLeft;

        private PullSoundInstance(AbstractCartEntity cart, SoundEvent sound) {
            super(sound, SoundSource.NEUTRAL, cart.getRandom());
            this.cart = cart;
            this.looping = true;
            this.delay = 0;
            this.volume = 0.2F;
            this.pitch = 1.4F;
            this.fadingOut = false;
            this.fadeTicksLeft = 0;
            this.x = cart.getX();
            this.y = cart.getY();
            this.z = cart.getZ();
        }

        @Override
        public void tick() {
            if (!this.cart.isAlive()) {
                requestFadeOut();
            }

            this.x = this.cart.getX();
            this.y = this.cart.getY();
            this.z = this.cart.getZ();

            if (this.fadingOut) {
                if (this.fadeTicksLeft <= 0) {
                    this.volume = 0.0F;
                    this.stop();
                    return;
                }
                float t = (float) this.fadeTicksLeft / (float) FADE_TICKS;
                this.volume = 0.2F * t;
                this.fadeTicksLeft--;
                return;
            }

            if (this.cart.getPulling() == null) {
                requestFadeOut();
                return;
            }

            double dx = this.cart.getX() - this.cart.xOld;
            double dz = this.cart.getZ() - this.cart.zOld;
            float speed = Mth.sqrt((float) (dx * dx + dz * dz));

            if (speed < 1.0E-4F) {
                requestFadeOut();
                return;
            }

            float pitchVariation = 0.1F + this.cart.getRandom().nextFloat() * 0.4F;
            this.pitch = 1.4F + pitchVariation;
            this.volume = 0.2F;
        }

        private void ensureActive() {
            this.fadingOut = false;
            this.fadeTicksLeft = 0;
        }

        private void requestFadeOut() {
            if (this.fadingOut || this.isStopped()) {
                return;
            }
            this.fadingOut = true;
            this.fadeTicksLeft = FADE_TICKS;
        }
    }

    public static void stopCartById(int cartId) {
        PullSoundInstance sound = ACTIVE_SOUNDS.get(cartId);
        if (sound != null) {
            sound.requestFadeOut();
            ACTIVE_SOUNDS.remove(cartId);
        }
        MOVE_ACCUMULATOR.remove(cartId);
    }
}
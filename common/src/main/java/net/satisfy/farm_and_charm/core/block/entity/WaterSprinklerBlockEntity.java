package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.SprinklerPressure;
import net.satisfy.farm_and_charm.core.block.WaterSprinklerBlock;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

public class WaterSprinklerBlockEntity extends BlockEntity {
    public static final float STEP_ANGLE = 15.0F;
    private static final float STEPS_PER_TICK = 2.0F / STEP_ANGLE;
    private static final float SNAP = 0.25F;
    private static final float SPIN_UP = 0.02F;
    private float phase;
    private float phasePrev;
    private float speed;

    public WaterSprinklerBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.SPRINKLER_BLOCK_ENTITY.get(), pos, state);
        this.phase = this.phasePrev = Mth.murmurHash3Mixer((int) pos.asLong()) & 31;
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, WaterSprinklerBlockEntity sprinkler) {
        float target = state.getValue(WaterSprinklerBlock.PRESSURE).getSpeed() * (level.isRaining() || level.isThundering() ? 2.0F : 1.0F);
        sprinkler.speed += (target - sprinkler.speed) * SPIN_UP;
        sprinkler.phasePrev = sprinkler.phase;
        sprinkler.phase += sprinkler.speed * STEPS_PER_TICK;
    }

    private float getPhase(float partialTick) {
        return Mth.lerp(partialTick, this.phasePrev, this.phase);
    }

    public SprinklerPressure getPressure() {
        return this.getBlockState().getValue(WaterSprinklerBlock.PRESSURE);
    }

    /**
     * Angle in degrees. Steady and high pressure turn evenly. Pulsing pressure rests, then snaps one step ahead
     * and springs back a little, like an impact sprinkler.
     */
    public float getRotationAngle(float partialTick) {
        float phase = this.getPhase(partialTick);
        if (this.getPressure() != SprinklerPressure.PULSING) {
            return (phase * STEP_ANGLE) % 360.0F;
        }
        float step = Mth.floor(phase);
        float snap = Math.min(1.0F, (phase - step) / SNAP);
        float c1 = 1.70158F;
        float eased = 1.0F + (c1 + 1.0F) * (float) Math.pow(snap - 1.0F, 3) + c1 * (float) Math.pow(snap - 1.0F, 2);
        return ((step + eased) * STEP_ANGLE) % 360.0F;
    }

    public float getRotationAngle() {
        return this.getRotationAngle(1.0F);
    }

    /** 1 right after a step, fading to 0 before the next one. */
    public float getJolt(float partialTick) {
        float phase = this.getPhase(partialTick);
        return 1.0F - (phase - Mth.floor(phase));
    }

    /** 0 while standing still, 1 at normal speed, more in the rain or at high pressure. */
    public float getSpeed() {
        return this.speed;
    }
}

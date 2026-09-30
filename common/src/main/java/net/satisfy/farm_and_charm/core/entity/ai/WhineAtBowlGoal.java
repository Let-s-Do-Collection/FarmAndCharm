package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

import java.util.List;

public class WhineAtBowlGoal extends BegAtBowlGoal<Wolf> {
    private static final List<SoundEvent> WHINE_SOUNDS = List.of(SoundEvents.WOLF_WHINE, SoundEvents.WOLF_PANT);
    private static final float BASE_VOLUME = 0.4f;
    private static final float BASE_PITCH = 0.4f;
    private static final double EVENING_SPEED_FACTOR = 0.8;
    private static final double OWNER_WHINE_DISTANCE = 4.0;
    private static final int OWNER_WHINE_CHANCE = 10;

    public WhineAtBowlGoal(Wolf wolf) {
        super(wolf);
    }

    @Override
    protected boolean isEnabled() {
        return PlatformHelper.isDogBeggingEnabled();
    }

    @Override
    protected double getSpeed(long dayTime) {
        return isEveningFeedingTime(dayTime) ? EVENING_SPEED_FACTOR : 1.0;
    }

    @Override
    protected void playBegSound() {
        SoundEvent sound = WHINE_SOUNDS.get(this.animal.getRandom().nextInt(WHINE_SOUNDS.size()));
        this.animal.playSound(sound, BASE_VOLUME + this.animal.getRandom().nextFloat() * 0.3f, BASE_PITCH + this.animal.getRandom().nextFloat() * 0.4f);
    }

    @Override
    protected void playGiveUpSound() {
        this.animal.playSound(SoundEvents.WOLF_GROWL, BASE_VOLUME, BASE_PITCH);
    }

    @Override
    protected void onBegTick(ServerLevel level) {
        LivingEntity owner = this.animal.getOwner();
        if (owner != null && this.animal.distanceTo(owner) < OWNER_WHINE_DISTANCE && level.getRandom().nextInt(100) < OWNER_WHINE_CHANCE) {
            this.animal.playSound(SoundEvents.WOLF_WHINE, 0.6f, 0.9f + level.getRandom().nextFloat() * 0.2f);
        }
    }
}

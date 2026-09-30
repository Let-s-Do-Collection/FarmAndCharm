package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.animal.Cat;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

public class MeowAtBowlGoal extends BegAtBowlGoal<Cat> {
    public MeowAtBowlGoal(Cat cat) {
        super(cat);
    }

    @Override
    protected boolean isEnabled() {
        return PlatformHelper.isCatBeggingEnabled();
    }

    @Override
    protected void playBegSound() {
        this.animal.playSound(SoundEvents.CAT_BEG_FOR_FOOD, 1.0f, 1.0f);
    }

    @Override
    protected void playGiveUpSound() {
        this.animal.playSound(SoundEvents.CAT_HISS, 1.0f, 1.0f);
    }
}

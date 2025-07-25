package net.satisfy.farm_and_charm.core.util;

import net.minecraft.world.entity.animal.Chicken;

/**
 * @author wdog5
 * @reason For Chicken Egg Timer
 */
public class ChickenLayEggUtil {

    /**
     * @author wdog5 - they should only walk towards it when the EggLayingCounter is going towards 0
     * @param chicken Chicken that need handled
     * @return when the chicken lay egg timer is going to 0
     */
    public static boolean willLayEgg(Chicken chicken) {
        return !chicken.level().isClientSide && chicken.isAlive() && !chicken.isBaby() && !chicken.isChickenJockey() && --chicken.eggTime <= 0;
    }
}

package net.satisfy.farm_and_charm.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.entity.animal.Chicken;

@Mixin(Chicken.class)
public interface ChickenAccessor {
    @Accessor("eggTime")
    void farmAndCharm$setEggTime(int time);

    @Accessor("eggTime")
    int farmAndCharm$getEggTime();
}

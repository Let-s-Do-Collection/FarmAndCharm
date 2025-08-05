package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.world.entity.animal.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Chicken.class)
public interface ChickenAccessor {
    @Accessor("eggTime")
    void farmAndCharm$setEggTime(int time);

    @Accessor("eggTime")
    int farmAndCharm$getEggTime();
}
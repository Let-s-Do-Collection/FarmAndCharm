package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TemptGoal.class)
public interface TemptGoalAccessor {
    @Accessor("player")
    Player getPlayer();
}
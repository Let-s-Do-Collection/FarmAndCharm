package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "net.minecraft.world.entity.animal.Cat$CatRelaxOnOwnerGoal")
public class CatGiftChanceMixin {

    @Shadow @Final
    private Cat cat;

    @ModifyConstant(
            method = "stop",
            constant = @Constant(doubleValue = 0.7)
    )
    private double increaseGiftChanceIfFed(double originalChance) {
        if (!(cat.level() instanceof ServerLevel serverLevel)) return originalChance;

        BlockPos pos = cat.blockPosition();
        for (BlockPos check : BlockPos.betweenClosed(pos.offset(-4, -2, -4), pos.offset(4, 2, 4))) {
            BlockEntity be = serverLevel.getBlockEntity(check);
            if (be instanceof PetBowlBlockEntity bowl && bowl.wasCatFed()) {
                bowl.resetFedFlags();
                return 1.0;
            }
        }

        return originalChance;
    }
}

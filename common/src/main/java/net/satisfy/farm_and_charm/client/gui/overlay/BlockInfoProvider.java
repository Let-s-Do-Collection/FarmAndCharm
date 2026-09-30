package net.satisfy.farm_and_charm.client.gui.overlay;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BlockInfoProvider {
    List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit);

    default void beforeBackground(Level level, BlockPos pos, BlockState state) {
    }
}

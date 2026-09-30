package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin {
    @Unique
    private static final VoxelShape BASE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    @Unique
    private static final VoxelShape[] CORNERS = new VoxelShape[]{
            Block.box(0.0, 7.0, 0.0, 1.0, 15.0, 1.0),
            Block.box(15.0, 7.0, 0.0, 16.0, 15.0, 1.0),
            Block.box(0.0, 7.0, 15.0, 1.0, 15.0, 16.0),
            Block.box(15.0, 7.0, 15.0, 16.0, 15.0, 16.0)
    };
    @Unique
    private static final VoxelShape TOP_LAYER = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    @Unique
    private static final VoxelShape FULL_CAMPFIRE_SHAPE = Shapes.or(BASE_SHAPE, CORNERS[0], CORNERS[1], CORNERS[2], CORNERS[3], TOP_LAYER);

    @Inject(method = "dowse", at = @At("HEAD"), cancellable = true)
    private static void modifyDowseParticles(@Nullable Entity entity, LevelAccessor levelAccessor, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {
        if (shouldCancelParticles(levelAccessor, blockPos)) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean shouldCancelParticles(LevelAccessor level, BlockPos pos) {
        return level.getBlockState(pos.above()).is(TagRegistry.SUPPRESS_CAMPFIRE_SMOKE_PARTICLES);
    }

    @Inject(method = "makeParticles", at = @At("HEAD"), cancellable = true)
    private static void onMakeParticles(Level level, BlockPos blockPos, boolean signalFire, boolean spawnExtraSmoke, CallbackInfo ci) {
        if (shouldCancelParticles(level, blockPos)) {
            ci.cancel();
        }
    }

    /**
     * @author satisfy
     * @reason adding a custom shape to the campfire block depending on the presence of specific blocks above it.
     */
    @Overwrite
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (world.getBlockState(pos.above()).is(TagRegistry.COOKING_POTS)) {
            return FULL_CAMPFIRE_SHAPE;
        }
        return BASE_SHAPE;
    }
}

package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CattlegridBlock extends Block {
    public CattlegridBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof Player p) {
            if (p.isCreative()) return;
            p.setSprinting(false);
            p.makeStuckInBlock(state, new Vec3(0.98, 1.0, 0.98));
            return;
        }
        if (entity instanceof Cat) {
            Vec3 v = entity.getDeltaMovement();
            entity.setDeltaMovement(v.x * 1.03, v.y, v.z * 1.03);
            return;
        }
        if (entity instanceof Mob m && isBlockedMob(m)) {
            entity.makeStuckInBlock(state, new Vec3(0.0, 1.0, 0.0));
            return;
        }
        if (entity instanceof LivingEntity l) {
            l.makeStuckInBlock(state, new Vec3(0.95, 1.0, 0.95));
        }
    }

    private boolean isBlockedMob(Mob mob) {
        return mob instanceof Cow || mob instanceof Sheep || mob instanceof Pig || mob instanceof Chicken || mob instanceof AbstractHorse;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }
}

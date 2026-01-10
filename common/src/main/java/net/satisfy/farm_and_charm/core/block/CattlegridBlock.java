package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CattlegridBlock extends Block {
    public CattlegridBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof Player player) {
            if (player.isCreative()) return;
            player.setSprinting(false);
            player.makeStuckInBlock(state, new Vec3(0.98, 1.0, 0.98));
            return;
        }
        if (entity instanceof Cat) {
            Vec3 vec3 = entity.getDeltaMovement();
            entity.setDeltaMovement(vec3.x * 1.03, vec3.y, vec3.z * 1.03);
            return;
        }
        if (entity instanceof Mob mob && isBlockedMob(mob)) {
            entity.makeStuckInBlock(state, new Vec3(0.0, 1.0, 0.0));
            return;
        }
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.makeStuckInBlock(state, new Vec3(0.95, 1.0, 0.95));
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

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        int earthy = 0xFFD966;
        int gold = 0xFFD700;

        if (Screen.hasShiftDown()) {
            tooltip.add(
                    Component.translatable("tooltip.farm_and_charm.cattlegrid.info_0")
                            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy)))
            );
        } else {
            Component key = Component.literal("[SHIFT]")
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(gold)));

            tooltip.add(
                    Component.translatable("tooltip.farm_and_charm.tooltip_information.hold", key)
                            .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy)))
            );
        }
    }
}

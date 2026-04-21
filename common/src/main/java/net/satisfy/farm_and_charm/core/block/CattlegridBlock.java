package net.satisfy.farm_and_charm.core.block;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CattlegridBlock extends Block {
    private static final VoxelShape FLOOR_COLLISION = Shapes.block();
    private static final VoxelShape MOB_WALLS = Shapes.or(
            Block.box(0.0, 16.0, 0.0, 16.0, 28.0, 2.0),
            Block.box(0.0, 16.0, 14.0, 16.0, 28.0, 16.0),
            Block.box(0.0, 16.0, 0.0, 2.0, 28.0, 16.0),
            Block.box(14.0, 16.0, 0.0, 16.0, 28.0, 16.0)
    );
    private static final VoxelShape MOB_COLLISION = Shapes.or(FLOOR_COLLISION, MOB_WALLS);

    public CattlegridBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity.getType().is(TagRegistry.CAN_WALK_OVER_CATTLEGRID)) {
            return;
        }

        if (entity instanceof Player player) {
            if (player.isCreative()) {
                return;
            }

            player.setSprinting(false);
            player.makeStuckInBlock(state, new Vec3(0.98, 1.0, 0.98));
            return;
        }

        if (entity instanceof Cat) {
            Vec3 movement = entity.getDeltaMovement();
            entity.setDeltaMovement(movement.x * 1.03, movement.y, movement.z * 1.03);
            return;
        }

        if (entity instanceof Mob) {
            return;
        }

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.makeStuckInBlock(state, new Vec3(0.95, 1.0, 0.95));
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (!(context instanceof EntityCollisionContext entityContext)) {
            return MOB_COLLISION;
        }

        Entity entity = entityContext.getEntity();
        if (!(entity instanceof Mob) || entity.getType().is(TagRegistry.CAN_WALK_OVER_CATTLEGRID)) {
            return FLOOR_COLLISION;
        }

        return MOB_COLLISION;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        int earthyColor = 0xFFD966;
        int goldColor = 0xFFD700;

        if (Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("tooltip.farm_and_charm.cattlegrid.info_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthyColor))));
        } else {
            Component shiftKey = Component.literal("[SHIFT]").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(goldColor)));
            tooltip.add(Component.translatable("tooltip.farm_and_charm.tooltip_information.hold", shiftKey).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthyColor))));
        }
    }
}

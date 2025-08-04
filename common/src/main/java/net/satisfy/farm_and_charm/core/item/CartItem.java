package net.satisfy.farm_and_charm.core.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;


public class CartItem extends Item {

    public CartItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide) {
            Entity entity = null;
            if (this == ObjectRegistry.SUPPLY_CART.get()) {
                entity = EntityTypeRegistry.SUPPLY_CART.get().create(world);
            } else if (this == ObjectRegistry.PLOW.get()) {
                entity = EntityTypeRegistry.PLOW.get().create(world);
            }
            if (entity != null) {
                entity.setPos(context.getClickedPos().getX() + 0.5, context.getClickedPos().getY() + 1, context.getClickedPos().getZ() + 0.5);
                world.addFreshEntity(entity);
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WOOD_PLACE, entity.getSoundSource(), 1.0F, 1.0F);
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }
}
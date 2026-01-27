package net.satisfy.farm_and_charm.core.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

import java.util.List;

public class SupplyCartItem extends AbstractCartItem {
    public SupplyCartItem(Properties properties) {
        super(properties);
    }

    @Override
    protected Entity createCartEntity(Level level) {
        return EntityTypeRegistry.SUPPLY_CART.get().create(level);
    }

    @Override
    protected String getCartTitleKey() {
        return "tooltip.farm_and_charm.supply_cart.title";
    }

    @Override
    protected void appendShiftDetails(List<Component> tooltipComponents, int earthy) {
        tooltipComponents.add(Component.translatable("tooltip.farm_and_charm.cart.attach_horse").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.translatable("tooltip.farm_and_charm.supply_cart.action_open").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
    }
}
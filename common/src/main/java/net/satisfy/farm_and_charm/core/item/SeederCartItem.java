package net.satisfy.farm_and_charm.core.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

import java.util.List;

public class SeederCartItem extends AbstractCartItem {
    public SeederCartItem(Properties properties) {
        super(properties);
    }

    @Override
    protected Entity createCartEntity(Level level) {
        return EntityTypeRegistry.SEEDER.get().create(level);
    }

    @Override
    protected String getCartTitleKey() {
        return "tooltip.farm_and_charm.seeder.title";
    }

    @Override
    protected void appendShiftDetails(List<Component> tooltipComponents, int earthy) {
        tooltipComponents.add(Component.translatable("tooltip.farm_and_charm.cart.attach_horse").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Component.translatable("tooltip.farm_and_charm.seeder.action_open").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
        tooltipComponents.add(Component.translatable("tooltip.farm_and_charm.seeder.action_sow").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
    }
}

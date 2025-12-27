package net.satisfy.farm_and_charm.core.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

import java.util.List;

public class PlowCartItem extends AbstractCartItem {
    public PlowCartItem(Properties properties) {
        super(properties);
    }

    @Override
    protected Entity createCartEntity(Level level) {
        return EntityTypeRegistry.PLOW.get().create(level);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int earthy = 0xFFD966;

        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.farm_and_charm.plow_cart.info_0")
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
            tooltipComponents.add(Component.translatable("tooltip.farm_and_charm.plow_cart.info_1")
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
            tooltipComponents.add(Component.translatable("tooltip.farm_and_charm.plow_cart.info_2")
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
        }
    }
}
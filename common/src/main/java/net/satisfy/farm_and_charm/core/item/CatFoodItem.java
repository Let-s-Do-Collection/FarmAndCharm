package net.satisfy.farm_and_charm.core.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

import java.util.List;

public class CatFoodItem extends Item {
    public CatFoodItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        if (PlatformHelper.enableCatTamingChance()) {
            tooltip.add(Component.translatable("tooltip.farm_and_charm.animal_fed_to_cat").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.farm_and_charm.cat_effect_1").withStyle(ChatFormatting.BLUE));
        }
    }
}

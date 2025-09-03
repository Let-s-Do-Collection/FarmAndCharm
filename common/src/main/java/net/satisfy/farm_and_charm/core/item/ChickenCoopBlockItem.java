package net.satisfy.farm_and_charm.core.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ChickenCoopBlockItem extends BlockItem {

    public ChickenCoopBlockItem(Block block, Properties properties) {
        super(block, properties);

    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        return data != null && !data.copyTag().isEmpty();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null) return;
        var tag = data.copyTag();
        int eggCount = tag.getInt("EggCount");
        ListTag chickens = tag.getList("Chickens", 10);
        boolean added = false;
        if (!chickens.isEmpty()) {
            tooltip.add(Component.empty());
            added = true;
            tooltip.add(Component.translatable("tooltip.farm_and_charm.chickencoop_chickens", chickens.size(), 3).withStyle(ChatFormatting.GRAY));
        }
        if (eggCount > 0) {
            if (!added) tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.farm_and_charm.chickencoop_eggs", eggCount, 9).withStyle(ChatFormatting.GRAY));
        }
    }
}

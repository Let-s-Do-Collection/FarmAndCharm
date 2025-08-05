package net.satisfy.farm_and_charm.core.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ChickenCoopBlockItem extends BlockItem {
    public ChickenCoopBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        CompoundTag tag = itemStack.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();
        if (tag != null) {
            int eggs = tag.getInt("eggCount");
            ListTag chickens = tag.getList("stored_chickens", 10);
            tooltip.add(Component.translatable("tooltip.farm_and_charm.chickencoop_hens", chickens.size(), 3));
            tooltip.add(Component.translatable("tooltip.farm_and_charm.chickencoop_eggs", eggs, 9));
        }
    }
}
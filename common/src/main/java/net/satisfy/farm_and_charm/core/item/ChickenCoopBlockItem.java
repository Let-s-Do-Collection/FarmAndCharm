package net.satisfy.farm_and_charm.core.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ChickenCoopBlockItem extends BlockItem {

    public ChickenCoopBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = getBlockEntityData(stack);
        if (tag == null) return;

        int eggCount = tag.getInt("EggCount");
        ListTag chickens = tag.getList("Chickens", 10);

        if (!chickens.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.farm_and_charm.chickencoop_chickens", chickens.size(), 3).withStyle(ChatFormatting.GRAY));
        }

        if (eggCount > 0) {
            tooltip.add(Component.translatable("tooltip.farm_and_charm.chickencoop_eggs", eggCount, 9).withStyle(ChatFormatting.GRAY));
        }
    }
}

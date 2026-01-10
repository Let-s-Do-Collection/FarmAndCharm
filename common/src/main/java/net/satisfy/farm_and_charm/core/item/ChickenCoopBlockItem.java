package net.satisfy.farm_and_charm.core.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

import java.util.List;
import java.util.Objects;

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
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        CustomData entityData = stack.get(DataComponents.ENTITY_DATA);
        if (entityData != null) {
            CompoundTag tag = entityData.copyTag();
            boolean isCoopData = tag.contains("Chickens", 9) || tag.contains("EggCount", 3);

            if (isCoopData) {
                if (stack.get(DataComponents.BLOCK_ENTITY_DATA) == null) {
                    CompoundTag blockEntityTag = tag.copy();
                    blockEntityTag.putString("id", Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(EntityTypeRegistry.CHICKEN_COOP_BLOCK_ENTITY.get())).toString());
                    stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(blockEntityTag));
                }
            }

            stack.remove(DataComponents.ENTITY_DATA);
        }

        super.inventoryTick(stack, level, entity, slot, selected);
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

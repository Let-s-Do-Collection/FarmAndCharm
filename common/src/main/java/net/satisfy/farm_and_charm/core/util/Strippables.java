package net.satisfy.farm_and_charm.core.util;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.satisfy.farm_and_charm.core.item.CleaverItem;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Strippables extends AxeItem {
    public static final int CHOPS = 2;

    private Strippables(Tier tier, Properties properties) {
        super(tier, properties);
    }

    public static boolean isAxe(ItemStack stack) {
        return stack.is(ItemTags.AXES) && !(stack.getItem() instanceof CleaverItem);
    }

    public record Entry(ItemStack log, ItemStack stripped) {
    }

    public static List<Entry> all() {
        List<Entry> entries = new ArrayList<>();
        for (Map.Entry<Block, Block> entry : STRIPPABLES.entrySet()) {
            ItemStack log = new ItemStack(entry.getKey());
            ItemStack stripped = new ItemStack(entry.getValue());
            if (!log.isEmpty() && !stripped.isEmpty() && !ItemStack.isSameItem(log, stripped)) {
                entries.add(new Entry(log, stripped));
            }
        }
        return entries;
    }

    public static Optional<ItemStack> strip(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return Optional.empty();
        }
        Block stripped = STRIPPABLES.get(blockItem.getBlock());
        if (stripped == null || stripped.asItem() == blockItem) {
            return Optional.empty();
        }
        ItemStack result = new ItemStack(stripped);
        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }
}

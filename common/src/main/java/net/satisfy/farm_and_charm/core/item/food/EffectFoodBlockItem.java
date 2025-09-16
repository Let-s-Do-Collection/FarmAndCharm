package net.satisfy.farm_and_charm.core.item.food;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EffectFoodBlockItem extends BlockItem implements EffectFood {

    private final int foodStages;

    public EffectFoodBlockItem(Block block, Properties properties, int foodStages) {
        super(block, properties);
        this.foodStages = foodStages;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (!world.isClientSide) {
            List<Pair<MobEffectInstance, Float>> effects = EffectFoodHelper.getEffects(stack);
            for (Pair<MobEffectInstance, Float> effect : effects) {
                if (effect.getFirst() != null && world.random.nextFloat() < effect.getSecond()) {
                    user.addEffect(new MobEffectInstance(effect.getFirst()));
                }
            }
        }
        int stage = EffectFoodHelper.getStage(stack);
        int slot = -1;
        Inventory inv = null;
        if (user instanceof Player player && !player.isCreative()) {
            inv = player.getInventory();
            slot = inv.findSlotMatchingUnusedItem(stack);
        }
        ItemStack eaten = user.eat(world, stack);
        if (inv != null && stage < this.foodStages) {
            ItemStack next = EffectFoodHelper.setStage(new ItemStack(this), stage + 1);
            if (slot >= 0 && slot < inv.items.size()) {
                if (inv.getItem(slot).isEmpty()) {
                    inv.add(slot, next);
                    return eaten;
                }
            }
            int space = inv.getSlotWithRemainingSpace(next);
            if (space >= 0 && space < inv.items.size()) {
                inv.add(space, next);
            }
        }
        return eaten;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        EffectFoodHelper.getTooltip(itemStack, tooltip);
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player != null && player.isShiftKeyDown()) {
            return super.place(context);
        }
        return InteractionResult.PASS;
    }
}
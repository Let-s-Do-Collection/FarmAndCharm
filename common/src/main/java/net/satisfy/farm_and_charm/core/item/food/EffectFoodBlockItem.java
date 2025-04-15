package net.satisfy.farm_and_charm.core.item.food;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
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
            List<FoodProperties.PossibleEffect> effects = EffectFoodHelper.getEffects(stack);
            effects.forEach(chance -> {
                if (world.random.nextFloat() < chance.probability()) {
                    user.addEffect(new MobEffectInstance(chance.effect()));
                }
            });
        }
        int slot = -1;
        Inventory playerInventory = null;
        if (user instanceof Player player && !player.isCreative()) {
            playerInventory = player.getInventory();
            slot = playerInventory.findSlotMatchingUnusedItem(stack);
        }
        ItemStack returnStack = super.finishUsingItem(stack, world, user);
        int stage = EffectFoodHelper.getStage(stack);
        if (playerInventory != null && stage < this.foodStages) {
            ItemStack itemStack = EffectFoodHelper.setStage(new ItemStack(this), stage + 1);
            if (slot >= 0 && slot < playerInventory.items.size()) {
                if (playerInventory.getItem(slot).isEmpty()) {
                    playerInventory.add(slot, itemStack);
                    return returnStack;
                }
            }
            slot = playerInventory.getSlotWithRemainingSpace(itemStack);
            if (slot >= 0 && slot < playerInventory.items.size()) {
                playerInventory.add(slot, itemStack);
            }
        }
        return returnStack;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.empty());
        list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
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

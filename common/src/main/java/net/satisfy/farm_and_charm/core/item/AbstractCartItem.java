package net.satisfy.farm_and_charm.core.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractCartItem extends Item {
    protected AbstractCartItem(Properties properties) {
        super(properties);
    }

    protected abstract Entity createCartEntity(Level level);

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide) {
            Entity entity = this.createCartEntity(level);
            if (entity != null) {
                entity.setPos(context.getClickedPos().getX() + 0.5D, context.getClickedPos().getY() + 1.0D, context.getClickedPos().getZ() + 0.5D);
                level.addFreshEntity(entity);
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WOOD_PLACE, entity.getSoundSource(), 1.0F, 1.0F);
                context.getItemInHand().shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int earthy = 0xFFD966;
        int gold = 0xFFD700;

        tooltipComponents.add(Component.translatable("tooltip.farm_and_charm.canbeplaced")
                .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        tooltipComponents.add(Component.empty());

        if (!Screen.hasShiftDown()) {
            Component key = Component.literal("[SHIFT]").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(gold)));
            tooltipComponents.add(Component.translatable("tooltip.farm_and_charm.tooltip_information.hold", key)
                    .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
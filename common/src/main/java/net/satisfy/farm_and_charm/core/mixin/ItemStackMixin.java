package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.MobEffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow
    public abstract Item getItem();

    @Inject(method = "finishUsingItem", at = @At("RETURN"))
    private void addNourishmentAfterSoup(Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        if (livingEntity == null || level.isClientSide()) return;
        Item consumedItem = ((ItemStack) (Object) this).getItem();
        boolean isSoup = consumedItem == Items.MUSHROOM_STEW || consumedItem == Items.BEETROOT_SOUP || consumedItem == Items.RABBIT_STEW;
        if (!isSoup) return;
        int duration = 20 * 90;
        int amplifier = 0;
        livingEntity.addEffect(new MobEffectInstance(MobEffectRegistry.getHolder(MobEffectRegistry.SUSTENANCE), duration, amplifier, false, false));
    }

    @Inject(method = "getTooltipLines", at = @At("RETURN"), cancellable = true)
    private void farm_and_charm$addSoupEffectLine(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir) {
        ItemStack self = (ItemStack) (Object) this;
        Item item = self.getItem();
        if (!(item == Items.MUSHROOM_STEW || item == Items.BEETROOT_SOUP || item == Items.RABBIT_STEW)) return;
        int durationTicks = 20 * 90;
        int totalSeconds = durationTicks / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        String timeText = String.format("%02d:%02d", minutes, seconds);
        List<Component> lines = cir.getReturnValue();
        lines.add(Component.translatable("effect.farm_and_charm.sustenance").append(Component.literal(" (" + timeText + ")")).withStyle(ChatFormatting.BLUE));
        cir.setReturnValue(lines);
    }
}

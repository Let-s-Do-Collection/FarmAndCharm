package net.satisfy.farm_and_charm.core.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.satisfy.farm_and_charm.core.item.food.EffectFood;
import net.satisfy.farm_and_charm.core.registry.DataComponentRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow protected abstract <T extends TooltipProvider> void addToTooltip(DataComponentType<T> dataComponentType, Item.TooltipContext tooltipContext, Consumer<Component> consumer, TooltipFlag tooltipFlag);

    @Inject(method = "getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V"))
    public void addMagisteelAttributes(
            Item.TooltipContext ctx, Player p, TooltipFlag flag,
            CallbackInfoReturnable<List<Component>> cir,
            @Local Consumer<Component> consumer
    ) {
        if (this instanceof EffectFood food && food.addToTooltip()) {
            addToTooltip(DataComponentRegistry.FOOD_EFFECTS.get(), ctx, consumer, flag);
        }
    }
}

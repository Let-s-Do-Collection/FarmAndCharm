package net.satisfy.farm_and_charm.neoforge.core.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Inject(method = "getRarity", at = @At("RETURN"), cancellable = true)
    private void farm_and_charm$modifyRarity(CallbackInfoReturnable<Rarity> cir) {
        var farm_and_charm$stack = this.getItem().getDefaultInstance();
        if (farm_and_charm$stack.is(ObjectRegistry.CHICKEN_COOP_ITEM.get())) {
            CustomData data = farm_and_charm$stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
            if (data.copyTag() != null && data.contains("BlockEntityTag")) {
                cir.setReturnValue(Rarity.COMMON);
            }
        }
    }
}

package net.satisfy.farm_and_charm.core.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DungareesItem extends ArmorItem {
    private final ResourceLocation leggingsTexture;

    public DungareesItem(Holder<ArmorMaterial> armorMaterial, Type type, Properties properties, ResourceLocation leggingsTexture) {
        super(armorMaterial, type, properties);
        this.leggingsTexture = leggingsTexture;
    }

    public ResourceLocation getLeggingsTexture() {
        return leggingsTexture;
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.LEGS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable("tooltip.farm_and_charm.dungarees_1").withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.farm_and_charm.dungarees_2").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.farm_and_charm.dungarees_3").withStyle(ChatFormatting.BLUE));
    }
}
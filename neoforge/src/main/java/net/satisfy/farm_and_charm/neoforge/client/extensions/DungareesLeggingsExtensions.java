package net.satisfy.farm_and_charm.neoforge.client.extensions;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.satisfy.farm_and_charm.core.item.DungareesItem;
import net.satisfy.farm_and_charm.core.registry.ArmorRegistry;
import org.jetbrains.annotations.NotNull;

public class DungareesLeggingsExtensions implements IClientItemExtensions {
    @Override
    public @NotNull Model getGenericArmorModel(@NotNull LivingEntity entity, @NotNull ItemStack stack, @NotNull EquipmentSlot slot, @NotNull HumanoidModel<?> original) {
        if (slot != EquipmentSlot.LEGS || !(stack.getItem() instanceof DungareesItem legs)) return original;

        return ArmorRegistry.getLeggingsModel(legs, original.rightLeg, original.leftLeg, original.body, original);
    }
}
package net.satisfy.farm_and_charm.core.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import org.jetbrains.annotations.NotNull;

public class DungareesItem extends ArmorItem {
    private final ResourceLocation leggingsTexture;

    public DungareesItem(ArmorMaterial armorMaterial, Type type, Properties properties, ResourceLocation leggingsTexture) {
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
}

package net.satisfy.farm_and_charm.core.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;

import java.util.function.IntSupplier;

public class CleaverItem extends AxeItem {
    private final IntSupplier chops;

    public CleaverItem(Tier tier, IntSupplier chops, Properties properties) {
        super(tier, properties);
        this.chops = chops;
    }

    public int getChops() {
        return Math.max(1, this.chops.getAsInt());
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }
}

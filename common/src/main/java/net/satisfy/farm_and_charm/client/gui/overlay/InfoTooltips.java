package net.satisfy.farm_and_charm.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

public final class InfoTooltips {
    private InfoTooltips() {
    }

    public static boolean isHidden(boolean enabled) {
        if (!enabled) {
            return true;
        }
        if (!PlatformHelper.infoTooltipsNeedDungarees()) {
            return false;
        }
        Player player = Minecraft.getInstance().player;
        return player == null || !player.getItemBySlot(EquipmentSlot.LEGS).is(ObjectRegistry.DUNGAREES.get());
    }
}

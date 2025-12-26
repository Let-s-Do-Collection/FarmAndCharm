package net.satisfy.farm_and_charm.core.network.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.entity.AbstractCartEntity;
import net.satisfy.farm_and_charm.core.network.packet.UpdateCartPullingPacket;

public final class CartPullingPacketClientHandler {
    private CartPullingPacketClientHandler() {
    }

    public static void handle(UpdateCartPullingPacket pkt) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        Entity cartEntity = level.getEntity(pkt.cartId());
        if (!(cartEntity instanceof AbstractCartEntity cart)) {
            return;
        }

        if (pkt.pullingId() < 0) {
            cart.setPulling(null);
            return;
        }

        cart.setPulling(level.getEntity(pkt.pullingId()));
    }
}
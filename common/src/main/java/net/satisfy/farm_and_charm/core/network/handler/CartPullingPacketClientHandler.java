package net.satisfy.farm_and_charm.core.network.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.satisfy.farm_and_charm.core.entity.AbstractCartEntity;
import net.satisfy.farm_and_charm.core.network.packet.UpdateCartPullingPacket;

public final class CartPullingPacketClientHandler {
    private CartPullingPacketClientHandler() {
    }

    public static void handle(UpdateCartPullingPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }

        Entity entity = level.getEntity(packet.cartId());
        if (!(entity instanceof AbstractCartEntity cart)) {
            return;
        }

        int pullingId = packet.pullingId();
        if (pullingId < 0) {
            cart.setPulling(null);
            return;
        }

        Entity pullingEntity = level.getEntity(pullingId);
        if (pullingEntity != null && pullingEntity.isAlive()) {
            cart.setPulling(pullingEntity);
        } else {
            cart.setPulling(null);
        }
    }
}
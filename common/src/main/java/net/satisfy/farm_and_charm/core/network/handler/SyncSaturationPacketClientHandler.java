package net.satisfy.farm_and_charm.core.network.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.network.packet.SyncSaturationPacket;
import net.satisfy.farm_and_charm.core.util.SaturationTracker;

public class SyncSaturationPacketClientHandler {
    public static void handle(SyncSaturationPacket packet) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        

        Entity entity = level.getEntity(packet.entityId());
        if (entity instanceof SaturationTracker.SaturatedAnimal saturated) {
            saturated.farm_and_charm$getSaturationTracker().clientSync(packet.level(), packet.foodCounter());
        }
    }
}
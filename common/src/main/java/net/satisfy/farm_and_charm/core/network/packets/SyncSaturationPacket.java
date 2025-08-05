package net.satisfy.farm_and_charm.core.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.util.SaturationTracker;

public class SyncSaturationPacket {
    private final int entityId;
    private final int level;
    private final int foodCounter;

    public SyncSaturationPacket(int entityId, int level, int foodCounter) {
        this.entityId = entityId;
        this.level = level;
        this.foodCounter = foodCounter;
    }

    public static void encode(SyncSaturationPacket packet, RegistryFriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
        buf.writeInt(packet.level);
        buf.writeInt(packet.foodCounter);
    }

    public static SyncSaturationPacket decode(RegistryFriendlyByteBuf buf) {
        return new SyncSaturationPacket(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static void handle(SyncSaturationPacket packet) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        Entity entity = level.getEntity(packet.entityId);
        if (entity instanceof SaturationTracker.SaturatedAnimal saturated) {
            saturated.farm_and_charm$getSaturationTracker().clientSync(packet.level, packet.foodCounter);
        }
    }
}
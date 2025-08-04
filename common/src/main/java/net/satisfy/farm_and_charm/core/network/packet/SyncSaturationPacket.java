package net.satisfy.farm_and_charm.core.network.packet;

import net.minecraft.network.FriendlyByteBuf;

public class SyncSaturationPacket {
    private final int entityId;
    private final int level;
    private final int foodCounter;

    public SyncSaturationPacket(int entityId, int level, int foodCounter) {
        this.entityId = entityId;
        this.level = level;
        this.foodCounter = foodCounter;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getLevel() {
        return level;
    }

    public int getFoodCounter() {
        return foodCounter;
    }

    public static void encode(SyncSaturationPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.entityId);
        buf.writeInt(packet.level);
        buf.writeInt(packet.foodCounter);
    }

    public static SyncSaturationPacket decode(FriendlyByteBuf buf) {
        return new SyncSaturationPacket(buf.readInt(), buf.readInt(), buf.readInt());
    }
}

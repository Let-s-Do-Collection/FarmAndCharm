package net.satisfy.farm_and_charm.core.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import org.jetbrains.annotations.NotNull;

public record SyncSaturationPacket(int entityId, int level, int foodCounter) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncSaturationPacket> TYPE =
            new Type<>(PacketHandler.SYNC_SATURATION);

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncSaturationPacket> STREAM_CODEC =
            StreamCodec.of(SyncSaturationPacket::toNetwork, SyncSaturationPacket::fromNetwork);

    public static void toNetwork(RegistryFriendlyByteBuf registryFriendlyByteBuf, SyncSaturationPacket packet) {
        registryFriendlyByteBuf.writeInt(packet.entityId);
        registryFriendlyByteBuf.writeInt(packet.level);
        registryFriendlyByteBuf.writeInt(packet.foodCounter);
    }

    public static @NotNull SyncSaturationPacket fromNetwork(RegistryFriendlyByteBuf buf) {
        int entityId = buf.readInt();
        int level = buf.readInt();
        int foodCounter = buf.readInt();
        return new SyncSaturationPacket(entityId, level, foodCounter);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
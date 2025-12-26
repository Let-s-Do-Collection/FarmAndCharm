package net.satisfy.farm_and_charm.core.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.satisfy.farm_and_charm.FarmAndCharm;

public record UpdateCartPullingPacket(int pullingId, int cartId) implements CustomPacketPayload {
    public static final Type<UpdateCartPullingPacket> TYPE = new Type<>(FarmAndCharm.identifier("update_cart_pulling"));

    public static final StreamCodec<FriendlyByteBuf, UpdateCartPullingPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public UpdateCartPullingPacket decode(FriendlyByteBuf buf) {
            return new UpdateCartPullingPacket(buf.readVarInt(), buf.readVarInt());
        }

        @Override
        public void encode(FriendlyByteBuf buf, UpdateCartPullingPacket packet) {
            buf.writeVarInt(packet.pullingId());
            buf.writeVarInt(packet.cartId());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
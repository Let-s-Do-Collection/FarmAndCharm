package net.satisfy.farm_and_charm.core.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

public class PacketHandler {
    public static final ResourceLocation SET_SIGN_TEXT = new FarmAndCharmIdentifier("set_text");

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), SET_SIGN_TEXT, (buf, context) -> {
            SetTextPacket packet = SetTextPacket.decode(buf);
            context.queue(() -> SetTextPacket.handle(packet, (ServerPlayer) context.getPlayer()));
        });
    }

    public static void sendToServer(SetTextPacket packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        SetTextPacket.encode(packet, buf);
        NetworkManager.sendToServer(SET_SIGN_TEXT, buf);
    }
}
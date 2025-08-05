package net.satisfy.farm_and_charm.core.network;

import dev.architectury.networking.NetworkManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

@SuppressWarnings("removal")
public class PacketHandler {
    public static final ResourceLocation SET_SIGN_TEXT = FarmAndCharmIdentifier.of("set_text");

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), SET_SIGN_TEXT, (buf, context) -> {
            SetTextPacket packet = SetTextPacket.decode(buf);
            context.queue(() -> SetTextPacket.handle(packet, (ServerPlayer) context.getPlayer()));
        });
    }

    public static void sendToServer(SetTextPacket packet) {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), null);//Currently null
        SetTextPacket.encode(packet, buf);
        NetworkManager.sendToServer(SET_SIGN_TEXT, buf);
    }
}
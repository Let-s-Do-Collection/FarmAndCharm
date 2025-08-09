package net.satisfy.farm_and_charm.core.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.satisfy.farm_and_charm.core.network.handler.SyncSaturationPacketClientHandler;
import net.satisfy.farm_and_charm.core.network.packet.SetTextPacket;
import net.satisfy.farm_and_charm.core.network.packet.SyncSaturationPacket;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

public class PacketHandler {
    public static final ResourceLocation SET_SIGN_TEXT = FarmAndCharmIdentifier.of("set_text");
    public static final ResourceLocation SYNC_SATURATION = FarmAndCharmIdentifier.of("sync_saturation");

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(),
                SetTextPacket.TYPE,
                SetTextPacket.STREAM_CODEC, (setTextPacket, packetContext) -> {
            packetContext.queue(() ->
                    SetTextPacket.handle(setTextPacket, (ServerPlayer) packetContext.getPlayer()));
        });
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(),
                    SyncSaturationPacket.TYPE,
                    SyncSaturationPacket.STREAM_CODEC,
                    (packet, packetContext) ->
                            packetContext.queue(() -> SyncSaturationPacketClientHandler.handle(packet)));
        }
    }

    public static void sendToServer(SetTextPacket packet) {
        NetworkManager.sendToServer(packet);
    }

    public static void sendSaturationSync(SyncSaturationPacket packet, Entity entity) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                NetworkManager.sendToPlayer(player, packet);
            }
        }
    }

    public static void sendToClient(ServerPlayer player, SyncSaturationPacket packet) {
        NetworkManager.sendToPlayer(player, packet);
    }
}
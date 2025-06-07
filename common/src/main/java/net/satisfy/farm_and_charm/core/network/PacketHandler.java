package net.satisfy.farm_and_charm.core.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.satisfy.farm_and_charm.core.network.packets.SetTextPacket;
import net.satisfy.farm_and_charm.core.network.packets.SyncSaturationPacket;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

public class PacketHandler {
    public static final ResourceLocation SET_SIGN_TEXT = new FarmAndCharmIdentifier("set_text");
    public static final ResourceLocation SYNC_SATURATION = new FarmAndCharmIdentifier("sync_saturation");

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), SET_SIGN_TEXT, (buf, context) -> {
            SetTextPacket packet = SetTextPacket.decode(buf);
            context.queue(() -> SetTextPacket.handle(packet, (ServerPlayer) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.s2c(), SYNC_SATURATION, (buf, context) -> {
            SyncSaturationPacket packet = SyncSaturationPacket.decode(buf);
            context.queue(() -> SyncSaturationPacket.handle(packet));
        });
    }

    public static void sendToClient(ServerPlayer player, SyncSaturationPacket packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        SyncSaturationPacket.encode(packet, buf);
        NetworkManager.sendToPlayer(player, SYNC_SATURATION, buf);
    }

    public static void sendToServer(SetTextPacket packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        SetTextPacket.encode(packet, buf);
        NetworkManager.sendToServer(SET_SIGN_TEXT, buf);
    }

    public static void sendSaturationSync(SyncSaturationPacket packet, Entity entity) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                SyncSaturationPacket.encode(packet, buf);
                NetworkManager.sendToPlayer(player, SYNC_SATURATION, buf);
            }
        }
    }
}

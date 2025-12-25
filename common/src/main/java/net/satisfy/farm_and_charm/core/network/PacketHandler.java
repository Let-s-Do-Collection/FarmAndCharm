package net.satisfy.farm_and_charm.core.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.network.handler.SyncSaturationPacketClientHandler;
import net.satisfy.farm_and_charm.core.network.packet.SetTextPacket;
import net.satisfy.farm_and_charm.core.network.packet.SyncSaturationPacket;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

public class PacketHandler {
    public static final ResourceLocation SET_SIGN_TEXT = FarmAndCharm.identifier("set_text");
    public static final ResourceLocation SYNC_SATURATION = FarmAndCharm.identifier("sync_saturation");

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), SetTextPacket.TYPE, SetTextPacket.STREAM_CODEC, (pkt, ctx) -> ctx.queue(() -> SetTextPacket.handle(pkt, (ServerPlayer) ctx.getPlayer())));

        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), SyncSaturationPacket.TYPE, SyncSaturationPacket.STREAM_CODEC, (pkt, ctx) -> ctx.queue(() -> SyncSaturationPacketClientHandler.handle(pkt)));
        } else {
            NetworkManager.registerS2CPayloadType(SyncSaturationPacket.TYPE, SyncSaturationPacket.STREAM_CODEC);
        }
    }

    public static void sendSaturationSync(SyncSaturationPacket packet, Entity entity) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        for (ServerPlayer player : serverLevel.players()) {
            boolean canReceive;
            try {
                canReceive = NetworkManager.canPlayerReceive(player, SyncSaturationPacket.TYPE);
            } catch (RuntimeException exception) {
                continue;
            }

            if (!canReceive) {
                continue;
            }
            if (!isPlayerCloseToEntity(player, entity, 64.0)) {
                continue;
            }
            if (!isWearingDungarees(player)) {
                continue;
            }
            NetworkManager.sendToPlayer(player, packet);
        }
    }

    private static boolean isPlayerCloseToEntity(ServerPlayer player, Entity entity, double maxDistance) {
        double maxDistanceSquared = maxDistance * maxDistance;
        return player.distanceToSqr(entity) <= maxDistanceSquared;
    }

    private static boolean isWearingDungarees(ServerPlayer player) {
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (leggings.isEmpty()) {
            return false;
        }
        return leggings.is(ObjectRegistry.DUNGAREES.get());
    }

    public static void sendToClient(ServerPlayer player, SyncSaturationPacket packet) {
        if (NetworkManager.canPlayerReceive(player, SyncSaturationPacket.TYPE)) {
            NetworkManager.sendToPlayer(player, packet);
        }
    }

    public static void sendToServer(SetTextPacket packet) {
        NetworkManager.sendToServer(packet);
    }
}
package net.satisfy.farm_and_charm.core.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.satisfy.farm_and_charm.core.block.entity.TextEditableBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class SetTextPacket {
    private final BlockPos pos;
    private final List<String> texts;

    public SetTextPacket(BlockPos pos, List<String> texts) {
        this.pos = pos;
        this.texts = texts;
    }

    public static void encode(SetTextPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.texts.size());
        for (String text : msg.texts) {
            buf.writeUtf(text);
        }
    }

    public static SetTextPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int size = buf.readInt();
        List<String> texts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            texts.add(buf.readUtf(50));
        }
        return new SetTextPacket(pos, texts);
    }

    public static void handle(SetTextPacket msg, ServerPlayer player) {
        Level level = player.level();
        if (level.isLoaded(msg.pos)) {
            BlockEntity entity = level.getBlockEntity(msg.pos);
            if (entity instanceof TextEditableBlockEntity editable) {
                int maxLines = editable.getTextLineCount();
                for (int i = 0; i < Math.min(msg.texts.size(), maxLines); i++) {
                    editable.setText(i, Component.literal(msg.texts.get(i)));
                }
            }
        }
    }
}

package net.satisfy.farm_and_charm.core.util;

import dev.architectury.event.events.common.TickEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.satisfy.farm_and_charm.core.entity.AbstractCartEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

public class CartWorldData extends SavedData {
    private static boolean initialized;
    private static CartWorldData clientInstance;

    private final Int2ObjectMap<AbstractCartEntity> pulling = new Int2ObjectOpenHashMap<>();

    public static void ensureInitialized() {
        if (initialized) {
            return;
        }
        initialized = true;
        TickEvent.SERVER_LEVEL_POST.register(level -> CartWorldData.get(level).tick());
    }

    public void addPulling(AbstractCartEntity cart) {
        Entity puller = cart.getPulling();
        if (puller == null) {
            return;
        }
        this.pulling.put(puller.getId(), cart);
        setDirty();
    }

    public void tick() {
        Iterator<Integer> iterator = this.pulling.keySet().iterator();
        while (iterator.hasNext()) {
            int pullId = iterator.next();
            AbstractCartEntity cart = this.pulling.get(pullId);
            if (cart == null || cart.shouldStopPulledTick()) {
                iterator.remove();
                setDirty();
            } else {
                if (!(cart.getPulling() instanceof AbstractCartEntity)) {
                    cart.pulledPostTick();
                }
            }
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag cartList = new ListTag();
        for (AbstractCartEntity cart : this.pulling.values()) {
            cartList.add(NbtUtils.createUUID(cart.getUUID()));
        }
        tag.put("cartList", cartList);
        return tag;
    }

    public static CartWorldData createFromNbt(CompoundTag tag, ServerLevel level) {
        CartWorldData data = new CartWorldData();
        ListTag cartList = tag.getList("cartList", Tag.TAG_INT_ARRAY);
        for (Tag value : cartList) {
            UUID uuid = NbtUtils.loadUUID(value);
            Entity entity = level.getEntity(uuid);
            if (entity instanceof AbstractCartEntity cart) {
                data.addPulling(cart);
            }
        }
        return data;
    }

    public static CartWorldData get(Level level) {
        ensureInitialized();
        if (level.isClientSide()) {
            if (clientInstance == null) {
                clientInstance = new CartWorldData();
            }
            return clientInstance;
        }
        return getServer(Objects.requireNonNull(level.getServer()), level.dimension());
    }

    private static CartWorldData getServer(MinecraftServer server, ResourceKey<Level> levelType) {
        ServerLevel level = Objects.requireNonNull(server.getLevel(levelType));
        Factory<CartWorldData> factory = new Factory<>(
                CartWorldData::new,
                (tag, provider) -> CartWorldData.createFromNbt(tag, level),
                null
        );
        return level.getDataStorage().computeIfAbsent(factory, "farm_and_charm_cart_world");
    }

    public void removePullingByCart(AbstractCartEntity cart) {
        Iterator<Int2ObjectMap.Entry<AbstractCartEntity>> iterator = this.pulling.int2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            Int2ObjectMap.Entry<AbstractCartEntity> entry = iterator.next();
            if (entry.getValue() == cart) {
                iterator.remove();
                setDirty();
            }
        }
    }
}
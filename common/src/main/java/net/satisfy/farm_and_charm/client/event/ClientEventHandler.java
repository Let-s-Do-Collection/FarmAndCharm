package net.satisfy.farm_and_charm.client.event;

import dev.architectury.event.events.client.ClientTickEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.satisfy.farm_and_charm.client.util.CartWheelUtil;
import net.satisfy.farm_and_charm.core.entity.AbstractCartEntity;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import net.satisfy.farm_and_charm.core.util.CartWorldData;

import java.util.List;

public final class ClientEventHandler {

    private static final Int2ObjectOpenHashMap<List<CartWheelUtil.CartWheel>> WHEELS = new Int2ObjectOpenHashMap<>();

    private ClientEventHandler() {
    }

    public static void init() {
        ClientTickEvent.CLIENT_POST.register(ClientEventHandler::onClientTick);
    }

    private static void onClientTick(Minecraft minecraft) {
        if (minecraft.level == null || minecraft.isPaused()) {
            return;
        }

        ClientLevel level = minecraft.level;

        CartWorldData.get(level).tick();

        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof AbstractCartEntity cart)) {
                continue;
            }

            List<CartWheelUtil.CartWheel> wheels = WHEELS.get(cart.getId());
            if (wheels == null) {
                wheels = CartWheelUtil.createDefaultWheels(cart);
                WHEELS.put(cart.getId(), wheels);
            }

            CartWheelUtil.tickCart(cart, wheels, SoundEventRegistry.CART_MOVING.get());
        }
    }
}
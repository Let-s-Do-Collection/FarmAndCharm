package net.satisfy.farm_and_charm.client;

import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.entity.CartWorldData;

public final class FarmAndCharmClientRuntime {
    private FarmAndCharmClientRuntime() {
    }

    public static void init() {
        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            if (minecraft.level == null) {
                return;
            }
            if (minecraft.isPaused()) {
                return;
            }
            Level level = minecraft.level;
            CartWorldData.get(level).tick();
        });
    }
}
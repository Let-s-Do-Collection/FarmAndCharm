package net.satisfy.farm_and_charm.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;

final class WaterTint {
    private WaterTint() {
    }

    static void apply(Particle particle, ClientLevel level, double x, double y, double z) {
        int color = BiomeColors.getAverageWaterColor(level, BlockPos.containing(x, y, z));
        particle.setColor((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F);
    }
}

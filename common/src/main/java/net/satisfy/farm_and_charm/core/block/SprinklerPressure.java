package net.satisfy.farm_and_charm.core.block;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum SprinklerPressure implements StringRepresentable {
    STEADY("steady", 1.0F),
    PULSING("pulsing", 1.0F),
    HIGH("high", 2.5F);

    private final String name;
    private final float speed;

    SprinklerPressure(String name, float speed) {
        this.name = name;
        this.speed = speed;
    }

    public float getSpeed() {
        return this.speed;
    }

    public String getTranslationKey() {
        return "hud.farm_and_charm.sprinkler_pressure." + this.name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}

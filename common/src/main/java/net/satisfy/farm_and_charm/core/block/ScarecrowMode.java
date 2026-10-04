package net.satisfy.farm_and_charm.core.block;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/** How the Scarecrow moves. Only changes the animation, not the crop growth. */
public enum ScarecrowMode implements StringRepresentable {
    CALM("calm"),
    WINDY("windy"),
    WATCHFUL("watchful");

    private final String name;

    ScarecrowMode(String name) {
        this.name = name;
    }

    public String getTranslationKey() {
        return "hud.farm_and_charm.scarecrow_mode." + this.name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return this.name;
    }
}

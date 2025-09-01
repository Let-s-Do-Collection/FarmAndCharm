package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.satisfy.farm_and_charm.FarmAndCharm;

public class SoundEventRegistry {

    private static final Registrar<SoundEvent> SOUND_EVENTS = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.SOUND_EVENT).getRegistrar();

    public static final RegistrySupplier<SoundEvent> COOKING_POT_BOILING = create("cooking_pot_boiling");
    public static final RegistrySupplier<SoundEvent> CRAFTING_BOWL_STIRRING = create("crafting_bowl_stirring");
    public static final RegistrySupplier<SoundEvent> MINCER_CRANKING = create("mincer_cranking");
    public static final RegistrySupplier<SoundEvent> ROASTER_COOKING = create("roaster_cooking");
    public static final RegistrySupplier<SoundEvent> STOVE_CRACKLING = create("stove_crackling");
    public static final RegistrySupplier<SoundEvent> WATER_SPRINKLER = create("water_sprinkler");
    public static final RegistrySupplier<SoundEvent> CART_MOVING = create("cart_moving");;

    public static void init() {}

    private static RegistrySupplier<SoundEvent> create(String name) {
        ResourceLocation id = FarmAndCharm.identifier(name);
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
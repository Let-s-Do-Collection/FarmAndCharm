package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.effect.ChickenEffect;
import net.satisfy.farm_and_charm.core.effect.DogFoodEffect;
import net.satisfy.farm_and_charm.core.effect.FarmersBlessingEffect;
import net.satisfy.farm_and_charm.core.effect.FeastEffect;
import net.satisfy.farm_and_charm.core.effect.GrandmasBlessingEffect;
import net.satisfy.farm_and_charm.core.effect.HorseFodderEffect;
import net.satisfy.farm_and_charm.core.effect.RestedEffect;
import net.satisfy.farm_and_charm.core.effect.SatiationEffect;
import net.satisfy.farm_and_charm.core.effect.SustenanceEffect;

public class MobEffectRegistry {
    private static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.MOB_EFFECT);

    public static final ResourceLocation HORSE_FODDER = FarmAndCharm.identifier("horse_fodder");
    public static final ResourceLocation DOG_FOOD = FarmAndCharm.identifier("dog_food");
    public static final ResourceLocation CLUCK = FarmAndCharm.identifier("cluck");
    public static final ResourceLocation GRANDMAS_BLESSING = FarmAndCharm.identifier("grandmas_blessing");
    public static final ResourceLocation RESTED = FarmAndCharm.identifier("rested");
    public static final ResourceLocation FARMERS_BLESSING = FarmAndCharm.identifier("farmers_blessing");
    public static final ResourceLocation SUSTENANCE = FarmAndCharm.identifier("sustenance");
    public static final ResourceLocation SATIATION = FarmAndCharm.identifier("satiation");
    public static final ResourceLocation FEAST = FarmAndCharm.identifier("feast");

    public static void init() {
        EFFECTS.register();
        EFFECTS.register(HORSE_FODDER, HorseFodderEffect::new);
        EFFECTS.register(DOG_FOOD, DogFoodEffect::new);
        EFFECTS.register(CLUCK, ChickenEffect::new);
        EFFECTS.register(GRANDMAS_BLESSING, GrandmasBlessingEffect::new);
        EFFECTS.register(RESTED, RestedEffect::new);
        EFFECTS.register(FARMERS_BLESSING, FarmersBlessingEffect::new);
        EFFECTS.register(SUSTENANCE, SustenanceEffect::new);
        EFFECTS.register(SATIATION, SatiationEffect::new);
        EFFECTS.register(FEAST, FeastEffect::new);
    }

    public static Holder<MobEffect> getHolder(ResourceLocation id) {
        Holder<MobEffect> holder = EFFECTS.getRegistrar().getHolder(id);
        if (holder == null) {
            throw new IllegalStateException("MobEffect not registered: " + id);
        }
        return holder;
    }

    public static MobEffectInstance inst(ResourceLocation id, int duration) {
        return new MobEffectInstance(getHolder(id), duration);
    }
}
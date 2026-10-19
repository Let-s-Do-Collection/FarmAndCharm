package net.satisfy.farm_and_charm;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.farm_and_charm.core.event.CleaverEvents;
import net.satisfy.farm_and_charm.core.event.VanillaItemPlacements;
import net.satisfy.foundation.overlay.BlockInfoSync;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.farm_and_charm.core.registry.*;
import net.satisfy.farm_and_charm.core.util.CartInteractionHooks;
import net.satisfy.foundation.rarity.FoundationRarities;
import net.satisfy.foundation.rarity.FoundationRarity;
import net.satisfy.foundation.entity.equipment.ZombieEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.ItemLike;

public class FarmAndCharm {
    public static final String MOD_ID = "farm_and_charm";

    public static ResourceLocation identifier(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static void init() {
        MobEffectRegistry.init();
        ObjectRegistry.init();
        ZombieEquipment.add(0.03).slot(EquipmentSlot.MAINHAND, ObjectRegistry.PITCHFORK);
        CompostableRegistry.registerCompostable();
        FlammableBlockRegistry.init();
        FeatureRegistry.init();
        VanillaItemPlacements.init();
        EntityTypeRegistry.init();
        TabRegistry.init();
        ScreenhandlerTypeRegistry.init();
        SoundEventRegistry.init();
        RecipeTypeRegistry.init();
        VillagerTradeRegistryHandler.init();
        PacketHandler.init();
        CartInteractionHooks.init();
        CleaverEvents.init();
        LifecycleEvent.SETUP.register(FarmAndCharm::registerRarities);
    }

    private static void registerRarities() {
        registerRarity(FoundationRarity.COMMON,
                ObjectRegistry.YEAST, ObjectRegistry.BUTTER, ObjectRegistry.DOUGH, ObjectRegistry.RAW_PASTA, ObjectRegistry.FLOUR,
                ObjectRegistry.MINCED_BEEF, ObjectRegistry.LAMB_HAM, ObjectRegistry.BACON, ObjectRegistry.CHICKEN_PARTS,
                ObjectRegistry.COOKED_MINCED_BEEF, ObjectRegistry.COOKED_BACON, ObjectRegistry.COOKED_CHICKEN_PARTS, ObjectRegistry.COOKED_LAMB_HAM,
                ObjectRegistry.CORN, ObjectRegistry.BARLEY, ObjectRegistry.OAT, ObjectRegistry.STRAWBERRY, ObjectRegistry.LETTUCE, ObjectRegistry.TOMATO, ObjectRegistry.ONION, ObjectRegistry.ROTTEN_TOMATO,
                ObjectRegistry.TOMATO_SEEDS, ObjectRegistry.LETTUCE_SEEDS, ObjectRegistry.STRAWBERRY_SEEDS, ObjectRegistry.OAT_SEEDS, ObjectRegistry.BARLEY_SEEDS, ObjectRegistry.KERNELS,
                ObjectRegistry.ROPE,
                ObjectRegistry.STRAWBERRY_BAG, ObjectRegistry.CARROT_BAG, ObjectRegistry.POTATO_BAG, ObjectRegistry.BEETROOT_BAG, ObjectRegistry.LETTUCE_BAG,
                ObjectRegistry.TOMATO_BAG, ObjectRegistry.CORN_BAG, ObjectRegistry.ONION_BAG, ObjectRegistry.FLOUR_BAG,
                ObjectRegistry.OAT_BALL, ObjectRegistry.BARLEY_BALL,
                ObjectRegistry.MINCER, ObjectRegistry.CRAFTING_BOWL, ObjectRegistry.CUTTING_BOARD, ObjectRegistry.COOKING_POT, ObjectRegistry.ROASTER,
                ObjectRegistry.FEEDING_TROUGH, ObjectRegistry.WATER_TROUGH, ObjectRegistry.SILO_WOOD, ObjectRegistry.PET_BOWL, ObjectRegistry.CHICKEN_NEST, ObjectRegistry.CHICKEN_COOP_ITEM,
                ObjectRegistry.WINDOW_SILL, ObjectRegistry.TOOL_RACK, ObjectRegistry.STURDY_LADDER, ObjectRegistry.IRON_DIVIDER, ObjectRegistry.CHICKEN_FENCE, ObjectRegistry.CATTLEGRID, ObjectRegistry.TIMBER_WELL);
        registerRarity(FoundationRarity.UNCOMMON,
                ObjectRegistry.FERTILIZER, ObjectRegistry.CAT_FOOD, ObjectRegistry.DOG_FOOD, ObjectRegistry.HORSE_FODDER, ObjectRegistry.CHICKEN_FEED,
                ObjectRegistry.STRAWBERRY_TEA_CUP, ObjectRegistry.NETTLE_TEA_CUP, ObjectRegistry.RIBWORT_TEA_CUP,
                ObjectRegistry.FARMER_SALAD, ObjectRegistry.SIMPLE_TOMATO_SOUP, ObjectRegistry.POTATO_SOUP, ObjectRegistry.ONION_SOUP, ObjectRegistry.BARLEY_SOUP,
                ObjectRegistry.CORN_GRITS, ObjectRegistry.OATMEAL_WITH_STRAWBERRIES, ObjectRegistry.PASTA_WITH_ONION_SAUCE, ObjectRegistry.ROASTED_CHICKEN,
                ObjectRegistry.PITCHFORK, ObjectRegistry.IRON_CLEAVER, ObjectRegistry.SUPPLY_CART, ObjectRegistry.PLOW, ObjectRegistry.SEEDER,
                ObjectRegistry.WATER_SPRINKLER, ObjectRegistry.SILO_COPPER, ObjectRegistry.FERTILIZED_SOIL_BLOCK, ObjectRegistry.FERTILIZED_FARM_BLOCK,
                ObjectRegistry.WILD_RIBWORT, ObjectRegistry.WILD_NETTLE, ObjectRegistry.WILD_EMMER, ObjectRegistry.WILD_CORN, ObjectRegistry.WILD_BARLEY, ObjectRegistry.WILD_OAT,
                ObjectRegistry.WILD_CARROTS, ObjectRegistry.WILD_BEETROOTS, ObjectRegistry.WILD_POTATOES, ObjectRegistry.WILD_TOMATOES, ObjectRegistry.WILD_LETTUCE,
                ObjectRegistry.WILD_ONIONS, ObjectRegistry.WILD_STRAWBERRIES);
        registerRarity(FoundationRarity.RARE,
                ObjectRegistry.STRAWBERRY_TEA, ObjectRegistry.NETTLE_TEA, ObjectRegistry.RIBWORT_TEA, ObjectRegistry.OAT_PANCAKE, ObjectRegistry.ROASTED_CORN, ObjectRegistry.FARMERS_BREAD_ITEM,
                ObjectRegistry.BACON_WITH_EGGS, ObjectRegistry.SAUSAGE_WITH_OAT_PATTY, ObjectRegistry.LAMB_WITH_CORN, ObjectRegistry.BEEF_PATTY_WITH_VEGETABLES,
                ObjectRegistry.BARLEY_PATTIES_WITH_POTATOES, ObjectRegistry.CHICKEN_WRAPPED_IN_BACON, ObjectRegistry.COOKED_SALMON, ObjectRegistry.COOKED_COD, ObjectRegistry.GOULASH,
                ObjectRegistry.COMPOST, ObjectRegistry.DOG_FOOD_BAG, ObjectRegistry.CAT_FOOD_BAG, ObjectRegistry.STOVE,
                ObjectRegistry.DUNGAREES, ObjectRegistry.DIAMOND_CLEAVER);
        registerRarity(FoundationRarity.EPIC,
                ObjectRegistry.POTATO_WITH_ROAST_MEAT_ITEM, ObjectRegistry.BAKED_LAMB_HAM_ITEM, ObjectRegistry.STUFFED_CHICKEN_ITEM, ObjectRegistry.STUFFED_RABBIT_ITEM,
                ObjectRegistry.FARMERS_BREAKFAST_ITEM, ObjectRegistry.GRANDMOTHERS_STRAWBERRY_CAKE_ITEM, ObjectRegistry.NETHERITE_CLEAVER);
        registerRarity(FoundationRarity.LEGENDARY, ObjectRegistry.SCARECROW);
    }

    @SafeVarargs
    private static void registerRarity(FoundationRarity rarity, RegistrySupplier<? extends ItemLike>... entries) {
        for (RegistrySupplier<? extends ItemLike> entry : entries) {
            FoundationRarities.register(entry.get(), rarity);
        }
    }
}

package net.satisfy.farm_and_charm.core.registry;

import net.satisfy.foundation.flammable.FoundationFlammables;

import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.*;

public class FlammableBlockRegistry {
    public static void init() {
        FoundationFlammables.plant(WILD_RIBWORT, WILD_NETTLE, WILD_EMMER, WILD_CORN, WILD_BARLEY, WILD_OAT, WILD_CARROTS, WILD_BEETROOTS, WILD_POTATOES, WILD_TOMATOES, WILD_LETTUCE, WILD_ONIONS, WILD_STRAWBERRIES, CHICKEN_NEST);
        FoundationFlammables.wool(STRAWBERRY_BAG, CARROT_BAG, POTATO_BAG, BEETROOT_BAG, LETTUCE_BAG, TOMATO_BAG, CORN_BAG, ONION_BAG, FLOUR_BAG, ROPE_BLOCK, FEATHER_PILE);
        FoundationFlammables.hay(OAT_BALL, BARLEY_BALL, SCARECROW, STRAW_STABLE_FLOOR, WHEAT_PILE, DOG_FOOD_BAG, CAT_FOOD_BAG);
        FoundationFlammables.wood(FEEDING_TROUGH, WATER_TROUGH, SILO_WOOD, CUTTING_BOARD, PET_BOWL, CHICKEN_COOP, ROPE_KNOT, CHICKEN_FENCE, STABLE_FLOOR, TRAMPLED_STABLE_FLOOR, TIMBER_WELL);
    }
}

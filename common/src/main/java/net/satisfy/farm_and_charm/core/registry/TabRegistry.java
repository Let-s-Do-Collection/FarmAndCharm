package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.FarmAndCharm;

public class TabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.CREATIVE_MODE_TAB);

    @SuppressWarnings("unused")
    public static final RegistrySupplier<CreativeModeTab> FARM_AND_CHARM_TAB = CREATIVE_MODE_TABS.register("farm_and_charm", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(ObjectRegistry.SUPPLY_CART.get()))
            .title(Component.translatable("creativetab.farm_and_charm.tab"))
            .displayItems((parameters, output) -> {
                output.accept(ObjectRegistry.KERNELS.get());
                output.accept(ObjectRegistry.CORN.get());
                output.accept(ObjectRegistry.OAT_SEEDS.get());
                output.accept(ObjectRegistry.OAT.get());
                output.accept(ObjectRegistry.BARLEY_SEEDS.get());
                output.accept(ObjectRegistry.BARLEY.get());
                output.accept(ObjectRegistry.LETTUCE_CROP.get());
                output.accept(ObjectRegistry.LETTUCE.get());
                output.accept(ObjectRegistry.ONION.get());
                output.accept(ObjectRegistry.TOMATO_SEEDS.get());
                output.accept(ObjectRegistry.TOMATO.get());
                output.accept(ObjectRegistry.ROTTEN_TOMATO.get());
                output.accept(ObjectRegistry.STRAWBERRY_SEEDS.get());
                output.accept(ObjectRegistry.STRAWBERRY.get());
                output.accept(ObjectRegistry.WILD_NETTLE.get());
                output.accept(ObjectRegistry.WILD_RIBWORT.get());
                output.accept(ObjectRegistry.WILD_EMMER.get());
                output.accept(ObjectRegistry.WILD_BEETROOTS.get());
                output.accept(ObjectRegistry.WILD_POTATOES.get());
                output.accept(ObjectRegistry.WILD_CARROTS.get());
                output.accept(ObjectRegistry.WILD_BARLEY.get());
                output.accept(ObjectRegistry.WILD_CORN.get());
                output.accept(ObjectRegistry.WILD_OAT.get());
                output.accept(ObjectRegistry.WILD_LETTUCE.get());
                output.accept(ObjectRegistry.WILD_ONIONS.get());
                output.accept(ObjectRegistry.WILD_TOMATOES.get());
                output.accept(ObjectRegistry.WILD_STRAWBERRIES.get());
                output.accept(ObjectRegistry.LETTUCE_BAG.get());
                output.accept(ObjectRegistry.TOMATO_BAG.get());
                output.accept(ObjectRegistry.CARROT_BAG.get());
                output.accept(ObjectRegistry.POTATO_BAG.get());
                output.accept(ObjectRegistry.ONION_BAG.get());
                output.accept(ObjectRegistry.BEETROOT_BAG.get());
                output.accept(ObjectRegistry.CORN_BAG.get());
                output.accept(ObjectRegistry.STRAWBERRY_BAG.get());
                output.accept(ObjectRegistry.FLOUR_BAG.get());
                output.accept(ObjectRegistry.OAT_BALL.get());
                output.accept(ObjectRegistry.BARLEY_BALL.get());
                output.accept(ObjectRegistry.FERTILIZED_SOIL_BLOCK.get());
                output.accept(ObjectRegistry.FERTILIZED_FARM_BLOCK.get());
                output.accept(ObjectRegistry.STOVE.get());
                output.accept(ObjectRegistry.FEEDING_TROUGH.get());
                output.accept(ObjectRegistry.WATER_SPRINKLER.get());
                output.accept(ObjectRegistry.SILO_WOOD.get());
                output.accept(ObjectRegistry.SILO_COPPER.get());
                output.accept(ObjectRegistry.PITCHFORK.get());
                output.accept(ObjectRegistry.SUPPLY_CART.get());
                output.accept(ObjectRegistry.PLOW.get());
                output.accept(ObjectRegistry.WINDOW_SILL.get());
                output.accept(ObjectRegistry.TOOL_RACK.get());
                output.accept(ObjectRegistry.CRAFTING_BOWL.get());
                output.accept(ObjectRegistry.MINCER.get());
                output.accept(ObjectRegistry.COOKING_POT.get());
                output.accept(ObjectRegistry.ROASTER.get());
                output.accept(ObjectRegistry.BUTTER.get());
                output.accept(ObjectRegistry.YEAST.get());
                output.accept(ObjectRegistry.FLOUR.get());
                output.accept(ObjectRegistry.DOUGH.get());
                output.accept(ObjectRegistry.RAW_PASTA.get());
                output.accept(ObjectRegistry.MINCED_BEEF.get());
                output.accept(ObjectRegistry.LAMB_HAM.get());
                output.accept(ObjectRegistry.CHICKEN_PARTS.get());
                output.accept(ObjectRegistry.BACON.get());
                output.accept(ObjectRegistry.FARMER_SALAD.get());
                output.accept(ObjectRegistry.OATMEAL_WITH_STRAWBERRIES.get());
                output.accept(ObjectRegistry.SIMPLE_TOMATO_SOUP.get());
                output.accept(ObjectRegistry.BARLEY_SOUP.get());
                output.accept(ObjectRegistry.ONION_SOUP.get());
                output.accept(ObjectRegistry.POTATO_SOUP.get());
                output.accept(ObjectRegistry.GOULASH.get());
                output.accept(ObjectRegistry.PASTA_WITH_ONION_SAUCE.get());
                output.accept(ObjectRegistry.CORN_GRITS.get());
                output.accept(ObjectRegistry.BARLEY_PATTIES_WITH_POTATOES.get());
                output.accept(ObjectRegistry.BEEF_PATTY_WITH_VEGETABLES.get());
                output.accept(ObjectRegistry.SAUSAGE_WITH_OAT_PATTY.get());
                output.accept(ObjectRegistry.ROASTED_CHICKEN.get());
                output.accept(ObjectRegistry.ROASTED_CORN.get());
                output.accept(ObjectRegistry.COOKED_SALMON.get());
                output.accept(ObjectRegistry.COOKED_COD.get());
                output.accept(ObjectRegistry.CHICKEN_WRAPPED_IN_BACON.get());
                output.accept(ObjectRegistry.LAMB_WITH_CORN.get());
                output.accept(ObjectRegistry.BACON_WITH_EGGS.get());
                output.accept(ObjectRegistry.OAT_PANCAKE.get());
                output.accept(ObjectRegistry.FARMERS_BREAKFAST.get());
                output.accept(ObjectRegistry.BAKED_LAMB_HAM.get());
                output.accept(ObjectRegistry.POTATO_WITH_ROAST_MEAT.get());
                output.accept(ObjectRegistry.STUFFED_CHICKEN.get());
                output.accept(ObjectRegistry.STUFFED_RABBIT.get());
                output.accept(ObjectRegistry.FARMERS_BREAD.get());
                output.accept(ObjectRegistry.GRANDMOTHERS_STRAWBERRY_CAKE.get());
                output.accept(ObjectRegistry.STRAWBERRY_TEA.get());
                output.accept(ObjectRegistry.NETTLE_TEA.get());
                output.accept(ObjectRegistry.RIBWORT_TEA.get());
                output.accept(ObjectRegistry.CAT_FOOD.get());
                output.accept(ObjectRegistry.DOG_FOOD.get());
                output.accept(ObjectRegistry.CHICKEN_FEED.get());
                output.accept(ObjectRegistry.HORSE_FODDER.get());
                output.accept(ObjectRegistry.FERTILIZER.get());
                output.accept(ObjectRegistry.SCARECROW.get());
                output.accept(ObjectRegistry.STRAWBERRY_TEA_CUP.get());
                output.accept(ObjectRegistry.NETTLE_TEA_CUP.get());
                output.accept(ObjectRegistry.RIBWORT_TEA_CUP.get());


            })
            .build());

    public static void init() {
        CREATIVE_MODE_TABS.register();
    }
}

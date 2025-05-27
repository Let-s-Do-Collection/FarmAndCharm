package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.level.entity.trade.TradeRegistry;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;

public class VillagerTradeRegistryHandler {
    public static void init() {
        registerFarmerTrades();
    }

    private static void registerFarmerTrades() {
        TradeRegistry.registerVillagerTrade(VillagerProfession.FARMER, 1, (entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 12), new ItemStack(ObjectRegistry.PITCHFORK.get(), 1), 2, 2, 0.05f));
        TradeRegistry.registerVillagerTrade(VillagerProfession.FARMER, 1, (entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, random.nextIntBetweenInclusive(2, 4)), new ItemStack(ObjectRegistry.BARLEY.get(), 18), 16, 2, 0.05f));
        TradeRegistry.registerVillagerTrade(VillagerProfession.FARMER, 1, (entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, random.nextIntBetweenInclusive(2, 4)), new ItemStack(ObjectRegistry.OAT.get(), 18), 16, 2, 0.05f));
        TradeRegistry.registerVillagerTrade(VillagerProfession.FARMER, 2, (entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 4), new ItemStack(ObjectRegistry.FARMER_SALAD.get(), 1), 6, 5, 0.05f));
        TradeRegistry.registerVillagerTrade(VillagerProfession.FARMER, 4, (entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 5), new ItemStack(ObjectRegistry.COMPOST.get(), 1), 5, 15, 0.05f));
        TradeRegistry.registerVillagerTrade(VillagerProfession.FARMER, 5, (entity, random) -> new MerchantOffer(new ItemStack(Items.EMERALD, 2), new ItemStack(ObjectRegistry.FERTILIZER.get(), 1), 24, 30, 0.05f));
    }
}

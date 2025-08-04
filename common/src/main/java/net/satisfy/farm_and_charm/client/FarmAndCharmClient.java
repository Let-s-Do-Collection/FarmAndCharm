package net.satisfy.farm_and_charm.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.satisfy.farm_and_charm.client.gui.CookingPotGui;
import net.satisfy.farm_and_charm.client.gui.RoasterGui;
import net.satisfy.farm_and_charm.client.gui.StoveGui;
import net.satisfy.farm_and_charm.client.model.*;
import net.satisfy.farm_and_charm.client.renderer.block.CraftingBowlRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.MincerRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.PlowCartRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.ScarecrowRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.StorageBlockEntityRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.StoveBlockRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.SupplyCartRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.ToolRackRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.WaterSprinklerRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.WindowSillRenderer;
import net.satisfy.farm_and_charm.client.renderer.entity.ChairRenderer;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ModelRegistry;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.StorageTypeRegistry;

import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.*;

@Environment(EnvType.CLIENT)
public class FarmAndCharmClient {

    public static void onInitializeClient() {
        RenderTypeRegistry.register(RenderType.cutout(), CRAFTING_BOWL.get(), WATER_SPRINKLER.get(),
                SCARECROW.get(), STOVE.get(), MINCER.get(), WILD_RIBWORT.get(), WILD_BARLEY.get(), WILD_CARROTS.get(),
                RIBWORT_TEA.get(), NETTLE_TEA.get(), STRAWBERRY_TEA.get(), WILD_BEETROOTS.get(), WILD_CORN.get(),
                WILD_EMMER.get(), WILD_LETTUCE.get(), WILD_NETTLE.get(), WILD_OAT.get(), WILD_ONIONS.get(), WILD_POTATOES.get(),
                WILD_TOMATOES.get(), WILD_STRAWBERRIES.get(), STUFFED_RABBIT.get(), STUFFED_CHICKEN.get(), FARMERS_BREAKFAST.get(),
                ROASTED_CORN_BLOCK.get(), OAT_PANCAKE_BLOCK.get(), CORN_CROP.get(), OAT_CROP.get(), BARLEY_CROP.get(), LETTUCE_CROP.get(),
                ONION_CROP.get(), TOMATO_CROP.get(), STRAWBERRY_CROP.get(), COOKING_POT.get(), ROASTER.get(), TOMATO_CROP_BODY.get()

        );

        ClientStorageTypes.init();
        registerStorageTypeRenderers();
        registerBlockEntityRenderer();
        MenuRegistry.registerScreenFactory(ScreenhandlerTypeRegistry.COOKING_POT_SCREEN_HANDLER.get(), CookingPotGui::new);
        MenuRegistry.registerScreenFactory(ScreenhandlerTypeRegistry.STOVE_SCREEN_HANDLER.get(), StoveGui::new);
        MenuRegistry.registerScreenFactory(ScreenhandlerTypeRegistry.ROASTER_SCREEN_HANDLER.get(), RoasterGui::new);
    }

    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(EntityTypeRegistry.ROTTEN_TOMATO, ThrownItemRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.SUPPLY_CART, SupplyCartRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.PLOW, PlowCartRenderer::new);
    }


    public static void preInitClient() {
        registerEntityRenderers();
        registerEntityModelLayer();
    }

    public static void registerEntityModelLayer() {
        EntityModelLayerRegistry.register(WaterSprinklerModel.LAYER_LOCATION, WaterSprinklerModel::getTexturedModelData);
        EntityModelLayerRegistry.register(CraftingBowlModel.LAYER_LOCATION, CraftingBowlModel::getTexturedModelData);
        EntityModelLayerRegistry.register(MincerModel.LAYER_LOCATION, MincerModel::getTexturedModelData);
        EntityModelLayerRegistry.register(ScarecrowModel.LAYER_LOCATION, ScarecrowModel::getTexturedModelData);
        EntityModelLayerRegistry.register(ModelRegistry.SUPPLY_CART, SupplyCartModel::createBodyLayer);
        EntityModelLayerRegistry.register(ModelRegistry.PLOW, PlowCartModel::createBodyLayer);
        EntityRendererRegistry.register(EntityTypeRegistry.CHAIR, ChairRenderer::new);
    }

    public static void registerStorageTypeRenderers() {
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.TOOL_RACK, new ToolRackRenderer());
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.WINDOW_SILL, new WindowSillRenderer());
    }

    public static void registerBlockEntityRenderer() {
        BlockEntityRendererRegistry.register(EntityTypeRegistry.STOVE_BLOCK_ENTITY.get(), StoveBlockRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.SCARECROW_BLOCK_ENTITY.get(), ScarecrowRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.MINCER_BLOCK_ENTITY.get(), MincerRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.CRAFTING_BOWL_BLOCK_ENTITY.get(), CraftingBowlRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.SPRINKLER_BLOCK_ENTITY.get(), WaterSprinklerRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.STORAGE_ENTITY.get(), context -> new StorageBlockEntityRenderer());
    }
}

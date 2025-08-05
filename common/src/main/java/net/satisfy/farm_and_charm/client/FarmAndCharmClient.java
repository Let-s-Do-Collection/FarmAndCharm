package net.satisfy.farm_and_charm.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.satisfy.farm_and_charm.client.gui.CookingPotGui;
import net.satisfy.farm_and_charm.client.gui.PetBowlEditGui;
import net.satisfy.farm_and_charm.client.gui.RoasterGui;
import net.satisfy.farm_and_charm.client.gui.StoveGui;
import net.satisfy.farm_and_charm.client.model.CraftingBowlModel;
import net.satisfy.farm_and_charm.client.model.DungareesLeggingsModel;
import net.satisfy.farm_and_charm.client.model.MincerModel;
import net.satisfy.farm_and_charm.client.model.PlowCartModel;
import net.satisfy.farm_and_charm.client.model.ScarecrowModel;
import net.satisfy.farm_and_charm.client.model.SupplyCartModel;
import net.satisfy.farm_and_charm.client.model.WaterSprinklerModel;
import net.satisfy.farm_and_charm.client.renderer.block.ChickenNestRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.CraftingBowlRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.MincerRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.PetBowlBlockRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.PlowCartRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.ScarecrowRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.StorageBlockEntityRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.StoveBlockRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.SupplyCartRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.ToolRackRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.WaterSprinklerRenderer;
import net.satisfy.farm_and_charm.client.renderer.block.WindowSillRenderer;
import net.satisfy.farm_and_charm.client.renderer.entity.ChairRenderer;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ModelRegistry;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.StorageTypeRegistry;

import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.BARLEY_CROP;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.COOKING_POT;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.CORN_CROP;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.CRAFTING_BOWL;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.FARMERS_BREAKFAST;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.LETTUCE_CROP;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.MINCER;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.NETTLE_TEA;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.OAT_CROP;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.OAT_PANCAKE_BLOCK;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.ONION_CROP;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.RIBWORT_TEA;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.ROASTED_CORN_BLOCK;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.ROASTER;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.SCARECROW;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.STOVE;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.STRAWBERRY_CROP;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.STRAWBERRY_TEA;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.STUFFED_CHICKEN;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.STUFFED_RABBIT;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.TOMATO_CROP;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.TOMATO_CROP_BODY;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WATER_SPRINKLER;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_BARLEY;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_BEETROOTS;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_CARROTS;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_CORN;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_EMMER;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_LETTUCE;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_NETTLE;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_OAT;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_ONIONS;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_POTATOES;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_RIBWORT;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_STRAWBERRIES;
import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.WILD_TOMATOES;

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
        EntityModelLayerRegistry.register(DungareesLeggingsModel.LAYER_LOCATION, DungareesLeggingsModel::createBodyLayer);
    }

    public static void registerStorageTypeRenderers() {
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.TOOL_RACK, new ToolRackRenderer());
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.WINDOW_SILL, new WindowSillRenderer());
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.CHICKEN_NEST, new ChickenNestRenderer());
    }

    public static void registerBlockEntityRenderer() {
        BlockEntityRendererRegistry.register(EntityTypeRegistry.STOVE_BLOCK_ENTITY.get(), StoveBlockRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.SCARECROW_BLOCK_ENTITY.get(), ScarecrowRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.MINCER_BLOCK_ENTITY.get(), MincerRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.CRAFTING_BOWL_BLOCK_ENTITY.get(), CraftingBowlRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.SPRINKLER_BLOCK_ENTITY.get(), WaterSprinklerRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.PET_BOWL_BLOCK_ENTITY.get(), context -> new PetBowlBlockRenderer());
        BlockEntityRendererRegistry.register(EntityTypeRegistry.STORAGE_ENTITY.get(), context -> new StorageBlockEntityRenderer());
    }

    public static void openPetBowlScreen(PetBowlBlockEntity entity) {
        Minecraft.getInstance().setScreen(new PetBowlEditGui(entity));
    }

}

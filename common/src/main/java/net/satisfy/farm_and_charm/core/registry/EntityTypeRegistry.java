package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.block.entity.*;
import net.satisfy.farm_and_charm.core.entity.ChairEntity;
import net.satisfy.farm_and_charm.core.entity.PlowCartEntity;
import net.satisfy.farm_and_charm.core.entity.RottenTomatoEntity;
import net.satisfy.farm_and_charm.core.entity.SupplyCartEntity;
import net.satisfy.farm_and_charm.core.util.FarmAndCharmIdentifier;

import java.util.HashSet;
import java.util.function.Supplier;

public class EntityTypeRegistry {
    private static final Registrar<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.BLOCK_ENTITY_TYPE).getRegistrar();
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<StorageBlockEntity>> STORAGE_ENTITY = registerBlockEntity("storage", () -> BlockEntityType.Builder.of(StorageBlockEntity::new, StorageTypeRegistry.registerBlocks(new HashSet<>()).toArray(new Block[0])).build(null));
    public static final RegistrySupplier<BlockEntityType<RoasterBlockEntity>> ROASTER_BLOCK_ENTITY = registerBlockEntity("roaster", () -> BlockEntityType.Builder.of(RoasterBlockEntity::new, ObjectRegistry.ROASTER.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<CraftingBowlBlockEntity>> CRAFTING_BOWL_BLOCK_ENTITY = registerBlockEntity("crafting_bowl", () -> BlockEntityType.Builder.of(CraftingBowlBlockEntity::new, ObjectRegistry.CRAFTING_BOWL.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<CookingPotBlockEntity>> COOKING_POT_BLOCK_ENTITY = registerBlockEntity("cooking_pot", () -> BlockEntityType.Builder.of(CookingPotBlockEntity::new, ObjectRegistry.COOKING_POT.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<StoveBlockEntity>> STOVE_BLOCK_ENTITY = registerBlockEntity("stove_block", () -> BlockEntityType.Builder.of(StoveBlockEntity::new, ObjectRegistry.STOVE.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<WaterSprinklerBlockEntity>> SPRINKLER_BLOCK_ENTITY = registerBlockEntity("water_sprinkler", () -> BlockEntityType.Builder.of(WaterSprinklerBlockEntity::new, ObjectRegistry.WATER_SPRINKLER.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<SiloBlockEntity>> SILO_BLOCK_ENTITY = registerBlockEntity("silo", () -> BlockEntityType.Builder.of(SiloBlockEntity::new, ObjectRegistry.SILO_WOOD.get(), ObjectRegistry.SILO_COPPER.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<FeedingTroughBlockEntity>> FEEDING_TROUGH_BLOCK_ENTITY = registerBlockEntity("feeding_trough", () -> BlockEntityType.Builder.of(FeedingTroughBlockEntity::new, ObjectRegistry.FEEDING_TROUGH.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<ScarecrowBlockEntity>> SCARECROW_BLOCK_ENTITY = registerBlockEntity("scarecrow", () -> BlockEntityType.Builder.of(ScarecrowBlockEntity::new, ObjectRegistry.SCARECROW.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<MincerBlockEntity>> MINCER_BLOCK_ENTITY = registerBlockEntity("mincer", () -> BlockEntityType.Builder.of(MincerBlockEntity::new, ObjectRegistry.MINCER.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<EffectFoodBlockEntity>> EFFECT_FOOD_BLOCK_ENTITY = registerBlockEntity("effect_food_block", () -> BlockEntityType.Builder.of(EffectFoodBlockEntity::new).build(null));
    public static final RegistrySupplier<BlockEntityType<PetBowlBlockEntity>> PET_BOWL_BLOCK_ENTITY = registerBlockEntity("pet_bowl", () -> BlockEntityType.Builder.of(PetBowlBlockEntity::new, ObjectRegistry.PET_BOWL.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<ChickenCoopBlockEntity>> CHICKEN_COOP_BLOCK_ENTITY = registerBlockEntity("chicken_coop", () -> BlockEntityType.Builder.of(ChickenCoopBlockEntity::new, ObjectRegistry.CHICKEN_COOP.get()).build(null));

    public static final RegistrySupplier<EntityType<RottenTomatoEntity>> ROTTEN_TOMATO = registerEntityType("rotten_tomato", () -> EntityType.Builder.<RottenTomatoEntity>of(RottenTomatoEntity::new, MobCategory.MISC).sized(0.25f, 0.25f).build(new FarmAndCharmIdentifier("rotten_tomato").toString()));
    public static final RegistrySupplier<EntityType<SupplyCartEntity>> SUPPLY_CART = registerEntityType("cart", () -> EntityType.Builder.of(SupplyCartEntity::new, MobCategory.MISC).sized(1.875f, 0.875f).clientTrackingRange(10).build(new FarmAndCharmIdentifier("supply_cart").toString()));
    public static final RegistrySupplier<EntityType<PlowCartEntity>> PLOW = registerEntityType("plow", () -> EntityType.Builder.of(PlowCartEntity::new, MobCategory.MISC).sized(1.875f, 0.875f).clientTrackingRange(10).build(new FarmAndCharmIdentifier("plow").toString()));
    public static final RegistrySupplier<EntityType<ChairEntity>> CHAIR = registerEntityType("chair", () -> EntityType.Builder.of(ChairEntity::new, MobCategory.MISC).sized(0.001F, 0.001F).build((new FarmAndCharmIdentifier("chair")).toString()));

    private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final String path, final Supplier<T> type) {
        return BLOCK_ENTITY_TYPES.register(new FarmAndCharmIdentifier(path), type);
    }

    private static <T extends EntityType<?>> RegistrySupplier<T> registerEntityType(final String path, final Supplier<T> type) {
        return ENTITY_TYPES.register(new FarmAndCharmIdentifier(path), type);
    }

    public static void init() {
        ENTITY_TYPES.register();
    }
}

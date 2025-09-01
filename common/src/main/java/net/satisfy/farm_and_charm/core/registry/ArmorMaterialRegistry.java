package net.satisfy.farm_and_charm.core.registry;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.satisfy.farm_and_charm.FarmAndCharm;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class ArmorMaterialRegistry {
    public static final Holder<ArmorMaterial> CLOTH = register(
            Util.make(new EnumMap<>(ArmorItem.Type.class), m -> {
                m.put(ArmorItem.Type.BOOTS, 1);
                m.put(ArmorItem.Type.LEGGINGS, 1);
                m.put(ArmorItem.Type.CHESTPLATE, 2);
                m.put(ArmorItem.Type.HELMET, 1);
            }),
            () -> Ingredient.of(ItemTags.WOOL),
            ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "armor/default")
    );

    public static final Holder<ArmorMaterial> JEWELRY = register(
            Util.make(new EnumMap<>(ArmorItem.Type.class), m -> {
                m.put(ArmorItem.Type.BOOTS, 1);
                m.put(ArmorItem.Type.LEGGINGS, 1);
                m.put(ArmorItem.Type.CHESTPLATE, 1);
                m.put(ArmorItem.Type.HELMET, 1);
            }),
            () -> Ingredient.of(Items.GOLD_INGOT),
            ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "armor/default")
    );

    private static final Map<String, Holder<ArmorMaterial>> CACHE = new HashMap<>();

    public static Holder<ArmorMaterial> withTextureNoOverlay(Holder<ArmorMaterial> base, ResourceLocation texturePng) {
        return withLayer(base, toLayerPrefix(texturePng));
    }

    private static ResourceLocation toLayerPrefix(ResourceLocation png) {
        String p = png.getPath();
        int slash = p.lastIndexOf('/');
        String name = slash >= 0 ? p.substring(slash + 1) : p;
        int dot = name.lastIndexOf('.');
        if (dot >= 0) name = name.substring(0, dot);
        return ResourceLocation.fromNamespaceAndPath(png.getNamespace(), name);
    }

    private static Holder<ArmorMaterial> register(EnumMap<ArmorItem.Type, Integer> map, Supplier<Ingredient> repair, ResourceLocation layerPrefix) {
        EnumMap<ArmorItem.Type, Integer> copy = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type t : ArmorItem.Type.values()) {
            Integer v = map.get(t);
            if (v == null) v = 0;
            copy.put(t, v);
        }
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(layerPrefix, "", true));
        return Registry.registerForHolder(
                BuiltInRegistries.ARMOR_MATERIAL,
                ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "basic"),
                new ArmorMaterial(copy, 15, SoundEvents.ARMOR_EQUIP_LEATHER, repair, layers, 0.0F, 0.0F)
        );
    }

    private static Holder<ArmorMaterial> withLayer(Holder<ArmorMaterial> base, ResourceLocation layerPrefix) {
        String key = BuiltInRegistries.ARMOR_MATERIAL.getKey(base.value()) + "|" + layerPrefix;
        Holder<ArmorMaterial> cached = CACHE.get(key);
        if (cached != null) return cached;
        EnumMap<ArmorItem.Type, Integer> def = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type t : ArmorItem.Type.values()) def.put(t, base.value().getDefense(t));
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(layerPrefix, "", true));
        Holder<ArmorMaterial> res = Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "mat_" + layerPrefix.getNamespace() + "_" + layerPrefix.getPath().replace('/', '_')), new ArmorMaterial(def, base.value().enchantmentValue(), base.value().equipSound(), base.value().repairIngredient(), layers, base.value().toughness(), base.value().knockbackResistance()));
        CACHE.put(key, res);
        return res;
    }

    private ArmorMaterialRegistry() {}
}

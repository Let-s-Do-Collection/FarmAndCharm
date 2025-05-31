package net.satisfy.farm_and_charm.core.registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.item.Item;
import net.satisfy.farm_and_charm.client.model.DungareesLeggingsModel;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class ArmorRegistry {
    private static final Map<Item, DungareesLeggingsModel<?>> leggingsModels = new HashMap<>();

    public static Model getLeggingsModel(Item item, ModelPart rightLeg, ModelPart leftLeg, ModelPart body) {
        DungareesLeggingsModel<?> model = leggingsModels.computeIfAbsent(item, key -> {
            if (key == ObjectRegistry.DUNGAREES.get()) {
                return new DungareesLeggingsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(DungareesLeggingsModel.LAYER_LOCATION));
            } else {
                return null;
            }
        });

        if (model != null) {
            model.copyLegs(rightLeg, leftLeg);
            model.copyBody(body);
        }

        return model;
    }
}

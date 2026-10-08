package net.satisfy.farm_and_charm.core.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import net.satisfy.farm_and_charm.FarmAndCharm;

public class FarmAndCharmEmiCategory extends EmiRecipeCategory {
    private final Component name;

    public FarmAndCharmEmiCategory(String id, ItemLike icon, String translationKey) {
        super(FarmAndCharm.identifier(id), EmiStack.of(icon));
        this.name = Component.translatable(translationKey);
    }

    @Override
    public Component getName() {
        return this.name;
    }
}

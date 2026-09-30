package net.satisfy.farm_and_charm.core.compat.rei.cutting;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.util.Strippables;

import java.util.List;
import java.util.Optional;

public class StrippingDisplay extends BasicDisplay {
    public static final CategoryIdentifier<StrippingDisplay> ID = CategoryIdentifier.of(FarmAndCharm.MOD_ID, "log_stripping");

    public StrippingDisplay(Strippables.Entry entry) {
        super(List.of(EntryIngredients.of(entry.log())), List.of(EntryIngredients.of(entry.stripped())), Optional.empty());
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ID;
    }
}

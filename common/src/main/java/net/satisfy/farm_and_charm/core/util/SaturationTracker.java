package net.satisfy.farm_and_charm.core.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class SaturationTracker {

    private static final int[] FEEDING_THRESHOLDS = {5, 10, 15, 20};

    private int level = 0;
    private int foodCounter = 0;
    private long lastFedTick = -1;
    private int decayDelay = -1;

    public void tryFeed(Animal animal, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!animal.isFood(stack) || animal.isBaby()) return;

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        foodCounter++;
        lastFedTick = animal.tickCount;
        decayDelay = 2400 + animal.getRandom().nextInt(2400);

        if (level < 4 && foodCounter >= FEEDING_THRESHOLDS[level]) {
            level++;
            foodCounter = 0;
        }
    }

    public void tick(Animal animal) {
        if (lastFedTick == -1 || decayDelay == -1) return;
        if (animal.tickCount - lastFedTick >= decayDelay) {
            if (foodCounter > 0) {
                foodCounter--;
            } else if (level > 0) {
                level--;
                foodCounter = FEEDING_THRESHOLDS[Math.max(0, level - 1)] - 1;
            }
            lastFedTick = animal.tickCount;
            decayDelay = 2400 + animal.getRandom().nextInt(2400);
        }
    }

    public void dropExtraLoot(Animal animal, DamageSource source) {
        if (!(animal.level() instanceof ServerLevel serverLevel)) return;

        int bonusRolls = switch (level) {
            case 1 -> 1;
            case 2 -> 1 + animal.getRandom().nextInt(2);
            case 3 -> 2 + animal.getRandom().nextInt(2);
            case 4 -> 3;
            default -> 0;
        };

        if (bonusRolls == 0) return;

        LootParams.Builder builder = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, animal)
                .withParameter(LootContextParams.ORIGIN, animal.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, source);

        LootTable table = serverLevel.getServer().getLootData().getLootTable(animal.getLootTable());
        for (int i = 0; i < bonusRolls; i++) {
            List<ItemStack> loot = table.getRandomItems(builder.create(LootContextParamSets.ENTITY));
            for (ItemStack stack : loot) {
                serverLevel.addFreshEntity(new ItemEntity(serverLevel, animal.getX(), animal.getY(), animal.getZ(), stack));
            }
        }
    }

    public int level() {
        return level;
    }

    public int foodCounter() {
        return foodCounter;
    }

    public void feedDirectly(Animal animal, int currentTick, int amount) {
        foodCounter += amount;
        lastFedTick = currentTick;
        decayDelay = 2400 + animal.getRandom().nextInt(2400);

        while (level < 4 && foodCounter >= FEEDING_THRESHOLDS[level]) {
            foodCounter -= FEEDING_THRESHOLDS[level];
            level++;
        }
    }


    public interface SaturatedAnimal {
        SaturationTracker farm_and_charm$getSaturationTracker();
    }
}

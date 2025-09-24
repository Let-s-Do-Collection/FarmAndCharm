package net.satisfy.farm_and_charm.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
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

public class SaturationTracker {
    private static final int[] FEEDING_THRESHOLDS = {5, 10, 15, 20};
    private static final Map<ResourceLocation, LootTable> LOOT_CACHE = new ConcurrentHashMap<>();

    private int level = 0;
    private int foodCounter = 0;
    private long lastFedTick = -1;
    private int decayDelay = -1;
    private int nextDecayTick = -1;
    private int nextTickCheck = 0;

    public void tryFeed(Animal animal, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!animal.isFood(stack) || animal.isBaby()) return;
        if (!player.getAbilities().instabuild) stack.shrink(1);
        foodCounter++;
        lastFedTick = animal.tickCount;
        decayDelay = 2400 + animal.getRandom().nextInt(2400);
        nextDecayTick = animal.tickCount + decayDelay;
        if (level < 4 && foodCounter >= FEEDING_THRESHOLDS[level]) {
            level++;
            foodCounter = 0;
        }
    }

    public void tick(Animal animal) {
        int now = animal.tickCount;
        if (now < nextTickCheck) return;
        nextTickCheck = now + 20;
        if (nextDecayTick == -1) return;
        if (now >= nextDecayTick) {
            if (foodCounter > 0) {
                foodCounter--;
            } else if (level > 0) {
                level--;
                foodCounter = FEEDING_THRESHOLDS[Math.max(0, level - 1)] - 1;
            }
            lastFedTick = now;
            decayDelay = 2400 + animal.getRandom().nextInt(2400);
            nextDecayTick = now + decayDelay;
        }
    }

    public void dropExtraLoot(Animal animal, DamageSource source) {
        if (!(animal.level() instanceof ServerLevel serverLevel)) return;
        int bonusRolls = level == 1 ? 1 : level == 2 ? 1 + animal.getRandom().nextInt(2) : level == 3 ? 2 + animal.getRandom().nextInt(2) : level == 4 ? 3 : 0;
        if (bonusRolls == 0) return;
        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, animal)
                .withParameter(LootContextParams.ORIGIN, animal.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                .create(LootContextParamSets.ENTITY);
        LootTable table = LOOT_CACHE.computeIfAbsent(
                animal.getLootTable(),
                id -> serverLevel.getServer().getLootData().getLootTable(id)
        );
        Consumer<ItemStack> spawn = stack -> serverLevel.addFreshEntity(new ItemEntity(serverLevel, animal.getX(), animal.getY(), animal.getZ(), stack));
        for (int i = 0; i < bonusRolls; i++) table.getRandomItems(params, spawn);
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
        nextDecayTick = currentTick + decayDelay;
        while (level < 4 && foodCounter >= FEEDING_THRESHOLDS[level]) {
            foodCounter -= FEEDING_THRESHOLDS[level];
            level++;
        }
    }

    public int getDecayDelay() {
        return decayDelay;
    }

    public void setDecayDelay(int delay) {
        this.decayDelay = delay;
        this.nextDecayTick = (int) lastFedTick + delay;
    }

    public long getLastFedTick() {
        return lastFedTick;
    }

    public void setLastFedTick(long tick) {
        this.lastFedTick = tick;
        if (decayDelay > 0) this.nextDecayTick = (int) tick + decayDelay;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setFoodCounter(int count) {
        this.foodCounter = count;
    }

    public void clientSync(int syncedLevel, int syncedFoodCounter) {
        this.level = syncedLevel;
        this.foodCounter = syncedFoodCounter;
    }

    public interface SaturatedAnimal {
        SaturationTracker farm_and_charm$getSaturationTracker();
        void farm_and_charm$setSaturationTracker(SaturationTracker tracker);
    }
}

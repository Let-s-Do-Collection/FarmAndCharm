package net.satisfy.farm_and_charm.core.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
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
    private long nextDecayGameTime = -1L;

    private void scheduleNextDecay(Animal animal, long now) {
        decayDelay = 2400 + animal.getRandom().nextInt(2400);
        nextDecayGameTime = now + decayDelay;
        lastFedTick = now;
    }

    public void tryFeed(Animal animal, Player player, InteractionHand hand) {
        if (animal.level().isClientSide) return;
        if (level >= 4) return;
        ItemStack stack = player.getItemInHand(hand);
        if (!animal.isFood(stack) || animal.isBaby()) return;
        if (!player.getAbilities().instabuild) stack.shrink(1);
        foodCounter++;
        long now = animal.level().getGameTime();
        scheduleNextDecay(animal, now);
        if (level < 4 && foodCounter >= FEEDING_THRESHOLDS[level]) {
            level++;
            foodCounter = 0;
        }
    }

    public void tick(Animal animal) {
        if (animal.level().isClientSide) return;
        long now = animal.level().getGameTime();
        if ((animal.getId() + now) % 20 != 0) return;
        if (nextDecayGameTime == -1L) return;
        if (now >= nextDecayGameTime) {
            if (foodCounter > 0) {
                foodCounter--;
            } else if (level > 0) {
                level--;
                foodCounter = FEEDING_THRESHOLDS[Math.max(0, level - 1)] - 1;
            }
            scheduleNextDecay(animal, now);
        }
    }

    public void dropExtraLoot(Animal animal, DamageSource source) {
        if (source == null) return;
        if (!(animal.level() instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) return;
        int bonusRolls = switch (level) {
            case 1 -> 1;
            case 2 -> 1 + animal.getRandom().nextInt(2);
            case 3 -> 2 + animal.getRandom().nextInt(2);
            case 4 -> 3;
            default -> 0;
        };
        if (bonusRolls == 0) return;
        Entity attacker = source.getEntity();
        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.THIS_ENTITY, animal)
                .withParameter(LootContextParams.ORIGIN, animal.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, attacker)
                .withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, attacker instanceof Player p ? p : null)
                .create(LootContextParamSets.ENTITY);
        LootTable table = serverLevel.getServer().reloadableRegistries().getLootTable(animal.getLootTable());
        for (int i = 0; i < bonusRolls; i++) {
            List<ItemStack> loot = table.getRandomItems(params);
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

    public void feedDirectly(Animal animal, int amount) {
        if (level >= 4) return;
        foodCounter += amount;
        long now = animal.level().getGameTime();
        scheduleNextDecay(animal, now);
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
        if (lastFedTick >= 0 && delay >= 0) this.nextDecayGameTime = lastFedTick + delay;
    }

    public long getLastFedTick() {
        return lastFedTick;
    }

    public void setLastFedTick(long tick) {
        this.lastFedTick = tick;
        if (decayDelay >= 0) this.nextDecayGameTime = tick + decayDelay;
    }

    public void setLevel(int level) {
        this.level = Math.max(0, Math.min(4, level));
        if (this.level == 4) this.foodCounter = 0;
    }

    public void setFoodCounter(int count) {
        this.foodCounter = Math.max(0, count);
        if (this.level >= 4) this.foodCounter = 0;
    }

    public void clientSync(int syncedLevel, int syncedFoodCounter) {
        this.level = Math.max(0, Math.min(4, syncedLevel));
        this.foodCounter = Math.max(0, syncedFoodCounter);
        if (this.level == 4) this.foodCounter = 0;
    }

    public interface SaturatedAnimal {
        SaturationTracker farm_and_charm$getSaturationTracker();
        void farm_and_charm$setSaturationTracker(SaturationTracker tracker);
    }
}

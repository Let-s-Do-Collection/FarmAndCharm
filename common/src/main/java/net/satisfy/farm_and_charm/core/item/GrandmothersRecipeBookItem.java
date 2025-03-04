package net.satisfy.farm_and_charm.core.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.*;

public class GrandmothersRecipeBookItem extends Item {
    private static final Map<ServerLevel, Map<UUID, Set<ResourceLocation>>> worldUnlockedRecipes = new HashMap<>();

    public GrandmothersRecipeBookItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        if (stack.hasTag() && level != null) {
            CompoundTag tag = stack.getTag();
            List<ResourceLocation> recipeIds = new ArrayList<>();
            assert tag != null;
            if (tag.contains("Recipes")) {

                tag.getList("Recipes", 8).forEach(e -> recipeIds.add(new ResourceLocation(e.getAsString())));
            } else if (tag.contains("Recipe")) {
                recipeIds.add(new ResourceLocation(tag.getString("Recipe")));
            }
            RecipeManager manager = level.getRecipeManager();
            recipeIds.forEach(id -> {
                Optional<? extends Recipe<?>> opt = manager.byKey(id);
                if (opt.isPresent()) {
                    ItemStack resultStack = opt.get().getResultItem(level.registryAccess());
                    tooltip.add(Component.literal("Unlocks: ").withStyle(ChatFormatting.WHITE).append(Component.literal("[").withStyle(style -> style.withColor(0xFFD700))).append(resultStack.getHoverName().copy().withStyle(style -> style.withColor(0xFFD700).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(resultStack))))).append(Component.literal("]").withStyle(style -> style.withColor(0xFFD700))).append(Component.literal(" Recipe").withStyle(ChatFormatting.WHITE)));
                } else {
                    tooltip.add(Component.literal("Unlocks: ").withStyle(ChatFormatting.WHITE).append(Component.literal("[").withStyle(style -> style.withColor(0xFFD700))).append(Component.literal(id.toString()).withStyle(ChatFormatting.GRAY)).append(Component.literal("]").withStyle(style -> style.withColor(0xFFD700))).append(Component.literal(" Recipe").withStyle(ChatFormatting.WHITE)));
                }
            });
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.getItemInHand(hand);
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                List<ResourceLocation> recipeIds = new ArrayList<>();
                if (tag.contains("Recipes")) {
                    ListTag list = tag.getList("Recipes", 8);
                    for (int i = 0; i < list.size(); i++) {
                        recipeIds.add(new ResourceLocation(list.getString(i)));
                    }
                } else if (tag.contains("Recipe")) {
                    recipeIds.add(new ResourceLocation(tag.getString("Recipe")));
                }
                if (!recipeIds.isEmpty()) {
                    ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
                    Map<UUID, Set<ResourceLocation>> worldMap = worldUnlockedRecipes.computeIfAbsent(serverLevel, k -> new HashMap<>());
                    Set<ResourceLocation> unlocked = worldMap.computeIfAbsent(serverPlayer.getUUID(), k -> new HashSet<>());
                    ResourceLocation firstId = recipeIds.get(0);
                    if (unlocked.contains(firstId)) {
                        serverPlayer.displayClientMessage(Component.translatable("tooltip.farm_and_charm.recipe_unlocker.already_unlocked")
                                .withStyle(ChatFormatting.RED), false);
                        return InteractionResultHolder.success(stack);
                    }
                    RecipeManager manager = level.getRecipeManager();
                    List<Recipe<?>> recipes = new ArrayList<>();
                    for (ResourceLocation id : recipeIds) {
                        Optional<? extends Recipe<?>> opt = manager.byKey(id);
                        opt.ifPresent(recipes::add);
                        unlocked.add(id);
                    }
                    if (!recipes.isEmpty()) {
                        Recipe<?> firstRecipe = recipes.get(0);
                        ItemStack resultStack = firstRecipe.getResultItem(level.registryAccess());
                        MutableComponent message = Component.translatable("tooltip.farm_and_charm.recipe_unlocker.unlocked.prefix").withStyle(ChatFormatting.YELLOW).append(Component.literal(" [").withStyle(ChatFormatting.WHITE)).append(resultStack.getHoverName().copy().withStyle(style -> style.withColor(0xFFD700).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(resultStack))))).append(Component.literal("]").withStyle(ChatFormatting.WHITE));
                        serverPlayer.displayClientMessage(message, false);
                        spawnLevelUpEffect(serverPlayer);
                        stack.shrink(1);
                        return InteractionResultHolder.success(stack);
                    }
                }
            }
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    public static ItemStack createUnlockerForRecipes(GrandmothersRecipeBookItem item, String... recipeIds) {
        ItemStack stack = new ItemStack(item);
        CompoundTag tag = new CompoundTag();
        if (recipeIds.length == 1) {
            tag.putString("Recipe", recipeIds[0]);
        } else {
            ListTag list = new ListTag();
            for (String id : recipeIds) {
                list.add(StringTag.valueOf(id));
            }
            tag.put("Recipes", list);
        }
        stack.setTag(tag);
        return stack;
    }

    private void spawnLevelUpEffect(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.level();
        Random random = new Random();
        level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.2f);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks++ >= 30) {
                    timer.cancel();
                    return;
                }
                double angle = (ticks * 12) * (Math.PI / 180);
                double radius = 0.6;
                double xOffset = Math.cos(angle) * radius + (random.nextFloat() * 0.2 - 0.1);
                double zOffset = Math.sin(angle) * radius + (random.nextFloat() * 0.2 - 0.1);
                double yOffset = (ticks * 0.04);

                level.sendParticles(new DustParticleOptions(new Vector3f(0.97f, 0.86f, 0.43f), 1.2f),
                        player.getX() + xOffset, player.getY() + 0.5 + yOffset, player.getZ() + zOffset,
                        3, 0.1, 0.1, 0.1, 0.02);

                level.sendParticles(new DustParticleOptions(new Vector3f(0.54f, 1.0f, 0.54f), 1.0f),
                        player.getX() - xOffset, player.getY() + 0.5 + yOffset, player.getZ() - zOffset,
                        2, 0.1, 0.1, 0.1, 0.02);
            }
        }, 0, 60);
    }

}

package net.satisfy.farm_and_charm.core.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;
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
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            var data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            List<ResourceLocation> recipeIds = new ArrayList<>();
            if (data != null && data.contains("Recipes")) {
                ListTag list = data.copyTag().getList("Recipes", 8);
                for (int i = 0; i < list.size(); i++) {
                    recipeIds.add(ResourceLocation.parse(list.getString(i)));
                }
            } else if (data.contains("Recipe")) {
                recipeIds.add(ResourceLocation.parse(data.copyTag().getString("Recipe")));
            }
            if (!recipeIds.isEmpty()) {
                RecipeManager manager = (Minecraft.getInstance().level).getRecipeManager();
                for (ResourceLocation id : recipeIds) {
                    Optional<? extends RecipeHolder<?>> opt = manager.byKey(id);
                    if (opt.isPresent()) {
                        RecipeHolder<?> recipe = opt.get();
                        ItemStack resultStack = recipe.value().getResultItem((Minecraft.getInstance().level).registryAccess());
                        tooltip.add(Component.translatable("tooltip.farm_and_charm.recipe_unlocker.unlocks", resultStack.getHoverName())
                                .withStyle(ChatFormatting.GRAY));
                    } else {
                        tooltip.add(Component.translatable("tooltip.farm_and_charm.recipe_unlocker.unlocks", id.toString())
                                .withStyle(ChatFormatting.GRAY));
                    }
                }
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ItemStack stack = player.getItemInHand(hand);
            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            if (data != null) {
                List<ResourceLocation> recipeIds = new ArrayList<>();
                if (data.contains("Recipes")) {
                    ListTag list = data.copyTag().getList("Recipes", 8);
                    for (int i = 0; i < list.size(); i++) {
                        recipeIds.add(ResourceLocation.parse(list.getString(i)));
                    }
                } else if (data.contains("Recipe")) {
                    recipeIds.add(ResourceLocation.parse(data.copyTag().getString("Recipe")));
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
                    List<RecipeHolder<?>> recipes = new ArrayList<>();
                    for (ResourceLocation id : recipeIds) {
                        Optional<? extends RecipeHolder<?>> opt = manager.byKey(id);
                        opt.ifPresent(recipes::add);
                        unlocked.add(id);
                    }
                    if (!recipes.isEmpty()) {
                        RecipeHolder<?> firstRecipe = recipes.get(0);
                        ItemStack resultStack = firstRecipe.value().getResultItem(level.registryAccess());
                        MutableComponent message = Component.literal("")
                                .append(Component.translatable("tooltip.farm_and_charm.recipe_unlocker.unlocked.prefix")
                                        .withStyle(ChatFormatting.YELLOW))
                                .append(Component.literal(" [").withStyle(ChatFormatting.WHITE))
                                .append(resultStack.getHoverName().copy().withStyle(style -> style.withColor(ChatFormatting.WHITE)
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                                                new HoverEvent.ItemStackInfo(resultStack)))))
                                .append(Component.literal("]").withStyle(ChatFormatting.WHITE));
                        serverPlayer.displayClientMessage(message, false);
                        spawnGoldenParticles(serverPlayer);
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
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    private void spawnGoldenParticles(ServerPlayer serverPlayer) {
        ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 40) {
                    timer.cancel();
                    return;
                }
                serverLevel.sendParticles(new DustParticleOptions(new Vector3f(1.0f, 0.84f, 0.0f), 1.0f),
                        serverPlayer.getX(), serverPlayer.getY() + 1.0, serverPlayer.getZ(),
                        5, 0.5, 0.5, 0.5, 0.0);
                ticks++;
            }
        }, 0, 50);
    }
}
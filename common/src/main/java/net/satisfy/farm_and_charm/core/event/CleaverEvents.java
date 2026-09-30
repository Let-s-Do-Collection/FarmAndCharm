package net.satisfy.farm_and_charm.core.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.satisfy.farm_and_charm.core.block.CuttingBoardBlock;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

import java.util.Map;

public class CleaverEvents {
    private static final Map<EntityType<?>, Item> HEADS = Map.of(
            EntityType.ZOMBIE, Items.ZOMBIE_HEAD,
            EntityType.SKELETON, Items.SKELETON_SKULL,
            EntityType.WITHER_SKELETON, Items.WITHER_SKELETON_SKULL,
            EntityType.CREEPER, Items.CREEPER_HEAD,
            EntityType.PIGLIN, Items.PIGLIN_HEAD,
            EntityType.ENDER_DRAGON, Items.DRAGON_HEAD
    );

    public static void init() {
        EntityEvent.LIVING_DEATH.register(CleaverEvents::dropHead);
    }

    private static EventResult dropHead(LivingEntity entity, DamageSource source) {
        if (entity.level().isClientSide || !PlatformHelper.isCleaverHeadDropsEnabled()) {
            return EventResult.pass();
        }
        if (!(source.getEntity() instanceof Player killer) || source.getDirectEntity() != killer || !killer.getMainHandItem().is(CuttingBoardBlock.CLEAVERS)) {
            return EventResult.pass();
        }
        ItemStack head = getHead(entity);
        if (!head.isEmpty()) {
            entity.spawnAtLocation(head);
        }
        return EventResult.pass();
    }

    private static ItemStack getHead(LivingEntity entity) {
        if (entity instanceof Player player) {
            ItemStack head = new ItemStack(Items.PLAYER_HEAD);
            head.set(DataComponents.PROFILE, new ResolvableProfile(player.getGameProfile()));
            return head;
        }
        Item head = HEADS.get(entity.getType());
        return head == null ? ItemStack.EMPTY : new ItemStack(head);
    }
}

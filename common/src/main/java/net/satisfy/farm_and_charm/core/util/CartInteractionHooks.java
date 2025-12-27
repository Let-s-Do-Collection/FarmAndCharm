package net.satisfy.farm_and_charm.core.util;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.InteractionEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.satisfy.farm_and_charm.core.entity.AbstractCartEntity;

public final class CartInteractionHooks {
    private CartInteractionHooks() {
    }

    public static void init() {
        InteractionEvent.INTERACT_ENTITY.register(CartInteractionHooks::onInteractEntity);
    }

    private static EventResult onInteractEntity(Player player, Entity entity, InteractionHand hand) {
        InteractionResult result = handle(player, entity, hand);
        if (result.consumesAction()) {
            return EventResult.interruptTrue();
        }
        return EventResult.pass();
    }

    private static InteractionResult handle(Player player, Entity entity, InteractionHand hand) {
        if (player == null || entity == null) {
            return InteractionResult.PASS;
        }
        if (player.level().isClientSide) {
            return InteractionResult.PASS;
        }
        if (!(entity instanceof AbstractHorse)) {
            return InteractionResult.PASS;
        }
        if (!player.getItemInHand(hand).isEmpty()) {
            return InteractionResult.PASS;
        }

        CartWorldData worldData = CartWorldData.get(player.level());

        if (player.isSecondaryUseActive()) {
            AbstractCartEntity cartPulledByHorse = worldData.getCartPulledBy(entity);
            if (cartPulledByHorse == null || !cartPulledByHorse.isAlive()) {
                return InteractionResult.PASS;
            }
            cartPulledByHorse.setPulling(null);
            return InteractionResult.CONSUME;
        }

        AbstractCartEntity cartPulledByPlayer = worldData.getCartPulledBy(player);
        if (cartPulledByPlayer == null || !cartPulledByPlayer.isAlive()) {
            return InteractionResult.PASS;
        }
        if (cartPulledByPlayer.level() != player.level()) {
            return InteractionResult.PASS;
        }

        AbstractCartEntity cartPulledByHorse = worldData.getCartPulledBy(entity);
        if (cartPulledByHorse != null && cartPulledByHorse != cartPulledByPlayer) {
            return InteractionResult.PASS;
        }

        cartPulledByPlayer.setPulling(entity);
        return InteractionResult.CONSUME;
    }
}
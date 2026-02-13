package net.satisfy.farm_and_charm.core.event;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.farm_and_charm.core.block.PileBlock;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;

import java.util.List;

public final class VanillaItemPlacements {

    private static final MutableComponent PLACEABLE_TOOLTIP = Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.DARK_GRAY);

    public static void init() {
        InteractionEvent.RIGHT_CLICK_BLOCK.register(VanillaItemPlacements::onRightClickBlock);
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ClientTooltipEvent.ITEM.register(VanillaItemPlacements::appendTooltip));
    }

    private static EventResult onRightClickBlock(Player player, InteractionHand hand, BlockPos pos, Direction face) {
        Level level = player.level();
        if (level.isClientSide()) return EventResult.pass();

        ItemStack heldStack = player.getItemInHand(hand);
        Item heldItem = heldStack.getItem();

        if (!player.isShiftKeyDown()) return EventResult.pass();
        if (face != Direction.UP) return EventResult.pass();

        Block placeableBlock = getPlaceableBlockForItem(heldItem);
        if (placeableBlock == null) return EventResult.pass();

        BlockPos targetPos = pos.relative(face);
        BlockState targetState = level.getBlockState(targetPos);

        if (targetState.getBlock() == placeableBlock && placeableBlock instanceof PileBlock pileBlock) {
            if (pileBlock.tryAddLayer(level, targetPos, targetState, player, heldStack)) {
                BlockState newState = level.getBlockState(targetPos);
                SoundType soundType = newState.getSoundType();
                level.playSound(null, targetPos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, targetPos);
                return EventResult.interruptTrue();
            }
            return EventResult.pass();
        }

        if (!targetState.canBeReplaced()) return EventResult.pass();

        BlockState placementState = placeableBlock.defaultBlockState();
        if (!placementState.canSurvive(level, targetPos)) return EventResult.pass();
        if (!level.setBlock(targetPos, placementState, 3)) return EventResult.pass();

        SoundType soundType = placementState.getSoundType();
        level.playSound(null, targetPos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        level.gameEvent(player, GameEvent.BLOCK_PLACE, targetPos);

        if (!player.getAbilities().instabuild) {
            heldStack.shrink(1);
        }

        return EventResult.interruptTrue();
    }

    private static void appendTooltip(ItemStack stack, List<Component> lines, net.minecraft.world.item.Item.TooltipContext tooltipContext, net.minecraft.world.item.TooltipFlag flag) {
        if (stack.is(Items.WHEAT) || stack.is(Items.FEATHER)) {
            lines.add(PLACEABLE_TOOLTIP);
        }
    }

    private static Block getPlaceableBlockForItem(Item item) {
        if (item == Items.WHEAT) return ObjectRegistry.WHEAT_PILE.get();
        if (item == Items.FEATHER) return ObjectRegistry.FEATHER_PILE.get();
        return null;
    }

    private VanillaItemPlacements() {
    }
}
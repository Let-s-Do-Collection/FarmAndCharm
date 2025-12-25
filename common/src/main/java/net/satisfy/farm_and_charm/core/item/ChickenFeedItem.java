package net.satisfy.farm_and_charm.core.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.MobEffectRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ChickenFeedItem extends Item {
    public ChickenFeedItem(Properties properties) {
        super(properties.food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).build()));
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        if (PlatformHelper.isChickenEffectsEnabled() && !player.level().isClientSide && entity.getType() == EntityType.CHICKEN) {
            Chicken chicken = (Chicken) entity;
            chicken.addEffect(new MobEffectInstance(MobEffectRegistry.getHolder(MobEffectRegistry.CLUCK), 1200));
            player.level().playSound(null, chicken.getX(), chicken.getY(), chicken.getZ(), SoundEvents.CHICKEN_AMBIENT, SoundSource.NEUTRAL, 1.0F, 1.0F);
            chicken.level().addParticle(ParticleTypes.HEART, chicken.getX(), chicken.getY() + 0.5, chicken.getZ(), 0.0D, 0.0D, 0.0D);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entityLiving) {
        if (PlatformHelper.isChickenEffectsEnabled() && entityLiving instanceof Player player && !world.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffectRegistry.getHolder(MobEffectRegistry.CLUCK), 400));
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return super.finishUsingItem(stack, world, entityLiving);
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.EAT;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        if (PlatformHelper.isChickenEffectsEnabled()) {
            tooltip.add(Component.translatable("tooltip.farm_and_charm.animal_fed_to_chicken").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.farm_and_charm.cluck").withStyle(ChatFormatting.BLUE));
        }
    }
}
package net.satisfy.farm_and_charm.core.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.MobEffectRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DogFoodItem extends Item {
    public DogFoodItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, net.minecraft.world.InteractionHand hand) {
        boolean isWolf = entity.getType().is(TagRegistry.IS_WOLF);
        if (PlatformHelper.isTamingEnabled() && entity instanceof TamableAnimal wolf && !wolf.isTame() && isWolf) {
            if (!entity.level().isClientSide) {
                wolf.tame(player);
                wolf.setOwnerUUID(player.getUUID());
                wolf.setInSittingPose(false);
                wolf.heal(10.0F);
                wolf.addEffect(new MobEffectInstance(MobEffectRegistry.DOG_FOOD, 3600, 0));
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            } else {
                Level world = entity.getCommandSenderWorld();
                world.addParticle(ParticleTypes.HEART, entity.getX(), entity.getY() + 1.0, entity.getZ(), 0.0, 0.0, 0.0);
                world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FOX_EAT, entity.getSoundSource(), 1.0f, 1.0f);
            }
            return InteractionResult.sidedSuccess(entity.getCommandSenderWorld().isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        if (PlatformHelper.isTamingEnabled()) {
            tooltip.add(Component.translatable("tooltip.farm_and_charm.animal_fed_to_dog").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("tooltip.farm_and_charm.dog_effect_1").withStyle(ChatFormatting.BLUE));
            tooltip.add(Component.translatable("tooltip.farm_and_charm.dog_effect_2").withStyle(ChatFormatting.BLUE));
            tooltip.add(Component.translatable("tooltip.farm_and_charm.dog_effect_3").withStyle(ChatFormatting.BLUE));
        }
    }
}


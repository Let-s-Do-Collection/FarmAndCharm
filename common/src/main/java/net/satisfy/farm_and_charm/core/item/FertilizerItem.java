package net.satisfy.farm_and_charm.core.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.crops.BigCropCapable;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FertilizerItem extends Item {

    public FertilizerItem(Properties properties) {
        super(properties.stacksTo(64).durability(10));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (!PlatformHelper.isFertilizerEnabled()) {
            return InteractionResult.PASS;
        }

        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        ItemStack stack = context.getItemInHand();
        boolean applied = false;

        if (!world.isClientSide && world instanceof ServerLevel serverWorld) {
            List<BlockPos> targets = new ArrayList<>();
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos targetPos = pos.offset(x, 0, z);
                    BlockState state = world.getBlockState(targetPos);
                    if (state.is(BlockTags.CROPS) || state.is(BlockTags.BAMBOO_PLANTABLE_ON)) {
                        targets.add(targetPos);
                    }
                }
            }

            RandomSource random = world.getRandom();
            int maxTargets = random.nextInt(5) + 2;

            for (int i = 0; i < maxTargets && !targets.isEmpty(); i++) {
                BlockPos targetPos = targets.remove(random.nextInt(targets.size()));
                BlockState state = world.getBlockState(targetPos);

                if (state.getBlock() instanceof BonemealableBlock bonemealableBlock) {
                    if (bonemealableBlock.isValidBonemealTarget(world, targetPos, state)
                            && bonemealableBlock.isBonemealSuccess(world, random, targetPos, state)) {
                        bonemealableBlock.performBonemeal(serverWorld, random, targetPos, state);
                    }

                    if (state.getBlock() instanceof BigCropCapable bigCrop) {
                        BlockState newState = world.getBlockState(targetPos);
                        bigCrop.tryTransformToBigCrop(world, targetPos, newState, true);
                    }

                    applied = true;

                    serverWorld.sendParticles(ParticleTypes.HAPPY_VILLAGER, targetPos.getX() + 0.5, targetPos.getY() + 1.0, targetPos.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0.0);
                    serverWorld.sendParticles(
                            new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ObjectRegistry.FERTILIZER.get())),
                            targetPos.getX() + 0.5, targetPos.getY() + 1.0, targetPos.getZ() + 0.5,
                            125, 0.5, 0.5, 0.5, 0.0
                    );
                    world.levelEvent(2005, targetPos, 0);
                }
            }

            if (applied) {
                EquipmentSlot slot = stack.equals(player.getItemBySlot(EquipmentSlot.OFFHAND)) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                stack.hurtAndBreak(1, player, slot);
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }

        if (world.isClientSide) {
            RandomSource random = world.getRandom();
            for (int i = 0; i < 100; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 2.0;
                double offsetY = random.nextDouble();
                double offsetZ = (random.nextDouble() - 0.5) * 2.0;
                double x = pos.getX() + 0.5 + offsetX;
                double y = pos.getY() + 1.0 + offsetY;
                double z = pos.getZ() + 0.5 + offsetZ;

                world.addParticle(ParticleTypes.HAPPY_VILLAGER, x, y, z, 0.0, 0.1, 0.0);
                world.addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(ObjectRegistry.FERTILIZER.get())),
                        x, y, z, 0.0, 0.1, 0.0
                );
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BRUSH;
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 32;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
}

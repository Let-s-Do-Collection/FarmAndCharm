package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class FertilizedSoilBlock extends Block {
    public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, 3);

    public FertilizedSoilBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SIZE, 3));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(SIZE)) {
            case 0 -> Block.box(0, 0, 0, 16, 4, 16);
            case 1 -> Block.box(0, 0, 0, 16, 8, 16);
            case 2 -> Block.box(0, 0, 0, 16, 12, 16);
            default -> Block.box(0, 0, 0, 16, 16, 16);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SIZE);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionHand hand = player.getUsedItemHand();
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.getItem() == ObjectRegistry.PITCHFORK.get()) {
            int newSize = state.getValue(SIZE) - 1;
            if (newSize < 0) {
                level.removeBlock(pos, false);
                spawnBreakParticles(level, pos, state);
                level.playSound(null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
                level.setBlock(pos, state.setValue(SIZE, newSize), 3);
                applyBoneMealEffect(level, pos);
                spawnBreakParticles(level, pos, state);
                level.playSound(null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            itemStack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(itemStack));
            return InteractionResult.SUCCESS;
        } else if (itemStack.getItem() instanceof HoeItem) {
            int currentSize = state.getValue(SIZE);
            if (currentSize == 3) {
                level.setBlock(pos, ObjectRegistry.FERTILIZED_FARM_BLOCK.get().defaultBlockState(), 3);
                if (!player.isCreative()) {
                    itemStack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(itemStack));
                }
                level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }


    private void spawnBreakParticles(Level level, BlockPos pos, BlockState state) {
        if (!level.isClientSide) {
            ((ServerLevel) level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0.2);
        }
    }

    private void applyBoneMealEffect(Level level, BlockPos centerPos) {
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            BlockPos.betweenClosedStream(centerPos.offset(-5, -1, -5), centerPos.offset(5, 1, 5))
                    .forEach(pos -> {
                        if (serverLevel.random.nextInt(100) < 20) {
                            BlockState blockState = serverLevel.getBlockState(pos);
                            Block block = blockState.getBlock();
                            if (block instanceof BonemealableBlock bonemealableBlock && bonemealableBlock.isValidBonemealTarget(serverLevel, pos, blockState)) {
                                bonemealableBlock.performBonemeal(serverLevel, serverLevel.random, pos, blockState);
                            }
                        }
                    });
        }
    }
}
package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class FertilizedSoilBlock extends Block {
    public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, 3);

    public FertilizedSoilBlock(Properties properties) {
        super(properties.randomTicks());
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
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        boolean hasSugarCaneAbove = level.getBlockState(pos.above()).is(Blocks.SUGAR_CANE);

        if (itemStack.getItem() == ObjectRegistry.PITCHFORK.get()) {
            if (hasSugarCaneAbove) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            int newSize = state.getValue(SIZE) - 1;
            if (newSize < 0) {
                level.removeBlock(pos, false);
            } else {
                level.setBlock(pos, state.setValue(SIZE, newSize), 3);
                applyBoneMealEffect(level, pos);
            }
            spawnParticles(level, pos, state, false);
            level.playSound(null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            itemStack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(itemStack));
            return ItemInteractionResult.SUCCESS;
        } else if (itemStack.getItem() instanceof HoeItem) {
            if (hasSugarCaneAbove) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            int currentSize = state.getValue(SIZE);
            if (currentSize == 3) {
                level.setBlock(pos, ObjectRegistry.FERTILIZED_FARM_BLOCK.get().defaultBlockState(), 3);
                if (!player.isCreative()) {
                    itemStack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(itemStack));
                }
                level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private void spawnParticles(Level level, BlockPos pos, BlockState state, boolean happy) {
        if (!level.isClientSide) {
            ServerLevel server = (ServerLevel) level;
            server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0.2);
            if (happy) {
                server.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5, 4, 0.25, 0.1, 0.25, 0.01);
            }
        }
    }

    private void applyBoneMealEffect(Level level, BlockPos centerPos) {
        if (!level.isClientSide) {
            ServerLevel serverLevel = (ServerLevel) level;
            int range = PlatformHelper.getFertilizedSoilRange();
            BlockPos.betweenClosedStream(centerPos.offset(-range, -1, -range), centerPos.offset(range, 1, range))
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

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (state.getValue(SIZE) != 3) return;
        if (random.nextInt(4) != 0) return;
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy <= 1; dy++) {
                    if (dx == 0 && dz == 0 && dy == 0) continue;
                    checkPos.set(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    BlockState targetState = level.getBlockState(checkPos);
                    if (targetState.is(Blocks.SUGAR_CANE)) {
                        BlockPos above = checkPos.above();
                        if (level.isEmptyBlock(above)) {
                            int height = 1;
                            while (level.getBlockState(checkPos.below(height)).is(Blocks.SUGAR_CANE)) {
                                height++;
                            }
                            if (height < 3) {
                                level.setBlock(above, Blocks.SUGAR_CANE.defaultBlockState(), 2);
                                spawnParticles(level, above, Blocks.SUGAR_CANE.defaultBlockState(), true);
                            }
                        }
                    }
                }
            }
        }
    }
}

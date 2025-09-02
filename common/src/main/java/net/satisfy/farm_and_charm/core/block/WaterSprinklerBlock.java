package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.WaterSprinklerBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import org.jetbrains.annotations.NotNull;

public class WaterSprinklerBlock extends BaseEntityBlock {
    public static final MapCodec<WaterSprinklerBlock> CODEC = simpleCodec(WaterSprinklerBlock::new);
    private static final VoxelShape SHAPE = Shapes.or(
            Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.0625, 0.9375),
            Shapes.box(0.0625, 0.0625, 0.0625, 0.125, 0.75, 0.9375),
            Shapes.box(0.875, 0.0625, 0.0625, 0.9375, 0.75, 0.9375),
            Shapes.box(0.125, 0.0625, 0.0625, 0.875, 0.75, 0.125),
            Shapes.box(0.125, 0.0625, 0.875, 0.875, 0.75, 0.9375),
            Shapes.box(0.4375, 0.125, 0.4375, 0.5625, 1.0625, 0.5625)
    );

    public WaterSprinklerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void onPlace(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        if (!world.isClientSide) world.scheduleTick(pos, this, 1);
    }

    @Override
    public void animateTick(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!world.isRaining() && !world.isThundering()) {
            if (world.isClientSide) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof WaterSprinklerBlockEntity sprinkler) {
                    float angle = sprinkler.getRotationAngle();
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 1.0;
                    double z = pos.getZ() + 0.5;
                    double velocity = 0.2;
                    double startOffset = 0.5;
                    for (int i = 0; i < 4; ++i) {
                        double a = Math.toRadians(angle + 90 * i);
                        double cos = Math.cos(a);
                        double sin = Math.sin(a);
                        double dx = cos * velocity;
                        double dz = sin * velocity;
                        double startX = x + cos * startOffset;
                        double startZ = z + sin * startOffset;
                        for (double len = 0; len < 3; len += 0.5) {
                            double cx = startX + dx * len;
                            double cz = startZ + dz * len;
                            world.addParticle(ParticleTypes.SPLASH, cx, y, cz, dx, 0.0D, dz);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void tick(@NotNull BlockState state, ServerLevel world, BlockPos pos, @NotNull RandomSource random) {
        BlockPos.betweenClosed(pos.offset(-4, -1, -4), pos.offset(4, 1, 4)).forEach(p -> {
            BlockState bs = world.getBlockState(p);
            if (bs.is(Blocks.FARMLAND) || bs.is(ObjectRegistry.FERTILIZED_FARM_BLOCK.get())) {
                world.setBlock(p, bs.setValue(BlockStateProperties.MOISTURE, 7), 2);
            }
            if (bs.is(Blocks.FIRE)) {
                world.removeBlock(p, false);
                world.playSound(null, p, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
            }
            if ((bs.is(Blocks.CAMPFIRE) || bs.is(Blocks.SOUL_CAMPFIRE)) && bs.getValue(CampfireBlock.LIT)) {
                world.setBlock(p, bs.setValue(CampfireBlock.LIT, false), 3);
                world.playSound(null, p, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
            if (bs.getBlock() instanceof CandleBlock && bs.getValue(CandleBlock.LIT)) {
                world.setBlock(p, bs.setValue(CandleBlock.LIT, false), 3);
                world.playSound(null, p, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
        });
        world.playSound(null, pos, SoundEventRegistry.WATER_SPRINKLER.get(), SoundSource.BLOCKS, 0.25F, 0.75F);
        world.scheduleTick(pos, this, 20);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new WaterSprinklerBlockEntity(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}

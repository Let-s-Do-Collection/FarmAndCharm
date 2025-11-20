package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TrampledStableFloorBlock extends Block {
    public static final MapCodec<TrampledStableFloorBlock> CODEC = simpleCodec(TrampledStableFloorBlock::new);
    private static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);
    private Supplier<Block> base;

    public TrampledStableFloorBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("unused")
    public void setBase(Supplier<Block> supplier) {
        base = supplier;
    }

    @Override
    public @NotNull MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.player.Player player, InteractionHand hand, BlockHitResult hit) {
        if ((stack.getItem() instanceof ShovelItem || stack.getItem() instanceof HoeItem) && base != null) {
            if (!level.isClientSide) {
                level.setBlock(pos, base.get().defaultBlockState(), 3);
            } else {
                RandomSource r = level.getRandom();
                for (int i = 0; i < 12; i++) {
                    double x = pos.getX() + 0.5 + (r.nextDouble() - 0.5) * 0.4;
                    double y = pos.getY() + 1.02;
                    double z = pos.getZ() + 0.5 + (r.nextDouble() - 0.5) * 0.4;
                    double vx = (r.nextDouble() - 0.5) * 0.06;
                    double vy = 0.15 + r.nextDouble() * 0.2;
                    double vz = (r.nextDouble() - 0.5) * 0.06;
                    level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), x, y, z, vx, vy, vz);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}

package net.satisfy.farm_and_charm.core.block;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.EffectFoodBlockEntity;
import net.satisfy.farm_and_charm.core.item.food.EffectFoodHelper;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class EffectFoodBlock extends BaseEntityBlock {
    public static final MapCodec<EffectFoodBlock> CODEC = simpleCodec(EffectFoodBlock::new);
    public static final DirectionProperty FACING;
    public static final IntegerProperty BITES;
    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.or(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.0625, 0.875));
        shape = Shapes.or(shape, Shapes.box(0.1875, 0.0625, 0.1875, 0.8125, 0.4375, 0.8125));


        return shape;
    };
    public static final Map<Direction, VoxelShape> SHAPE = Util.make(new HashMap<>(), map -> {
        for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
        }
    });

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        BITES = IntegerProperty.create("bites", 0, 3);
    }

    private final int maxBites;
    private final FoodProperties foodComponent;

    public EffectFoodBlock(Properties properties) {
        super(properties);
        this.maxBites = 3;
        this.foodComponent = new FoodProperties.Builder().nutrition(1).build();
    }

    public EffectFoodBlock(Properties settings, int maxBites, FoodProperties foodComponent) {
        super(settings);
        this.maxBites = maxBites;
        this.foodComponent = foodComponent;
        registerDefaultState(this.defaultBlockState().setValue(BITES, 0).setValue(FACING, Direction.NORTH));
    }


    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        if (!Objects.requireNonNull(ctx.getPlayer()).isShiftKeyDown()) {
            return null;
        }

        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return GeneralUtil.isFullAndSolid(levelReader, blockPos);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EffectFoodBlockEntity effectFoodBlockEntity) {
            effectFoodBlockEntity.addEffects(EffectFoodHelper.getEffects(itemStack));
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        InteractionHand hand = player.getUsedItemHand();
        ItemStack itemStack = player.getItemInHand(hand);
        if (world.isClientSide) {
            if (tryEat(world, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }

            if (itemStack.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return tryEat(world, pos, state, player);
    }

    private InteractionResult tryEat(LevelAccessor world, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        } else {
            player.getFoodData().eat(foodComponent.nutrition(), foodComponent.saturation());
            world.playSound(null, pos, SoundEvents.FOX_EAT, SoundSource.PLAYERS, 0.5f, world.getRandom().nextFloat() * 0.1f + 0.9f);
            world.gameEvent(player, GameEvent.EAT, pos);

            int bites = state.getValue(BITES);
            if (bites < maxBites) {
                world.setBlock(pos, state.setValue(BITES, bites + 1), 3);
            } else {
                world.destroyBlock(pos, false);
                world.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof EffectFoodBlockEntity effectFoodBlockEntity) {
                for (Pair<MobEffectInstance, Float> effect : effectFoodBlockEntity.getEffects()) {
                    player.addEffect(effect.getFirst());
                }
            }

            return InteractionResult.SUCCESS;
        }
    }

    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BITES);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, world, pos, newState, moved);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EffectFoodBlockEntity(pos, state);
    }
}
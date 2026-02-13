package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.CookingPotBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ParticleTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CookingPotBlock extends BaseEntityBlock {
    public static final MapCodec<CookingPotBlock> CODEC = simpleCodec(CookingPotBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty COOKING = BooleanProperty.create("cooking");
    public static final BooleanProperty NEEDS_SUPPORT = BooleanProperty.create("needs_support");
    public static final EnumProperty<CookpotStage> STAGE = EnumProperty.create("stage", CookpotStage.class);

    private static final Map<Direction, VoxelShape> SHAPES = Util.make(new HashMap<>(), map -> {
        Supplier<VoxelShape> voxelShapeSupplier = () -> {
            VoxelShape shape = Shapes.empty();
            shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.5, 0.875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0.875, 0.375, 0.3125, 1, 0.5, 0.6875), BooleanOp.OR);
            shape = Shapes.join(shape, Shapes.box(0, 0.375, 0.3125, 0.125, 0.5, 0.6875), BooleanOp.OR);
            return shape;
        };

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            map.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
        }
    });

    public CookingPotBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false)
                .setValue(COOKING, false)
                .setValue(NEEDS_SUPPORT, false)
                .setValue(STAGE, CookpotStage.NORMAL));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, COOKING, NEEDS_SUPPORT, STAGE);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), Shapes.empty());
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!world.isClientSide) {
            if (state.getValue(NEEDS_SUPPORT)) {
                boolean isSupported = world.getBlockState(pos.below()).isSolidRender(world, pos.below()) || world.getBlockState(pos.below()).is(BlockTags.CAMPFIRES);
                if (!isSupported) {
                    for (Direction direction : Direction.Plane.HORIZONTAL) {
                        BlockPos neighborPos = pos.relative(direction);
                        BlockState neighborState = world.getBlockState(neighborPos);
                        if (neighborState.getBlock() instanceof CookingPotBlock && neighborState.getValue(NEEDS_SUPPORT)) {
                            isSupported = true;
                            break;
                        }
                    }
                }
                if (!isSupported) {
                    world.destroyBlock(pos, true);
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState belowState = world.getBlockState(pos.below());
        boolean needsSupport = belowState.is(BlockTags.CAMPFIRES);
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(NEEDS_SUPPORT, needsSupport)
                .setValue(STAGE, CookpotStage.NORMAL);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = world.getBlockState(belowPos);
        boolean isCampfireBelow = belowState.is(BlockTags.CAMPFIRES);
        boolean isSolidBelow = belowState.isFaceSturdy(world, belowPos, Direction.UP);
        return isCampfireBelow || isSolidBelow;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(world, pos)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(world, pos)) {
            world.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos blockPos, Player player, BlockHitResult blockHitResult) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof MenuProvider) {
                player.openMenu((MenuProvider) blockEntity);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.SUCCESS;
    }

    public static void updateHeatState(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CookingPotBlockEntity cookingPotBlockEntity)) return;

        BlockState currentState = level.getBlockState(pos);

        boolean heated = currentState.getValue(LIT);
        boolean cooking = currentState.getValue(COOKING);
        boolean finished = cookingPotBlockEntity.hasOutputItem();

        CookpotStage stage;
        if (!heated && !finished) {
            stage = CookpotStage.NORMAL;
        } else if (cooking) {
            stage = CookpotStage.COOKING;
        } else if (finished) {
            stage = CookpotStage.FILLED;
        } else {
            stage = CookpotStage.WARM;
        }

        BlockState updatedState = currentState
                .setValue(STAGE, stage)
                .setValue(COOKING, stage == CookpotStage.COOKING);

        if (!updatedState.equals(currentState)) {
            level.setBlock(pos, updatedState, 3);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        CookpotStage stage = state.getValue(STAGE);
        if (stage == CookpotStage.NORMAL) return;

        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.7;
        double centerZ = pos.getZ() + 0.5;

        if (stage == CookpotStage.WARM) {
            if (random.nextInt(100) < 18) {
                level.addParticle(ParticleTypes.SMOKE, centerX, centerY + 0.4, centerZ, 0.0, 0.05, 0.0);
            }
            return;
        }

        if (stage == CookpotStage.COOKING) {

            if (random.nextInt(100) < 95) {
                int bubbleAmount = 2 + random.nextInt(3);

                for (int index = 0; index < bubbleAmount; index++) {
                    double offsetX = (random.nextDouble() - 0.5) * 0.4;
                    double offsetZ = (random.nextDouble() - 0.5) * 0.4;
                    double bubbleY = centerY - 0.2;

                    level.addParticle(ParticleTypeRegistry.SOUP_BUBBLE.get(), centerX + offsetX, bubbleY, centerZ + offsetZ, 0.0, 0.0, 0.0);
                    level.addParticle(ParticleTypeRegistry.SOUP_COOKING_BUBBLE.get(), centerX + offsetX, bubbleY, centerZ + offsetZ, 0.0, 0.0, 0.0);
                }
            }

            if (random.nextInt(100) < 80) {
                int steamAmount = 2 + random.nextInt(3);

                for (int index = 0; index < steamAmount; index++) {
                    double offsetX = (random.nextDouble() - 0.5) * 0.35;
                    double offsetZ = (random.nextDouble() - 0.5) * 0.35;

                    level.addParticle(ParticleTypeRegistry.SOUP_STEAM.get(),
                            centerX + offsetX, centerY + 0.3, centerZ + offsetZ,
                            0.0, 0.08, 0.0);
                }
            }

            if (random.nextInt(100) < 15) {
                level.addParticle(ParticleTypes.SMOKE,
                        centerX, centerY + 0.45, centerZ,
                        0.0, 0.06, 0.0);
            }

            if (random.nextInt(100) < 6) {
                level.playLocalSound(centerX, centerY, centerZ, SoundEventRegistry.COOKING_POT_BOILING.get(), SoundSource.BLOCKS, 0.75F, 0.75F, false);
            }

            return;
        }

        if (stage == CookpotStage.FILLED) {
            if (random.nextInt(100) < 24) {
                level.addParticle(ParticleTypes.SMOKE, centerX, centerY + 0.45, centerZ, 0.0, 0.05, 0.0);
            }

            if (random.nextInt(100) < 38) {
                double offsetX = (random.nextDouble() - 0.5) * 0.3;
                double offsetZ = (random.nextDouble() - 0.5) * 0.3;
                level.addParticle(ParticleTypeRegistry.SOUP_STEAM.get(), centerX + offsetX, centerY + 0.6, centerZ + offsetZ, 0.0, 0.07, 0.0);
            }
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CookingPotBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CookingPotBlockEntity) {
                Containers.dropContents(world, pos, ((CookingPotBlockEntity) blockEntity).getItems());
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        if (!world.isClientSide) {
            return (lvl, pos, blkState, blockEntity) -> {
                if (blockEntity instanceof CookingPotBlockEntity cookingPot) {
                    cookingPot.tick(lvl, pos, blkState, cookingPot);
                    updateHeatState(lvl, pos);
                }
            };
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
    }

    public enum CookpotStage implements StringRepresentable {
        NORMAL("normal"),
        WARM("warm"),
        COOKING("cooking"),
        FILLED("filled");

        private final String name;

        CookpotStage(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }
    }
}
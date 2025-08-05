package net.satisfy.farm_and_charm.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.client.FarmAndCharmClient;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class PetBowlBlock extends FacingBlock implements EntityBlock {
    public static final EnumProperty<GeneralUtil.FoodType> FOOD_TYPE = EnumProperty.create("food_type", GeneralUtil.FoodType.class);
    public static final BooleanProperty HAS_NAME_TAG = BooleanProperty.create("has_name_tag");

    private static final Supplier<VoxelShape> voxelShapeSupplier =
            () -> Shapes.box(0.25, 0, 0.25, 0.75, 0.25, 0.75);
    public static final Map<Direction, VoxelShape> SHAPE = new HashMap<>();

    static {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            SHAPE.put(direction, GeneralUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
        }
    }

    public PetBowlBlock(Properties properties) {
        super(properties.mapColor(MapColor.COLOR_BROWN));
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.NORTH)
                .setValue(FOOD_TYPE, GeneralUtil.FoodType.NONE)
                .setValue(HAS_NAME_TAG, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FOOD_TYPE, HAS_NAME_TAG);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack heldItem, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!(blockEntity instanceof PetBowlBlockEntity entity)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (heldItem.is(Items.SHEARS)) {
            if (state.getValue(HAS_NAME_TAG)) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(HAS_NAME_TAG, false), 3);
                    popResource(level, pos, new ItemStack(Items.NAME_TAG));
                    if (!player.getAbilities().instabuild) {
                        heldItem.hurtAndBreak(1, player, player.getEquipmentSlotForItem(player.getItemInHand(hand)));
                    }
                }
                return ItemInteractionResult.SUCCESS;
            }
        }

        if (heldItem.is(Items.NAME_TAG)) {
            if (!state.getValue(HAS_NAME_TAG)) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(HAS_NAME_TAG, true), 3);
                    if (!player.getAbilities().instabuild) heldItem.shrink(1);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }

        if (state.getValue(HAS_NAME_TAG) && player.isShiftKeyDown()) {
            if (level.isClientSide) {
                FarmAndCharmClient.openPetBowlScreen(entity);
            }
            return ItemInteractionResult.SUCCESS;
        }

        if (!level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            if (state.getValue(FOOD_TYPE) == GeneralUtil.FoodType.NONE) {
                GeneralUtil.FoodType type = getFoodType(heldItem);
                if (type != GeneralUtil.FoodType.NONE) {
                    level.setBlock(pos, state.setValue(FOOD_TYPE, type), 3);
                    if (!player.getAbilities().instabuild) {
                        heldItem.shrink(1);
                    }
                    entity.setItem(0, new ItemStack(heldItem.getItem()));

                    entity.onFed(heldItem);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private GeneralUtil.FoodType getFoodType(ItemStack stack) {
        if (stack.is(ObjectRegistry.CAT_FOOD.get())) return GeneralUtil.FoodType.CAT;
        if (stack.is(ObjectRegistry.DOG_FOOD.get())) return GeneralUtil.FoodType.DOG;
        return GeneralUtil.FoodType.NONE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PetBowlBlockEntity(pos, state);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
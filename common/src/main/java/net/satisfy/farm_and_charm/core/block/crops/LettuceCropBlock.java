package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;

public class LettuceCropBlock extends CropBlock implements BigCropCapable {
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    public static final BooleanProperty BIG = BooleanProperty.create("big");
    public static final BooleanProperty GIANT = BooleanProperty.create("giant");

    public LettuceCropBlock(Properties settings) {
        super(settings.randomTicks());
        registerDefaultState(getStateDefinition().any()
                .setValue(AGE, 0)
                .setValue(BIG, false)
                .setValue(GIANT, false));
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return ObjectRegistry.LETTUCE_SEEDS.get();
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    public @NotNull IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public BooleanProperty getBigProperty() {
        return BIG;
    }

    @Override
    public BooleanProperty getGiantProperty() {
        return GIANT;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, BIG, GIANT);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return getAge(state) < getMaxAge() || !state.getValue(BIG) || !state.getValue(GIANT);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getRawBrightness(pos, 0) >= 9) {
            int age = getAge(state);
            if (age < getMaxAge()) {
                float growth = GeneralUtil.GrowthSpeedUtil.getGrowthSpeed(state, level, pos);
                if (random.nextInt((int)(25.0F / growth) + 1) == 0) {
                    level.setBlock(pos, getStateForAge(age + 1), 2);
                }
            }
        }
        if (state.getValue(AGE) == getMaxAge() && (!state.getValue(BIG) || !state.getValue(GIANT))) {
            tryTransformToBigCrop(level, pos, state, false);
        }
    }
}

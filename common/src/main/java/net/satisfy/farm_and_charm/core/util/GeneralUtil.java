package net.satisfy.farm_and_charm.core.util;

import com.google.gson.JsonArray;
import com.mojang.datafixers.util.Pair;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import io.netty.buffer.Unpooled;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.entity.ChairEntity;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("unused, deprecation")
public class GeneralUtil {
    private static final Map<ResourceLocation, Map<BlockPos, Pair<ChairEntity, BlockPos>>> CHAIRS = new HashMap<>();
    private static final String BLOCK_POS_KEY = "block_pos";
    private static final String BLOCK_POSES_KEY = "block_poses";
    public static final EnumProperty<GeneralUtil.LineConnectingType> LINE_CONNECTING_TYPE = EnumProperty.create("type", GeneralUtil.LineConnectingType.class);

    public static <T extends Block> RegistrySupplier<T> registerWithItem(DeferredRegister<Block> registerB, Registrar<Block> registrarB, DeferredRegister<Item> registerI, Registrar<Item> registrarI, ResourceLocation name, Supplier<T> block) {
        RegistrySupplier<T> toReturn = registerWithoutItem(registerB, registrarB, name, block);
        registerItem(registerI, registrarI, name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    public static <T extends Block> RegistrySupplier<T> registerWithoutItem(DeferredRegister<Block> register, Registrar<Block> registrar, ResourceLocation path, Supplier<T> block) {
        return Platform.isForge() ? register.register(path.getPath(), block) : registrar.register(path, block);
    }

    public static <T extends Item> RegistrySupplier<T> registerItem(DeferredRegister<Item> register, Registrar<Item> registrar, ResourceLocation path, Supplier<T> itemSupplier) {
        return Platform.isForge() ? register.register(path.getPath(), itemSupplier) : registrar.register(path, itemSupplier);
    }

    public static boolean isFullAndSolid(LevelReader levelReader, BlockPos blockPos) {
        return isFaceFull(levelReader, blockPos) && isSolid(levelReader, blockPos);
    }

    public static boolean isFaceFull(LevelReader levelReader, BlockPos blockPos) {
        BlockPos belowPos = blockPos.below();
        return Block.isFaceFull(levelReader.getBlockState(belowPos).getShape(levelReader, belowPos), Direction.UP);
    }

    public static boolean isSolid(LevelReader levelReader, BlockPos blockPos) {
        return levelReader.getBlockState(blockPos.below()).isSolid();
    }

    public static boolean matchesRecipe(Container inventory, NonNullList<Ingredient> recipe, int startIndex, int endIndex) {
        List<ItemStack> inputStacks = new ArrayList<>();
        for (int i = startIndex; i <= endIndex; i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) inputStacks.add(stack.copy());
        }

        if (inputStacks.size() != recipe.size()) return false;

        List<Ingredient> unmatched = new ArrayList<>(recipe);
        for (ItemStack input : inputStacks) {
            boolean matched = false;
            Iterator<Ingredient> iter = unmatched.iterator();
            while (iter.hasNext()) {
                if (iter.next().test(input)) {
                    iter.remove();
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }

        return unmatched.isEmpty();
    }

    public static NonNullList<Ingredient> deserializeIngredients(JsonArray json) {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for(int i = 0; i < json.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(json.get(i));
            if (!ingredient.isEmpty()) {
                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;

        for(int i = 0; i < times; ++i) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.joinUnoptimized(buffer[1], Shapes.box(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX), BooleanOp.OR));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static void spawnSlice(Level level, ItemStack stack, double x, double y, double z, double xMotion, double yMotion, double zMotion) {
        ItemEntity entity = new ItemEntity(level, x, y, z, stack);
        entity.setDeltaMovement(xMotion, yMotion, zMotion);
        level.addFreshEntity(entity);
    }

    public static Collection<ServerPlayer> tracking(ServerLevel world, ChunkPos pos) {
        Objects.requireNonNull(world, "The world cannot be null");
        Objects.requireNonNull(pos, "The chunk pos cannot be null");
        return world.getChunkSource().chunkMap.getPlayers(pos, false);
    }

    public static Collection<ServerPlayer> tracking(ServerLevel world, BlockPos pos) {
        Objects.requireNonNull(pos, "BlockPos cannot be null");
        return tracking(world, new ChunkPos(pos));
    }

    public static ItemStack convertStackAfterFinishUsing(LivingEntity entity, ItemStack used, Item returnItem, Item usedItem) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, used);
            serverPlayer.awardStat(Stats.ITEM_USED.get(usedItem));
        }

        if (used.isEmpty()) {
            return new ItemStack(returnItem);
        } else {
            if (entity instanceof Player player) {
                if (!((Player)entity).getAbilities().instabuild) {
                    ItemStack itemStack2 = new ItemStack(returnItem);
                    if (!player.getInventory().add(itemStack2)) {
                        player.drop(itemStack2, false);
                    }
                }
            }

            return used;
        }
    }

    public static InteractionResult onUse(Level world, Player player, InteractionHand hand, BlockHitResult hit, double extraHeight) {
        if (world.isClientSide) return InteractionResult.PASS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        if (GeneralUtil.isPlayerSitting(player)) return InteractionResult.PASS;
        if (hit.getDirection() == Direction.DOWN) return InteractionResult.PASS;
        BlockPos hitPos = hit.getBlockPos();
        if (!GeneralUtil.isOccupied(world, hitPos) && player.getItemInHand(hand).isEmpty()) {
            ChairEntity chair = EntityTypeRegistry.CHAIR.get().create(world);
            assert chair != null;
            chair.moveTo(hitPos.getX() + 0.5D, hitPos.getY() + 0.25D + extraHeight, hitPos.getZ() + 0.5D, 0, 0);
            if (GeneralUtil.addChairEntity(world, hitPos, chair, player.blockPosition())) {
                world.addFreshEntity(chair);
                player.startRiding(chair);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public static BlockPos getPreviousPlayerPosition(Player player, ChairEntity chairEntity) {
        if (!player.level().isClientSide()) {
            ResourceLocation id = getDimensionTypeId(player.level());
            if (CHAIRS.containsKey(id)) {
                Map<BlockPos, Pair<ChairEntity, BlockPos>> chairsInDimension = CHAIRS.get(id);

                for (Pair<ChairEntity, BlockPos> chairPair : chairsInDimension.values()) {
                    if (chairPair.getFirst() == chairEntity) {
                        return chairPair.getSecond();
                    }
                }
            }
        }

        return null;
    }

    public static boolean isOccupied(Level world, BlockPos pos) {
        ResourceLocation id = getDimensionTypeId(world);
        return GeneralUtil.CHAIRS.containsKey(id) && GeneralUtil.CHAIRS.get(id).containsKey(pos);
    }

    public static boolean isPlayerSitting(Player player) {
        for (ResourceLocation i : CHAIRS.keySet()) {
            for (Pair<ChairEntity, BlockPos> pair : CHAIRS.get(i).values()) {
                if (pair.getFirst().hasPassenger(player))
                    return true;
            }
        }
        return false;
    }

    private static ResourceLocation getDimensionTypeId(Level world) {
        return world.dimensionTypeId().location();
    }

    public static void onStateReplaced(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            ChairEntity entity = GeneralUtil.getChairEntity(world, pos);
            if (entity != null) {
                GeneralUtil.removeChairEntity(world, pos);
                entity.ejectPassengers();
            }
        }
    }

    public static boolean addChairEntity(Level world, BlockPos blockPos, ChairEntity entity, BlockPos playerPos) {
        if (!world.isClientSide) {
            ResourceLocation id = getDimensionTypeId(world);
            if (!CHAIRS.containsKey(id)) CHAIRS.put(id, new HashMap<>());
            CHAIRS.get(id).put(blockPos, Pair.of(entity, playerPos));
            return true;
        }
        return false;
    }

    public static void removeChairEntity(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            ResourceLocation id = getDimensionTypeId(world);
            if (CHAIRS.containsKey(id)) {
                CHAIRS.get(id).remove(pos);
            }
        }
    }

    public static ChairEntity getChairEntity(Level world, BlockPos pos) {
        if (!world.isClientSide()) {
            ResourceLocation id = getDimensionTypeId(world);
            if (CHAIRS.containsKey(id) && CHAIRS.get(id).containsKey(pos))
                return CHAIRS.get(id).get(pos).getFirst();
        }
        return null;
    }

    public static boolean isDamageType(DamageSource source, List<ResourceKey<DamageType>> damageTypes) {
        Iterator<ResourceKey<DamageType>> iterator = damageTypes.iterator();

        ResourceKey<DamageType> damageKey;
        do {
            if (!iterator.hasNext()) {
                return false;
            }

            damageKey = iterator.next();
        } while (!source.is(damageKey));

        return true;
    }

    public static boolean isFire(DamageSource source) {
        return isDamageType(source, List.of(DamageTypes.ON_FIRE, DamageTypes.IN_FIRE, DamageTypes.FIREBALL, DamageTypes.FIREWORKS, DamageTypes.UNATTRIBUTED_FIREBALL));
    }

    public static boolean isIndexInRange(int index, int startInclusive, int endInclusive) {
        return index >= startInclusive && index <= endInclusive;
    }

    public static FriendlyByteBuf create() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static void popResourceFromFace(Level level, BlockPos blockPos, Direction side, ItemStack itemStack) {
        BlockState blockState = level.getBlockState(blockPos);
        double itemWidth = EntityType.ITEM.getWidth();
        double itemHeight = EntityType.ITEM.getHeight();
        VoxelShape shape = blockState.getCollisionShape(level, blockPos);
        double posX = (double)blockPos.getX() + 0.5;
        double posY = (double)blockPos.getY() + 0.5;
        double posZ = (double)blockPos.getZ() + 0.5;
        double offsetX = 0.0;
        double offsetY = 0.0;
        double offsetZ = 0.0;
        switch (side) {
            case DOWN:
                posY = (double)blockPos.getY() - shape.min(Direction.Axis.Y);
                offsetY = -itemHeight * 2.0;
                break;
            case UP:
                posY = (double)blockPos.getY() + shape.max(Direction.Axis.Y);
                break;
            case NORTH:
                posZ = (double)blockPos.getZ() + shape.min(Direction.Axis.Z);
                offsetZ = -itemWidth;
                break;
            case SOUTH:
                posZ = (double)blockPos.getZ() + shape.max(Direction.Axis.Z);
                offsetZ = itemWidth;
                break;
            case WEST:
                posX = (double)blockPos.getX() + shape.min(Direction.Axis.X);
                offsetX = -itemWidth;
                break;
            case EAST:
                posX = (double)blockPos.getX() + shape.max(Direction.Axis.X);
                offsetX = itemWidth;
        }

        int i = side.getStepX();
        int j = side.getStepY();
        int k = side.getStepZ();
        double deltaX = i == 0 ? Mth.nextDouble(level.random, -0.1, 0.1) : (double)i * 0.1;
        double deltaY = j == 0 ? Mth.nextDouble(level.random, 0.0, 0.1) : (double)j * 0.1 + 0.1;
        double deltaZ = k == 0 ? Mth.nextDouble(level.random, -0.1, 0.1) : (double)k * 0.1;
        popResource(level, new ItemEntity(level, posX + offsetX, posY + offsetY, posZ + offsetZ, itemStack, deltaX, deltaY, deltaZ), itemStack);
    }

    private static void popResource(Level level, ItemEntity itemEntity, ItemStack itemStack) {
        if (!level.isClientSide && !itemStack.isEmpty() && level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
        }

    }

    public static void putBlockPos(CompoundTag compoundTag, BlockPos blockPos) {
        if (blockPos != null) {
            int[] positions = new int[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()};
            compoundTag.putIntArray("block_pos", positions);
        }
    }

    public static void putBlockPoses(CompoundTag compoundTag, Collection<BlockPos> blockPoses) {
        if (blockPoses != null && !blockPoses.isEmpty()) {
            int[] positions = new int[blockPoses.size() * 3];
            int pos = 0;

            for(Iterator<BlockPos> var4 = blockPoses.iterator(); var4.hasNext(); ++pos) {
                BlockPos blockPos = var4.next();
                positions[pos * 3] = blockPos.getX();
                positions[pos * 3 + 1] = blockPos.getY();
                positions[pos * 3 + 2] = blockPos.getZ();
            }

            compoundTag.putIntArray("block_poses", positions);
        }
    }

    public static @Nullable BlockPos readBlockPos(CompoundTag compoundTag) {
        if (!compoundTag.contains("block_pos")) {
            return null;
        } else {
            int[] positions = compoundTag.getIntArray("block_pos");
            return new BlockPos(positions[0], positions[1], positions[2]);
        }
    }

    public static Set<BlockPos> readBlockPoses(CompoundTag compoundTag) {
        Set<BlockPos> blockSet = new HashSet<>();
        if (compoundTag.contains("block_poses")) {
            int[] positions = compoundTag.getIntArray("block_poses");

            for (int pos = 0; pos < positions.length / 3; ++pos) {
                blockSet.add(new BlockPos(positions[pos * 3], positions[pos * 3 + 1], positions[pos * 3 + 2]));
            }

        }
        return blockSet;
    }

    public enum LineConnectingType implements StringRepresentable {
        NONE("none"),
        MIDDLE("middle"),
        LEFT("left"),
        RIGHT("right");

        private final String name;

        LineConnectingType(String type) {
            this.name = type;
        }

        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    public static class FoodComponent extends FoodProperties {
        public FoodComponent(List<Pair<MobEffectInstance, Float>> statusEffects) {
            super(1, 0.0F, false, true, false, statusEffects);
        }
    }

    public static Optional<Tuple<Float, Float>> getRelativeHitCoordinatesForBlockFace(BlockHitResult blockHitResult, Direction direction, Direction[] unAllowedDirections) {
        Direction direction2 = blockHitResult.getDirection();
        if (Arrays.asList(unAllowedDirections).contains(direction2)) {
            return Optional.empty();
        } else if (direction != direction2 && direction2 != Direction.UP && direction2 != Direction.DOWN) {
            return Optional.empty();
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos().relative(direction2);
            Vec3 vec3 = blockHitResult.getLocation().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            float d = (float) vec3.x();
            float f = (float) vec3.z();
            float y = (float) vec3.y();

            if (direction2 == Direction.UP || direction2 == Direction.DOWN) {
                direction2 = direction;
            }

            return switch (direction2) {
                case NORTH -> Optional.of(new Tuple<>((float) (1.0 - d), y));
                case SOUTH -> Optional.of(new Tuple<>(d, y));
                case WEST -> Optional.of(new Tuple<>(f, y));
                case EAST -> Optional.of(new Tuple<>((float) (1.0 - f), y));
                case DOWN, UP -> Optional.empty();
            };
        }
    }

    public interface FedTracker {
        void farmAndCharm$$markAsFed();
        boolean farmAndCharm$$isFed();
    }

    public enum FoodType implements StringRepresentable {
        NONE("none"),
        CAT("cat"),
        DOG("dog");

        private final String name;

        FoodType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

package net.satisfy.farm_and_charm.core.util;

import dev.architectury.event.events.common.TickEvent;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.entity.AbstractCartEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

public class CartWorldData extends SavedData {
    private static boolean initialized;
    private static CartWorldData clientInstance;

    private static final ResourceLocation CART_SLOW_ID = FarmAndCharm.identifier("cart_slow");

    private final Int2ObjectMap<AbstractCartEntity> pulling = new Int2ObjectOpenHashMap<>();
    private final ArrayList<UUID> pendingCartUuids = new ArrayList<>();

    private final Int2ObjectMap<Vec3> horsePreTickPositions = new Int2ObjectOpenHashMap<>();
    private final IntOpenHashSet slowedPlayers = new IntOpenHashSet();
    private final IntOpenHashSet slowedHorses = new IntOpenHashSet();

    public static void ensureInitialized() {
        if (initialized) {
            return;
        }
        initialized = true;
        TickEvent.SERVER_LEVEL_PRE.register(level -> CartWorldData.get(level).preTick(level));
        TickEvent.SERVER_LEVEL_POST.register(level -> CartWorldData.get(level).tick(level));
    }

    public boolean canAttach(Entity puller, AbstractCartEntity cart) {
        if (puller == null || cart == null) {
            return false;
        }
        AbstractCartEntity existing = this.pulling.get(puller.getId());
        return existing == null || existing == cart;
    }

    public AbstractCartEntity getCartPulledBy(Entity puller) {
        if (puller == null) {
            return null;
        }
        return this.pulling.get(puller.getId());
    }

    public void addPulling(AbstractCartEntity cart) {
        Entity puller = cart.getPulling();
        if (puller == null) {
            return;
        }
        AbstractCartEntity existing = this.pulling.get(puller.getId());
        if (existing != null && existing != cart) {
            return;
        }
        this.pulling.put(puller.getId(), cart);
        setDirty();
    }

    public void preTick(ServerLevel level) {
        this.horsePreTickPositions.clear();
        for (AbstractCartEntity cart : this.pulling.values()) {
            Entity puller = cart.getPulling();
            if (puller instanceof AbstractHorse horse) {
                this.horsePreTickPositions.put(horse.getId(), horse.position());
            }
        }
    }

    public void tick(ServerLevel level) {
        if (!this.pendingCartUuids.isEmpty()) {
            for (int index = this.pendingCartUuids.size() - 1; index >= 0; index--) {
                UUID cartUuid = this.pendingCartUuids.get(index);
                Entity entity = level.getEntity(cartUuid);
                if (entity instanceof AbstractCartEntity cart) {
                    if (cart.getPulling() != null && this.canAttach(cart.getPulling(), cart)) {
                        this.addPulling(cart);
                    }
                    this.pendingCartUuids.remove(index);
                }
            }
        }

        IntOpenHashSet currentPlayers = new IntOpenHashSet();
        IntOpenHashSet currentHorses = new IntOpenHashSet();

        Iterator<Integer> iterator = this.pulling.keySet().iterator();
        while (iterator.hasNext()) {
            int pullId = iterator.next();
            AbstractCartEntity cart = this.pulling.get(pullId);
            if (cart == null || cart.shouldStopPulledTick()) {
                iterator.remove();
                setDirty();
                continue;
            }

            Entity puller = cart.getPulling();

            if (puller instanceof Player player) {
                applyCartSlow(player);
                currentPlayers.add(player.getId());
            } else if (puller instanceof AbstractHorse horse) {
                applyCartSlow(horse);
                currentHorses.add(horse.getId());

                Entity controller = cart.getControllingPassenger();
                boolean controlled = controller instanceof Player controllingPlayer && controllingPlayer.getVehicle() == cart;

                if (controlled) {
                    suppressHorseIdleAnimations(horse);
                    driveHorseFromCart(cart, horse, (Player) controller);
                } else {
                    Vec3 prePos = this.horsePreTickPositions.get(horse.getId());
                    if (prePos != null) {
                        Vec3 current = horse.position();
                        double dx = current.x - prePos.x;
                        double dz = current.z - prePos.z;
                        if (dx * dx + dz * dz > 1.0E-10D) {
                            horse.setPos(prePos.x, current.y, prePos.z);
                        }
                    }
                    horse.setDeltaMovement(0.0D, horse.getDeltaMovement().y, 0.0D);
                    cart.setCartSpeed(0.0F);
                }
            }

            if (!(cart.getPulling() instanceof AbstractCartEntity)) {
                cart.pulledPostTick();
            }
        }

        cleanupSlowness(level, currentPlayers, currentHorses);
    }

    private void driveHorseFromCart(AbstractCartEntity cart, AbstractHorse horse, Player player) {
        float input = player.zza;
        float speed = cart.getCartSpeed();

        float accel = 0.02F;
        float decel = 0.03F;
        float maxSpeed = 0.23F;

        if (input > 0.001F) {
            speed = Mth.clamp(speed + accel * input, 0.0F, maxSpeed);
        } else if (input < -0.001F) {
            speed = Mth.clamp(speed + decel * input, 0.0F, maxSpeed);
        } else {
            speed = Mth.clamp(speed - 0.01F, 0.0F, maxSpeed);
        }

        cart.setCartSpeed(speed);

        float yaw = player.getYRot();
        horse.setYRot(yaw);
        horse.yRotO = yaw;
        horse.setYHeadRot(yaw);
        horse.yHeadRotO = yaw;

        horse.getNavigation().stop();

        if (speed <= 1.0E-4F) {
            horse.setDeltaMovement(0.0D, horse.getDeltaMovement().y, 0.0D);
            return;
        }

        float yawRad = yaw * ((float) Math.PI / 180.0F);
        double forwardX = -Mth.sin(yawRad);
        double forwardZ = Mth.cos(yawRad);

        Vec3 delta = new Vec3(forwardX * speed, 0.0D, forwardZ * speed);
        horse.setDeltaMovement(delta.x, horse.getDeltaMovement().y, delta.z);
        horse.move(net.minecraft.world.entity.MoverType.SELF, new Vec3(delta.x, 0.0D, delta.z));
        horse.hasImpulse = true;
    }

    private void suppressHorseIdleAnimations(AbstractHorse horse) {
        horse.setEating(false);
        horse.setStanding(false);
    }

    private void applyCartSlow(Entity entity) {
        if (!(entity instanceof net.minecraft.world.entity.LivingEntity livingEntity)) {
            return;
        }
        AttributeInstance attr = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) {
            return;
        }
        AttributeModifier existing = attr.getModifier(CART_SLOW_ID);
        if (existing != null) {
            return;
        }
        attr.addTransientModifier(new AttributeModifier(CART_SLOW_ID, -0.1D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    private void removeCartSlow(Entity entity) {
        if (!(entity instanceof net.minecraft.world.entity.LivingEntity livingEntity)) {
            return;
        }
        AttributeInstance attr = livingEntity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) {
            return;
        }
        AttributeModifier existing = attr.getModifier(CART_SLOW_ID);
        if (existing == null) {
            return;
        }
        attr.removeModifier(CART_SLOW_ID);
    }

    private void cleanupSlowness(ServerLevel level, IntOpenHashSet currentPlayers, IntOpenHashSet currentHorses) {
        int[] playerIds = this.slowedPlayers.toIntArray();
        for (int playerId : playerIds) {
            if (!currentPlayers.contains(playerId)) {
                Entity entity = level.getEntity(playerId);
                if (entity != null) {
                    removeCartSlow(entity);
                }
                this.slowedPlayers.remove(playerId);
            }
        }

        int[] horseIds = this.slowedHorses.toIntArray();
        for (int horseId : horseIds) {
            if (!currentHorses.contains(horseId)) {
                Entity entity = level.getEntity(horseId);
                if (entity != null) {
                    removeCartSlow(entity);
                }
                this.slowedHorses.remove(horseId);
            }
        }

        this.slowedPlayers.addAll(currentPlayers);
        this.slowedHorses.addAll(currentHorses);
    }

    public void tickClient(Level level) {
        Iterator<Integer> iterator = this.pulling.keySet().iterator();
        while (iterator.hasNext()) {
            int pullId = iterator.next();
            AbstractCartEntity cart = this.pulling.get(pullId);
            if (cart == null || !cart.isAlive()) {
                iterator.remove();
                continue;
            }

            Entity puller = cart.getPulling();
            if (puller == null || !puller.isAlive()) {
                iterator.remove();
                continue;
            }

            cart.pulledPostTick();
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag cartList = new ListTag();
        for (AbstractCartEntity cart : this.pulling.values()) {
            cartList.add(NbtUtils.createUUID(cart.getUUID()));
        }
        tag.put("cartList", cartList);
        return tag;
    }

    public static CartWorldData createFromNbt(CompoundTag tag) {
        CartWorldData data = new CartWorldData();
        ListTag cartList = tag.getList("cartList", Tag.TAG_INT_ARRAY);
        for (Tag value : cartList) {
            data.pendingCartUuids.add(NbtUtils.loadUUID(value));
        }
        return data;
    }

    public static CartWorldData get(Level level) {
        ensureInitialized();
        if (level.isClientSide()) {
            if (clientInstance == null) {
                clientInstance = new CartWorldData();
            }
            return clientInstance;
        }
        return getServer(Objects.requireNonNull(level.getServer()), level.dimension());
    }

    private static CartWorldData getServer(MinecraftServer server, ResourceKey<Level> levelType) {
        ServerLevel level = Objects.requireNonNull(server.getLevel(levelType));
        Factory<CartWorldData> factory = new Factory<>(
                CartWorldData::new,
                (tag, provider) -> CartWorldData.createFromNbt(tag),
                null
        );
        return level.getDataStorage().computeIfAbsent(factory, "farm_and_charm_cart_world");
    }

    public void removePullingByCart(AbstractCartEntity cart) {
        Iterator<Int2ObjectMap.Entry<AbstractCartEntity>> iterator = this.pulling.int2ObjectEntrySet().iterator();
        while (iterator.hasNext()) {
            Int2ObjectMap.Entry<AbstractCartEntity> entry = iterator.next();
            if (entry.getValue() == cart) {
                iterator.remove();
                setDirty();
            }
        }
    }
}
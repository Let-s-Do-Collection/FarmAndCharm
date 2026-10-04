package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.farm_and_charm.core.block.ScarecrowBlock;
import net.satisfy.farm_and_charm.core.block.ScarecrowMode;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

public class ScarecrowBlockEntity extends BlockEntity {

    private static final double WATCH_RANGE = 16.0;
    private static final double STARTLE_RANGE = 3.0;
    private static final float MAX_HEAD_YAW = 75.0F;
    private long nextGrowthTime;
    private float headYaw;
    private float headYawPrev;
    private float armRaise;
    private float armRaisePrev;

    public ScarecrowBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.SCARECROW_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.nextGrowthTime = tag.getLong("NextGrowthTime");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putLong("NextGrowthTime", this.nextGrowthTime);
    }

    /** Client only: where the head looks and how far the arms are raised in watchful mode. */
    public void clientTick(Level level, BlockPos pos, BlockState state) {
        this.headYawPrev = this.headYaw;
        this.armRaisePrev = this.armRaise;
        float targetYaw = 0.0F;
        float targetRaise = 0.0F;
        if (state.getValue(ScarecrowBlock.MODE) == ScarecrowMode.WATCHFUL) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1.5;
            double z = pos.getZ() + 0.5;
            Player player = level.getNearestPlayer(x, y, z, WATCH_RANGE, EntitySelector.NO_SPECTATORS);
            if (player != null) {
                float yawToPlayer = (float) Math.toDegrees(Math.atan2(-(player.getX() - x), player.getZ() - z));
                targetYaw = Mth.clamp(Mth.wrapDegrees(yawToPlayer - state.getValue(ScarecrowBlock.FACING).toYRot()), -MAX_HEAD_YAW, MAX_HEAD_YAW);
                if (player.distanceToSqr(x, y, z) < STARTLE_RANGE * STARTLE_RANGE) {
                    targetRaise = 1.0F;
                }
            } else {
                double time = level.getGameTime() + (Mth.murmurHash3Mixer((int) pos.asLong()) & 1023);
                targetYaw = (float) (Math.sin(time * 0.017) * 45.0 + Math.sin(time * 0.043) * 15.0);
            }
        }
        this.headYaw += (targetYaw - this.headYaw) * 0.08F;
        this.armRaise += (targetRaise - this.armRaise) * (targetRaise > this.armRaise ? 0.5F : 0.1F);
    }

    /** Head yaw in degrees relative to the body, Minecraft yaw direction. */
    public float getHeadYaw(float partialTick) {
        return Mth.lerp(partialTick, this.headYawPrev, this.headYaw);
    }

    /** 0 = arms down, 1 = arms thrown up. */
    public float getArmRaise(float partialTick) {
        return Mth.lerp(partialTick, this.armRaisePrev, this.armRaise);
    }

    public static <T extends BlockEntity> void tick(Level level, T be) {
        if (!(be instanceof ScarecrowBlockEntity self)) return;
        if (!(level instanceof ServerLevel serverLevel)) return;

        long currentTime = serverLevel.getGameTime();

        if (self.nextGrowthTime == 0L) {
            self.nextGrowthTime = currentTime + PlatformHelper.getScarecrowGrowthInterval() * 20L;
            self.setChanged();
            return;
        }

        if (currentTime < self.nextGrowthTime) return;

        self.nextGrowthTime = currentTime + PlatformHelper.getScarecrowGrowthInterval() * 20L;
        self.setChanged();

        int range = PlatformHelper.getScarecrowRange();
        BlockPos.betweenClosedStream(
                self.worldPosition.offset(-range, -1, -range),
                self.worldPosition.offset(range, 1, range)
        ).forEach(targetPos -> {
            BlockState targetState = serverLevel.getBlockState(targetPos);

            if (targetState.getBlock() instanceof CropBlock crop && !crop.isMaxAge(targetState)) {
                crop.randomTick(targetState, serverLevel, targetPos, serverLevel.random);
                serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Context.of(targetState));
                return;
            }

            if (targetState.getBlock() instanceof ClimbingCropBlock climbingCropBlock) {
                climbingCropBlock.randomTick(targetState, serverLevel, targetPos, serverLevel.random);
                serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, GameEvent.Context.of(targetState));
            }
        });
    }
}

package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.SturdyLadderBlock;
import net.satisfy.farm_and_charm.core.block.entity.SturdyLadderBlockEntity;
import org.joml.Quaternionf;

public class SturdyLadderRenderer implements BlockEntityRenderer<SturdyLadderBlockEntity> {
    private static final float TILT_ANGLE = 1.2F;
    private static final float JITTER = 0.008F;
    private static final float RAMP_TICKS = 4.0F;

    public SturdyLadderRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SturdyLadderBlockEntity ladder, float partialTick, PoseStack pose, MultiBufferSource buffers, int light, int overlay) {
        Level level = ladder.getLevel();
        BlockState state = ladder.getBlockState();
        if (level == null || !state.getValue(SturdyLadderBlock.SHAKING)) return;

        double time = level.getGameTime() + partialTick;
        float ramp = Math.min(1.0F, (float) (time - ladder.getShakeStart()) / RAMP_TICKS);
        Direction facing = state.getValue(LadderBlock.FACING);
        Direction wall = facing.getOpposite();
        float tilt = (0.6F + Math.abs((float) Math.sin(time * 1.1))) * TILT_ANGLE * ramp;
        float jitter = (float) Math.sin(time * 2.3) * JITTER * ramp;

        pose.pushPose();
        pose.translate(0.5 + wall.getStepX() * 0.5 + facing.getStepZ() * jitter, 0.0, 0.5 + wall.getStepZ() * 0.5 - facing.getStepX() * jitter);
        pose.mulPose(new Quaternionf().rotationAxis(tilt * ((float) Math.PI / 180F), facing.getStepZ(), 0.0F, -facing.getStepX()));
        pose.translate(-0.5 - wall.getStepX() * 0.5, 0.0, -0.5 - wall.getStepZ() * 0.5);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state.setValue(SturdyLadderBlock.SHAKING, false), pose, buffers, light, overlay);
        pose.popPose();
    }
}

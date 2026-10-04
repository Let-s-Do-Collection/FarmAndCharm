package net.satisfy.farm_and_charm.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.model.CraftingBowlModel;
import net.satisfy.farm_and_charm.core.block.CraftingBowlBlock;
import net.satisfy.farm_and_charm.core.block.entity.CraftingBowlBlockEntity;

public class CraftingBowlRenderer implements BlockEntityRenderer<CraftingBowlBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "textures/entity/crafting_bowl.png");
    private static final float TILT_ANGLE = 4.0F;
    private static final float DOUGH_SURFACE = 17.0F / 16.0F;
    private static final float DOUGH_DEPTH = 6.0F / 16.0F;
    private static final float DOUGH_SLOSH = 6.0F;
    private static final float DOUGH_BOB = 0.015F;
    private final ModelPart bowl;
    private final ModelPart dough;
    private final ModelPart swing;

    public CraftingBowlRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(CraftingBowlModel.LAYER_LOCATION);
        this.bowl = root.getChild("bowl");
        this.dough = root.getChild("dough");
        this.swing = root.getChild("swing");
    }

    @Override
    public void render(CraftingBowlBlockEntity be, float f, PoseStack pose, MultiBufferSource buf, int light, int overlay) {
        Level level = be.getLevel();
        if (level == null) return;
        BlockState state = level.getBlockState(be.getBlockPos());
        if (!(state.getBlock() instanceof CraftingBowlBlock)) return;

        pose.pushPose();
        pose.mulPose(Axis.XP.rotationDegrees(180));
        pose.translate(0.5f, -1.5f, -0.5f);
        float whiskAngle = be.getInterpolatedWhiskAngle(f);
        float tilt = be.getWhiskSpeed() / CraftingBowlBlockEntity.WHISK_MAX_SPEED * TILT_ANGLE;
        if (tilt > 0F) {
            // the bowl leans towards the side the whisk is pushing against and follows it around
            pose.translate(0f, 1.5f, 0f);
            pose.mulPose(Axis.XP.rotationDegrees((float) Math.cos(whiskAngle) * tilt));
            pose.mulPose(Axis.ZP.rotationDegrees((float) Math.sin(whiskAngle) * tilt));
            pose.translate(0f, -1.5f, 0f);
        }

        VertexConsumer vc = buf.getBuffer(RenderType.entityTranslucent(TEXTURE));

        bowl.render(pose, vc, light, overlay);
        this.renderDough(be, level, f, whiskAngle, pose, vc, light, overlay);
        pose.mulPose(Axis.YP.rotation(whiskAngle));
        swing.render(pose, vc, light, overlay);

        this.renderItems(pose, buf, be.getItems(), light, overlay);
        pose.popPose();
    }

    /** The dough rises from the bottom while stirring and sloshes along with the whisk. */
    private void renderDough(CraftingBowlBlockEntity be, Level level, float partialTick, float whiskAngle, PoseStack pose, VertexConsumer vc, int light, int overlay) {
        float fill = be.getDoughFill();
        if (fill <= 0.0F) return;
        float speed = be.getWhiskSpeed() / CraftingBowlBlockEntity.WHISK_MAX_SPEED;
        double time = level.getGameTime() + partialTick;
        // the pose is upside down here, so a positive y moves the dough further down into the bowl
        float surface = DOUGH_SURFACE + (1.0F - fill) * DOUGH_DEPTH + (float) Math.sin(time * 0.6) * DOUGH_BOB * speed;
        pose.pushPose();
        pose.translate(0f, surface, 0f);
        pose.mulPose(Axis.XP.rotationDegrees((float) Math.cos(whiskAngle) * DOUGH_SLOSH * speed));
        pose.mulPose(Axis.ZP.rotationDegrees((float) Math.sin(whiskAngle) * DOUGH_SLOSH * speed));
        pose.translate(0f, -DOUGH_SURFACE, 0f);
        dough.render(pose, vc, light, overlay);
        pose.popPose();
    }

    private void renderItems(PoseStack poseStack, MultiBufferSource multiBufferSource, NonNullList<ItemStack> items, int i, int j) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        LocalPlayer player = Minecraft.getInstance().player;

        poseStack.translate(0f, 1.25f, 0f);
        poseStack.scale(0.35f, 0.35f, 0.35f);

        float offset = 0.26f;

        poseStack.translate(-offset, 0.1f, -offset);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        itemRenderer.renderStatic(player, items.get(0), ItemDisplayContext.FIXED, false, poseStack, multiBufferSource, Minecraft.getInstance().level, i, j, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));

        poseStack.translate(2 * offset, 0.1f, 0f);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        itemRenderer.renderStatic(player, items.get(1), ItemDisplayContext.FIXED, false, poseStack, multiBufferSource, Minecraft.getInstance().level, i, j, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));

        poseStack.translate(0f, 0.1f, 2 * offset);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        itemRenderer.renderStatic(player, items.get(2), ItemDisplayContext.FIXED, false, poseStack, multiBufferSource, Minecraft.getInstance().level, i, j, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));

        poseStack.translate(-2 * offset, 0.1f, 0f);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        itemRenderer.renderStatic(player, items.get(3), ItemDisplayContext.FIXED, false, poseStack, multiBufferSource, Minecraft.getInstance().level, i, j, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
    }
}

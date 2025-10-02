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
    private final ModelPart bowl;
    private final ModelPart swing;

    public CraftingBowlRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(CraftingBowlModel.LAYER_LOCATION);
        this.bowl = root.getChild("bowl");
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

        ResourceLocation tex = be.getStirringProgress() >= CraftingBowlBlock.STIRS_NEEDED
                ? ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "textures/entity/crafting_bowl_full.png")
                : ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "textures/entity/crafting_bowl.png");

        VertexConsumer vc = buf.getBuffer(RenderType.entityTranslucent(tex));

        bowl.render(pose, vc, light, overlay);
        pose.mulPose(Axis.YP.rotation(be.getInterpolatedWhiskAngle(f)));
        swing.render(pose, vc, light, overlay);

        this.renderItems(pose, buf, be.getItems(), light, overlay);
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

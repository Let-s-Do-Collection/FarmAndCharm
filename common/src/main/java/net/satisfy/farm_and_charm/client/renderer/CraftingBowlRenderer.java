package net.satisfy.farm_and_charm.client.renderer;

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
import net.satisfy.farm_and_charm.core.block.CraftingBowlBlock;
import net.satisfy.farm_and_charm.core.block.entity.CraftingBowlBlockEntity;
import net.satisfy.farm_and_charm.client.model.CraftingBowlModel;

public class CraftingBowlRenderer implements BlockEntityRenderer<CraftingBowlBlockEntity> {
    private final ModelPart bowl;
    private final ModelPart swing;


    public CraftingBowlRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(CraftingBowlModel.LAYER_LOCATION);

        this.bowl = root.getChild("bowl");
        this.swing = root.getChild("swing");
    }

    @Override
    public void render(CraftingBowlBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {

        Level level = blockEntity.getLevel();
        assert level != null;
        BlockState blockState = level.getBlockState(blockEntity.getBlockPos());
        if (!(blockState.getBlock() instanceof CraftingBowlBlock)) return;

        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.translate(0.5f, -1.5f, -0.5f);


        ResourceLocation location;
        if (blockEntity.getStirringProgress() >= CraftingBowlBlock.STIRS_NEEDED) {
            location = ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "textures/entity/crafting_bowl_full.png");
        } else {
            location = ResourceLocation.fromNamespaceAndPath(FarmAndCharm.MOD_ID, "textures/entity/crafting_bowl.png");
        }


        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(location));

        bowl.render(poseStack, vertexConsumer, i, j);
        if (level.getBlockState(blockEntity.getBlockPos()).getValue(CraftingBowlBlock.STIRRING) > 0)
            poseStack.mulPose(Axis.YP.rotation(((float) (System.currentTimeMillis() % 100000) / 100f) % 360));
        swing.render(poseStack, vertexConsumer, i, j);

        this.renderItems(poseStack, multiBufferSource, blockEntity.getItems(), i, j);

        poseStack.popPose();


    }

    private void renderItems(PoseStack poseStack, MultiBufferSource multiBufferSource, NonNullList<ItemStack> items, int i, int j) {

        final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        final LocalPlayer player = Minecraft.getInstance().player;

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

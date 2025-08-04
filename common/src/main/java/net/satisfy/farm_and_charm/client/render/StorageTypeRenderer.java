package net.satisfy.farm_and_charm.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.block.entity.StorageBlockEntity;

public interface StorageTypeRenderer {
    void render(StorageBlockEntity var1, PoseStack var2, MultiBufferSource var3, NonNullList<ItemStack> var4);
}
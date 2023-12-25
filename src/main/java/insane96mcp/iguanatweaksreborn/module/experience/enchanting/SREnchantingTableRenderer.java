package insane96mcp.iguanatweaksreborn.module.experience.enchanting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class SREnchantingTableRenderer implements BlockEntityRenderer<SREnchantingTableBlockEntity> {
    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

    public SREnchantingTableRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(SREnchantingTableBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.renderHoveringItem(blockEntity, ((Container) blockEntity).getItem(SREnchantingTableMenu.ITEM_SLOT), partialTicks, poseStack, bufferSource, packedLight);
    }

    private void renderHoveringItem(SREnchantingTableBlockEntity blockEntity, ItemStack itemToEnchant, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.0F, 0.5F);
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(itemToEnchant, blockEntity.getLevel(), null, 0);
        float hoverOffset = Mth.sin(((float)blockEntity.getLevel().dayTime() + partialTicks) / 10.0F) * 0.1F + 0.1F;
        float modelYScale = model.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
        poseStack.translate(0.0, hoverOffset + 0.2F * modelYScale, 0.0);
        poseStack.mulPose(Axis.YP.rotation(((float)blockEntity.getLevel().dayTime() + partialTicks) / 20.0F));
        Minecraft.getInstance().getItemRenderer().render(itemToEnchant, ItemDisplayContext.GROUND, false, poseStack, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, model);
        poseStack.popPose();
    }
}

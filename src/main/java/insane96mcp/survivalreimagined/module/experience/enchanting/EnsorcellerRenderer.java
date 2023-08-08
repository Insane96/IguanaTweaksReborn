package insane96mcp.survivalreimagined.module.experience.enchanting;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnsorcellerRenderer implements BlockEntityRenderer<EnsorcellerBlockEntity> {
    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

    public EnsorcellerRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(EnsorcellerBlockEntity blockEntity, float pPartialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        int posData = (int) blockEntity.getBlockPos().asLong();
        this.renderFlatItem(EnsorcellerMenu.ITEM_SLOT, ((Container) blockEntity).getItem(EnsorcellerMenu.ITEM_SLOT), poseStack, bufferSource, packedLight, packedOverlay, posData, blockEntity.getLevel());
    }

    private void renderFlatItem(int index, ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, int posData, Level level) {
        if (stack.isEmpty())
            return;

        poseStack.pushPose();
        poseStack.translate(0.0,0, 0.0);
        poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
        this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, level, posData + index);
        poseStack.popPose();
    }
}

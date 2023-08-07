package insane96mcp.survivalreimagined.module.mining.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import insane96mcp.survivalreimagined.module.mining.block.ForgeBlock;
import insane96mcp.survivalreimagined.module.mining.block.ForgeBlockEntity;
import insane96mcp.survivalreimagined.module.mining.inventory.ForgeMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ForgeRenderer implements BlockEntityRenderer<ForgeBlockEntity> {
    private final ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

    public ForgeRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(ForgeBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction direction = blockEntity.getBlockState().getValue(ForgeBlock.FACING);
        int posData = (int) blockEntity.getBlockPos().asLong();
        this.renderFlatItem(ForgeMenu.INGREDIENT_SLOT, (float) blockEntity.smashes / blockEntity.smashesRequired, ((Container) blockEntity).getItem(ForgeMenu.INGREDIENT_SLOT), direction, poseStack, bufferSource, packedLight, packedOverlay, posData, blockEntity.getLevel());
        this.renderFlatItem(ForgeMenu.GEAR_SLOT, 0f, ((Container) blockEntity).getItem(ForgeMenu.GEAR_SLOT), direction, poseStack, bufferSource, packedLight, packedOverlay, posData, blockEntity.getLevel());
        this.renderFlatItem(ForgeMenu.RESULT_SLOT, 0f, ((Container) blockEntity).getItem(ForgeMenu.RESULT_SLOT), direction, poseStack, bufferSource, packedLight, packedOverlay, posData, blockEntity.getLevel());
    }

    private void renderFlatItem(int index, float forgePercentage, ItemStack stack, Direction direction, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, int posData, Level level) {
        if (stack.isEmpty()) return;
        poseStack.pushPose();
        poseStack.translate(0.0,1.02, 0.0);
        poseStack.mulPose(Axis.XN.rotationDegrees(90.0F));
        double x = index == 2 ? -0.25d : -0.6;
        double y = switch (index) {
            case 0 -> 0.6d;
            case 1 -> 0.5d;
            default -> 0.5;
        };
        double z = index == 0 ? 0.03d - (forgePercentage * 0.03d) : 0;
        switch (direction.getAxis()) {
            case X -> {
                poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
                poseStack.translate(x, y, z);
            }
            case Z -> {
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
                poseStack.translate(x, -y, z);
            }
        }
        poseStack.scale(0.375F, 0.375F, 0.375F);
        this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, level, posData + index);
        poseStack.popPose();
    }
}

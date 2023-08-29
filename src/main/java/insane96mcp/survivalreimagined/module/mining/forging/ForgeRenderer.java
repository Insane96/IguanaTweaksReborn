package insane96mcp.survivalreimagined.module.mining.forging;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.block.ForgeBlock;
import insane96mcp.survivalreimagined.module.mining.multiblockfurnaces.block.ForgeBlockEntity;
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
        poseStack.translate(0.5, 1.02, 0.5);
        poseStack.mulPose(Axis.XN.rotationDegrees(-90.0F));
        double x = index == 2 ? -0.25d : 0.2d;
        double y = switch (index) {
            case 0 -> 0.075d;
            case 1 -> -0.075d;
            default -> 0;
        };
        double z = index == 0 ? -0.035d + (forgePercentage * 0.04d) : 0;
        poseStack.mulPose(Axis.ZP.rotationDegrees((direction.get2DDataValue() - 1) * 90f));
        poseStack.translate(x, y, z);
        poseStack.scale(0.4F, 0.4F, index == 0 ? 0.55f : 0.4F);
        this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, level, posData + index);
        poseStack.popPose();
    }
}

package insane96mcp.survivalreimagined.module.mining.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import insane96mcp.survivalreimagined.module.mining.entity.PrimedMiningCharge;
import insane96mcp.survivalreimagined.module.mining.feature.MiningCharge;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MiningChargeRenderer extends EntityRenderer<PrimedMiningCharge> {
    private final BlockRenderDispatcher blockRenderer;
    public MiningChargeRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.blockRenderer = pContext.getBlockRenderDispatcher();
    }

    @Override
    public void render(PrimedMiningCharge pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, 0.5F, 0.0F);
        int fuse = pEntity.getFuse();
        if ((float)fuse - pPartialTick + 1.0F < 10.0F) {
            float f = 1.0F - ((float)fuse - pPartialTick + 1.0F) / 10.0F;
            f = Mth.clamp(f, 0.0F, 1.0F);
            f *= f;
            f *= f;
            float f1 = 1.0F + f * 0.3F;
            pPoseStack.scale(f1, f1, f1);
        }

        pPoseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        pPoseStack.translate(-0.5F, -0.5F, 0.5F);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        TntMinecartRenderer.renderWhiteSolidBlock(this.blockRenderer, MiningCharge.MINING_CHARGE.block().get().defaultBlockState(), pPoseStack, pBuffer, pPackedLight, fuse / 5 % 2 == 0);
        pPoseStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PrimedMiningCharge pEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

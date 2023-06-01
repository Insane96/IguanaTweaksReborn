package insane96mcp.survivalreimagined.module.combat.client;

import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.module.combat.feature.Fletching;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Arrow;

public class SRArrowRenderer extends ArrowRenderer<Arrow> {
    public static final ResourceLocation QUARTZ_ARROW_LOCATION = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "textures/entity/projectiles/quartz.png");
    public static final ResourceLocation DIAMOND_ARROW_LOCATION = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "textures/entity/projectiles/diamond.png");
    public static final ResourceLocation EXPLOSIVE_ARROW_LOCATION = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "textures/entity/projectiles/explosive.png");
    public SRArrowRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(Arrow pEntity) {
        if (pEntity.getType().equals(Fletching.QUARTZ_ARROW.get()))
            return QUARTZ_ARROW_LOCATION;
        else if (pEntity.getType().equals(Fletching.DIAMOND_ARROW.get()))
            return DIAMOND_ARROW_LOCATION;
        else if (pEntity.getType().equals(Fletching.EXPLOSIVE_ARROW.get()))
            return EXPLOSIVE_ARROW_LOCATION;
        return null;
    }
}

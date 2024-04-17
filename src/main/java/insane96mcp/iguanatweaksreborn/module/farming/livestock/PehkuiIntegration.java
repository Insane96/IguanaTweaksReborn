package insane96mcp.iguanatweaksreborn.module.farming.livestock;

import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleTypes;

public class PehkuiIntegration {

    public static void setSize(LivingEntity entity, float ageRatio) {
        float scale = ageRatio < 0.75f ? ageRatio * 0.5f + 0.8f : (1f - ageRatio) * 0.70f + 1f;
        ScaleTypes.MODEL_WIDTH.getScaleData(entity).setScale(scale);
        ScaleTypes.MODEL_HEIGHT.getScaleData(entity).setScale(scale);
    }
}

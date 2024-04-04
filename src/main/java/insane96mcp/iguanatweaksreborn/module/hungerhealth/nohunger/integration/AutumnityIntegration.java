package insane96mcp.iguanatweaksreborn.module.hungerhealth.nohunger.integration;

import com.teamabnormals.autumnity.core.registry.AutumnityMobEffects;
import net.minecraft.world.entity.LivingEntity;

public class AutumnityIntegration {
    public static float tryApplyFoulTaste(LivingEntity livingEntity, float amount) {
        if (livingEntity.hasEffect(AutumnityMobEffects.FOUL_TASTE.get()))
            amount = amount * 1.5f;
        return amount;
    }
}

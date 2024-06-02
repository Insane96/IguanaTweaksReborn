package insane96mcp.iguanatweaksreborn.module.sleeprespawn.tiredness;

import insane96mcp.insanelib.world.effect.ILMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class TirednessEffect extends ILMobEffect {
    public TirednessEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn, false);
    }

    @Override
    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        if (pAmplifier == 0)
            return;
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier - 1);
    }
}

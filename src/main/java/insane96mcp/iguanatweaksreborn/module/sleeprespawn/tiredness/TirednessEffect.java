package insane96mcp.iguanatweaksreborn.module.sleeprespawn.tiredness;

import insane96mcp.iguanatweaksreborn.module.movement.stamina.IStaminaModifier;
import insane96mcp.insanelib.world.effect.ILMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class TirednessEffect extends ILMobEffect implements IStaminaModifier {
    public TirednessEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn, false);
    }

    @Override
    public float consumedStaminaModifier(int amplifier) {
        return 0.25f * amplifier;
    }

    @Override
    public float regenStaminaModifier(int amplifier) {
        return -0.1f * amplifier;
    }

    @Override
    public void addAttributeModifiers(LivingEntity pLivingEntity, AttributeMap pAttributeMap, int pAmplifier) {
        if (pAmplifier == 0)
            return;
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier - 1);
    }
}

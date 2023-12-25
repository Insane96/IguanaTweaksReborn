package insane96mcp.iguanatweaksreborn.module.hungerhealth.healthregen;

import insane96mcp.iguanatweaksreborn.module.movement.stamina.IStaminaModifier;
import insane96mcp.insanelib.world.effect.ILMobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class VigourEffect extends ILMobEffect implements IStaminaModifier {
    public VigourEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn, false);
    }

    @Override
    public float consumedStaminaModifier(int amplifier) {
        return -0.25f * (amplifier + 1);
    }

    @Override
    public float regenStaminaModifier(int amplifier) {
        return 0.1f * (amplifier + 1);
    }
}

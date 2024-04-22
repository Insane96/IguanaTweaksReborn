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
        int lvl = amplifier + 1;
        return (lvl * -0.1f - 0.15f);
    }

    @Override
    public float regenStaminaModifier(int amplifier) {
        return 0f;
    }
}

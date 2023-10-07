package insane96mcp.survivalreimagined.module.sleeprespawn.tiredness;

import insane96mcp.insanelib.world.effect.ILMobEffect;
import insane96mcp.survivalreimagined.module.movement.stamina.IStaminaModifier;
import net.minecraft.world.effect.MobEffectCategory;

public class TirednessEffect extends ILMobEffect implements IStaminaModifier {
    public TirednessEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn, false);
    }

    @Override
    public float consumedStaminaModifier(int amplifier) {
        return 0.25f * (amplifier + 1);
    }

    @Override
    public float regenStaminaModifier(int amplifier) {
        return -0.1f * (amplifier + 1);
    }
}

package insane96mcp.iguanatweaksreborn.effect;

import insane96mcp.iguanatweaksreborn.setup.ITMobEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;

/**
 * Reduces exhaustion by 20% per level
 */
public class Vigour extends ITMobEffect {
    public Vigour(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    public static float decreaseExhaustionConsumption(Player player, float exhaustion) {
        if (!player.hasEffect(ITMobEffects.VIGOUR.get()))
            return exhaustion;

        //noinspection ConstantConditions
        int amp = player.getEffect(ITMobEffects.VIGOUR.get()).getAmplifier() + 1;
        if (amp == 1)
            return exhaustion * 1 / 1.5f;
        else
            return exhaustion * 1 / amp;
    }
}

package insane96mcp.iguanatweaksreborn.effect;

import insane96mcp.iguanatweaksreborn.setup.ITMobEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;

/**
 * Reduces exhaustion by 20% per level
 */
public class Pumped extends ITMobEffect {
    public Pumped(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    public static float decreaseExhaustionConsumption(Player player, float exhaustion) {
        if (!player.hasEffect(ITMobEffects.PUMPED.get()))
            return exhaustion;

        //noinspection ConstantConditions
        int amp = player.getEffect(ITMobEffects.PUMPED.get()).getAmplifier() + 1;
        return exhaustion * Math.max(0, 1 - amp * 0.2f);
    }
}

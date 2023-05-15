package insane96mcp.survivalreimagined.effect;

import insane96mcp.insanelib.util.ILMobEffect;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.event.PlayerExhaustionEvent;
import insane96mcp.survivalreimagined.setup.SRMobEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Reduces exhaustion by 20% per level
 */
@Mod.EventBusSubscriber(modid = SurvivalReimagined.MOD_ID)
public class Vigour extends ILMobEffect {
    public Vigour(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }

    @SubscribeEvent
    public void decreaseExhaustionConsumption(PlayerExhaustionEvent event) {
        if (!event.getEntity().hasEffect(SRMobEffects.VIGOUR.get()))
            return;

        //noinspection ConstantConditions
        int amp = event.getEntity().getEffect(SRMobEffects.VIGOUR.get()).getAmplifier() + 1;
        event.setAmount(event.getAmount() * 1 / (amp == 1 ? 1.5f : amp));
    }
}

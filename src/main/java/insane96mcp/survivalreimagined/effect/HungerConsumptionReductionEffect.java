package insane96mcp.survivalreimagined.effect;

import insane96mcp.insanelib.util.ILMobEffect;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.event.PlayerExhaustionEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = SurvivalReimagined.MOD_ID)
public class HungerConsumptionReductionEffect extends ILMobEffect {
    public HungerConsumptionReductionEffect(MobEffectCategory typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn, false);
    }

    @SubscribeEvent
    public static void decreaseExhaustionConsumption(PlayerExhaustionEvent event) {
        for (Map.Entry<MobEffect, MobEffectInstance> entry : event.getEntity().getActiveEffectsMap().entrySet()) {
            if (!(entry.getKey() instanceof HungerConsumptionReductionEffect))
                continue;

            //noinspection ConstantConditions
            int amp = entry.getValue().getAmplifier() + 1;
            event.setAmount(event.getAmount() * 1 / (amp == 1 ? 1.5f : amp));
        }
    }
}

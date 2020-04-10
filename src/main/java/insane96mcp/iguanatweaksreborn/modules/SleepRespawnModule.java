package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Collectors;

public class SleepRespawnModule {
	public static void wakeUpHunger(SleepFinishedTimeEvent event) {
		if (!ModConfig.Modules.sleepRespawn)
			return;
		if (ModConfig.SleepRespawn.hungerDepletedOnWakeUp == 0 && ModConfig.SleepRespawn.effectsOnWakeUp.isEmpty())
			return;
		event.getWorld().getPlayers().stream().filter(LivingEntity::isSleeping).collect(Collectors.toList()).forEach((player) -> {
			player.getFoodStats().addStats(-ModConfig.SleepRespawn.hungerDepletedOnWakeUp, 1.0f);
			for (ModConfig.SleepRespawn.EffectOnWakeUp effectOnWakeUp : ModConfig.SleepRespawn.effectsOnWakeUp) {
				EffectInstance effectInstance = new EffectInstance(ForgeRegistries.POTIONS.getValue(effectOnWakeUp.potionId), effectOnWakeUp.duration, effectOnWakeUp.amplifier, true, true);
				player.addPotionEffect(effectInstance);
			}
		});
	}
}

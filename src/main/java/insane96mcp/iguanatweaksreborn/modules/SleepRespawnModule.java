package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.stream.Collectors;

public class SleepRespawnModule {
	public static void wakeUpHungerAndEffects(SleepFinishedTimeEvent event) {
		if (!ModConfig.Modules.sleepRespawn)
			return;
		if (ModConfig.SleepRespawn.hungerDepletedOnWakeUp == 0 && ModConfig.SleepRespawn.effectsOnWakeUp.isEmpty())
			return;
		event.getWorld().getPlayers().stream().filter(LivingEntity::isSleeping).collect(Collectors.toList()).forEach((player) -> {
			player.getFoodStats().addStats(-ModConfig.SleepRespawn.hungerDepletedOnWakeUp, 1.0f);
			//For some reasons saturation can go below 0 so I get it back up to 0
			if (player.getFoodStats().getSaturationLevel() < 0.0f)
				player.getFoodStats().addStats(1, -player.getFoodStats().getSaturationLevel() / 2f);
			for (ModConfig.SleepRespawn.EffectOnWakeUp effectOnWakeUp : ModConfig.SleepRespawn.effectsOnWakeUp) {
				EffectInstance effectInstance = new EffectInstance(ForgeRegistries.POTIONS.getValue(effectOnWakeUp.potionId), effectOnWakeUp.duration, effectOnWakeUp.amplifier, true, true);
				player.addPotionEffect(effectInstance);
			}
		});
	}

	public static void tooHungryToSleep(PlayerSleepInBedEvent event) {
		if (!ModConfig.Modules.sleepRespawn)
			return;
		if (!ModConfig.SleepRespawn.noSleepIfHungry)
			return;
		if (ModConfig.SleepRespawn.hungerDepletedOnWakeUp == 0)
			return;
		if (event.getPlayer().getFoodStats().getFoodLevel() >= ModConfig.SleepRespawn.hungerDepletedOnWakeUp)
			return;
		event.setResult(PlayerEntity.SleepResult.OTHER_PROBLEM);
		event.getPlayer().sendStatusMessage(new TranslationTextComponent(Strings.Translatable.NO_FOOD_FOR_SLEEP), true);
	}
}

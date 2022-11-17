package insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.utils.ITMobEffectInstance;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;

@Label(name = "Sleeping Effects", description = "Prevents the player from sleeping if has not enough Hunger and gives him effects on wake up. Effects on wake up are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class SleepingEffects extends ITFeature {
	public static final ArrayList<ITMobEffectInstance> EFFECTS_ON_WAKE_UP_DEFAULT = new ArrayList<>(Arrays.asList(
			new ITMobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 0),
			new ITMobEffectInstance(MobEffects.WEAKNESS, 300, 1),
			new ITMobEffectInstance(MobEffects.DIG_SLOWDOWN, 300, 1)
	));
	public static final ArrayList<ITMobEffectInstance> effectsOnWakeUp = new ArrayList<>();

	@Config(min = 0, max = 20)
	@Label(name = "Hunger Depleted on Wake Up", description = "How much the hunger bar is depleted when you wake up in the morning. Saturation depleted is based off this value times 2. Setting to 0 will disable this feature.")
	public static Integer hungerDepletedOnWakeUp = 11;
	@Config
	@Label(name = "No Sleep If Hungry", description = "If the player's hunger bar is below 'Hunger Depleted on Wake Up' he can't sleep.")
	public static Boolean noSleepIfHungry = true;

	public SleepingEffects(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadJsonConfigs() {
		super.loadJsonConfigs();
		this.loadAndReadFile("effects_on_wake_up.json", effectsOnWakeUp, EFFECTS_ON_WAKE_UP_DEFAULT, ITMobEffectInstance.LIST_TYPE);
	}

	@SubscribeEvent
	public void wakeUpHungerAndEffects(SleepFinishedTimeEvent event) {
		if (!this.isEnabled()
				|| (hungerDepletedOnWakeUp == 0 && effectsOnWakeUp.isEmpty()))
			return;
		event.getLevel().players().stream().filter(LivingEntity::isSleeping).toList().forEach((player) -> {
			player.getFoodData().eat(-hungerDepletedOnWakeUp, 1.0f);
			//For some reasons saturation can go below 0, so I get it back up to 0
			if (player.getFoodData().getSaturationLevel() < 0.0f)
				player.getFoodData().eat(1, -player.getFoodData().getSaturationLevel() / 2f);
			for (MobEffectInstance mobEffectInstance : effectsOnWakeUp) {
				if (mobEffectInstance.getEffect().isBeneficial() && player.getFoodData().getFoodLevel() <= 0)
					continue;
				player.addEffect(new MobEffectInstance(mobEffectInstance));
			}
		});
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void tooHungryToSleep(PlayerSleepInBedEvent event) {
		if (!this.isEnabled()
				|| event.getResultStatus() != null
				|| !noSleepIfHungry
				|| hungerDepletedOnWakeUp == 0
				|| event.getEntity().getFoodData().getFoodLevel() >= hungerDepletedOnWakeUp)
			return;
		event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
		event.getEntity().displayClientMessage(Component.translatable(Strings.Translatable.NO_FOOD_FOR_SLEEP), true);
	}
}
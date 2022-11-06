package insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.utils.EffectOnWakeUp;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Sleeping Effects", description = "Prevents the player from sleeping if has not enough Hunger and gives him effects on wake up")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class SleepingEffects extends Feature {

	private static ForgeConfigSpec.ConfigValue<List<? extends String>> effectsOnWakeUpConfig;
	//TODO Move to datapacks (or reloadable stuff like MobsPropertiesRandomness)?
	private static final List<String> effectsOnWakeUpDefault = List.of("minecraft:slowness,400,0", "minecraft:regeneration,200,1", "minecraft:weakness,300,1", "minecraft:mining_fatigue,300,1");
	public ArrayList<MobEffectInstance> effectsOnWakeUp;

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
	public void loadConfigOptions() {
		super.loadConfigOptions();
		effectsOnWakeUpConfig = this.getBuilder()
				.comment("A list of effects to apply to the player when he wakes up.\nThe format is modid:potion_id,duration_in_ticks,amplifier\nE.g. 'minecraft:slowness,240,1' will apply Slowness II for 12 seconds to the player.")
				.defineList("Effects on Wake Up", effectsOnWakeUpDefault, o -> o instanceof String);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		effectsOnWakeUp = EffectOnWakeUp.parseStringList(effectsOnWakeUpConfig.get());
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
			for (MobEffectInstance mobEffectInstance : this.effectsOnWakeUp) {
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
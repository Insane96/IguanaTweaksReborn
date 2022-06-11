package insane96mcp.iguanatweaksreborn.module.sleeprespawn.feature;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.module.sleeprespawn.utils.EffectOnWakeUp;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Sleeping Effects", description = "Prevents the player from sleeping if has not enough Hunger and gives him effects on wake up")
public class SleepingEffects extends Feature {

	private final ForgeConfigSpec.ConfigValue<Integer> hungerDepletedOnWakeUpConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> effectsOnWakeUpConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> noSleepIfHungryConfig;

	private final List<String> effectsOnWakeUpDefault = Lists.newArrayList("minecraft:slowness,400,0", "minecraft:regeneration,200,1", "minecraft:weakness,300,1", "minecraft:mining_fatigue,300,1");

	public int hungerDepletedOnWakeUp = 11;
	public ArrayList<MobEffectInstance> effectsOnWakeUp;
	public boolean noSleepIfHungry = true;

	public SleepingEffects(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		hungerDepletedOnWakeUpConfig = Config.builder
				.comment("How much the hunger bar is depleted when you wake up in the morning. Saturation depleted is based off this value times 2. Setting to 0 will disable this feature.")
				.defineInRange("Hunger Depleted on Wake Up", this.hungerDepletedOnWakeUp, -20, 20);
		effectsOnWakeUpConfig = Config.builder
				.comment("A list of effects to apply to the player when he wakes up.\nThe format is modid:potion_id,duration_in_ticks,amplifier\nE.g. 'minecraft:slowness,240,1' will apply Slowness II for 12 seconds to the player.")
				.defineList("Effects on Wake Up", this.effectsOnWakeUpDefault, o -> o instanceof String);
		noSleepIfHungryConfig = Config.builder
				.comment("If the player's hunger bar is below 'Hunger Depleted on Wake Up' he can't sleep.")
				.define("No Sleep If Hungry", true);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.hungerDepletedOnWakeUp = this.hungerDepletedOnWakeUpConfig.get();
		this.effectsOnWakeUp = EffectOnWakeUp.parseStringList(this.effectsOnWakeUpConfig.get());
		this.noSleepIfHungry = this.noSleepIfHungryConfig.get();
	}

	@SubscribeEvent
	public void wakeUpHungerAndEffects(SleepFinishedTimeEvent event) {
		if (!this.isEnabled())
			return;
		if (hungerDepletedOnWakeUp == 0 && effectsOnWakeUp.isEmpty())
			return;
		event.getWorld().players().stream().filter(LivingEntity::isSleeping).toList().forEach((player) -> {
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
		if (!this.isEnabled())
			return;
		if (!this.noSleepIfHungry)
			return;
		if (this.hungerDepletedOnWakeUp == 0)
			return;
		if (event.getPlayer().getFoodData().getFoodLevel() >= this.hungerDepletedOnWakeUp)
			return;
		event.setResult(Player.BedSleepingProblem.OTHER_PROBLEM);
		event.getPlayer().displayClientMessage(new TranslatableComponent(Strings.Translatable.NO_FOOD_FOR_SLEEP), true);
	}
}
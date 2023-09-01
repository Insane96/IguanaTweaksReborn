package insane96mcp.survivalreimagined.module.sleeprespawn;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.SRMobEffectInstance;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.sleeprespawn.tiredness.Tiredness;
import insane96mcp.survivalreimagined.module.sleeprespawn.tiredness.TirednessHandler;
import insane96mcp.survivalreimagined.setup.Strings;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Sleeping Effects", description = "Prevents the player from sleeping if has not enough Hunger and gives him effects on wake up. Effects on wake up are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.SLEEP_RESPAWN)
public class SleepingEffects extends SRFeature {

	public static final ArrayList<SRMobEffectInstance> EFFECTS_ON_WAKE_UP_DEFAULT = new ArrayList<>(List.of(
			new SRMobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 0),
			new SRMobEffectInstance(MobEffects.WEAKNESS, 300, 1),
			new SRMobEffectInstance(MobEffects.DIG_SLOWDOWN, 300, 1),
			new SRMobEffectInstance(MobEffects.REGENERATION, 900, 0)
	));
	public static final ArrayList<SRMobEffectInstance> effectsOnWakeUp = new ArrayList<>();

	@Config(min = 0, max = 20)
	@Label(name = "Hunger Depleted on Wake Up", description = "How much the hunger bar is depleted when you wake up in the morning. Saturation is depleted before depleting hunger bar. Setting to 0 will disable this feature.")
	public static Integer hungerDepletedOnWakeUp = 15;
	@Config
	@Label(name = "No Sleep If Hungry", description = "If the player's hunger bar is below 'Hunger Depleted on Wake Up' he can't sleep.")
	public static Boolean noSleepIfHungry = false;
	@Config
	@Label(name = "No beneficial effect when hungry", description = "If the player has no hunger on wake up, beneficial effects are not applied.")
	public static Boolean noBeneficialEffectWhenHungry = true;
	@Config
	@Label(name = "Dizzy when tired", description = "Apply the bad effects only when too tired")
	public static Boolean dizzyWhenToTired = true;

	public SleepingEffects(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("effects_on_wake_up.json", effectsOnWakeUp, EFFECTS_ON_WAKE_UP_DEFAULT, SRMobEffectInstance.LIST_TYPE));
	}

	@Override
	public void loadJsonConfigs() {
		super.loadJsonConfigs();
	}

	@SubscribeEvent
	public void wakeUpHungerAndEffects(SleepFinishedTimeEvent event) {
		if (!this.isEnabled()
				|| (hungerDepletedOnWakeUp == 0 && effectsOnWakeUp.isEmpty()))
			return;

		event.getLevel().players().stream().filter(LivingEntity::isSleeping).toList().forEach(player -> {
			float tirednessOnWakeUp = TirednessHandler.getOnWakeUp(player);

			FoodData foodData = player.getFoodData();
			int hungerToDeplete = hungerDepletedOnWakeUp;
			if (foodData.getSaturationLevel() > 0) {
				float saturation = foodData.saturationLevel;
				float saturationToDeplete = Math.min(hungerToDeplete, saturation);
				foodData.setSaturation(saturation - saturationToDeplete);
				hungerToDeplete -= saturationToDeplete;
			}
			if (hungerToDeplete > 0)
				foodData.setFoodLevel(foodData.foodLevel - Math.min(hungerToDeplete, foodData.foodLevel));

			for (MobEffectInstance mobEffectInstance : effectsOnWakeUp) {
				if (noBeneficialEffectWhenHungry && mobEffectInstance.getEffect().isBeneficial() && player.getFoodData().getFoodLevel() <= 0)
					continue;
				if (dizzyWhenToTired && Feature.isEnabled(Tiredness.class) && tirednessOnWakeUp == 0f && !mobEffectInstance.getEffect().isBeneficial())
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
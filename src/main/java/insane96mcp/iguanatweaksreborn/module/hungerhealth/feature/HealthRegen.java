package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.ITMobEffects;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.text.DecimalFormat;

@Label(name = "Health Regen", description = "Makes Health regen work differently, like in Combat Test snapshots. Can be customized. Also adds Well Fed and Injured effects.")
@LoadFeature(module = Modules.Ids.HUNGER_HEALTH)
public class HealthRegen extends Feature {
	//TODO remove the Enum and set a true false value. When true config values will be replaced with the preset ones and this set back to false
	@Config
	@Label(name = "Health Regen Preset", description = """
						Sets the other config options to some default values (actual config is not changed, but custom values are ignored):
						NONE: Use custom values
						COMBAT_TEST: health regeneration works like the Combat Tests Snapshots""")
	public static HealthRegenPreset healthRegenPreset = HealthRegenPreset.COMBAT_TEST;
	@Config(min = 0)
	@Label(name = "Health Regen Speed", description = "Sets how many ticks between the health regeneration happens (vanilla is 80; Combat Test is 40).")
	public static Integer healthRegenSpeed = 80;
	@Config(min = 0)
	@Label(name = "Regen when Hunger Above", description = "Sets how much hunger the player must have to regen health (vanilla is >17; Combat Test is >3).")
	public static Integer regenWhenFoodAbove = 17;
	@Config(min = 0)
	@Label(name = "Starve Speed", description = "Sets how many ticks between starve damage happens (vanilla and Combat Test is 80).")
	public static Integer starveSpeed = 80;
	@Config(min = 0)
	@Label(name = "Starve Damage", description = "Set how much damage is dealt when starving (vanilla and Combat Test are 1).")
	public static Integer starveDamage = 1;
	@Config
	@Label(name = "Disable Saturation Regen Boost", description = "Set to true to disable the health regen boost given when max hunger and saturation (false in Vanilla; true for Combat Test).")
	public static Boolean disableSaturationRegenBoost = false;
	@Config
	@Label(name = "Consume Hunger Only", description = "Set to true to consume Hunger only (and not saturation) when regenerating health (false for Vanilla; true for Combat Test).")
	public static Boolean consumeHungerOnly = false;
	@Config(min = 0d, max = 40d)
	@Label(name = "Max Exhaustion", description = "Vanilla consumes 1 saturation or hunger whenever Exhaustion reaches 4.0. You can change that value with this config option. NOTE that Minecraft caps this value to 40.")
	public static Double maxExhaustion = 4.0d;

	@Config(min = 0d, max = 1d)
	@Label(name = "Hunger Consumption Chance", description = "If 'Consume Hunger Only' is true then this is the chance to consume an hunger whenever the player is healed (vanilla ignores this; Combat Test has this set to 0.5).")
	public static Double hungerConsumptionChance = 0d;
	@Config(min = 0d, max = 1f)
	@Label(name = "Food Heal Multiplier", description = "When eating you'll get healed by this percentage of (hunger + saturation) restored.")
	public static Double foodHealMultiplier = 0d;
	//Effects
	//TODO Change well fed? Make it activate when eating a lot from low hunger
	@Config
	@Label(name = "Effects.Enable Well Fed", description = "Set to true to enable Well Fed, a new effect that speeds up health regen and is applied whenever the player eats.")
	public static Boolean enableWellFed = true;
	@Config(min = 0d, max = 128d)
	@Label(name = "Effects.Well Fed Duration Multiplier", description = "Multiplies the base duration of Well Fed by this value. Base duration is 1 second per food effectiveness (hunger + saturation).")
	public static Double wellFedDurationMultiplier = 1.0d;
	@Config(min = 0d, max = 10d)
	@Label(name = "Effects.Well Fed Effectiveness", description = "How much does health regen Well Fed increases per level. (This is inversely proportional, a value of 0.25 makes makes time to regen lower by 20%. A value of 1.0 makes time to regen lower by 50%.")
	public static Double wellFedEffectiveness = 0.25d;
	@Config
	@Label(name = "Effects.Enable Injured", description = "Set to true to enable Injured, a new effect that slows down health regen and is applied whenever the player is damaged. The effect slows down health regen by 20% per level.")
	public static Boolean enableInjured = true;
	@Config(min = 0d, max = 128d)
	@Label(name = "Effects.Well Fed Duration Multiplier", description = "Multiplies the base duration of Well Fed by this value. Base duration is 1 second per food effectiveness (hunger + saturation).")
	public static Double injuredDurationMultiplier = 1.0d;
	@Config(min = 0d, max = 10d)
	@Label(name = "Effects.Injured Effectiveness", description = "How much does health regen Injured decreases per level.")
	public static Double injuredEffectiveness = 0.2d;

	public HealthRegen(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		if (healthRegenPreset == HealthRegenPreset.COMBAT_TEST) {
			healthRegenSpeed = 40;
			regenWhenFoodAbove = 6;
			starveSpeed = 80;
			starveDamage = 1;
			disableSaturationRegenBoost = true;
			consumeHungerOnly = true;
			maxExhaustion = 4d;
			hungerConsumptionChance = 0.5d;
		}
	}

	@SubscribeEvent
	public void onPlayerDamaged(LivingDamageEvent event) {
		if (!this.isEnabled()
				|| !enableInjured
				|| !(event.getEntity() instanceof Player playerEntity)
				|| event.getSource().equals(DamageSource.STARVE) || event.getSource().equals(DamageSource.DROWN) || event.getSource().equals(DamageSource.FREEZE))
			return;
		int duration = (int) ((event.getAmount() * 20) * injuredDurationMultiplier);
		if (duration == 0)
			return;
		if (playerEntity.hasEffect(ITMobEffects.INJURED.get()))
			//noinspection ConstantConditions
			duration += playerEntity.getEffect(ITMobEffects.INJURED.get()).getDuration();
		playerEntity.addEffect(MCUtils.createEffectInstance(ITMobEffects.INJURED.get(), duration, 0, true, false, true, false));
	}

	@SubscribeEvent
	public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
		if (!this.isEnabled())
			return;
		if (!event.getItem().isEdible())
			return;
		if (!(event.getEntity() instanceof Player))
			return;

		processWellFed(event);
		healOnEat(event);
	}

	private void processWellFed(LivingEntityUseItemEvent.Finish event) {
		if (!enableWellFed)
			return;
		Player playerEntity = (Player) event.getEntity();
		FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), playerEntity);
		int duration = (int) (((food.getNutrition() + food.getNutrition() * food.getSaturationModifier() * 2) * 20) * wellFedDurationMultiplier);
		if (duration == 0)
			return;
		int amplifier = 0;
		playerEntity.addEffect(MCUtils.createEffectInstance(ITMobEffects.WELL_FED.get(), duration, amplifier, true, false, true, false));
	}

	public void healOnEat(LivingEntityUseItemEvent.Finish event) {
		if (foodHealMultiplier == 0d)
			return;
		FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), event.getEntity());
		double heal = (food.getNutrition() + food.getNutrition() * food.getSaturationModifier() * 2) * foodHealMultiplier;
		event.getEntity().heal((float) heal);
	}

	/**
	 * Returns true if overrides the vanilla tick, otherwise false
	 */
	public static boolean tickFoodStats(FoodData foodStats, Player player) {
		if (!isEnabled(HealthRegen.class))
			return false;
		Difficulty difficulty = player.level.getDifficulty();
		foodStats.lastFoodLevel = foodStats.getFoodLevel();
		if (foodStats.exhaustionLevel > maxExhaustion) {
			foodStats.exhaustionLevel -= maxExhaustion;
			if (foodStats.saturationLevel > 0.0F) {
				foodStats.saturationLevel = Math.max(foodStats.saturationLevel - 1.0F, 0.0F);
			}
			else if (difficulty != Difficulty.PEACEFUL) {
				foodStats.foodLevel = Math.max(foodStats.foodLevel - 1, 0);
			}
		}
		tick(foodStats, player, difficulty);

		return true;
	}

	private static void tick(FoodData foodStats, Player player, Difficulty difficulty) {
		boolean naturalRegen = player.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
		if (naturalRegen && foodStats.saturationLevel > 0.0F && player.isHurt() && foodStats.foodLevel >= 20 && !disableSaturationRegenBoost) {
			++foodStats.tickTimer;
			if (foodStats.tickTimer >= 10) {
				float f = Math.min(foodStats.saturationLevel, 6.0F);
				player.heal(f / 6.0F);
				foodStats.addExhaustion(f);
				foodStats.tickTimer = 0;
			}
		}
		else if (naturalRegen && foodStats.foodLevel > regenWhenFoodAbove && player.isHurt()) {
			++foodStats.tickTimer;
			if (foodStats.tickTimer >= getRegenSpeed(player)) {
				player.heal(1.0F);
				if (consumeHungerOnly) {
					if (player.level.getRandom().nextDouble() < 0.5d)
						addHunger(foodStats, -1);
				}
				else
					foodStats.addExhaustion(6.0F);
				foodStats.tickTimer = 0;
			}
		}
		else if (foodStats.foodLevel <= 0) {
			++foodStats.tickTimer;
			if (foodStats.tickTimer >= starveSpeed) {
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.hurt(DamageSource.STARVE, starveDamage);
				}
				foodStats.tickTimer = 0;
			}
		}
		else {
			foodStats.tickTimer = 0;
		}
	}

	public static void addHunger(FoodData foodStats, int hunger) {
		foodStats.foodLevel = Mth.clamp(foodStats.foodLevel + hunger, 0, 20);
	}

	private static int getRegenSpeed(Player player) {
		int ticksToRegen = healthRegenSpeed;
		MobEffectInstance injured = player.getEffect(ITMobEffects.INJURED.get());
		if (injured != null)
			ticksToRegen *= 1 + ((injured.getAmplifier() + 1) * injuredEffectiveness);
		MobEffectInstance wellFed = player.getEffect(ITMobEffects.WELL_FED.get());
		if (wellFed != null)
			ticksToRegen *= 1 / (((wellFed.getAmplifier() + 1) * wellFedEffectiveness) + 1);
		return ticksToRegen;
	}

	private enum HealthRegenPreset {
		NONE,
		COMBAT_TEST
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void debugScreen(CustomizeGuiOverlayEvent.DebugText event) {
		if (!this.isEnabled())
			return;
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer playerEntity = mc.player;
		if (playerEntity == null)
			return;
		if (mc.options.renderDebug) {
			FoodData foodStats = playerEntity.getFoodData();
			event.getLeft().add(String.format("Hunger: %d, Saturation: %s, Exhaustion: %s", foodStats.foodLevel, new DecimalFormat("#.#").format(foodStats.saturationLevel), new DecimalFormat("0.00").format(foodStats.exhaustionLevel)));
		}
	}
}
package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.iguanatweaksreborn.setup.ITMobEffects;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
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
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.text.DecimalFormat;

@Label(name = "Health Regen", description = "Makes Health regen work differently, like in Combat Test snapshots. Can be customized. Also adds Well Fed and Injured effects.")
public class HealthRegen extends Feature {

	private final ForgeConfigSpec.ConfigValue<HealthRegenPreset> healthRegenPresetConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> healthRegenSpeedConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> regenWhenFoodAboveConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> starveSpeedConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> starveDamageConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> disableSaturationRegenBoostConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> consumeHungerOnlyConfig;
	private final ForgeConfigSpec.ConfigValue<Double> maxExhaustionConfig;
	private final ForgeConfigSpec.ConfigValue<Double> hungerConsumptionChanceConfig;
	private final ForgeConfigSpec.ConfigValue<Double> foodHealMultiplierConfig;
	//Effects
	private final ForgeConfigSpec.BooleanValue enableWellFedConfig;
	private final ForgeConfigSpec.DoubleValue wellFedDurationMultiplierConfig;
	private final ForgeConfigSpec.DoubleValue wellFedEffectivenessConfig;
	private final ForgeConfigSpec.BooleanValue enableInjuredConfig;
	private final ForgeConfigSpec.DoubleValue injuredDurationMultiplierConfig;
	private final ForgeConfigSpec.DoubleValue injuredEffectivenessConfig;

	public HealthRegenPreset healthRegenPreset = HealthRegenPreset.COMBAT_TEST;
	public int healthRegenSpeed = 80;
	public int regenWhenFoodAbove = 17;
	public int starveSpeed = 80;
	public int starveDamage = 1;
	public boolean disableSaturationRegenBoost = false;
	public boolean consumeHungerOnly = false;
	public double maxExhaustion = 4.0;
	public double hungerConsumptionChance = 0;
	public double foodHealMultiplier = 0d;
	//Effects
	public boolean enableWellFed = true;
	public double wellFedDurationMultiplier = 1.0d;
	public double wellFedEffectiveness = 0.25d;
	public boolean enableInjured = true;
	public double injuredDurationMultiplier = 1.0d;
	public double injuredEffectiveness = 0.2d;

	public HealthRegen(Module module) {
		super(ITCommonConfig.builder, module);
		this.pushConfig(ITCommonConfig.builder);
		healthRegenPresetConfig = ITCommonConfig.builder
				.comment("""
						Sets the other config options to some default values (actual config is not changed, but custom values are ignored):
						NONE: Use custom values
						COMBAT_TEST: health regeneration works like the Combat Tests Snapshots""")
				.defineEnum("Health Regen Preset", this.healthRegenPreset);
		healthRegenSpeedConfig = ITCommonConfig.builder
				.comment("Sets how many ticks between the health regeneration happens (vanilla is 80; Combat Test is 40).")
				.defineInRange("Health Regen Speed", this.healthRegenSpeed, 0, Integer.MAX_VALUE);
		regenWhenFoodAboveConfig = ITCommonConfig.builder
				.comment("Sets how much hunger the player must have to regen health (vanilla is >17; Combat Test is >3).")
				.defineInRange("Regen when Hunger Above", this.regenWhenFoodAbove, 0, Integer.MAX_VALUE);
		starveSpeedConfig = ITCommonConfig.builder
				.comment("Sets how many ticks between starve damage happens (vanilla and Combat Test is 80).")
				.defineInRange("Starve Speed", this.starveSpeed, 0, Integer.MAX_VALUE);
		starveDamageConfig = ITCommonConfig.builder
				.comment("Set how much damage is dealt when starving (vanilla and Combat Test are 1).")
				.defineInRange("Starve Damage", this.starveDamage, 0, Integer.MAX_VALUE);
		consumeHungerOnlyConfig = ITCommonConfig.builder
				.comment("Set to true to consume Hunger only (and not saturation) when regenerating health (false for Vanilla; true for Combat Test).")
				.define("Consume Hunger Only", this.consumeHungerOnly);
		maxExhaustionConfig = ITCommonConfig.builder
				.comment("Vanilla consumes 1 saturation or hunger whenever Exhaustion reaches 4.0. You can change that value with this config option. NOTE that Minecraft caps this value to 40")
				.defineInRange("Max Exhaustion", this.maxExhaustion, 0d, 40d);
		disableSaturationRegenBoostConfig = ITCommonConfig.builder
				.comment("Set to true to disable the health regen boost given when max hunger and saturation (false in Vanilla; true for Combat Test).")
				.define("Disable Saturation Regen Boost", this.disableSaturationRegenBoost);
		hungerConsumptionChanceConfig = ITCommonConfig.builder
				.comment("If 'Consume Hunger Only' is true then this is the chance to consume an hunger whenever the player is healed (vanilla ignores this; Combat Test has this set to 0.5).")
				.defineInRange("Hunger Consumption Chance", this.hungerConsumptionChance, 0d, 1d);
		foodHealMultiplierConfig = ITCommonConfig.builder
				.comment("When eating you'll get healed by this percentage of (hunger + saturation) restored.")
				.defineInRange("Food Heal Multiplier", this.foodHealMultiplier, 0.0d, 128d);

		ITCommonConfig.builder.push("Effects");
		this.enableWellFedConfig = ITCommonConfig.builder
				.comment("Set to true to enable Well Fed, a new effect that speeds up health regen and is applied whenever the player eats.")
				.define("Enable Well Fed", this.enableWellFed);
		this.wellFedDurationMultiplierConfig = ITCommonConfig.builder
				.comment("Multiplies the base duration of Well Fed by this value. Base duration is 1 second per food effectiveness (hunger + saturation).")
				.defineInRange("Well Fed Duration Multiplier", this.wellFedDurationMultiplier, 0.0d, 128d);
		this.wellFedEffectivenessConfig = ITCommonConfig.builder
				.comment("How much does health regen Well Fed increases per level. (This is inversely proportional, a value of 0.25 makes makes time to regen lower by 20%. A value of 1.0 makes time to regen lower by 50%.")
				.defineInRange("Well Fed Effectiveness", this.wellFedEffectiveness, 0.0d, 10.0d);

		this.enableInjuredConfig = ITCommonConfig.builder
				.comment("Set to true to enable Injured, a new effect that slows down health regen and is applied whenever the player is damaged. The effect slows down health regen by 20% per level.")
				.define("Enable Injured", this.enableInjured);
		this.injuredDurationMultiplierConfig = ITCommonConfig.builder
				.comment("Multiplies the base duration of Injured by this value. Base duration is 1 second per point of damage taken.")
				.defineInRange("Injured Duration Multiplier", this.injuredDurationMultiplier, 0.0d, 128d);
		this.injuredEffectivenessConfig = ITCommonConfig.builder
				.comment("How much does health regen Injured decreases per level.")
				.defineInRange("Injured Effectiveness", this.injuredEffectiveness, 0.0d, 10.0d);
		ITCommonConfig.builder.pop();

		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.healthRegenPreset = this.healthRegenPresetConfig.get();
		switch (this.healthRegenPreset) {
			case NONE -> {
				this.healthRegenSpeed = this.healthRegenSpeedConfig.get();
				this.regenWhenFoodAbove = this.regenWhenFoodAboveConfig.get();
				this.starveSpeed = this.starveSpeedConfig.get();
				this.starveDamage = this.starveDamageConfig.get();
				this.disableSaturationRegenBoost = this.disableSaturationRegenBoostConfig.get();
				this.consumeHungerOnly = this.consumeHungerOnlyConfig.get();
				this.maxExhaustion = this.maxExhaustionConfig.get();
				this.hungerConsumptionChance = this.hungerConsumptionChanceConfig.get();
			}
			case COMBAT_TEST -> {
				this.healthRegenSpeed = 40;
				this.regenWhenFoodAbove = 6;
				this.starveSpeed = 80;
				this.starveDamage = 1;
				this.disableSaturationRegenBoost = true;
				this.consumeHungerOnly = true;
				this.maxExhaustion = 4d;
				this.hungerConsumptionChance = 0.5d;
			}
		}

		this.foodHealMultiplier = this.foodHealMultiplierConfig.get();
		//Effects
		this.enableWellFed = this.enableWellFedConfig.get();
		this.wellFedDurationMultiplier = this.wellFedDurationMultiplierConfig.get();
		this.wellFedEffectiveness = this.wellFedEffectivenessConfig.get();
		this.enableInjured = this.enableInjuredConfig.get();
		this.injuredDurationMultiplier = this.injuredDurationMultiplierConfig.get();
		this.injuredEffectiveness = this.injuredEffectivenessConfig.get();
	}

	@SubscribeEvent
	public void onPlayerDamaged(LivingDamageEvent event) {
		if (!this.isEnabled()
				|| !this.enableInjured
				|| !(event.getEntityLiving() instanceof Player playerEntity)
				|| event.getSource().equals(DamageSource.STARVE) || event.getSource().equals(DamageSource.DROWN) || event.getSource().equals(DamageSource.FREEZE))
			return;
		int duration = (int) ((event.getAmount() * 20) * this.injuredDurationMultiplier);
		if (duration == 0)
			return;
		if (playerEntity.hasEffect(ITMobEffects.INJURED.get()))
			duration += playerEntity.getEffect(ITMobEffects.INJURED.get()).getDuration();
		playerEntity.addEffect(MCUtils.createEffectInstance(ITMobEffects.INJURED.get(), duration, 0, true, false, true, false));
	}

	@SubscribeEvent
	public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
		if (!this.isEnabled())
			return;
		if (!event.getItem().isEdible())
			return;
		if (!(event.getEntityLiving() instanceof Player))
			return;

		processWellFed(event);
		healOnEat(event);
	}

	private void processWellFed(LivingEntityUseItemEvent.Finish event) {
		if (!this.enableWellFed)
			return;
		Player playerEntity = (Player) event.getEntityLiving();
		FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), playerEntity);
		int duration = (int) (((food.getNutrition() + food.getNutrition() * food.getSaturationModifier() * 2) * 20) * this.wellFedDurationMultiplier);
		if (duration == 0)
			return;
		int amplifier = 0;
		playerEntity.addEffect(MCUtils.createEffectInstance(ITMobEffects.WELL_FED.get(), duration, amplifier, true, false, true, false));
	}

	public void healOnEat(LivingEntityUseItemEvent.Finish event) {
		if (this.foodHealMultiplier == 0d)
			return;
		FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), event.getEntityLiving());
		double heal = (food.getNutrition() + food.getNutrition() * food.getSaturationModifier() * 2) * this.foodHealMultiplier;
		event.getEntityLiving().heal((float) heal);
	}

	/**
	 * Returns true if overrides the vanilla tick, otherwise false
	 */
	public boolean tickFoodStats(FoodData foodStats, Player player) {
		if (!this.isEnabled())
			return false;
		Difficulty difficulty = player.level.getDifficulty();
		foodStats.lastFoodLevel = foodStats.getFoodLevel();
		if (foodStats.exhaustionLevel > this.maxExhaustion) {
			foodStats.exhaustionLevel -= this.maxExhaustion;
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

	private void tick(FoodData foodStats, Player player, Difficulty difficulty) {
		boolean naturalRegen = player.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
		if (naturalRegen && foodStats.saturationLevel > 0.0F && player.isHurt() && foodStats.foodLevel >= 20 && !this.disableSaturationRegenBoost) {
			++foodStats.tickTimer;
			if (foodStats.tickTimer >= 10) {
				float f = Math.min(foodStats.saturationLevel, 6.0F);
				player.heal(f / 6.0F);
				foodStats.addExhaustion(f);
				foodStats.tickTimer = 0;
			}
		}
		else if (naturalRegen && foodStats.foodLevel > this.regenWhenFoodAbove && player.isHurt()) {
			++foodStats.tickTimer;
			if (foodStats.tickTimer >= getRegenSpeed(player)) {
				player.heal(1.0F);
				if (this.consumeHungerOnly) {
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
			if (foodStats.tickTimer >= this.starveSpeed) {
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.hurt(DamageSource.STARVE, this.starveDamage);
				}
				foodStats.tickTimer = 0;
			}
		}
		else {
			foodStats.tickTimer = 0;
		}
	}

	public void addHunger(FoodData foodStats, int hunger) {
		foodStats.foodLevel = Mth.clamp(foodStats.foodLevel + hunger, 0, 20);
	}

	private int getRegenSpeed(Player player) {
		int ticksToRegen = this.healthRegenSpeed;
		MobEffectInstance injured = player.getEffect(ITMobEffects.INJURED.get());
		if (injured != null)
			ticksToRegen *= 1 + ((injured.getAmplifier() + 1) * this.injuredEffectiveness);
		MobEffectInstance wellFed = player.getEffect(ITMobEffects.WELL_FED.get());
		if (wellFed != null)
			ticksToRegen *= 1 / (((wellFed.getAmplifier() + 1) * this.wellFedEffectiveness) + 1);
		return ticksToRegen;
	}

	private enum HealthRegenPreset {
		NONE,
		COMBAT_TEST
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void debugScreen(RenderGameOverlayEvent.Text event) {
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
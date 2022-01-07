package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.ITEffects;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
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

@Label(name = "Health Regen", description = "Makes Health regen work differently, like in Combat Test snapshots or similar to Hunger Overhaul")
public class HealthRegen extends Feature {

	private final ForgeConfigSpec.ConfigValue<HealthRegenPreset> healthRegenPresetConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> healthRegenSpeedConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> regenWhenFoodAboveConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> starveSpeedConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> starveDamageConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> disableSaturationRegenBoostConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> consumeHungerOnlyConfig;
	private final ForgeConfigSpec.ConfigValue<Double> hungerConsumptionChanceConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> enableWellFedConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> enableInjuredConfig;
	private final ForgeConfigSpec.ConfigValue<Double> foodHealMultiplierConfig;

	public HealthRegenPreset healthRegenPreset = HealthRegenPreset.IGUANA_TWEAKS;
	public int healthRegenSpeed = 80;
	public int regenWhenFoodAbove = 17;
	public int starveSpeed = 80;
	public int starveDamage = 1;
	public boolean disableSaturationRegenBoost = false;
	public boolean consumeHungerOnly = false;
	public double hungerConsumptionChance = 0;
	public boolean enableWellFed = false;
	public boolean enableInjured = false;
	public double foodHealMultiplier = 0d;

	public HealthRegen(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		healthRegenPresetConfig = Config.builder
				.comment("Sets the other config options to some default values (actual config is not changed, but custom values are ignored):\n" +
						"NONE: Use custom values\n" +
						"COMBAT_TEST: health regeneration works like the Combat Tests Shapshots," +
						"IGUANA_TWEAKS: health regen is slow (1 hp every 10 secs) and also the player can have Bleeding and Well Fed effects that slow down / speed up the health regen.")
				.defineEnum("Health Regen Preset", this.healthRegenPreset);
		healthRegenSpeedConfig = Config.builder
				.comment("Sets how many ticks between the health regeneration happens (vanilla is 80; Combat Test Snapshot is 40; Iguana Tweaks preset is 200).")
				.defineInRange("Health Regen Speed", this.healthRegenSpeed, 0, Integer.MAX_VALUE);
		regenWhenFoodAboveConfig = Config.builder
				.comment("Sets how much hunger the player must have to regen health (vanilla is >17; Combat Test Snapshot; Iguana Tweaks preset is >3).")
				.defineInRange("Regen when Hunger Above", this.regenWhenFoodAbove, 0, Integer.MAX_VALUE);
		starveSpeedConfig = Config.builder
				.comment("Sets how many ticks between starve damage happens (vanilla and Combat Test Snapshot is 80; Iguana Tweaks preset is 40, but you start talking damage when hunger <= 3 and less hunger = faster starve, also in hard is faster and in easy is slower).")
				.defineInRange("Starve Speed", this.starveSpeed, 0, Integer.MAX_VALUE);
		starveDamageConfig = Config.builder
				.comment("Sets how many ticks between the health regeneration happens (vanilla, Combat Test and IguanaTweaks preset are all 1).")
				.defineInRange("Starve Damage", this.starveDamage, 0, Integer.MAX_VALUE);
		consumeHungerOnlyConfig = Config.builder
				.comment("Set to true to consume Hunger only (and not saturation) when regenerating health (false for Vanilla and Iguana Tweaks presets; true for Combat Test).")
				.define("Consume Hunger Only", this.consumeHungerOnly);
		disableSaturationRegenBoostConfig = Config.builder
				.comment("Set to true to disable the health regen boost given when max hunger and saturation (false for Vanilla; true for Combat Test and IguanaTweaks Presets).")
				.define("Disable Saturation Regen Boost", this.disableSaturationRegenBoost);
		hungerConsumptionChanceConfig = Config.builder
				.comment("If 'Consume Hunger Only' is true then this is the chance to consume an hunger whenever the player is healed (vanilla and Iguanatweaks ignore this; Combat Test has this set to 0.5).")
				.defineInRange("Hunger Consumption Chance", this.hungerConsumptionChance, 0d, 1d);
		enableWellFedConfig = Config.builder
				.comment("Set to true to enable Well Fed, a new effect that speeds up health regen and is applied whenever the player eats (disabled for Vanilla and Combat Test; enabled for Iguana Tweaks preset).")
				.define("Enable Well Fed", this.enableWellFed);
		enableInjuredConfig = Config.builder
				.comment("Set to true to enable Injured, a new effect that slows down health regen and is applied whenever the player is damaged (disabled for Vanilla and Combat Test; enabled for Iguana Tweaks preset).")
				.define("Enable Injured", this.enableInjured);
		foodHealMultiplierConfig = Config.builder
				.comment("When eating you'll get healed by this percentage of (hunger + saturation) restored.")
				.defineInRange("Food Heal Multiplier", this.foodHealMultiplier, 0.0d, 128d);
		Config.builder.pop();
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
				this.hungerConsumptionChance = this.hungerConsumptionChanceConfig.get();
				this.enableWellFed = this.enableWellFedConfig.get();
				this.enableInjured = this.enableInjuredConfig.get();
				this.foodHealMultiplier = this.foodHealMultiplierConfig.get();
			}
			case COMBAT_TEST -> {
				this.healthRegenSpeed = 40;
				this.regenWhenFoodAbove = 6;
				this.starveSpeed = 80;
				this.starveDamage = 1;
				this.disableSaturationRegenBoost = true;
				this.consumeHungerOnly = true;
				this.hungerConsumptionChance = 0.5d;
				this.enableWellFed = false;
				this.enableInjured = false;
				this.foodHealMultiplier = 0d;
			}
			case IGUANA_TWEAKS -> {
				this.healthRegenSpeed = 200;
				this.regenWhenFoodAbove = 4;
				this.starveSpeed = 600;
				this.starveDamage = 1;
				this.disableSaturationRegenBoost = true;
				this.consumeHungerOnly = false;
				this.hungerConsumptionChance = 0d;
				this.enableWellFed = true;
				this.enableInjured = true;
				this.foodHealMultiplier = 0.1d;
			}
		}
	}

	@SubscribeEvent
	public void onPlayerDamaged(LivingDamageEvent event) {
		if (!this.isEnabled())
			return;
		if (!this.enableInjured)
			return;
		if (!(event.getEntityLiving() instanceof Player playerEntity))
			return;
		if (event.getSource().equals(DamageSource.STARVE) || event.getSource().equals(DamageSource.DROWN))
			return;
		int duration = (int) (event.getAmount() * 4 * 20);
		if (playerEntity.hasEffect(ITEffects.INJURED.get()))
			duration += playerEntity.getEffect(ITEffects.INJURED.get()).getDuration();
		playerEntity.addEffect(MCUtils.createEffectInstance(ITEffects.INJURED.get(), duration, 0, true, false, true, false));
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
		FoodProperties food = event.getItem().getItem().getFoodProperties();
		int duration = (int) ((food.getNutrition() * food.getSaturationModifier() * 2) * 5 * 20);
		if (playerEntity.hasEffect(ITEffects.WELL_FED.get()))
			duration += playerEntity.getEffect(ITEffects.WELL_FED.get()).getDuration();
		int amplifier = Math.max(food.getNutrition() / 2 - 1, 0);
		playerEntity.addEffect(MCUtils.createEffectInstance(ITEffects.WELL_FED.get(), duration, amplifier, true, false, true, false));
	}

	public void healOnEat(LivingEntityUseItemEvent.Finish event) {
		if (this.foodHealMultiplier == 0d)
			return;
		FoodProperties food = event.getItem().getItem().getFoodProperties();
		double heal = (food.getNutrition() + (food.getNutrition() * food.getSaturationModifier() * 2)) * this.foodHealMultiplier;
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
		if (foodStats.exhaustionLevel > 4.0F) {
			foodStats.exhaustionLevel -= 4.0F;
			if (foodStats.saturationLevel > 0.0F) {
				foodStats.saturationLevel = Math.max(foodStats.saturationLevel - 1.0F, 0.0F);
			}
			else if (difficulty != Difficulty.PEACEFUL) {
				foodStats.foodLevel = Math.max(foodStats.foodLevel - 1, 0);
			}
		}
		if (healthRegenPreset.equals(HealthRegenPreset.IGUANA_TWEAKS))
			tickIguanaTweaks(foodStats, player, difficulty);
		else
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
		if (naturalRegen && foodStats.foodLevel > this.regenWhenFoodAbove /*>= 18*/ && player.isHurt()) {
			++foodStats.tickTimer;
			if (foodStats.tickTimer >= getRegenSpeed(player) /*80*/) {
				player.heal(1.0F);
				if (this.consumeHungerOnly)
					if (player.level.getRandom().nextDouble() < 0.5d)
						addHunger(foodStats, -1);
					else
						foodStats.addExhaustion(6.0F);
				foodStats.tickTimer = 0;
			}
		}
		else if (foodStats.foodLevel <= 0) {
			++foodStats.tickTimer;
			if (foodStats.tickTimer >= getStarveSpeed(player, difficulty)) {
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

	private void tickIguanaTweaks(FoodData foodStats, Player player, Difficulty difficulty) {
		boolean naturalRegen = player.level.getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION);
		if (naturalRegen && foodStats.foodLevel > this.regenWhenFoodAbove && player.isHurt()) {
			++foodStats.tickTimer;
			foodStats.addExhaustion(0.03F);
			if (foodStats.tickTimer >= getRegenSpeed(player)) {
				player.heal(1.0F);
				foodStats.tickTimer = 0;
			}
		}
		else if (foodStats.foodLevel <= 4) {
			++foodStats.tickTimer;
			if (foodStats.tickTimer >= getStarveSpeed(player, difficulty)) {
				player.hurt(DamageSource.STARVE, this.starveDamage);
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
		int speed = this.healthRegenSpeed;
		MobEffectInstance injured = player.getEffect(ITEffects.INJURED.get());
		if (injured != null)
			speed *= 1 + ((injured.getAmplifier() + 1) * 0.2d);
		MobEffectInstance wellFed = player.getEffect(ITEffects.WELL_FED.get());
		if (wellFed != null)
			speed *= 1 - (Math.log10(0.6d + (wellFed.getAmplifier() + 1) * 0.8d));
		return speed;
	}

	private int getStarveSpeed(Player player, Difficulty difficulty) {
		if (this.healthRegenPreset != HealthRegenPreset.IGUANA_TWEAKS)
			return this.starveSpeed;
		else {
			int speed = this.starveSpeed;
			if (difficulty == Difficulty.EASY || difficulty == Difficulty.PEACEFUL)
				speed *= 2;
			else if (difficulty == Difficulty.HARD)
				speed *= 0.75d;
			int playerHunger = player.getFoodData().foodLevel;
			speed *= (playerHunger + 1) / 5d;
			return speed;
		}
	}

	private enum HealthRegenPreset {
		NONE,
		COMBAT_TEST,
		IGUANA_TWEAKS
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
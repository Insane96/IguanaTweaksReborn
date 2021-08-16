package insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.ITEffects;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.text.DecimalFormat;

@Label(name = "Health Regen", description = "Makes Health regen work differently, like in Combat Test snapshots or similar to Hunger Overhaul")
public class HealthRegenFeature extends Feature {

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

	public HealthRegenFeature(Module module) {
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
			case NONE:
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
				break;
			case COMBAT_TEST:
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
				break;
			case IGUANA_TWEAKS:
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
				break;
		}
	}

	@SubscribeEvent
	public void onPlayerDamaged(LivingDamageEvent event) {
		if (!this.isEnabled())
			return;
		if (!this.enableInjured)
			return;
		if (!(event.getEntityLiving() instanceof PlayerEntity))
			return;
		PlayerEntity playerEntity = (PlayerEntity) event.getEntityLiving();
		if (event.getSource().damageType.equals("starve") || event.getSource().damageType.equals("drown"))
			return;
		int duration = (int) (event.getAmount() * 4 * 20);
		if (playerEntity.isPotionActive(ITEffects.INJURED.get()))
			duration += playerEntity.getActivePotionEffect(ITEffects.INJURED.get()).getDuration();
		playerEntity.addPotionEffect(MCUtils.createEffectInstance(ITEffects.INJURED.get(), duration, 0, true, false, true, false));
	}

	@SubscribeEvent
	public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
		if (!this.isEnabled())
			return;
		if (!event.getItem().isFood())
			return;
		if (!(event.getEntityLiving() instanceof PlayerEntity))
			return;

		processWellFed(event);
		healOnEat(event);
	}

	private void processWellFed(LivingEntityUseItemEvent.Finish event) {
		if (!this.enableWellFed)
			return;
		PlayerEntity playerEntity = (PlayerEntity) event.getEntityLiving();
		Food food = event.getItem().getItem().getFood();
		int duration = (int) ((food.getHealing() * food.getSaturation() * 2) * 5 * 20);
		if (playerEntity.isPotionActive(ITEffects.WELL_FED.get()))
			duration += playerEntity.getActivePotionEffect(ITEffects.WELL_FED.get()).getDuration();
		int amplifier = Math.max(food.value / 2 - 1, 0);
		playerEntity.addPotionEffect(MCUtils.createEffectInstance(ITEffects.WELL_FED.get(), duration, amplifier, true, false, true, false));
	}

	public void healOnEat(LivingEntityUseItemEvent.Finish event) {
		if (this.foodHealMultiplier == 0d)
			return;
		Food food = event.getItem().getItem().getFood();
		double heal = (food.getHealing() + (food.getHealing() * food.getSaturation() * 2)) * this.foodHealMultiplier;
		event.getEntityLiving().heal((float) heal);
	}

	/**
	 * Returns true if overrides the vanilla tick, otherwise false
	 */
	public boolean tickFoodStats(FoodStats foodStats, PlayerEntity player) {
		if (!this.isEnabled())
			return false;
		Difficulty difficulty = player.world.getDifficulty();
		foodStats.prevFoodLevel = foodStats.foodLevel;
		if (foodStats.foodExhaustionLevel > 4.0F) {
			foodStats.foodExhaustionLevel -= 4.0F;
			if (foodStats.foodSaturationLevel > 0.0F) {
				foodStats.foodSaturationLevel = Math.max(foodStats.foodSaturationLevel - 1.0F, 0.0F);
			}
			else if (difficulty != Difficulty.PEACEFUL) {
				foodStats.foodLevel = Math.max(foodStats.foodLevel - 1, 0);
			}
		}
		if (healthRegenPreset.equals(HealthRegenPreset.COMBAT_TEST))
			tick(foodStats, player, difficulty);
		else if (healthRegenPreset.equals(HealthRegenPreset.IGUANA_TWEAKS))
			tickIguanaTweaks(foodStats, player, difficulty);

		return true;
	}

	private void tick(FoodStats foodStats, PlayerEntity player, Difficulty difficulty) {
		boolean naturalRegen = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
		if (naturalRegen && foodStats.foodSaturationLevel > 0.0F && player.shouldHeal() && foodStats.foodLevel >= 20 && !this.disableSaturationRegenBoost) {
			++foodStats.foodTimer;
			if (foodStats.foodTimer >= 10) {
				float f = Math.min(foodStats.foodSaturationLevel, 6.0F);
				player.heal(f / 6.0F);
				foodStats.addExhaustion(f);
				foodStats.foodTimer = 0;
			}
		}
		if (naturalRegen && foodStats.foodLevel > this.regenWhenFoodAbove /*>= 18*/ && player.shouldHeal()) {
			++foodStats.foodTimer;
			if (foodStats.foodTimer >= getRegenSpeed(player) /*80*/) {
				player.heal(1.0F);
				if (this.consumeHungerOnly)
					if (player.world.rand.nextDouble() < 0.5d)
						addHunger(foodStats, -1);
					else
						foodStats.addExhaustion(6.0F);
				foodStats.foodTimer = 0;
			}
		}
		else if (foodStats.foodLevel <= 0) {
			++foodStats.foodTimer;
			if (foodStats.foodTimer >= getStarveSpeed(player, difficulty)) {
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.attackEntityFrom(DamageSource.STARVE, this.starveDamage);
				}
				foodStats.foodTimer = 0;
			}
		}
		else {
			foodStats.foodTimer = 0;
		}
	}

	private void tickIguanaTweaks(FoodStats foodStats, PlayerEntity player, Difficulty difficulty) {
		boolean naturalRegen = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
		if (naturalRegen && foodStats.foodLevel > this.regenWhenFoodAbove && player.shouldHeal()) {
			++foodStats.foodTimer;
			foodStats.addExhaustion(0.03F);
			if (foodStats.foodTimer >= getRegenSpeed(player)) {
				player.heal(1.0F);
				foodStats.foodTimer = 0;
			}
		}
		else if (foodStats.foodLevel <= 4) {
			++foodStats.foodTimer;
			player.sendStatusMessage(new StringTextComponent("starveSpeed: " + getStarveSpeed(player, difficulty)), true);
			if (foodStats.foodTimer >= getStarveSpeed(player, difficulty)) {
				player.attackEntityFrom(DamageSource.STARVE, this.starveDamage);
				foodStats.foodTimer = 0;
			}
		}
		else {
			foodStats.foodTimer = 0;
		}
	}

	public void addHunger(FoodStats foodStats, int hunger) {
		foodStats.foodLevel = MathHelper.clamp(foodStats.foodLevel + hunger, 0, 20);
	}

	private int getRegenSpeed(PlayerEntity player) {
		int speed = this.healthRegenSpeed;
		EffectInstance injured = player.getActivePotionEffect(ITEffects.INJURED.get());
		if (injured != null)
			speed *= 1 + ((injured.getAmplifier() + 1) * 0.2d);
		EffectInstance wellFed = player.getActivePotionEffect(ITEffects.WELL_FED.get());
		if (wellFed != null)
			speed *= 1 - (Math.log10(0.6d + (wellFed.getAmplifier() + 1) * 0.8d));
		return speed;
	}

	private int getStarveSpeed(PlayerEntity player, Difficulty difficulty) {
		if (this.healthRegenPreset != HealthRegenPreset.IGUANA_TWEAKS)
			return this.starveSpeed;
		else {
			int speed = this.starveSpeed;
			if (difficulty == Difficulty.EASY || difficulty == Difficulty.PEACEFUL)
				speed *= 2;
			else if (difficulty == Difficulty.HARD)
				speed *= 0.75d;
			int playerHunger = player.getFoodStats().foodLevel;
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
		ClientPlayerEntity playerEntity = mc.player;
		if (playerEntity == null)
			return;
		if (mc.gameSettings.showDebugInfo) {
			FoodStats foodStats = playerEntity.getFoodStats();
			event.getLeft().add(String.format("Hunger: %d, Saturation: %s, Exhaustion: %s", foodStats.foodLevel, new DecimalFormat("#.#").format(foodStats.foodSaturationLevel), new DecimalFormat("0.00").format(foodStats.foodExhaustionLevel)));
		}
	}
}

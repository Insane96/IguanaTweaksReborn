package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.setup.ITMobEffects;
import insane96mcp.iguanatweaksreborn.setup.ITSoundEvents;
import insane96mcp.iguanatweaksreborn.setup.Strings;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.MCUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
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

	@Config
	@Label(name = "Load Combat Test Config Options", description = "If true, restart the game and the following config options will be changed to the ones of the combat test snapshot and then set this back to false.")
	public static Boolean loadCombatTestConfigOptions = true;
	@Config(min = 0)
	@Label(name = "Health Regen Speed", description = "Sets how many ticks between the health regeneration happens (vanilla is 80; Combat Test is 40).")
	public static Integer healthRegenSpeed = 80;
	@Config(min = 0)
	@Label(name = "Regen when Hunger Above", description = "Sets how much hunger the player must have to regen health (vanilla is >17; Combat Test is >3).")
	public static Integer regenWhenFoodAbove = 17;
	@Config(min = 0)
	@Label(name = "Starve Speed", description = "Sets how many ticks between starve damage happens (vanilla and Combat Test are 80).")
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
	@Config
	@Label(name = "Effects.Well Fed.Enable", description = "Set to true to enable Well Fed, a new effect that speeds up health regen and is applied whenever the player eats from less than 4 drumsticks to more than 9 drumstick in less than 15 seconds.")
	public static Boolean enableWellFed = true;
	@Config(min = 0d, max = 128d)
	@Label(name = "Effects.Well Fed.Duration Multiplier", description = "Multiplies the base duration of Well Fed by this value. Base duration is 1 second per food effectiveness (hunger + saturation).")
	public static Double wellFedDurationMultiplier = 1.0d;
	@Config(min = 0d, max = 1d)
	@Label(name = "Effects.Well Fed.Effectiveness", description = "How much does health regen Well Fed increases per level.")
	public static Double wellFedEffectiveness = 0.05d;
	@Config(min = 0, max = 255)
	@Label(name = "Effects.Well Fed.Max Amplifier", description = "Max amplifier of the Well Fed effect (amplifier 0 = I, amplifier 1 = II, ...).")
	public static Integer wellFedMaxAmplifier = 9;
	@Config
	@Label(name = "Effects.Injured.Enable Injured", description = "Set to true to enable Injured, a new effect that slows down health regen. It's applied when the player takes 3 hits (at least half a heart) in the last 9 seconds (by default). The effect slows down health regen by 20% per level.")
	public static Boolean enableInjured = true;
	@Config(min = 0d, max = 128d)
	@Label(name = "Effects.Injured.Duration Multiplier", description = "Multiplies the base duration of Injured by this value. Base duration is 1 second per point of damage.")
	public static Double injuredDurationMultiplier = 1.0d;
	@Config(min = 0d, max = 10d)
	@Label(name = "Effects.Injured.Effectiveness", description = "How much does health regen Injured decreases per level.")
	public static Double injuredEffectiveness = 0.2d;
	@Config(min = 0)
	@Label(name = "Effects.Injured.Times hit", description = "How many times the player must be hit in a span of \"Injured time to get damaged\" second to apply the effect")
	public static Integer injuredTimesHit = 3;
	@Config(min = 0)
	@Label(name = "Effects.Injured.Time to get damaged", description = "Time to get hit \"Injured Times hit\" times and get the effect")
	public static Integer injuredTimeToGetDamaged = 9;
	@Config(min = 0d, max = 1024d)
	@Label(name = "Effects.Injured.Min Damage", description = "How much damage will make the damage account for \"Injured Times hit\"")
	public static Double injuredMinDamage = 1d;

	public HealthRegen(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		if (loadCombatTestConfigOptions) {
			this.setConfig("Health Regen Speed", 40);
			this.setConfig("Regen when Hunger Above", 6);
			this.setConfig("Starve Speed", 80);
			this.setConfig("Starve Damage", 1);
			this.setConfig("Disable Saturation Regen Boost", true);
			this.setConfig("Consume Hunger Only", true);
			this.setConfig("Max Exhaustion", 4d);
			this.setConfig("Hunger Consumption Chance", 0.5d);

			this.setConfig("Load Combat Test Config Options", false);
		}
	}

	@SubscribeEvent
	public void onPlayerDamaged(LivingDamageEvent event) {
		if (!this.isEnabled()
				|| !enableInjured
				|| !(event.getEntity() instanceof Player playerEntity)
				|| (!(event.getSource().getEntity() instanceof LivingEntity) && !event.getSource().isFall() && !event.getSource().isExplosion() && !event.getSource().isFire())
				|| event.getAmount() < injuredMinDamage)
			return;

		ListTag listTag;
		if (!playerEntity.getPersistentData().contains(Strings.Tags.DAMAGE_HISTORY)) {
			listTag = new ListTag();
		}
		else {
			listTag = playerEntity.getPersistentData().getList(Strings.Tags.DAMAGE_HISTORY, 10);
			if (listTag.size() > 0 && listTag.getCompound(0).getInt("tick") > playerEntity.tickCount)
				listTag.clear();
		}
		//Save the current hit
		CompoundTag tag = new CompoundTag();
		tag.putInt("tick", playerEntity.tickCount);
		tag.putFloat("damage", event.getAmount());
		listTag.add(tag);

		//Remove the older hits to be left with injuredTimesHit
		if (listTag.size() > injuredTimesHit) {
			int toRemove = listTag.size() - injuredTimesHit;
			if (toRemove > 0) {
				listTag.subList(0, toRemove).clear();
			}
		}

		int firstHit = listTag.getCompound(0).getInt("tick");

		if (listTag.size() == injuredTimesHit && playerEntity.tickCount - firstHit < injuredTimeToGetDamaged * 20) {
			int duration;
			if (playerEntity.hasEffect(ITMobEffects.INJURED.get())) {
				duration = (int) ((event.getAmount() * 20) * injuredDurationMultiplier);
				//noinspection ConstantConditions
				duration += playerEntity.getEffect(ITMobEffects.INJURED.get()).getDuration();
			}
			else {
				float totalDamage = 0f;
				for (int i = 0; i < listTag.size(); i++) {
					totalDamage += listTag.getCompound(i).getFloat("damage");
				}
				duration = (int) ((totalDamage * 20) * injuredDurationMultiplier);
			}
			if (duration == 0)
				return;
			playerEntity.addEffect(MCUtils.createEffectInstance(ITMobEffects.INJURED.get(), duration, 0, true, false, true, false));
			playerEntity.level.playSound(null, playerEntity, ITSoundEvents.INJURED.get(), SoundSource.PLAYERS, 1f, 0.9f);
			listTag.remove(0);
		}
		playerEntity.getPersistentData().put(Strings.Tags.DAMAGE_HISTORY, listTag);
	}

	@SubscribeEvent
	public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
		if (!this.isEnabled()
				|| !event.getItem().isEdible()
				|| !(event.getEntity() instanceof Player)
				|| event.getEntity().level.isClientSide)
			return;

		processWellFed(event);
		healOnEat(event);
	}

	private void processWellFed(LivingEntityUseItemEvent.Finish event) {
		if (!enableWellFed)
			return;
		Player playerEntity = (Player) event.getEntity();
		//Do not try to apply well-fed if already has it
		if (playerEntity.hasEffect(ITMobEffects.WELL_FED.get()))
			return;
		ListTag listTag;
		if (!playerEntity.getPersistentData().contains(Strings.Tags.EAT_HISTORY)) {
			listTag = new ListTag();
		}
		else {
			listTag = playerEntity.getPersistentData().getList(Strings.Tags.EAT_HISTORY, 10);
			if (listTag.size() > 0 && listTag.getCompound(0).getInt("tick") > playerEntity.tickCount)
				listTag.clear();
		}
		//Clear the "combo" if the first food eaten is 15 seconds later
		if (listTag.size() > 0 && listTag.getCompound(0).getInt("tick") > 15 * 20) {
			listTag.clear();
		}

		//Don't proceed if hunger higher than 8 and no eat history
		if (playerEntity.getFoodData().getLastFoodLevel() > 8 && listTag.isEmpty())
			return;
		//Save the current eat
		FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), playerEntity);
		//noinspection ConstantConditions
		float effectiveness = Utils.getFoodEffectiveness(food);
		CompoundTag tag = new CompoundTag();
		tag.putInt("tick", playerEntity.tickCount);
		tag.putFloat("effectiveness", effectiveness);
		listTag.add(tag);
		int firstEat = listTag.getCompound(0).getInt("tick");

		//Apply well-fed if more than 9 drumsticks and if less than 15 seconds passed from the first eating
		if (playerEntity.getFoodData().getFoodLevel() >= 19 && playerEntity.tickCount - firstEat < 15 * 20) {
			float totalEffectiveness = 0f;
			for (int i = 0; i < listTag.size(); i++) {
				totalEffectiveness += listTag.getCompound(i).getFloat("effectiveness");
			}
			int duration = (int) ((totalEffectiveness * 20) * wellFedDurationMultiplier);
			int amplifier = Math.min(listTag.size() - 1, wellFedMaxAmplifier);
			playerEntity.addEffect(MCUtils.createEffectInstance(ITMobEffects.WELL_FED.get(), duration, amplifier, true, false, true, false));
			listTag.clear();
		}
		playerEntity.getPersistentData().put(Strings.Tags.EAT_HISTORY, listTag);
	}

	public void healOnEat(LivingEntityUseItemEvent.Finish event) {
		if (foodHealMultiplier == 0d)
			return;
		FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), event.getEntity());
		//noinspection ConstantConditions
		double heal = Utils.getFoodEffectiveness(food) * foodHealMultiplier;
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
					if (player.level.getRandom().nextDouble() < hungerConsumptionChance)
						addHunger(foodStats, -1);
				}
				else
					foodStats.addExhaustion(6.0F);
				foodStats.tickTimer = 0;
			}
		}
		else if (foodStats.foodLevel <= 0) {
			++foodStats.tickTimer;
			int actualStarveSpeed = starveSpeed;
			if (foodStats.foodLevel < 0)
				actualStarveSpeed += foodStats.foodLevel * 5;
			if (foodStats.tickTimer >= actualStarveSpeed) {
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
			ticksToRegen *= 1 - (((wellFed.getAmplifier() + 1) * wellFedEffectiveness));
		return ticksToRegen;
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
package insane96mcp.iguanatweaksreborn.module.hungerhealth.healthregen;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.nohunger.NoHunger;
import insane96mcp.iguanatweaksreborn.setup.ITRRegistries;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.RegistryObject;

import java.text.DecimalFormat;

@Label(name = "Hunger Health Regen", description = "Makes Health regen work differently, similar to Combat Test snapshots. Can be customized. Also adds Vigour effect. Hunger related stuff doesn't work (for obvious reasons) if No Hunger feature is enabled")
@LoadFeature(module = Modules.Ids.HUNGER_HEALTH)
public class HealthRegen extends Feature {
	public static final RegistryObject<MobEffect> VIGOUR = ITRRegistries.MOB_EFFECTS.register("vigour", () -> new VigourEffect(MobEffectCategory.BENEFICIAL, 0xFCD373));

	public static final String HUNGER_ON_DEATH_TAG = IguanaTweaksReborn.RESOURCE_PREFIX + "hunger_on_death";
	public static final String SATURATION_ON_DEATH_TAG = IguanaTweaksReborn.RESOURCE_PREFIX + "saturation_on_death";

	@Config(min = 0)
	@Label(name = "Health Regen Speed", description = "Sets how many ticks between the health regeneration happens (vanilla is 80).")
	public static Integer healthRegenSpeed = 40;
	@Config(min = 0)
	@Label(name = "Regen when Hunger Above", description = "Sets how much hunger the player must have to regen health (vanilla is >17).")
	public static Integer regenWhenFoodAbove = 6;
	@Config(min = 0)
	@Label(name = "Starve Speed", description = "Sets how many ticks between starve damage happens (vanilla is 80).")
	public static Integer starveSpeed = 640;
	@Config(min = 0)
	@Label(name = "Starve Damage", description = "Set how much damage is dealt when starving (vanilla is 1).")
	public static Integer starveDamage = 1;
	@Config(min = 0, max = 20)
	@Label(name = "Starve at Hunger", description = "The player will start starving at this hunger (Vanilla is 0)")
	public static Integer starveAtHunger = 4;
	@Config
	@Label(name = "Faster Starving when really hungry", description = "If below 'Starve at Hunger' player will starve faster.")
	public static Boolean fasterStarvingWhenReallyHungry = true;
	@Config
	@Label(name = "Disable Saturation Regen Boost", description = "Set to true to disable the health regen boost given when max hunger and saturation (false in Vanilla).")
	public static Boolean disableSaturationRegenBoost = true;
	@Config
	@Label(name = "Consume Hunger Only", description = "Set to true to consume Hunger only (and not saturation) when regenerating health (false for Vanilla).")
	public static Boolean consumeHungerOnly = true;
	@Config(min = 0d, max = 40d)
	@Label(name = "Max Exhaustion", description = "Vanilla consumes 1 saturation or hunger whenever Exhaustion reaches 4.0. You can change that value with this config option. NOTE that Minecraft caps this value to 40.")
	public static Double maxExhaustion = 4.0d;
	@Config(min = 0d, max = 1d)
	@Label(name = "Hunger Consumption Chance", description = "If 'Consume Hunger Only' is true then this is the chance to consume an hunger whenever the player is healed (vanilla ignores this; Combat Test has this set to 0.5).")
	public static Double hungerConsumptionChance = 0.5d;
	//TODO
	/*@Config
	@Label(name = "Peaceful Hunger", description = "If enabled, peaceful difficulty no longer heals the player")
	public static Boolean hungerConsumptionChance = true;*/

	@Config(min = 0d, max = 1f)
	@Label(name = "Food Heal Multiplier", description = "When eating you'll get healed by this percentage of (hunger + saturation) restored.")
	public static Double foodHealMultiplier = 0d;
	@Config
	@Label(name = "Respawn.Only if below", description = "If hunger or saturation were above the values on respawn, they will not be reduced.")
	public static Boolean respawnFoodOnlyIfBelow = true;
	@Config(min = 0, max = 20)
	@Label(name = "Respawn.Health", description = "Health of respawning players")
	public static insane96mcp.insanelib.base.config.Difficulty healthOnRespawn = new insane96mcp.insanelib.base.config.Difficulty(20, 14, 8);
	@Config(min = 0, max = 20)
	@Label(name = "Respawn.Hunger", description = "Hunger of respawning players")
	public static insane96mcp.insanelib.base.config.Difficulty hungerOnRespawn = new insane96mcp.insanelib.base.config.Difficulty(14, 10, 10);
	@Config(min = 0, max = 20)
	@Label(name = "Respawn.Saturation", description = "Saturation of respawning players")
	public static insane96mcp.insanelib.base.config.Difficulty saturationOnRespawn = new insane96mcp.insanelib.base.config.Difficulty(10, 10, 6);
	//Effects
	@Config
	@Label(name = "Vigour.Enable", description = "Set to true to enable Vigour, a new effect that lowers hunger consumption and increases health regen speed. Applied when good foods are eaten. If stamina is enabled, decreases its consumption")
	public static Boolean enableVigour = false;
	@Config(min = 0d, max = 128d)
	@Label(name = "Vigour.Duration Multiplier", description = "Multiplies the base duration of Vigour by this value. Base duration is 1 second per food saturation.")
	public static Double vigourDurationMultiplier = 20d;
	@Config(min = 0d, max = 1d)
	@Label(name = "Vigour.Effectiveness", description = "How much does health regen Vigour increases.")
	public static Double vigourEffectiveness = 0.20d;
	@Config(min = 0d, max = 20d)
	@Label(name = "Vigour.Minimum Saturation", description = "Minimum saturation given by the food to apply Vigour.")
	public static Double vigourMinimumSaturation = 7d;
	@Config
	@Label(name = "Vigour.Stacks", description = "If true, eating when already under Vigour increases the duration.")
	public static Boolean vigourStacks = true;

	public HealthRegen(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@SubscribeEvent
	public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
		if (!this.isEnabled()
				|| event.getItem().getItem().getFoodProperties() == null
				|| !(event.getEntity() instanceof Player)
				|| event.getEntity().level().isClientSide)
			return;

		processVigour(event);
		healOnEat(event);
	}

	private void processVigour(LivingEntityUseItemEvent.Finish event) {
		if (!enableVigour)
			return;
		Player player = (Player) event.getEntity();
		boolean hasEffect = player.hasEffect(VIGOUR.get());
		if (hasEffect && !vigourStacks)
			return;

		FoodProperties food = event.getItem().getItem().getFoodProperties(event.getItem(), player);
		float saturationRestored = Utils.getFoodSaturationRestored(food);
		if (saturationRestored < vigourMinimumSaturation)
			return;
		int duration = (int) (saturationRestored * vigourDurationMultiplier) * 20;
		if (hasEffect) {
			MobEffectInstance effectInstance = player.getEffect(VIGOUR.get());
			//noinspection DataFlowIssue
			duration += effectInstance.duration;
		}
		player.addEffect(new MobEffectInstance(VIGOUR.get(), duration, 0, false, false, true));
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
		if (!Feature.isEnabled(HealthRegen.class))
			return false;
		Difficulty difficulty = player.level().getDifficulty();
		foodStats.lastFoodLevel = foodStats.getFoodLevel();
		if (foodStats.exhaustionLevel > maxExhaustion) {
			foodStats.exhaustionLevel -= maxExhaustion;
			if (foodStats.saturationLevel > 0.0F) {
				foodStats.saturationLevel = Math.max(foodStats.saturationLevel - 1.0F, 0.0F);
			}
			//TODO remove with 'Peaceful Hunger'
			else if (difficulty != Difficulty.PEACEFUL) {
				foodStats.foodLevel = Math.max(foodStats.foodLevel - 1, 0);
			}
		}
		tick(foodStats, player, difficulty);

		return true;
	}

	/**
	 * Different from Player#isHurt as doesn't return true if missing less than half a heart
	 */
	public static boolean isPlayerHurt(Player player) {
		return player.getHealth() > 0 && player.getHealth() <= player.getMaxHealth() - 1;
	}

	private static void tick(FoodData foodStats, Player player, Difficulty difficulty) {
		boolean naturalRegen = player.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION) && !Feature.isEnabled(NoHunger.class);
		if (naturalRegen && foodStats.saturationLevel > 0.0F && isPlayerHurt(player) && foodStats.foodLevel >= 20 && !disableSaturationRegenBoost) {
			++foodStats.tickTimer;
			if (foodStats.tickTimer >= 10) {
				float f = Math.min(foodStats.saturationLevel, 6.0F);
				player.heal(f / 6.0F);
				foodStats.addExhaustion(f);
				foodStats.tickTimer = 0;
			}
		}
		else if (naturalRegen && foodStats.foodLevel > regenWhenFoodAbove && isPlayerHurt(player)) {
			++foodStats.tickTimer;
			if (foodStats.tickTimer >= getRegenSpeed(player)) {
				player.heal(1.0F);
				if (consumeHungerOnly) {
					if (player.level().getRandom().nextDouble() < hungerConsumptionChance)
						addHunger(foodStats, -1);
				}
				else
					foodStats.addExhaustion(6.0F);
				foodStats.tickTimer = 0;
			}
		}
		else if (foodStats.foodLevel <= starveAtHunger) {
			++foodStats.tickTimer;
			int actualStarveSpeed = starveSpeed;
			if (fasterStarvingWhenReallyHungry && foodStats.foodLevel < starveAtHunger) {
				int pow = Mth.abs(foodStats.foodLevel - starveAtHunger);
				actualStarveSpeed = actualStarveSpeed >> pow;
			}
			if (foodStats.tickTimer >= actualStarveSpeed) {
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.hurt(player.damageSources().starve(), starveDamage);
				}
				foodStats.tickTimer = 0;
			}
		}
		else if (!Feature.isEnabled(NoHunger.class)){
			foodStats.tickTimer = 0;
		}
	}

	public static void addHunger(FoodData foodStats, int hunger) {
		foodStats.foodLevel = Mth.clamp(foodStats.foodLevel + hunger, 0, 20);
	}

	private static int getRegenSpeed(Player player) {
		int ticksToRegen = healthRegenSpeed;
		MobEffectInstance vigour = player.getEffect(VIGOUR.get());
		if (vigour != null)
			ticksToRegen *= 1 - (((vigour.getAmplifier() + 1) * vigourEffectiveness));
		return ticksToRegen;
	}

	@SubscribeEvent
	public void onPlayerRespawn(LivingDeathEvent event) {
		if (!this.isEnabled()
				|| !(event.getEntity() instanceof Player player))
			return;

		MCUtils.getOrCreatePersistedData(player).putInt(HUNGER_ON_DEATH_TAG, player.getFoodData().foodLevel);
		MCUtils.getOrCreatePersistedData(player).putFloat(SATURATION_ON_DEATH_TAG, player.getFoodData().saturationLevel);
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (!this.isEnabled()
				|| event.isEndConquered())
			return;

		Player player = event.getEntity();
		int hunger = MCUtils.getOrCreatePersistedData(player).getInt(HUNGER_ON_DEATH_TAG);
		int hOnRespawn = (int) hungerOnRespawn.getByDifficulty(player.level());
		if (!respawnFoodOnlyIfBelow || hunger < hOnRespawn)
			player.getFoodData().foodLevel = hOnRespawn;
		else
			player.getFoodData().foodLevel = hunger;
		float saturation = MCUtils.getOrCreatePersistedData(player).getFloat(SATURATION_ON_DEATH_TAG);
		float sOnRespawn = (float) saturationOnRespawn.getByDifficulty(player.level());
		if (!respawnFoodOnlyIfBelow || saturation < sOnRespawn)
			player.getFoodData().saturationLevel = sOnRespawn;
		else
			player.getFoodData().saturationLevel = saturation;
		player.setHealth((float) healthOnRespawn.getByDifficulty(player.level()));
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void debugScreen(CustomizeGuiOverlayEvent.DebugText event) {
		if (!this.isEnabled()
			|| Feature.isEnabled(NoHunger.class))
			return;
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer playerEntity = mc.player;
		if (playerEntity == null)
			return;
		if (mc.options.renderDebug && !mc.showOnlyReducedInfo()) {
			FoodData foodStats = playerEntity.getFoodData();
			event.getLeft().add(String.format("Hunger: %d, Saturation: %s, Exhaustion: %s", foodStats.foodLevel, new DecimalFormat("#.#").format(foodStats.saturationLevel), new DecimalFormat("0.00").format(foodStats.exhaustionLevel)));
		}
	}
}
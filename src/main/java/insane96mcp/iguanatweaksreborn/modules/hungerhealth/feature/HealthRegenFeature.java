package insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.ITEffects;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Health Regen", description = "Makes Health regen work differently, like in Combat Test snapshots or similar to Hunger Overhaul")
public class HealthRegenFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<HealthRegenType> healthRegenTypeConfig;

	public HealthRegenType healthRegenType = HealthRegenType.IGUANA_TWEAKS;

	public HealthRegenFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		healthRegenTypeConfig = Config.builder
				.comment("Changes how health regen works:\n" +
						"VANILLA: no changes,\n" +
						"COMBAT_TEST: health regeneration works like the Combat Tests Shapshots," +
						"IGUANA_TWEAKS: health regen is slow (1 hp every 10 secs) and also the player can have Bleeding and Well Fed effects that slow down / speed up the health regen.")
				.defineEnum("Health Regen Type", healthRegenType);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.healthRegenType = this.healthRegenTypeConfig.get();
	}

	@SubscribeEvent
	public void onPlayerDamaged(LivingHurtEvent event) {
		if (!this.isEnabled())
			return;
		if (!this.healthRegenType.equals(HealthRegenType.IGUANA_TWEAKS))
			return;
		if (!(event.getEntityLiving() instanceof PlayerEntity))
			return;
		PlayerEntity playerEntity = (PlayerEntity) event.getEntityLiving();
		playerEntity.addPotionEffect(new EffectInstance(ITEffects.BLEEDING.get(), (int) (event.getAmount() * 4 * 20), 0, true, false, true));
	}

	@SubscribeEvent
	public void onPlayerEat(LivingEntityUseItemEvent.Finish event) {
		if (!this.isEnabled())
			return;
		if (!this.healthRegenType.equals(HealthRegenType.IGUANA_TWEAKS))
			return;
		if (!event.getItem().getItem().isFood())
			return;
		if (!(event.getEntityLiving() instanceof PlayerEntity))
			return;
		PlayerEntity playerEntity = (PlayerEntity) event.getEntityLiving();
		Food food = event.getItem().getItem().getFood();
		playerEntity.addPotionEffect(new EffectInstance(ITEffects.WELL_FED.get(), (int) (food.saturation * food.value * 4 * 20), food.value / 2, true, false, true));
	}

	public void tickFoodStats(FoodStats foodStats, PlayerEntity player) {
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
		//if (healthRegenType.equals(HealthRegenType.COMBAT_TEST))
		tickCombatTest(foodStats, player, difficulty);
	}

	private void tickCombatTest(FoodStats foodStats, PlayerEntity player, Difficulty difficulty) {
		//Changes (basically the combat snapshots changes):
		// player no longer has the strong regen effect when full hunger bar and has saturation
		// player can heal when hunger is >= 7 (3.5 shranks)
		// player no longer consumes saturation when healing, instead has 50% chance to consume hunger.
		boolean naturalRegen = player.world.getGameRules().getBoolean(GameRules.NATURAL_REGENERATION);
		/*if (naturalRegen && this.foodSaturationLevel > 0.0F && player.shouldHeal() && this.foodLevel >= 20) {
			++this.foodTimer;
			if (this.foodTimer >= 10) {
				float f = Math.min(this.foodSaturationLevel, 6.0F);
				player.heal(f / 6.0F);
				this.addExhaustion(f);
				this.foodTimer = 0;
			}
		}
		else*/
		if (naturalRegen && foodStats.foodLevel > 6 /*>= 18*/ && player.shouldHeal()) {
			++foodStats.foodTimer;
			if (foodStats.foodTimer >= getRegenSpeed(player) /*80*/) {
				player.heal(1.0F);
				if (player.world.rand.nextDouble() < 0.5d)
					addHunger(foodStats, -1);
				//this.addExhaustion(6.0F);
				foodStats.foodTimer = 0;
			}
		}
		else if (foodStats.foodLevel <= 0) {
			++foodStats.foodTimer;
			if (foodStats.foodTimer >= getStarveSpeed(player)) {
				if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
					player.attackEntityFrom(DamageSource.STARVE, 1.0F);
				}
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
		int speed = this.healthRegenType.equals(HealthRegenType.COMBAT_TEST) ? 40 : 200;
		EffectInstance bleeding = player.getActivePotionEffect(ITEffects.BLEEDING.get());
		if (bleeding != null)
			speed *= 1 + ((bleeding.getAmplifier() + 1) * 0.2d);
		EffectInstance wellFed = player.getActivePotionEffect(ITEffects.WELL_FED.get());
		if (wellFed != null) {
			speed *= 1 - (Math.log10(0.6d + (wellFed.getAmplifier() + 1) * 0.8d));
			player.sendStatusMessage(new StringTextComponent(" calculus: " + (Math.log10(0.6d + (wellFed.getAmplifier() + 1) * 0.8d))), false);
		}
		player.sendStatusMessage(new StringTextComponent("regenSpeed: " + speed), false);
		return speed;
	}

	private int getStarveSpeed(PlayerEntity player) {
		return 80;
	}

	private enum HealthRegenType {
		COMBAT_TEST,
		IGUANA_TWEAKS
	}
}

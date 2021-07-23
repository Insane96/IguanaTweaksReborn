package insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.setup.ITEffects;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

@Label(name = "Health Regen", description = "Makes Health regen work like in Combat Test snapshots")
public class HealthRegenFeature extends Feature {
	//private final ForgeConfigSpec.ConfigValue<Double> blockBreakExhaustionMultiplierConfig;
	//public double blockBreakExhaustionMultiplier = 0d;

	public HealthRegenFeature(Module module) {
		super(Config.builder, module);
		/*Config.builder.comment(this.getDescription()).push(this.getName());
		blockBreakExhaustionMultiplierConfig = Config.builder
				.comment("When you break a block you'll get exhaustion equal to the block hardness multiplied by this value. Setting this to 0 will default to the vanilla exaustion (0.005). (It's not affected by the Mining Hardness Features)")
				.defineInRange("Block Break Exhaustion Multiplier", blockBreakExhaustionMultiplier, 0.0d, 1024d);
		Config.builder.pop();*/
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		//this.blockBreakExhaustionMultiplier = this.blockBreakExhaustionMultiplierConfig.get();
		//this.exhaustionOnBlockBreaking = this.exhaustionOnBlockBreakingConfig.get();
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

	final int BASE_REGEN = 40;

	private int getRegenSpeed(PlayerEntity player) {
		int speed = BASE_REGEN;
		EffectInstance bleeding = player.getActivePotionEffect(ITEffects.BLEEDING.get());
		if (bleeding != null)
			speed *= 1 + ((bleeding.getAmplifier() + 1) * 0.2d);
		EffectInstance wellFed = player.getActivePotionEffect(ITEffects.WELL_FED.get());
		if (wellFed != null)
			speed *= 1 - ((wellFed.getAmplifier() + 1) * 0.2d);
		return speed;
	}

	private int getStarveSpeed(PlayerEntity player) {
		return 80;
	}
}

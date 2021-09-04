package insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;

@Label(name = "Food Consuming", description = "Changes to the speed on how food is eaten or how items are consumed.")
public class FoodConsumingFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Boolean> fasterPotionConsumingConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> fasterMilkConsumingConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> eatingSpeedBasedOffFoodConfig;
	private final ForgeConfigSpec.ConfigValue<Double> eatingSpeedMultiplierConfig;

	public boolean fasterPotionConsuming = true;
	public boolean fasterMilkConsuming = true;
	public boolean eatingSpeedBasedOffFood = true;
	public double eatingSpeedMultiplier = 0.18d;

	public FoodConsumingFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		fasterPotionConsumingConfig = Config.builder
				.comment("Makes potion faster to drink, 1 second instead of 1.6.")
				.define("Faster Potion Consuming", this.fasterPotionConsuming);
		fasterMilkConsumingConfig = Config.builder
				.comment("Makes milk faster to drink, 1 second instead of 1.6.")
				.define("Faster Milk Consuming", this.fasterMilkConsuming);
		eatingSpeedBasedOffFoodConfig = Config.builder
				.comment("Makes the speed for eating food based off the hunger and saturation they provide. At 2 (hunger + saturation) the speed is vanilla, higher / lower (hunger + saturation) will lower / raise the speed. Minimum 16 ticks.")
				.define("Eating Speed Based Off Food Config", this.eatingSpeedBasedOffFood);
		eatingSpeedMultiplierConfig = Config.builder
				.comment("Multiplier for the time taken to eat. Only applied if 'Eating Speed Based Off Food Config' is active.")
				.defineInRange("Eating Speed Multiplier", this.eatingSpeedMultiplier, 0, Double.MAX_VALUE);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.fasterPotionConsuming = this.fasterPotionConsumingConfig.get();
		this.fasterMilkConsuming = this.fasterMilkConsumingConfig.get();
		this.eatingSpeedBasedOffFood = this.eatingSpeedBasedOffFoodConfig.get();
		this.eatingSpeedMultiplier = this.eatingSpeedMultiplierConfig.get();
	}

	public int getFoodConsumingTime(ItemStack stack) {
		Food food = stack.getItem().getFood();
		float time = 32 * ((food.getHealing() + (food.getHealing() * food.getSaturation() * 2)));
		if (food.isFastEating())
			time /= 2;
		time *= this.eatingSpeedMultiplier;

		int minTime = food.isFastEating() ? 16 : 32;
		return (int) Math.max(time, minTime);
	}
}

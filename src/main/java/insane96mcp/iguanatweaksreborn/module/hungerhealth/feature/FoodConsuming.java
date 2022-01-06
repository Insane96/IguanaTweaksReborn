package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;

@Label(name = "Food Consuming", description = "Changes to the speed on how food is eaten or how items are consumed.")
public class FoodConsuming extends Feature {
	private final ForgeConfigSpec.ConfigValue<Boolean> fasterPotionConsumingConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> fasterMilkConsumingConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> eatingSpeedBasedOffFoodConfig;
	private final ForgeConfigSpec.ConfigValue<Double> eatingTimeMultiplierConfig;

	public boolean fasterPotionConsuming = true;
	public boolean fasterMilkConsuming = true;
	public boolean eatingSpeedBasedOffFood = true;
	public double eatingTimeMultiplier = 0.15d;

	public FoodConsuming(Module module) {
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
		eatingTimeMultiplierConfig = Config.builder
				.comment("Multiplier for the time taken to eat. Only applied if 'Eating Speed Based Off Food Config' is active.")
				.defineInRange("Eating Time Multiplier", this.eatingTimeMultiplier, 0, Double.MAX_VALUE);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.fasterPotionConsuming = this.fasterPotionConsumingConfig.get();
		this.fasterMilkConsuming = this.fasterMilkConsumingConfig.get();
		this.eatingSpeedBasedOffFood = this.eatingSpeedBasedOffFoodConfig.get();
		this.eatingTimeMultiplier = this.eatingTimeMultiplierConfig.get();
	}

	public int getFoodConsumingTime(ItemStack stack) {
		FoodProperties food = stack.getItem().getFoodProperties();
		float time = 32 * ((food.getNutrition() + (food.getNutrition() * food.getSaturationModifier() * 2)));
		if (food.isFastFood())
			time /= 2;
		time *= this.eatingTimeMultiplier;

		int minTime = food.isFastFood() ? 16 : 32;
		return (int) Math.max(time, minTime);
	}
}

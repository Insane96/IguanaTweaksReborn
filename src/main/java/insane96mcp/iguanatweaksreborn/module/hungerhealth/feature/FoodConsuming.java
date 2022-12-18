package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Food Consuming", description = "Changes to the speed on how food is eaten or how items are consumed.")
public class FoodConsuming extends Feature {
	private final ForgeConfigSpec.BooleanValue fasterDrinkConsumingConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> eatingSpeedBasedOffFoodConfig;
	private final ForgeConfigSpec.ConfigValue<Double> eatingTimeMultiplierConfig;
	private final ForgeConfigSpec.IntValue eatingTimeMinConfig;
	private final ForgeConfigSpec.ConfigValue<Boolean> stopConsumingOnHitConfig;

	public boolean fasterDrinkConsuming = true;
	public boolean eatingSpeedBasedOffFood = true;
	public double eatingTimeMultiplier = 0.115d;
	public int eatingTimeMin = 24;
	public boolean stopConsumingOnHit = true;

	public FoodConsuming(Module module) {
		super(ITCommonConfig.builder, module);
		ITCommonConfig.builder.comment(this.getDescription()).push(this.getName());
		fasterDrinkConsumingConfig = ITCommonConfig.builder
				.comment("Makes potions, milk and honey faster to drink, 1 second instead of 1.6.")
				.define("Faster Drink Consuming", this.fasterDrinkConsuming);
		eatingSpeedBasedOffFoodConfig = ITCommonConfig.builder
				.comment("Makes the speed for eating food based off the hunger and saturation they provide. At 2 (hunger + saturation) the speed is vanilla, higher / lower (hunger + saturation) will lower / raise the speed. Minimum 16 ticks.")
				.define("Eating Speed Based Off Food Restored", this.eatingSpeedBasedOffFood);
		eatingTimeMultiplierConfig = ITCommonConfig.builder
				.comment("Multiplier for the time taken to eat. Only applied if 'Eating Speed Based Off Food Config' is active.")
				.defineInRange("Eating Time Multiplier", this.eatingTimeMultiplier, 0, Double.MAX_VALUE);
		eatingTimeMinConfig = ITCommonConfig.builder
				.comment("The minimum speed a food will take to eat. \"Fast Food\" items have this value halved. Vanilla time is 32/16")
				.defineInRange("Eating Time Minimum", this.eatingTimeMin, 0, Integer.MAX_VALUE);
		stopConsumingOnHitConfig = ITCommonConfig.builder
				.comment("If true, eating/drinking stops when the player's hit.")
				.define("Stop consuming on hit", this.stopConsumingOnHit);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.fasterDrinkConsuming = this.fasterDrinkConsumingConfig.get();
		this.eatingSpeedBasedOffFood = this.eatingSpeedBasedOffFoodConfig.get();
		this.eatingTimeMultiplier = this.eatingTimeMultiplierConfig.get();
		this.eatingTimeMin = this.eatingTimeMinConfig.get();
		this.stopConsumingOnHit = this.stopConsumingOnHitConfig.get();
	}

	public int getFoodConsumingTime(ItemStack stack) {
		FoodProperties food = stack.getItem().getFoodProperties();
		float time = 32 * ((food.getNutrition() + (food.getNutrition() * food.getSaturationModifier() * 2)));
		if (food.isFastFood())
			time /= 2;
		time *= this.eatingTimeMultiplier;

		int minTime = food.isFastFood() ? this.eatingTimeMin / 2 : this.eatingTimeMin;
		return (int) Math.max(time, minTime);
	}

	@SubscribeEvent
	public void onPlayerHit(LivingDamageEvent event) {
		if (!this.isEnabled())
			return;

		if (!this.stopConsumingOnHit)
			return;

		if (!(event.getSource().getEntity() instanceof LivingEntity))
			return;

		if (!(event.getEntityLiving() instanceof Player player))
			return;

		if (!player.getUseItem().getUseAnimation().equals(UseAnim.EAT) && !player.getUseItem().getUseAnimation().equals(UseAnim.DRINK))
			return;

		player.stopUsingItem();
	}
}

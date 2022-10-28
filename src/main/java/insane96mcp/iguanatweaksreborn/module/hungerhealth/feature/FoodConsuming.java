package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Food Consuming", description = "Changes to the speed on how food is eaten or how items are consumed.")
@LoadFeature(module = Modules.Ids.HUNGER_HEALTH)
public class FoodConsuming extends Feature {
	@Config
	@Label(name = "Faster Potion Consuming", description = "Makes potion faster to drink, 1 second instead of 1.6.")
	public static Boolean fasterPotionConsuming = true;
	@Config
	@Label(name = "Faster Milk Consuming", description = "Makes milk faster to drink, 1 second instead of 1.6.")
	public static Boolean fasterMilkConsuming = true;
	@Config
	@Label(name = "Eating Speed Based Off Food Restored", description = "Makes the speed for eating food based off the hunger and saturation they provide. At 2 (hunger + saturation) the speed is vanilla, higher / lower (hunger + saturation) will lower / raise the speed.")
	public static Boolean eatingSpeedBasedOffFood = true;
	@Config(min = 0d)
	@Label(name = "Eating Time Multiplier", description = "Multiplier for the time taken to eat. Only applied if 'Eating Speed Based Off Food Config' is active.")
	public static Double eatingTimeMultiplier = 0.115d;
	@Config(min = 0)
	@Label(name = "Eating Time Minimum", description = "The minimum speed a food will take to eat. \"Fast Food\" items have this value halved. Vanilla time is 32/16")
	public static Integer eatingTimeMin = 24;
	@Config
	@Label(name = "Stop consuming on hit", description = "If true, eating/drinking stops when the player's hit.")
	public static Boolean stopConsumingOnHit = true;

	public FoodConsuming(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	public static int getFoodConsumingTime(ItemStack stack) {
		FoodProperties food = stack.getItem().getFoodProperties(stack, null);
		//noinspection ConstantConditions
		float time = 32 * ((food.getNutrition() + (food.getNutrition() * food.getSaturationModifier() * 2)));
		if (food.isFastFood())
			time /= 2;
		time *= eatingTimeMultiplier;

		int minTime = food.isFastFood() ? eatingTimeMin / 2 : eatingTimeMin;
		return (int) Math.max(time, minTime);
	}

	@SubscribeEvent
	public void onPlayerHit(LivingDamageEvent event) {
		if (!this.isEnabled()
				|| !stopConsumingOnHit
				|| !(event.getSource().getEntity() instanceof LivingEntity)
				|| !(event.getEntity() instanceof Player player)
				|| (!player.getUseItem().getUseAnimation().equals(UseAnim.EAT) && !player.getUseItem().getUseAnimation().equals(UseAnim.DRINK)))
			return;

		player.stopUsingItem();
	}
}

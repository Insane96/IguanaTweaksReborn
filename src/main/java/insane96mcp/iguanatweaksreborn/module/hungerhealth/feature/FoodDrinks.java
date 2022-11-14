package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.utils.CustomFoodProperties;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Food & Drinks", description = "Changes to food nourishment and the speed on how food is eaten or how items are consumed. Removing entries from the json requires a minecraft restart.")
@LoadFeature(module = Modules.Ids.HUNGER_HEALTH)
public class FoodDrinks extends ITFeature {
	public static final ResourceLocation FOOD_BLACKLIST = new ResourceLocation(IguanaTweaksReborn.RESOURCE_PREFIX + "food_drinks_no_hunger_changes");

	public static final ArrayList<CustomFoodProperties> CUSTOM_FOOD_PROPERTIES_DEFAULT = new ArrayList<>(Arrays.asList(
			new CustomFoodProperties(IdTagMatcher.Type.ID, "minecraft:rotten_flesh", 2, -1, 50, false)
	));
	public static final ArrayList<CustomFoodProperties> customFoodProperties = new ArrayList<>();

	@Config(min = 0d, max = 20d)
	@Label(name = "Food Hunger Multiplier", description = "Food's hunger restored will be multiplied by this value (rounded up). E.g. With this set to 0.5 a Cooked Porkchop would restore 4 hunger instead of 8. Setting to 1 will disable this feature.")
	public static Double foodHungerMultiplier = 0.63d;
	@Config(min = 0d, max = 64d)
	@Label(name = "Food Saturation Multiplier", description = "Food's saturation restored will be multiplied by this value. Be aware that saturation is a multiplier and not a flat value, it is used to calculate the effective saturation restored when a player eats, and this calculation includes hunger, so by reducing hunger you automatically reduce saturation too. Setting to 1 will disable this feature.\nThis requires a Minecraft Restart.")
	public static Double foodSaturationMultiplier = 1.0d;

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

	public FoodDrinks(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	static final Type customFoodPropertiesListType = new TypeToken<ArrayList<CustomFoodProperties>>(){}.getType();
	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
		this.loadAndReadFile("food_properties.json", customFoodProperties, CUSTOM_FOOD_PROPERTIES_DEFAULT, customFoodPropertiesListType);
		processFoodMultipliers();
		processCustomFoodValues();
	}

	private static CustomFoodProperties customFoodPropertiesCache;
	public static int getFoodConsumingTime(ItemStack stack) {
		if (customFoodPropertiesCache != null && customFoodPropertiesCache.matchesItem(stack.getItem())) {
			return customFoodPropertiesCache.eatingTime;
		}
		else {
			for (CustomFoodProperties cfp : customFoodProperties) {
				if (cfp.matchesItem(stack.getItem())) {
					customFoodPropertiesCache = cfp;
					return cfp.eatingTime;
				}
			}
		}

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

	private boolean processedFoodMultipliers = false;

	@SuppressWarnings("ConstantConditions")
	public void processFoodMultipliers() {
		if (!this.isEnabled()
				|| processedFoodMultipliers)
			return;
		processedFoodMultipliers = true;

		for (Item item : ForgeRegistries.ITEMS.getValues()) {
			if (!item.isEdible()
					|| isItemInTag(item, FOOD_BLACKLIST))
				continue;

			FoodProperties food = item.getFoodProperties();
			if (foodHungerMultiplier != 1d)
				food.nutrition = (int) Math.ceil(item.getFoodProperties().getNutrition() * foodHungerMultiplier);
			food.saturationModifier = (float) (item.getFoodProperties().getSaturationModifier() * foodSaturationMultiplier);
		}

	}

	@SuppressWarnings("ConstantConditions")
	public void processCustomFoodValues() {
		if (!this.isEnabled()
				|| customFoodProperties.isEmpty())
			return;

		for (CustomFoodProperties foodValue : customFoodProperties) {
			List<Item> items = foodValue.getAllItems();
			for (Item item : items) {
				if (!item.isEdible()) {
					LogHelper.warn("In Custom Food Value %s is not a food", item);
					continue;
				}
				FoodProperties food = item.getFoodProperties();
				if (foodValue.nutrition >= 0)
					food.nutrition = foodValue.nutrition;
				if (foodValue.saturationModifier >= 0f)
					food.saturationModifier = foodValue.saturationModifier;
				if (foodValue.fastEating != null)
					food.fastFood = foodValue.fastEating;
			}
		}

		//reset cache when reloading
		customFoodPropertiesCache = null;

	}
}

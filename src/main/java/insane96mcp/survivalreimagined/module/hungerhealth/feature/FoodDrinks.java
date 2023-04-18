package insane96mcp.survivalreimagined.module.hungerhealth.feature;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.hungerhealth.utils.CustomFoodProperties;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import insane96mcp.survivalreimagined.utils.LogHelper;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
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
import java.util.List;

@Label(name = "Food & Drinks", description = "Changes to food nourishment and the speed on how food is eaten or how items are consumed. Custom Food Properties are controlled via json in this feature's folder. Removing entries from the json requires a minecraft restart.")
@LoadFeature(module = Modules.Ids.HUNGER_HEALTH)
public class FoodDrinks extends SRFeature {
	public static final ResourceLocation FOOD_BLACKLIST = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "food_drinks_no_hunger_changes");

	public static final ArrayList<CustomFoodProperties> CUSTOM_FOOD_PROPERTIES_DEFAULT = new ArrayList<>(List.of(
			new CustomFoodProperties.Builder(IdTagMatcher.Type.ID, "minecraft:rotten_flesh").setNutrition(2).setEatingTime(50).build()
	));
	public static final ArrayList<CustomFoodProperties> customFoodProperties = new ArrayList<>();

	//TODO Change this to formula
	@Config(min = 0d, max = 20d)
	@Label(name = "Food Hunger Multiplier", description = "Food's hunger restored will be multiplied by this value (rounded up). E.g. With this set to 0.5 a Cooked Porkchop would restore 4 hunger instead of 8. Setting to 1 will disable this feature.")
	public static Double foodHungerMultiplier = 1d;
	//TODO Change this to formula
	@Config(min = 0d, max = 64d)
	@Label(name = "Food Saturation Multiplier", description = "Food's saturation restored will be multiplied by this value. Be aware that saturation is a multiplier and not a flat value, it is used to calculate the effective saturation restored when a player eats, and this calculation includes hunger, so by reducing hunger you automatically reduce saturation too. Setting to 1 will disable this feature.\nThis requires a Minecraft Restart.")
	public static Double foodSaturationMultiplier = 1.0d;

	@Config
	@Label(name = "Faster Drink Consuming", description = "Makes potion, milk and honey faster to drink, 1 second instead of 1.6.")
	public static Boolean fasterDrinkConsuming = true;
	@Config
	@Label(name = "Eating Speed Based Off Food Restored", description = "Makes the speed for eating food based off the hunger and saturation they provide.")
	public static Boolean eatingSpeedBasedOffFood = true;
	@Config
	@Label(name = "Eating Speed Formula", description = "The formula to calculate the ticks required to eat a food. Variables as hunger, saturation_modifier, effectiveness as numbers and fast_food as boolean can be used. This is evaluated with EvalEx https://ezylang.github.io/EvalEx/concepts/parsing_evaluation.html. The default formula increases the time to eat exponentially when higher effectiveness.")
	public static String eatingSpeedFormula = "MAX((32 * effectiveness^1.35) / IF(fast_food, 2, 1) * 0.04, 32 / IF(fast_food, 2, 1))"; //max((32 * x^1.4) * 0.04, 24)
	@Config
	@Label(name = "Stop consuming on hit", description = "If true, eating/drinking stops when the player's hit.")
	public static Boolean stopConsumingOnHit = true;

	@Config
	@Label(name = "Furnace food and smoker recipe", description = "Furnaces take 2x more time to smelt Food and change smokers recipe.")
	public static Boolean slowerFoodInFurnaces = true;

	public FoodDrinks(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "slower_food_in_furnace", net.minecraft.network.chat.Component.literal("Survival Reimagined Slower Food in Furnace"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && slowerFoodInFurnaces));
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

	@Override
	public void loadConfigOptions() {
		super.loadConfigOptions();
	}

	private static CustomFoodProperties customFoodPropertiesCache;
	private static FoodProperties lastFoodEatenCache;
	private static int lastFoodEatenTime;
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
		if (food == lastFoodEatenCache)
			return lastFoodEatenTime;

		Expression expression = new Expression(eatingSpeedFormula);
		try {
			//noinspection ConstantConditions
			EvaluationValue result = expression
					.with("hunger", food.getNutrition())
					.and("saturation_modifier", food.getSaturationModifier())
					.and("effectiveness", Utils.getFoodEffectiveness(food))
					.and("fast_food", food.isFastFood())
					.evaluate();
			lastFoodEatenCache = food;
			lastFoodEatenTime = result.getNumberValue().intValue();
			return lastFoodEatenTime;
		}
		catch (Exception ex) {
			LogHelper.error("Failed to evaluate or parse eating speed formula: %s", expression);
			return food.isFastFood() ? 16 : 32;
		}
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
			List<Item> items = getAllItems(foodValue);
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

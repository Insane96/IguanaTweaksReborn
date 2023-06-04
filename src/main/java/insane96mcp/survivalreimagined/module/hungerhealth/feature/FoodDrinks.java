package insane96mcp.survivalreimagined.module.hungerhealth.feature;

import com.ezylang.evalex.Expression;
import com.ezylang.evalex.data.EvaluationValue;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.generator.SRItemTagsProvider;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.hungerhealth.data.CustomFoodProperties;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.network.message.JsonConfigSyncMessage;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import insane96mcp.survivalreimagined.setup.SRItems;
import insane96mcp.survivalreimagined.utils.LogHelper;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Foods & Drinks", description = "Changes to food nourishment and the speed on how food is eaten or how items are consumed. Custom Food Properties are controlled via json in this feature's folder. Removing entries from the json requires a minecraft restart.")
@LoadFeature(module = Modules.Ids.HUNGER_HEALTH)
public class FoodDrinks extends SRFeature {

	public static final RegistryObject<Item> BROWN_MUSHROOM_STEW = SRItems.REGISTRY.register("brown_mushroom_stew", () -> new BowlFoodItem(new Item.Properties()
			.food(new FoodProperties.Builder().nutrition(3).saturationMod(0.6F).build())
	));
	public static final RegistryObject<Item> RED_MUSHROOM_STEW = SRItems.REGISTRY.register("red_mushroom_stew", () -> new BowlFoodItem(new Item.Properties()
			.food(new FoodProperties.Builder().nutrition(3).saturationMod(0.6F).build())
	));

	public static final RegistryObject<Item> OVER_EASY_EGG = SRItems.REGISTRY.register("over_easy_egg", () -> new Item(new Item.Properties()
			.food(new FoodProperties.Builder().nutrition(4).saturationMod(0.6F).build())
	));

	public static final RegistryObject<Item> PUMPKIN_PULP = SRItems.REGISTRY.register("pumpkin_pulp", () -> new Item(new Item.Properties()
			.food(new FoodProperties.Builder().nutrition(2).saturationMod(0.3F).build())
	));

	public static final TagKey<Item> FOOD_BLACKLIST = SRItemTagsProvider.create("food_drinks_no_hunger_changes");

	public static final ArrayList<CustomFoodProperties> CUSTOM_FOOD_PROPERTIES_DEFAULT = new ArrayList<>(List.of(
			new CustomFoodProperties.Builder(IdTagMatcher.Type.ID, "minecraft:rotten_flesh").setNutrition(2).setEatingTime(50).build()
	));
	public static final ArrayList<CustomFoodProperties> customFoodProperties = new ArrayList<>();

	//TODO Change this to formula
	@Config(min = 0d, max = 20d)
	@Label(name = "Food Hunger Multiplier", description = "Food's hunger restored will be multiplied by this value (rounded up). E.g. With this set to 0.5 a Cooked Pork-chop would restore 4 hunger instead of 8. Setting to 1 will disable this feature.")
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
	public static String eatingSpeedFormula = "MAX((32 * effectiveness^1.35) / IF(fast_food, 2, 1) * 0.04, 32 / IF(fast_food, 2, 1))"; //max((32 * x^1.35) * 0.04, 32) or, if the food is fast eat max((32 * x^1.35) / 2 * 0.04, 32 / 2)
	@Config
	@Label(name = "Stop consuming on hit", description = "If true, eating/drinking stops when the player's hit.")
	public static Boolean stopConsumingOnHit = true;

	@Config
	@Label(name = "No Furnace food and smoker recipe", description = "Food can no longer be smelted in furnaces and change smokers recipe to require Mithril ingot.")
	public static Boolean noFurnaceFoodAndSmokerRecipe = true;

	public FoodDrinks(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "no_food_in_furnace", Component.literal("Survival Reimagined No Food in Furnace"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && noFurnaceFoodAndSmokerRecipe));
		JSON_CONFIGS.add(new JsonConfig<>("food_properties.json", customFoodProperties, CUSTOM_FOOD_PROPERTIES_DEFAULT, CustomFoodProperties.LIST_TYPE, FoodDrinks::processCustomFoodValues, true, JsonConfigSyncMessage.ConfigType.CUSTOM_FOOD_PROPERTIES));
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
		processFoodMultipliers();
	}

	public static void handleCustomFoodPropertiesPacket(String json) {
		loadAndReadJson(json, customFoodProperties, CUSTOM_FOOD_PROPERTIES_DEFAULT, CustomFoodProperties.LIST_TYPE);
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

	private static boolean processedFoodMultipliers = false;

	@SuppressWarnings("ConstantConditions")
	public static void processFoodMultipliers() {
		if (processedFoodMultipliers)
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
	public static void processCustomFoodValues(List<CustomFoodProperties> list, boolean isClientSide) {
		if (list.isEmpty())
			return;

		for (CustomFoodProperties foodValue : list) {
			List<Item> items = getAllItems(foodValue, false);
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

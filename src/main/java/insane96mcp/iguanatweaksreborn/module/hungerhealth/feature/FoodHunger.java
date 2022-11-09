package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.hungerhealth.utils.CustomFoodProperties;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
@Label(name = "Food Hunger", description = "Change food's hunger and saturation given, also makes food heal you by a bit. Changing anything requires a Minecraft restart.")
@LoadFeature(module = Modules.Ids.HUNGER_HEALTH)
//TODO Merge into one feature "Food Properties" and split config like these features
public class FoodHunger extends ITFeature {
	public static final ResourceLocation FOOD_BLACKLIST = new ResourceLocation(IguanaTweaksReborn.RESOURCE_PREFIX + "no_hunger_changes_food");
	private static ForgeConfigSpec.ConfigValue<List<? extends String>> customFoodValueConfig;

	public static final ArrayList<CustomFoodProperties> CUSTOM_FOOD_PROPERTIES_DEFAULT = new ArrayList<>(Arrays.asList(
			new CustomFoodProperties(IdTagMatcher.Type.ID, "minecraft:rotten_flesh", 2, -1, 40, false)
	));
	public static final ArrayList<CustomFoodProperties> customFoodProperties = new ArrayList<>();

	@Config(min = 0d, max = 20d)
	@Label(name = "Food Hunger Multiplier", description = "Food's hunger restored will be multiplied by this value (rounded up). E.g. With this set to 0.5 a Cooked Porkchop would restore 4 hunger instead of 8. Setting to 1 will disable this feature.")
	public static Double foodHungerMultiplier = 0.63d;
	@Config(min = 0d, max = 64d)
	@Label(name = "Food Saturation Multiplier", description = "Food's saturation restored will be multiplied by this value. Be aware that saturation is a multiplier and not a flat value, it is used to calculate the effective saturation restored when a player eats, and this calculation includes hunger, so by reducing hunger you automatically reduce saturation too. Setting to 1 will disable this feature.\nThis requires a Minecraft Restart.")
	public static Double foodSaturationMultiplier = 1.0d;

	public FoodHunger(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	static final Type customFoodPropertiesListType = new TypeToken<ArrayList<CustomFoodProperties>>(){}.getType();
	@Override
	public void loadJsonConfigs() {
		super.loadJsonConfigs();
		this.loadAndReadFile("custom_food_properties.json", customFoodProperties, CUSTOM_FOOD_PROPERTIES_DEFAULT, customFoodPropertiesListType);
		processCustomFoodValues();
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);

		processFoodMultipliers();
	}

	private boolean processedFoodMultipliers = false;
	private boolean processedCustomFoodValues = false;

	@SuppressWarnings("ConstantConditions")
	public void processFoodMultipliers() {
		if (!this.isEnabled()
				|| processedFoodMultipliers)
			return;
		processedFoodMultipliers = true;

		for (Item item : ForgeRegistries.ITEMS.getValues()) {
			if (!item.isEdible())
				continue;

			if (Utils.isItemInTag(item, FOOD_BLACKLIST))
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
				|| processedCustomFoodValues)
			return;
		processedCustomFoodValues = true;
		if (customFoodProperties.isEmpty())
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

	}
}

package insane96mcp.iguanatweaksreborn.module.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.module.hungerhealth.utils.CustomFoodProperties;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.config.BlacklistConfig;
import insane96mcp.insanelib.utils.IdTagMatcher;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Label(name = "Food Hunger", description = "Change food's hunger and saturation given, also makes food heal you by a bit. Changing anything requires a Minecraft restart.")
public class FoodHunger extends Feature {
	private final ForgeConfigSpec.ConfigValue<Double> foodHungerMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> foodSaturationMultiplierConfig;
	private final BlacklistConfig foodBlacklistConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> customFoodValueConfig;

	public double foodHungerMultiplier = 0.63d;
	public double foodSaturationMultiplier = 1.0d;
	public ArrayList<IdTagMatcher> foodBlacklist;
	public boolean foodBlacklistAsWhitelist = false;
	public ArrayList<CustomFoodProperties> customFoodValues;

	public FoodHunger(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		foodHungerMultiplierConfig = Config.builder
				.comment("Food's hunger restored will be multiplied by this value (rounded up). E.g. With this set to 0.5 a Cooked Porkchop would heal 4 hunger instead of 8. Setting to 1 will disable this feature.")
				.defineInRange("Food Hunger Multiplier", foodHungerMultiplier, 0d, 20d);
		foodSaturationMultiplierConfig = Config.builder
				.comment("Food's saturation restored will be multiplied by this value. Be aware that saturation is a multiplier and not a flat value, it is used to calculate the effective saturation restored when a player eats, and this calculation includes hunger, so by reducing hunger you automatically reduce saturation too. Setting to 1 will disable this feature.\nThis requires a Minecraft Restart.")
				.defineInRange("Food Saturation Multiplier", foodSaturationMultiplier, 0.0d, 64d);
		foodBlacklistConfig = new BlacklistConfig(Config.builder, "Food Blacklist", "Items or tags that will ignore the food multipliers. This can be inverted via 'Blacklist as Whitelist'. Each entry has an item or tag. E.g. [\"minecraft:stone\", \"minecraft:cooked_porkchop\"].", Collections.emptyList(), this.foodBlacklistAsWhitelist);
		customFoodValueConfig = Config.builder
				.comment("Define custom food values, one string = one item. Those items are not affected by other changes such as 'Food Hunger Multiplier'.\nThe format is modid:itemid,hunger,saturation. Saturation is optional\nE.g. 'minecraft:cooked_porkchop,16,1.0' will make cooked porkchops give 8 shranks of food and 16 saturation (actual saturation is calculated by 'saturation * 2 * hunger').")
				.defineList("Custom Food Hunger", Arrays.asList("minecraft:rotten_flesh,2"), o -> o instanceof String);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.foodHungerMultiplier = this.foodHungerMultiplierConfig.get();
		this.foodSaturationMultiplier = this.foodSaturationMultiplierConfig.get();
		this.customFoodValues = CustomFoodProperties.parseStringList(this.customFoodValueConfig.get());
		this.foodBlacklist = (ArrayList<IdTagMatcher>) IdTagMatcher.parseStringList(this.foodBlacklistConfig.listConfig.get());
		this.foodBlacklistAsWhitelist = this.foodBlacklistConfig.listAsWhitelistConfig.get();

		processFoodMultipliers();
		processCustomFoodValues();
	}

	private boolean processedFoodMultipliers = false;
	private boolean processedCustomFoodValues = false;

	public void processFoodMultipliers() {
		if (!this.isEnabled())
			return;
		if (processedFoodMultipliers)
			return;
		processedFoodMultipliers = true;

		for (Item item : ForgeRegistries.ITEMS.getValues()) {
			if (!item.isEdible())
				continue;

			//Check for item black/whitelist
			boolean isInWhitelist = false;
			boolean isInBlacklist = false;
			for (IdTagMatcher blacklistEntry : this.foodBlacklist) {
				if (blacklistEntry.matchesItem(item)) {
					if (!this.foodBlacklistAsWhitelist)
						isInBlacklist = true;
					else
						isInWhitelist = true;
					break;
				}
			}
			if (isInBlacklist || (!isInWhitelist && this.foodBlacklistAsWhitelist))
				continue;

			FoodProperties food = item.getFoodProperties();
			if (this.foodHungerMultiplier != 1d)
				food.nutrition = (int) Math.ceil(item.getFoodProperties().getNutrition() * foodHungerMultiplier);
			food.saturationModifier = (float) (item.getFoodProperties().getSaturationModifier() * foodSaturationMultiplier);
		}

	}

	public void processCustomFoodValues() {
		if (!this.isEnabled())
			return;
		if (processedCustomFoodValues)
			return;
		processedCustomFoodValues = true;
		if (customFoodValues.isEmpty())
			return;

		for (CustomFoodProperties foodValue : customFoodValues) {
			if (!ForgeRegistries.ITEMS.containsKey(foodValue.id)) {
				LogHelper.warn("In Custom Food Value %s is not a valid item", foodValue.id);
				continue;
			}
			Item item = ForgeRegistries.ITEMS.getValue(foodValue.id);
			if (!item.isEdible()) {
				LogHelper.warn("In Custom Food Value %s is not a food", foodValue.id);
				continue;
			}
			FoodProperties food = item.getFoodProperties();
			food.nutrition = foodValue.nutrition;
			if (foodValue.saturationModifier != -1f)
				food.saturationModifier = foodValue.saturationModifier;
		}

	}
}

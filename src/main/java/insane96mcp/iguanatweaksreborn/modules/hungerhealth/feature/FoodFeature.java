package insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.modules.hungerhealth.classutils.FoodValue;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.config.BlacklistConfig;
import insane96mcp.insanelib.utils.IdTagMatcher;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Label(name = "Food Overhaul", description = "Change food's hunger and saturation given, also makes food heal you by a bit")
public class FoodFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> foodHungerMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> foodSaturationMultiplierConfig;
	private final BlacklistConfig blacklistConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> customFoodValueConfig;

	public double foodHungerMultiplier = 0.5d;
	public double foodSaturationMultiplier = 1.0d;
	public ArrayList<IdTagMatcher> blacklist;
	public boolean blacklistAsWhitelist = false;
	public ArrayList<FoodValue> customFoodValues;


	public FoodFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		foodHungerMultiplierConfig = Config.builder
				.comment("Food's hunger restored will be multiplied by this value + 0.5. E.g. With this set to 0.5 a Cooked Porkchop would heal 5 hunger instead of 8. Setting to 1 will disable this feature.")
				.defineInRange("Food Hunger Multiplier", foodHungerMultiplier, 0.0d, 128d);
		foodSaturationMultiplierConfig = Config.builder
				.comment("Food's saturation restored will be multiplied by this value. Be aware that saturation is a multiplier and not a flat value, it is used to calculate the effective saturation restored when a player eats, and this calculation includes hunger, so by reducing hunger you automatically reduce saturation too. Setting to 1 will disable this feature.\nThis requires a Minecraft Restart.")
				.defineInRange("Food Saturation Multiplier", foodSaturationMultiplier, 0.0d, 64d);
		blacklistConfig = new BlacklistConfig(Config.builder, "Food Blacklist", "Items or tags that will ignore the food multipliers. This can be inverted via 'Blacklist as Whitelist'. Each entry has an item or tag. E.g. [\"minecraft:stone\", \"minecraft:cooked_porkchop\"].", Collections.emptyList(), this.blacklistAsWhitelist);
        customFoodValueConfig = Config.builder
                .comment("Define custom food values, one string = one item. Those items are not affected by other changes such as 'Food Hunger Multiplier'.\nThe format is modid:itemid,hunger,saturation. Saturation is optional\nE.g. 'minecraft:cooked_porkchop,16,1.0' will make cooked porkchops give 8 shranks of food and 16 saturation (actual saturation is calculated by 'saturation * 2 * hunger').")
                .defineList("Custom Food Hunger", new ArrayList<>(), o -> o instanceof String);
        Config.builder.pop();
    }

    private ArrayList<FoodValue> defaultFoodValues = new ArrayList<>();

    @Override
    public void loadConfig() {
        super.loadConfig();
        this.foodHungerMultiplier = this.foodHungerMultiplierConfig.get();
        this.foodSaturationMultiplier = this.foodSaturationMultiplierConfig.get();
        this.customFoodValues = parseCustomFoodHungerList(this.customFoodValueConfig.get());
        this.blacklist = IdTagMatcher.parseStringList(this.blacklistConfig.listConfig.get());
        this.blacklistAsWhitelist = this.blacklistConfig.listAsWhitelistConfig.get();

        if (this.defaultFoodValues.isEmpty())
            this.defaultFoodValues = saveDefaultFoodValues();

        processFoodMultipliers();
        processCustomFoodValues();
    }

    private ArrayList<FoodValue> parseCustomFoodHungerList(List<? extends String> list) {
        ArrayList<FoodValue> foodValues = new ArrayList<>();
        for (String line : list) {
            FoodValue customFoodValue = FoodValue.parseLine(line);
            if (customFoodValue != null)
                foodValues.add(customFoodValue);
        }
        return foodValues;
    }

    private ArrayList<FoodValue> saveDefaultFoodValues() {
        ArrayList<FoodValue> defaultFoodValues = new ArrayList<>();
        Collection<Item> items = ForgeRegistries.ITEMS.getValues();
        for (Item item : items) {
            if (!item.isFood())
                continue;

            Food food = item.getFood();
            defaultFoodValues.add(new FoodValue(item.getRegistryName(), food.value, food.saturation));
        }

        return defaultFoodValues;
    }

    public void processFoodMultipliers() {
        if (!this.isEnabled())
            return;


        for (FoodValue defaultFood : defaultFoodValues) {
            Item item = ForgeRegistries.ITEMS.getValue(defaultFood.id);

            boolean isInWhitelist = false;
            boolean isInBlacklist = false;
            for (IdTagMatcher blacklistEntry : blacklist) {
                if (!blacklistAsWhitelist) {
                    if (blacklistEntry.matchesItem(item, null)) {
						isInBlacklist = true;
						break;
					}
                }
                else {
					if (blacklistEntry.matchesItem(item, null)) {
						isInWhitelist = true;
						break;
					}
                }
            }
            if (isInBlacklist)
                continue;
            if (!isInWhitelist && blacklistAsWhitelist)
                continue;
            Food food = item.getFood();
			if (this.foodHungerMultiplier != 1d)
            	food.value = (int) Math.ceil((defaultFood.hunger * foodHungerMultiplier) + 0.5f);
            food.saturation = (float) (defaultFood.saturation * foodSaturationMultiplier);
        }
    }

    public void processCustomFoodValues() {
        if (!this.isEnabled())
            return;

        if (customFoodValues.isEmpty())
            return;

        for (FoodValue foodValue : customFoodValues) {
            Item item = ForgeRegistries.ITEMS.getValue(foodValue.id);
            Food food = item.getFood();
            food.value = foodValue.hunger;
            if (foodValue.saturation != -1f)
				food.saturation = foodValue.saturation;
        }
    }
}

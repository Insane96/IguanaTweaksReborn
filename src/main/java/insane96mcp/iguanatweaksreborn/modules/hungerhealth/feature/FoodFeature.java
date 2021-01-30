package insane96mcp.iguanatweaksreborn.modules.hungerhealth.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
import insane96mcp.iguanatweaksreborn.modules.hungerhealth.classutils.FoodValue;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.iguanatweaksreborn.utils.LogHelper;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Label(name = "Food Overhaul", description = "Change food's hunger and saturation given, also makes food heal you by a bit")
public class FoodFeature extends ITFeature {

    private final ForgeConfigSpec.ConfigValue<Double> foodHungerMultiplierConfig;
    private final ForgeConfigSpec.ConfigValue<Double> foodSaturationMultiplierConfig;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklistConfig;
    private final ForgeConfigSpec.ConfigValue<Boolean> blacklistAsWhitelistConfig;
    private final ForgeConfigSpec.ConfigValue<Double> foodHealMultiplierConfig;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> customFoodValueConfig;

    private List<String> blackListDefault = Arrays.asList("minecraft:rotten_flesh", "minecraft:potion");

    public double foodHungerMultiplier = 0.5d;
    public double foodSaturationMultiplier = 1.0d;
    public ArrayList<IdTagMatcher> blacklist;
    public boolean blacklistAsWhitelist = false;
    public double foodHealMultiplier = 0.33333d;
    public ArrayList<FoodValue> customFoodValues;


    public FoodFeature(ITModule module) {
        super(module);
        Config.builder.comment(this.getDescription()).push(this.getName());
        foodHungerMultiplierConfig = Config.builder
                .comment("Food's hunger restored will be multiplied by this value + 0.5. E.g. With this set to 0.5 a Cooked Porkchop would heal 5 hunger instead of 8. Setting to 1 will disable this feature.")
                .defineInRange("Food Hunger Multiplier", foodHungerMultiplier, 0.0d, 128d);
        foodSaturationMultiplierConfig = Config.builder
                .comment("Food's saturation restored will be multiplied by this value. Be aware that saturation is a multiplier and not a flat value, it is used to calculate the effective saturation restored when a player eats, and this calculation includes hunger, so by reducing hunger you automatically reduce saturation too. Setting to 1 will disable this feature.\nThis requires a Minecraft Restart.")
                .defineInRange("Food Saturation Multiplier", foodSaturationMultiplier, 0.0d, 64d);
        blacklistConfig = Config.builder
                .comment("Items or tags that will ignore the food multipliers. This can be inverted via 'Blacklist as Whitelist'. Each entry has an item or tag. E.g. [\"minecraft:stone\", \"minecraft:cooked_porkchop\"].")
                .defineList("Items Blacklist", blackListDefault, o -> o instanceof String);
        blacklistAsWhitelistConfig = Config.builder
                .comment("Items Blacklist will be treated as a whitelist.")
                .define("Blacklist as Whitelist", blacklistAsWhitelist);
        foodHealMultiplierConfig = Config.builder
                .comment("When eating you'll get healed by this percentage of hunger restored. Setting to 0 will disable this feature.")
                .defineInRange("Food Heal Multiplier", foodHealMultiplier, 0.0d, 128d);
        customFoodValueConfig = Config.builder
                .comment("Define custom food values, one string = one item. Those items are not affected by other changes such as 'Food Hunger Multiplier'.\nThe format is modid:itemid,hunger,saturation. Saturation is optional\nE.g. 'minecraft:cooked_porkchop,16,1.0' will make cooked porkchops give 8 shranks of food and 16 saturation (actual saturation is calculated by 'saturation * 2 * hunger').")
                .defineList("Custom Food Hunger", new ArrayList<>(), o -> o instanceof String);
        Config.builder.pop();
    }

    private ArrayList<FoodValue> defaultFoodValues = new ArrayList<>();

    @Override
    public void loadConfig() {
        super.loadConfig();
        foodHungerMultiplier = foodHungerMultiplierConfig.get();
        foodSaturationMultiplier = foodSaturationMultiplierConfig.get();
        customFoodValues = parseCustomFoodHungerList(customFoodValueConfig.get());
        blacklist = parseBlacklist(blacklistConfig.get());
        blacklistAsWhitelist = blacklistAsWhitelistConfig.get();
        foodHealMultiplier = foodHealMultiplierConfig.get();

        if (defaultFoodValues.isEmpty())
            defaultFoodValues = saveDefaultFoodValues();

        processFoodMultipliers();
        processCustomFoodValues();
    }

    private ArrayList<IdTagMatcher> parseBlacklist(List<? extends String> list) {
        ArrayList<IdTagMatcher> idTagMatchers = new ArrayList<>();
        for (String line : list) {
            IdTagMatcher idTagMatcher = IdTagMatcher.parseLine(line);
            if (idTagMatcher != null)
                idTagMatchers.add(idTagMatcher);
        }
        return idTagMatchers;
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
            LogHelper.Info("item: " + item.getRegistryName() + " hunger: " + food.value + " saturation: " + food.saturation);
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
                    if (MCUtils.isInTagOrItem(blacklistEntry, item, null)) {
                        isInBlacklist = true;
                        break;
                    }
                }
                else {
                    if (MCUtils.isInTagOrItem(blacklistEntry, item, null)) {
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
            LogHelper.Info("item: " + item.getRegistryName() + " hunger: " + defaultFood.hunger + " saturation: " + defaultFood.saturation);
            food.value = (int) Math.ceil((defaultFood.hunger * foodHungerMultiplier) + 0.5f);
            food.saturation = (float) (defaultFood.saturation * foodSaturationMultiplier);
            LogHelper.Info("item: " + item.getRegistryName() + " hunger: " + food.value + " saturation: " + food.saturation);
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
            if (foodValue.saturation != 1f)
                food.saturation = foodValue.saturation;
        }
    }

    @SubscribeEvent
    public void healOnEat(LivingEntityUseItemEvent.Finish event) {
        if (!this.isEnabled())
            return;

        if (foodHealMultiplier == 0d)
            return;
        if (!event.getItem().isFood())
            return;
        Food food = event.getItem().getItem().getFood();
        float heal = food.value * (float) foodHealMultiplier;
        event.getEntityLiving().heal(heal);
    }
}

package insane96mcp.iguanatweaksreborn.setup;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final CommonConfig COMMON;

	public static final ForgeConfigSpec.Builder builder;

	static {
		builder = new ForgeConfigSpec.Builder();
		final Pair<CommonConfig, ForgeConfigSpec> specPair = builder.configure(CommonConfig::new);
		COMMON = specPair.getLeft();
		COMMON_SPEC = specPair.getRight();
	}

	public static class CommonConfig {
		public final Modules modules;
		public final Farming farming;
		public final StackSizes stackSizes;
		public final HungerHealth hungerHealth;
		public final Misc misc;

		public CommonConfig(final ForgeConfigSpec.Builder builder) {
			insane96mcp.iguanatweaksreborn.base.Modules.init();

			modules = new Modules(builder);
			farming = new Farming(builder);
			stackSizes = new StackSizes(builder);
			hungerHealth = new HungerHealth(builder);
			misc = new Misc(builder);
		}

		public static class Modules {
			public static String name = "Modules";
			public static String comment = "Disable entire modules with just a 'false'";

			public ForgeConfigSpec.ConfigValue<Boolean> farming;
			public ForgeConfigSpec.ConfigValue<Boolean> experience;
			public ForgeConfigSpec.ConfigValue<Boolean> stackSizes;
			public ForgeConfigSpec.ConfigValue<Boolean> hungerHealth;
			public ForgeConfigSpec.ConfigValue<Boolean> misc;

			public Modules(ForgeConfigSpec.Builder builder) {
				builder.comment(comment).push(name);
				farming = builder
						.comment("Set to false to disable the Farming Module")
						.define("Farming Module", true);
				experience = builder
						.comment("Set to false to disable the Experience Module")
						.define("Experience Module", true);
				stackSizes = builder
						.comment("Set to false to disable the Stack Sizes Module")
						.define("Stack Sizes Module", true);
				hungerHealth = builder
						.comment("Set to false to disable the Hunger & Health Module")
						.define("Hunger & Health Module", true);
				misc = builder
						.comment("Set to false to disable the Misc Module")
						.define("Misc Module", true);
				builder.pop();
			}
		}

		public static class Farming {
			public static String name = "Farming";

			public final Livestock livestock;
			public final Agriculture agriculture;

			public Farming(ForgeConfigSpec.Builder builder) {
				builder.push(name);
				livestock = new Livestock(builder);
				agriculture = new Agriculture(builder);
				builder.pop();

			}

			public static class Livestock {
				public static String name = "Livestock";
				public ForgeConfigSpec.ConfigValue<Double> childGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<Double> breedingMultiplier;
				public ForgeConfigSpec.ConfigValue<Double> eggLayMultiplier;
				public ForgeConfigSpec.ConfigValue<Integer> cowMilkDelay;

				public Livestock(ForgeConfigSpec.Builder builder) {
					builder.push(name);
					childGrowthMultiplier = builder
							.comment("Increases the time required for Baby Animals to grow (e.g. at 2.0 Animals will take twice to grow).\n1.0 will make Animals grow like normal.")
							.defineInRange("Childs Growth Multiplier", 2.5d, 1.0d, 128d);
					breedingMultiplier = builder
							.comment("Increases the time required for Animals to breed again (e.g. at 2.0 Animals will take twice to be able to breed again).\n1.0 will make Animals breed like normal.")
							.defineInRange("Breeding Time Multiplier", 3.5d, 1.0d, 128d);
					eggLayMultiplier = builder
							.comment("Increases the time required for Chickens to lay an egg (e.g. at 2.0 Chickens will take twice the time to lay an egg).\n1.0 will make chickens lay eggs like normal.")
							.defineInRange("Egg Lay Multiplier", 3.0d, 1.0d, 128d);
					cowMilkDelay = builder
							.comment("Ticks before a cow can be milked again (20 ticks = 1 second). This applies to Mooshroom stew too.\n0 will disable this feature.")
							.defineInRange("Cow Milk Delay", 24000, 0, Integer.MAX_VALUE);
					builder.pop();
				}
			}

			public static class Agriculture {
				public static String name = "Agriculture";

				public ForgeConfigSpec.ConfigValue<FarmingModule.Agriculture.NerfedBonemeal> nerfedBonemeal;
				public ForgeConfigSpec.ConfigValue<Double> bonemealFailChance;
				public ForgeConfigSpec.ConfigValue<FarmingModule.Agriculture.CropsRequireWater> cropsRequireWater;
				public ForgeConfigSpec.ConfigValue<Double> cropsGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<Double> noSunlightGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<Integer> minSunlight;
				public ForgeConfigSpec.ConfigValue<Double> sugarCanesGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<Double> cactusGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<Double> cocoaBeansGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<Double> netherwartGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<Double> chorusPlantGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<Double> saplingGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<Double> stemGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<Double> berryBushGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<Double> kelpGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<Double> bambooGrowthMultiplier;
				public ForgeConfigSpec.ConfigValue<List<? extends String>> hoesCooldowns;
				public ForgeConfigSpec.ConfigValue<Boolean> disableLowTierHoes;
				public ForgeConfigSpec.ConfigValue<Integer> hoesDamageOnUseMultiplier;

				public Agriculture(ForgeConfigSpec.Builder builder) {
					builder.push(name);
					nerfedBonemeal = builder
							.comment("Makes more Bone Meal required for Crops. Valid Values are\nDISABLED: No Bone Meal changes\nSLIGHT: Makes Bone Meal grow 1-2 crop stages\nNERFED: Makes Bone Meal grow only 1 Stage")
							.defineEnum("Nerfed Bonemeal", FarmingModule.Agriculture.NerfedBonemeal.NERFED);
					bonemealFailChance = builder
							.comment("Makes Bone Meal have a chance to fail to grow crops.")
							.defineInRange("Bonemeal Fail Chance", 0d, 0.0d, 100d);
					cropsRequireWater = builder
							.comment("Set if crops require wet farmland to grow.\nValid Values:\nNO: Crops will not require water to grow\nBONEMEAL_ONLY: Crops will grow on dry farmland by only using bonemeal\nANY_CASE: Will make Crops not grow in any case when on dry farmland")
							.defineEnum("Crops Require Water", FarmingModule.Agriculture.CropsRequireWater.ANY_CASE);
					cropsGrowthMultiplier = builder
							.comment("Increases the time required for a crop to grow (e.g. at 2.0 the plant will take twice to grow).\nSetting this to 0 will prevent crops from growing naturally.\n1.0 will make crops grow like normal.")
							.defineInRange("Crops Growth Speed Mutiplier", 2.5d, 0.0d, 128d);
					noSunlightGrowthMultiplier = builder
							.comment("Increases the time required for a crop to grow when it's sky light level is below \"Min Sunlight\", (e.g. at 2.0 when the crop has a skylight below \"Min Sunlight\" will take twice to grow).\nSetting this to 0 will prevent crops from growing when sky light level is below \"Min Sunlight\".\n1.0 will make crops growth not affected by skylight.")
							.defineInRange("No Sunlight Growth Multiplier", 2.0d, 0.0d, 128d);
					minSunlight = builder
							.comment("Minimum Sky Light level required for crops to not be affected by \"No Sunlight Growth Multiplier\".")
							.defineInRange("Min Sunlight", 10, 0, 15);
					//region Growth Multipliers
					sugarCanesGrowthMultiplier = builder
							.comment("Increases the time required for Sugar Canes to grow (e.g. at 2.0 Sugar Canes will take twice to grow).\nSetting this to 0 will prevent Sugar Canes from growing naturally.\n1.0 will make Sugar Canes grow like normal.")
							.defineInRange("Sugar Canes Growth Speed Mutiplier", 2.5d, 0.0d, 128d);
					cactusGrowthMultiplier = builder
							.comment("Increases the time required for Cactuses to grow (e.g. at 2.0 Cactuses will take twice to grow).\nSetting this to 0 will prevent Cactuses from growing naturally.\n1.0 will make Cactuses grow like normal.")
							.defineInRange("Cactus Growth Speed Mutiplier", 2.5d, 0.0d, 128d);
					cocoaBeansGrowthMultiplier = builder
							.comment("Increases the time required for Cocoa Beans to grow (e.g. at 2.0 Cocoa Beans will take twice to grow).\nSetting this to 0 will prevent Cocoa Beans from growing naturally.\n1.0 will make Cocoa Beans grow like normal.")
							.defineInRange("Cocoa Beans Growth Speed Mutiplier", 3.0d, 0.0d, 128d);
					netherwartGrowthMultiplier = builder
							.comment("Increases the time required for Netherwart to grow (e.g. at 2.0 Netherwart will take twice to grow).\nSetting this to 0 will prevent Netherwart from growing naturally.\n1.0 will make Netherwart grow like normal.")
							.defineInRange("Netherwart Growth Speed Mutiplier", 3.0d, 0.0d, 128d);
					chorusPlantGrowthMultiplier = builder
							.comment("Increases the time required for Chorus Plants to grow (e.g. at 2.0 Chorus Plants will take twice to grow).\nSetting this to 0 will prevent Chorus Plants from growing naturally.\n1.0 will make Chorus Plants grow like normal.")
							.defineInRange("Chorus Plants Growth Speed Mutiplier", 3.0d, 0.0d, 128d);
					saplingGrowthMultiplier = builder
							.comment("Increases the time required for Saplings to grow (e.g. at 2.0 Saplings will take twice to grow).\nSetting this to 0 will prevent Saplings from growing naturally.\n1.0 will make Saplings grow like normal.")
							.defineInRange("Saplings Growth Speed Mutiplier", 2.0d, 0.0d, 128d);
					stemGrowthMultiplier = builder
							.comment("Increases the time required for Pumpkin & Melon to grow (e.g. at 2.0 Pumpkin & Melon will take twice to grow).\nSetting this to 0 will prevent Pumpkin & Melon from growing naturally.\n1.0 will make Pumpkin & Melon grow like normal.")
							.defineInRange("Pumpkin & Melon Growth Speed Mutiplier", 3.0d, 0.0d, 128d);
					berryBushGrowthMultiplier = builder
							.comment("Increases the time required for Berry Bushes to grow (e.g. at 2.0 Berry Bushes will take twice to grow).\nSetting this to 0 will prevent Berry Bushes from growing naturally.\n1.0 will make Berry Bushes grow like normal.")
							.defineInRange("Berry Bushes Growth Speed Mutiplier", 2.5d, 0.0d, 128d);
					kelpGrowthMultiplier = builder
							.comment("Increases the time required for Kelp to grow (e.g. at 2.0 Kelp will take twice to grow).\nSetting this to 0 will prevent Kelp from growing naturally.\n1.0 will make Kelp grow like normal.")
							.defineInRange("Saplings Growth Speed Mutiplier", 2.5d, 0.0d, 128d);
					bambooGrowthMultiplier = builder
							.comment("Increases the time required for Bamboo to grow (e.g. at 2.0 Bamboo will take twice to grow).\nSetting this to 0 will prevent Bamboo from growing naturally.\n1.0 will make Bamboo grow like normal.")
							.defineInRange("Bamboo Growth Speed Mutiplier", 2.5d, 0.0d, 128d);
					//endregion
					hoesCooldowns = builder
							.comment("A list of hoes and ticks that a hoe will go on cooldown. The format is modid:itemid,ticks. 20 ticks = 1 second. You can even use tags as #modid:tag,ticks.")
							.defineList("Hoes Cooldowns", Arrays.asList("minecraft:stone_hoe,20", "minecraft:iron_hoe,15", "minecraft:golden_hoe,4", "minecraft:diamond_hoe,10", "minecraft:netherite_hoe,6", "vulcanite:vulcanite_hoe,15"), o -> o instanceof String);
					disableLowTierHoes = builder
							.comment("When true, Wooden Hoes will not be usable and will be heavily damaged when trying to. The list of \"unusable\" hoes can be changed with datapacks by changing the iguanatweaksreborn:disabled_hoes tag")
							.define("Disable Low Tier Hoes", true);
					hoesDamageOnUseMultiplier = builder
							.comment("When an hoe is used it will lose this durability instead of 1. Set to 1 to disable")
							.defineInRange("Hoes Damage On Use Multiplier", 3, 1, 1024);
					builder.pop();
				}
			}
		}

		public static class StackSizes {
			public static String name = "Stack Sizes";

			public ForgeConfigSpec.ConfigValue<Boolean> foodStackReduction;
			public ForgeConfigSpec.ConfigValue<Double> foodStackMultiplier;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> customStackList;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> blacklist;
			public ForgeConfigSpec.ConfigValue<Boolean> blacklistAsWhitelist;
			public ForgeConfigSpec.ConfigValue<Double> itemStackMultiplier;
			public ForgeConfigSpec.ConfigValue<Boolean> blockStackReduction;
			public ForgeConfigSpec.ConfigValue<Double> blockStackMultiplier;

			public StackSizes(ForgeConfigSpec.Builder builder) {
				builder.comment("Changes in this section require a Minecraft Restart.").push(name);
				foodStackReduction = builder
						.comment("Food stack sizes will be reduced based off their hunger points restored + 1. E.g. Cooked Porkchops give 8 hunger points so their stack size will be '64 / (8 + 1) = 7' (Even foods that normally stack up to 16 will use the same formula, like Honey).\nThis is affected by Food Module's feature 'Hunger Restore Multiplier'\nNote that even soups will stack.")
						.define("Food Stack Reduction", true);
				foodStackMultiplier = builder
						.comment("All the foods max stack sizes will be multiplied by this value to increase / decrease them. In the example with the Porkchop with this set to 2.0 Cooked Porkchops will stack up to 14.")
						.defineInRange("Food Stack Multiplier", 1.1d, 0.01d, 64d);
				blacklist = builder
						.comment("Items or tags that will ignore the stack changes. This can be inverted via 'Blacklist as Whitelist'. Each entry has an item or tag. E.g. [\"minecraft:stone\", \"minecraft:cooked_porkchop\"].")
						.defineList("Items Blacklist", Arrays.asList("minecraft:rotten_flesh"), o -> o instanceof String);
				customStackList = builder
						.comment("Define custom item stack sizes, one string = one item/tag. Those items are not affected by other changes such as 'Food Stack Reduction'.\nThe format is modid:itemid,stack_size or #modid:tagid,stack_size\nE.g. 'minecraft:stone,16' will make stone stack up to 16.\nE.g. '#forge:stone,16' will make all the stone types stack up to 16.\nValues over 64 or lower than 1 will not work.")
						.defineList("Custom Stack Sizes", Lists.newArrayList(), o -> o instanceof String);
				blacklistAsWhitelist = builder
						.comment("Items Blacklist will be treated as a whitelist.")
						.define("Blacklist as Whitelist", false);
				itemStackMultiplier = builder
						.comment("Items max stack sizes (excluding blocks) will be multiplied by this value. Foods will be overridden by 'Food Stack Reduction' or 'Food Stack Multiplier' if are active. Setting to 1 will disable this feature.")
						.defineInRange("Item Stack Multiplier", 0.5d, 0.01d, 1.0d);
				blockStackReduction = builder
						.comment("Blocks max stack sizes will be reduced based off their \"Weight\".")
						.define("Block Stack Reduction", true);
				blockStackMultiplier = builder
						.comment("All the blocks max stack sizes will be multiplied by this value to increase / decrease them. This is applied after the reduction from 'Block Stack Reduction'.")
						.defineInRange("Block Stack Multiplier", 1d, 0.01d, 1d);
				builder.pop();
			}
		}

		public static class HungerHealth {
			public static String name = "Hunger & Health";

			public ForgeConfigSpec.ConfigValue<Double> foodHungerMultiplier;
			public ForgeConfigSpec.ConfigValue<Double> foodSaturationMultiplier;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> customFoodValue;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> blacklist;
			public ForgeConfigSpec.ConfigValue<Boolean> blacklistAsWhitelist;
			public ForgeConfigSpec.ConfigValue<Double> foodHealMultiplier;
			public ForgeConfigSpec.ConfigValue<Double> blockBreakExaustionMultiplier;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> debuffs;

			public HungerHealth(ForgeConfigSpec.Builder builder) {
				builder.push(name);
				foodHungerMultiplier = builder
						.comment("Food hunger restored will be multiplied by this value + 0.5. E.g. With the default value a Cooked Porkchop would heal 5 hunger instead of 8. Setting to 1 will disable this feature.\nThis requires a Minecraft Restart.")
						.defineInRange("Food Hunger Multiplier", 0.5d, 0.0d, 128d);
				foodSaturationMultiplier = builder
						.comment("Food saturation restored will be multiplied by this value. Be aware that saturation is a multiplier and not a flat value, it is used to calculate the effective saturation restored when a player eats, and this calculation includes hunger, so by reducing hunger you automatically reduce saturation too. Setting to 1 will disable this feature.\nThis requires a Minecraft Restart.")
						.defineInRange("Food Saturation Multiplier", 0.8d, 0.0d, 64d);
				customFoodValue = builder
						.comment("Define custom food values, one string = one item. Those items are not affected by other changes such as 'Food Hunger Multiplier'.\nThe format is modid:itemid,hunger,saturation. Saturation is optional\nE.g. 'minecraft:cooked_porkchop,16,1.0' will make cooked porkchops give 8 shranks of food and 16 saturation (actual saturation is calculated by 'saturation * 2 * hunger').")
						.defineList("Custom Food Hunger", Lists.newArrayList(), o -> o instanceof String);
				blacklist = builder
						.comment("Items or tags that will ignore the food multipliers. This can be inverted via 'Blacklist as Whitelist'. Each entry has an item or tag. E.g. [\"minecraft:stone\", \"minecraft:cooked_porkchop\"].\nThis requires a Minecraft Restart.")
						.defineList("Items Blacklist", Arrays.asList("minecraft:rotten_flesh"), o -> o instanceof String);
				blacklistAsWhitelist = builder
						.comment("Items Blacklist will be treated as a whitelist.\nThis requires a Minecraft Restart.")
						.define("Blacklist as Whitelist", false);
				foodHealMultiplier = builder
						.comment("When eating you'll get healed by this percentage of hunger restored. Setting to 0 will disable this feature.")
						.defineInRange("Food Heal Multiplier", 0.35d, 0.0d, 128d);
				blockBreakExaustionMultiplier = builder
						.comment("When breaking block you'll get exaustion equal to the block hardness (block hardness multipliers are taken into account too) multiplied by this value. Setting this to 0 will default to the vanilla exaustion (0.005).")
						.defineInRange("Block Break Exaustion Multiplier", 0.01d, 0.0d, 1024d);
				debuffs = builder
						.comment("A list of debuffs to apply to the player when has on low hunger / health. Each string must be 'stat,range,status_effect,amplifier', where stat MUST BE one of the following: HUNGER, HEALTH, EXPERIENCE_LEVEL; range must be a range for the statistic like it's done in commands.\n" +
								"'10' When the player has exactly ten of the specified stat.\n" +
								"'10..12' When the player has between 10 and 12 (inclusive) of the specified stat.\n" +
								"'5..' When the player has five or greater of the specified stat.\n" +
								"'..15' When the player has 15 or less of the specified stat.\n" +
								"effect must be a potion id, e.g. minecraft:weakness\n" +
								"amplifier must be the potion level starting from 0 (0 = level I)\n" +
								"Thus is called Debuffs, this can be used to give the player positive effects.")
						.defineList("Debuffs",
								Arrays.asList("HUNGER,..2,minecraft:mining_fatigue,0",
										"HUNGER,..4,minecraft:slowness,0",
										"HEALTH,..3,minecraft:slowness,0"), o -> o instanceof String);
				builder.pop();
			}
		}

		public static class Misc {
			public static String name = "Misc";

			public Misc(ForgeConfigSpec.Builder builder) {
				builder.push(name);

				builder.pop();
			}
		}
	}

	@SubscribeEvent
	public static void onModConfigEvent(final net.minecraftforge.fml.config.ModConfig.ModConfigEvent event) {
		ModConfig.load();
		insane96mcp.iguanatweaksreborn.base.Modules.sleepRespawnModule.loadConfig();
		insane96mcp.iguanatweaksreborn.base.Modules.experienceModule.loadConfig();
	}
}

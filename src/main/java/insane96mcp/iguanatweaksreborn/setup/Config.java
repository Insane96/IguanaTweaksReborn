package insane96mcp.iguanatweaksreborn.setup;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Config {
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final CommonConfig COMMON;

	static {
		final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON = specPair.getLeft();
		COMMON_SPEC = specPair.getRight();
	}

	public static class CommonConfig {

		public final Modules modules;
		public final Experience experience;
		public final Farming farming;
		public final Hardness hardness;
		public final StackSizes stackSizes;
		public final HungerHealth hungerHealth;
		public final SleepRespawn sleepRespawn;

		public CommonConfig(final ForgeConfigSpec.Builder builder) {
			modules = new Modules(builder);
			experience = new Experience(builder);
			farming = new Farming(builder);
			hardness = new Hardness(builder);
			stackSizes = new StackSizes(builder);
			hungerHealth = new HungerHealth(builder);
			sleepRespawn = new SleepRespawn(builder);
		}

		public static class Modules {
			public static String name = "Modules";
			public static String comment = "Disable entire modules with just a 'false'";

			public ForgeConfigSpec.ConfigValue<Boolean> farming;
			public ForgeConfigSpec.ConfigValue<Boolean> experience;
			public ForgeConfigSpec.ConfigValue<Boolean> hardness;
			public ForgeConfigSpec.ConfigValue<Boolean> stackSizes;
			public ForgeConfigSpec.ConfigValue<Boolean> hungerHealth;
			public ForgeConfigSpec.ConfigValue<Boolean> sleepRespawn;

			public Modules(ForgeConfigSpec.Builder builder) {
				builder.comment(comment).push(name);
				farming = builder
						.comment("Set to false to disable the Farming Module")
						.define("Farming Module", true);
				experience = builder
						.comment("Set to false to disable the Experience Module")
						.define("Experience Module", true);
				hardness = builder
						.comment("Set to false to disable the Hardness Module")
						.define("Hardness Module", true);
				stackSizes = builder
						.comment("Set to false to disable the Stack Sizes Module")
						.define("Stack Sizes Module", true);
				hungerHealth = builder
						.comment("Set to false to disable the Hunger & Health Module")
						.define("Hunger & Health Module", true);
				sleepRespawn = builder
						.comment("Set to false to disable the Sleep & Respawn Module")
						.define("Sleep & Respawn Module", true);
				builder.pop();
			}
		}

		public static class Experience {
			public static String name = "Experience";
			public static String comment = "For all your Experience nerfs needs";

			public ForgeConfigSpec.ConfigValue<Double> oreMultiplier;
			public ForgeConfigSpec.ConfigValue<Double> globalMultiplier;
			public ForgeConfigSpec.ConfigValue<Double> mobsFromSpawnersMultiplier;

			public Experience(ForgeConfigSpec.Builder builder) {
				builder.comment(comment).push(name);
				oreMultiplier = builder
						.comment("Experience dropped by blocks (Ores and Spawners) will be multiplied by this multiplier. Experience dropped by blocks are still affected by 'Global Experience Multiplier'; so if you have e.g. 'Global Experience Multiplier' at 0.5, this needs to be set to 2.0 to make blocks drop normal experience\nCan be set to 0 to make blocks drop no experience")
						.defineInRange("Experience from Blocks Multiplier", 2.5d, 0.0d, 1000d);
				globalMultiplier = builder
						.comment("Experience dropped will be multiplied by this multiplier.\nCan be set to 0 to disable experience drop from any source.")
						.defineInRange("Global Experience Multiplier", 1.0d, 0.0d, 1000d);
				mobsFromSpawnersMultiplier = builder
						.comment("Experience dropped from mobs that come from spawners will be multiplied by this multiplier. Experience dropped by mobs from spawners are still affected by 'Global Experience Multiplier'; so if you have e.g. 'Global Experience Multiplier' at 0.5, this needs to be set to 2.0 to make mobs from spawners drop normal experience\nCan be set to 0 to disable experience drop from mob that come from spawners.")
						.defineInRange("Mobs from Spawners Multiplier", 0.667d, 0.0d, 1000d);
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
							.defineList("Hoes Cooldowns", Arrays.asList("minecraft:stone_hoe,30", "minecraft:iron_hoe,22", "minecraft:golden_hoe,5", "minecraft:diamond_hoe,15", "vulcanite:vulcanite_hoe,20", "carbonado:carbonado_hoe,10"), o -> o instanceof String);
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

		public static class Hardness {
			public static String name = "Hardness";
			public static String comment = "For all your Hardness increase needs";

			public ForgeConfigSpec.ConfigValue<Double> multiplier;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionMultiplier;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> blacklist;
			public ForgeConfigSpec.ConfigValue<Boolean> backlistAsWhitelist;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> customHardness;

			public Hardness(ForgeConfigSpec.Builder builder) {
				builder.comment(comment).push(name);
				multiplier = builder
						.comment("Multiplier applied to the hardness of blocks. E.g. with this set to 3.0 blocks will take three times more time to break.")
						.defineInRange("Multiplier", 3.0d, 0.0d, 128d);
				dimensionMultiplier = builder
						.comment("A list of dimensions and their relative block hardness multiplier. Each entry has a a dimension and hardness. This overrides the global multiplier.\nE.g. [\"minecraft:overworld,2\", \"minecraft:the_nether,4\"]")
						.defineList("Dimension Multiplier", new ArrayList<String>(), o -> o instanceof String);
				blacklist = builder
						.comment("Block ids or tags that will ignore the global or dimensional multipliers. This can be inverted via 'Blacklist as Whitelist'. Each entry has a block or tag and a dimension. E.g. [\"minecraft:stone\", \"minecraft:diamond_block,minecraft:the_nether\"]")
						.defineList("Block Blacklist", new ArrayList<String>(), o -> o instanceof String);
				backlistAsWhitelist = builder
						.comment("Block Blacklist will be treated as a whitelist")
						.define("Blacklist as Whitelist", false);
				customHardness = builder
						.comment("Define custom blocks hardness, one string = one block/tag. Those blocks are not affected by the global block hardness multiplier.\nThe format is modid:blockid,hardness,dimensionid or #modid:tagid,hardness,dimensionid\nE.g. 'minecraft:stone,5.0' will make stone have 5 hardness in every dimension.\nE.g. '#forge:stone,5.0,minecraft:overworld' will make all the stone types have 5 hardness but only in the overworld.")
						.defineList("Custom Hardness", Lists.newArrayList("minecraft:coal_ore,6", "minecraft:iron_ore,9.0", "minecraft:gold_ore,10.5", "minecraft:diamond_ore,18", "minecraft:redstone_ore,12", "minecraft:lapis_ore,12", "minecraft:emerald_ore,21", "minecraft:nether_quartz_ore,6", "minecraft:obsidian,35"), o -> o instanceof String);
				builder.pop();
			}
		}

		public static class StackSizes {
			public static String name = "Stack Sizes";

			public ForgeConfigSpec.ConfigValue<Boolean> foodStackReduction;
			public ForgeConfigSpec.ConfigValue<Double> foodStackMultiplier;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> customStackList;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> blacklist;
			public ForgeConfigSpec.ConfigValue<Boolean> blacklistAsWhitelist;

			public StackSizes(ForgeConfigSpec.Builder builder) {
				builder.push(name);
				foodStackReduction = builder
						.comment("Food stack sizes will be reduced based off their hunger points restored + 1. E.g. Cooked Porkchops give 8 hunger points so their stack size will be '64 / (8 + 1) = 7' (Even foods that normally stack up to 16 will use the same formula, like Honey).\nThis is affected by Food Module's feature 'Hunger Restore Multiplier'\nNote that even soups will stack and will have eating multiple soups at once fixed.\nThis requires a Minecraft Restart.")
						.define("Food Stack Reduction", true);
				foodStackMultiplier = builder
						.comment("This multiplier will be multiplied by all the food stack sizes to increase / decrease them. In the example with the Porkchop with this set to 2.0 Cooked Porkchops will stack up to 14.\nThis requires a Minecraft Restart.")
						.defineInRange("Food Stack Multiplier", 1.1d, 0.01d, 64d);
				blacklist = builder
						.comment("Items or tags that will ignore the stack changes. This can be inverted via 'Blacklist as Whitelist'. Each entry has an item or tag. E.g. [\"minecraft:stone\", \"minecraft:cooked_porkchop\"].\nThis requires a Minecraft Restart.")
						.defineList("Items Blacklist", Arrays.asList("minecraft:rotten_flesh"), o -> o instanceof String);
				customStackList = builder
						.comment("Define custom item stack sizes, one string = one item/tag. Those items are not affected by other changes such as 'Food Stack Reduction'.\nThe format is modid:itemid,hardness or #modid:tagid,hardness\nE.g. 'minecraft:stone,16' will make stone stack up to 16.\nE.g. '#forge:stone,16' will make all the stone types stack up to 16.\nValues over 64 or lower than 1 will not work.")
						.defineList("Custom Stack Sizes", Lists.newArrayList(), o -> o instanceof String);
				blacklistAsWhitelist = builder
						.comment("Items Blacklist will be treated as a whitelist.\nThis requires a Minecraft Restart.")
						.define("Blacklist as Whitelist", false);
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

			public HungerHealth(ForgeConfigSpec.Builder builder) {
				builder.push(name);
				foodHungerMultiplier = builder
						.comment("Food hunger restored will be multiplied by this value + 0.5. E.g. With the default value a Cooked Porkchop would heal 5 hunger instead of 8. Setting to 1 will disable this feature.\nThis requires a Minecraft Restart.")
						.defineInRange("Food Hunger Multiplier", 0.5d, 0.0d, 128d);
				foodSaturationMultiplier = builder
						.comment("Food saturation restored will be multiplied by this value. Be aware that saturation is a multiplier and not a flat value, it is used to calculate the effective saturation restored when a player eats, and this calculation includes hunger, so by reducing hunger you automatically reduce saturation too. Setting to 1 will disable this feature.\nThis requires a Minecraft Restart.")
						.defineInRange("Food Stack Multiplier", 0.8d, 0.0d, 64d);
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
				builder.pop();
			}
		}

		public static class SleepRespawn {
			public static String name = "Sleep & Respawn";

			public ForgeConfigSpec.ConfigValue<Integer> hungerDepletedOnWakeUp;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> effectsOnWakeUp;
			public ForgeConfigSpec.ConfigValue<Boolean> noSleepIfHungry;

			public SleepRespawn(ForgeConfigSpec.Builder builder) {
				builder.push(name);
				hungerDepletedOnWakeUp = builder
						.comment("How much the hunger bar is depleted when you wake up in the morning. Saturation depleted is based off this value times 2. Setting to 0 will disable this feature.")
						.defineInRange("Hunger Depleted on Wake Up", 11, -20, 20);
				effectsOnWakeUp = builder
						.comment("A list of effects to apply to the player when he wakes up.\nThe format is modid:potion_id,duration_in_ticks,amplifier\nE.g. 'minecraft:slowness,240,1' will apply Slowness II for 12 seconds to the player.")
						.defineList("Effects on Wake Up", Lists.newArrayList("minecraft:slowness,300,1", "minecraft:regeneration,200,1", "minecraft:weakness,300,1"), o -> o instanceof String);
				noSleepIfHungry = builder
						.comment("If the player's hunger bar is below 'Hunger Depleted on Wake Up' he can't sleep.")
						.define("No Sleep If Hungry", true);
				builder.pop();
			}
		}
	}
}

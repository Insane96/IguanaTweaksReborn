package insane96mcp.iguanatweaksreborn.setup;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.modules.FarmingModule;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
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

		public CommonConfig(final ForgeConfigSpec.Builder builder) {
			modules = new Modules(builder);
			experience = new Experience(builder);
			farming = new Farming(builder);
			hardness = new Hardness(builder);
		}

		public static class Modules {
			public static String name = "Modules";
			public static String comment = "Disable entire modules with just a 'false'";

			public ForgeConfigSpec.ConfigValue<Boolean> farming;
			public ForgeConfigSpec.ConfigValue<Boolean> experience;
			public ForgeConfigSpec.ConfigValue<Boolean> hardness;

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
						.defineInRange("Mobs from Spawners Multiplier", 0.75d, 0.0d, 1000d);
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

				public Livestock(ForgeConfigSpec.Builder builder) {
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
							.defineInRange("Crops Growth Speed Mutiplier", 2.5d, 0.0f, 1000d);
					noSunlightGrowthMultiplier = builder
							.comment("Increases the time required for a crop to grow when it's sky light level is below \"Min Sunlight\", (e.g. at 2.0 when the crop has a skylight below \"Min Sunlight\" will take twice to grow).\nSetting this to 0 will prevent crops from growing when sky light level is below \"Min Sunlight\".\n1.0 will make crops growth not affected by skylight.")
							.defineInRange("No Sunlight Growth Multiplier", 2.0d, 0.0f, 1000d);
					minSunlight = builder
							.comment("Minimum Sky Light level required for crops to not be affected by \"No Sunlight Growth Multiplier\".")
							.defineInRange("Min Sunlight", 10, 0, 15);
					sugarCanesGrowthMultiplier = builder
							.comment("Increases the time required for Sugar Canes to grow (e.g. at 2.0 Sugar Canes will take twice to grow).\nSetting this to 0 will prevent Sugar Canes from growing naturally.\n1.0 will make Sugar Canes grow like normal.")
							.defineInRange("Sugar Canes Growth Speed Mutiplier", 2.5d, 0.0f, 1000d);
					cactusGrowthMultiplier = builder
							.comment("Increases the time required for Cactuses to grow (e.g. at 2.0 Cactuses will take twice to grow).\nSetting this to 0 will prevent Cactuses from growing naturally.\n1.0 will make Cactuses grow like normal.")
							.defineInRange("Cactus Growth Speed Mutiplier", 2.5d, 0.0f, 1000d);
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
						.defineList("Custom Hardness", Lists.newArrayList(), o -> o instanceof String);
				builder.pop();
			}
		}

		public static class HUD {
			public static String name = "HUD";
			public static String comment = "For all your interface hiding needs";

			public ForgeConfigSpec.ConfigValue<Double> multiplier;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionMultiplier;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> blacklist;
			public ForgeConfigSpec.ConfigValue<Boolean> backlistAsWhitelist;
			public ForgeConfigSpec.ConfigValue<List<? extends String>> customHardness;

			public HUD(ForgeConfigSpec.Builder builder) {
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
						.defineList("Custom Hardness", Lists.newArrayList(), o -> o instanceof String);
				builder.pop();
			}
		}

	}
}

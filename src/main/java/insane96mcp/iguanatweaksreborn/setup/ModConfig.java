package insane96mcp.iguanatweaksreborn.setup;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class ModConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static ForgeConfigSpec SPEC;

	public static void init(Path file) {
		final CommentedFileConfig configData = CommentedFileConfig.builder(file)
				.sync()
				.autosave()
				.writingMode(WritingMode.REPLACE)
				.build();

		configData.load();
		SPEC.setConfig(configData);
	}

	public static class Modules {
		public static String name = "Modules";

		public static ForgeConfigSpec.ConfigValue<Boolean> farming;
		public static ForgeConfigSpec.ConfigValue<Boolean> experience;

		public static void init() {
			BUILDER.push(name).comment("Disable entire modules");
			farming = BUILDER
					.comment("Set to false to disable the Farming Module")
					.define("Farming Module", true);
			experience = BUILDER
					.comment("Set to false to disable the Experience Module")
					.define("Experience Module", true);
			BUILDER.pop();

		}
	}

	public static class Experience {
		public static String name = "Experience";

		public static ForgeConfigSpec.ConfigValue<Double> oreMultiplier;
		public static ForgeConfigSpec.ConfigValue<Double> globalMultiplier;
		public static ForgeConfigSpec.ConfigValue<Double> mobsFromSpawnersMultiplier;

		public static void init() {
			BUILDER.push(name).comment("For all your Experience nerfs needs");
			oreMultiplier = BUILDER
					.comment("Experience dropped by blocks (Ores and Spawners) will be multiplied by this multiplier. Experience dropped by blocks are still affected by 'Global Experience Multiplier'; so if you have e.g. 'Global Experience Multiplier' at 0.5, this needs to be set to 2.0 to make blocks drop normal experience\nCan be set to 0 to make blocks drop no experience")
					.defineInRange("Experience from Blocks Multiplier", 2.5d, 0.0d, 1000d);
			globalMultiplier = BUILDER
					.comment("Experience dropped will be multiplied by this multiplier.\nCan be set to 0 to disable experience drop from any source.")
					.defineInRange("Global Experience Multiplier", 1.0d, 0.0d, 1000d);
			mobsFromSpawnersMultiplier = BUILDER
					.comment("Experience dropped from mobs that come from spawners will be multiplied by this multiplier. Experience dropped by mobs from spawners are still affected by 'Global Experience Multiplier'; so if you have e.g. 'Global Experience Multiplier' at 0.5, this needs to be set to 2.0 to make mobs from spawners drop normal experience\nCan be set to 0 to disable experience drop from mob that come from spawners.")
					.defineInRange("Mobs from Spawners Multiplier", 0.75d, 0.0d, 1000d);
			BUILDER.pop();
		}
	}

	public static class Farming {
		public static String name = "Farming";

		public enum NerfedBonemeal {
			DISABLED,
			SLIGHT,
			NERFED
		}

		public static ForgeConfigSpec.ConfigValue<NerfedBonemeal> nerfedBonemeal;
		public static ForgeConfigSpec.ConfigValue<Boolean> cropsRequireWater;
		public static ForgeConfigSpec.ConfigValue<Double> cropsGrowthMultiplier;
		public static ForgeConfigSpec.ConfigValue<Double> noSunlightGrowthMultiplier;
		public static ForgeConfigSpec.ConfigValue<Integer> minSunlight;
		public static ForgeConfigSpec.ConfigValue<Double> sugarCanesGrowthMultiplier;
		public static ForgeConfigSpec.ConfigValue<Double> cactusGrowthMultiplier;

		public static void init() {
			BUILDER.push(name).comment("For all your nerfed farming needs");
			nerfedBonemeal = BUILDER
					.comment("Makes more Bone Meal required for Crops. Valid Values are\nDISABLED: No Bone Meal changes\nSLIGHT: Makes Bone Meal grow 1-2 crop stages\nNERFED: Makes Bone Meal grow only 1 Stage")
					.define("Nerfed Bonemeal", NerfedBonemeal.NERFED);
			cropsRequireWater = BUILDER
					.comment("Crops will no longer grow if Farmland is not Wet.")
					.define("Crops Require Water", true);
			cropsGrowthMultiplier = BUILDER
					.comment("Increases the time required for a crop to grow (e.g. at 2.0 the plant will take twice to grow).\nSetting this to 0 will prevent crops from growing naturally.\n1.0 will make crops grow like normal.")
					.defineInRange("Crops Growth Speed Mutiplier", 2.5d, 0.0f, 1000d);
			noSunlightGrowthMultiplier = BUILDER
					.comment("Increases the time required for a crop to grow when it's sky light level is below \"Min Sunlight\", (e.g. at 2.0 when the crop has a skylight below \"Min Sunlight\" will take twice to grow).\nSetting this to 0 will prevent crops from growing when sky light level is below \"Min Sunlight\".\n1.0 will make crops growth not affected by skylight.")
					.defineInRange("No Sunlight Growth Multiplier", 2.0d, 0.0f, 1000d);
			minSunlight = BUILDER
					.comment("Minimum Sky Light level required for crops to not be affected by \"No Sunlight Growth Multiplier\".")
					.defineInRange("Min Sunlight", 10, 0, 15);
			sugarCanesGrowthMultiplier = BUILDER
					.comment("Increases the time required for Sugar Canes to grow (e.g. at 2.0 Sugar Canes will take twice to grow).\nSetting this to 0 will prevent Sugar Canes from growing naturally.\n1.0 will make Sugar Canes grow like normal.")
					.defineInRange("Sugar Canes Growth Speed Mutiplier", 2.5d, 0.0f, 1000d);
			cactusGrowthMultiplier = BUILDER
					.comment("Increases the time required for Cactuses to grow (e.g. at 2.0 Cactuses will take twice to grow).\nSetting this to 0 will prevent Cactuses from growing naturally.\n1.0 will make Cactuses grow like normal.")
					.defineInRange("Cactus Growth Speed Mutiplier", 2.5d, 0.0f, 1000d);
			BUILDER.pop();

		}
	}

	static {
		Modules.init();
		Farming.init();
		Experience.init();

		SPEC = BUILDER.build();
	}
}

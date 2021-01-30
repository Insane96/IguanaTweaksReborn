package insane96mcp.iguanatweaksreborn.setup;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.base.Modules;
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

		public CommonConfig(final ForgeConfigSpec.Builder builder) {
			insane96mcp.iguanatweaksreborn.base.Modules.init();

			modules = new Modules(builder);
			farming = new Farming(builder);
		}

		public static class Modules {
			public static String name = "Modules";
			public static String comment = "Disable entire modules with just a 'false'";

			public ForgeConfigSpec.ConfigValue<Boolean> farming;
			public ForgeConfigSpec.ConfigValue<Boolean> stackSizes;
			public ForgeConfigSpec.ConfigValue<Boolean> misc;

			public Modules(ForgeConfigSpec.Builder builder) {
				builder.comment(comment).push(name);
				farming = builder
						.comment("Set to false to disable the Farming Module")
						.define("Farming Module", true);
				stackSizes = builder
						.comment("Set to false to disable the Stack Sizes Module")
						.define("Stack Sizes Module", true);
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

	}

	@SubscribeEvent
	public static void onModConfigEvent(final net.minecraftforge.fml.config.ModConfig.ModConfigEvent event) {
		ModConfig.load();
		Modules.sleepRespawnModule.loadConfig();
		Modules.experienceModule.loadConfig();
		Modules.miningModule.loadConfig();
		Modules.hungerHealthModule.loadConfig();
		Modules.stackSizeModule.loadConfig();
	}
}

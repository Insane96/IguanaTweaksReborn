package insane96mcp.iguanatweaksreborn.module.farming.feature;

import insane96mcp.iguanatweaksreborn.module.farming.Farming;
import insane96mcp.iguanatweaksreborn.module.farming.utils.PlantGrowthModifier;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

@Label(name = "Crops Growth", description = "Slower Crops growing based off various factors")
public class CropsGrowth extends Feature {

	//TODO Wrong Biome Multiplier
	private final ForgeConfigSpec.ConfigValue<CropsRequireWater> cropsRequireWaterConfig;
	private final ForgeConfigSpec.ConfigValue<Double> cropsGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> noSunlightGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> nightTimeGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> minSunlightConfig;

	public CropsRequireWater cropsRequireWater = CropsRequireWater.ANY_CASE;
	public double cropsGrowthMultiplier = 2.5d;
	public double noSunLightGrowthMultiplier = 2.0d;
	public double nightTimeGrowthMultiplier = 1d;
	public int minSunlight = 10;

	public ArrayList<PlantGrowthModifier> plantGrowthModifiers = new ArrayList<>();

	public CropsGrowth(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		cropsRequireWaterConfig = Config.builder
				.comment("Set if crops require wet farmland to grow.\nValid Values:\nNO: Crops will not require water to grow\nBONEMEAL_ONLY: Crops will grow on dry farmland by only using bonemeal\nANY_CASE: Will make Crops not grow in any case when on dry farmland")
				.defineEnum("Crops Require Water", cropsRequireWater);
		cropsGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for a crop (stems NOT included) to grow (e.g. at 2.0 the crop will take twice to grow).\nSetting this to 0 will prevent crops from growing naturally.\n1.0 will make crops grow like normal.")
				.defineInRange("Crops Growth Speed Multiplier", cropsGrowthMultiplier, 0.0d, 128d);
		noSunlightGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for a crop to grow when it's sky light level is below \"Min Sunlight\", (e.g. at 2.0 when the crop has a skylight below \"Min Sunlight\" will take twice to grow).\n" +
						"Setting this to 0 will prevent crops from growing when sky light level is below \"Min Sunlight\".\n" +
						"1.0 will make crops growth not affected by skylight.")
				.defineInRange("No Sunlight Growth Multiplier", noSunLightGrowthMultiplier, 0.0d, 128d);
		nightTimeGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for a crop to grow when it's night time.\n" +
						"Setting this to 0 will prevent crops from growing when it's night time.\n" +
						"1.0 will make crops growth not affected by night.")
				.defineInRange("Night Time Growth Multiplier", nightTimeGrowthMultiplier, 0.0d, 128d);
		minSunlightConfig = Config.builder
				.comment("Minimum Sky Light level required for crops to not be affected by \"No Sunlight Growth Multiplier\".")
				.defineInRange("Min Sunlight", minSunlight, 0, 15);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.cropsRequireWater = this.cropsRequireWaterConfig.get();
		this.cropsGrowthMultiplier = this.cropsGrowthMultiplierConfig.get();
		this.noSunLightGrowthMultiplier = this.noSunlightGrowthMultiplierConfig.get();
		this.nightTimeGrowthMultiplier = this.nightTimeGrowthMultiplierConfig.get();
		this.minSunlight = this.minSunlightConfig.get();
		if (plantGrowthModifiers.isEmpty()) {
			for (Block block : ForgeRegistries.BLOCKS.getValues()) {
				if (!(block instanceof CropBlock))
					continue;
				PlantGrowthModifier plantGrowthModifier = new PlantGrowthModifier(IdTagMatcher.Type.ID, block.getRegistryName())
						.growthMultiplier(this.cropsGrowthMultiplier)
						.noSunlightGrowthMultiplier(this.noSunLightGrowthMultiplier)
						.minSunlightRequired(this.minSunlight)
						.nightTimeGrowthMultiplier(this.nightTimeGrowthMultiplier);
				plantGrowthModifiers.add(plantGrowthModifier);
			}
		}
	}

	@SubscribeEvent
	public void cropsRequireWater(BlockEvent.CropGrowEvent.Pre event) {
		if (!this.isEnabled())
			return;
		if (event.getResult().equals(Event.Result.DENY))
			return;
		if (this.cropsRequireWater.equals(CropsRequireWater.NO))
			return;
		if (!Farming.isAffectedByFarmlandState(event.getWorld(), event.getPos()))
			return;
		if (!Farming.isCropOnWetFarmland(event.getWorld(), event.getPos())) {
			event.setResult(Event.Result.DENY);
		}
	}

	/**
	 * Handles Crop Growth Speed Multiplier, No Sunlight Growth multiplier and Night Time Growth Multiplier
	 */
	@SubscribeEvent
	public void cropsGrowthSpeedMultiplier(BlockEvent.CropGrowEvent.Pre event) {
		if (!this.isEnabled())
			return;
		if (event.getResult().equals(Event.Result.DENY))
			return;
		Level level = (Level) event.getWorld();
		double multiplier = 1d;
		for (PlantGrowthModifier plantGrowthModifier : plantGrowthModifiers) {
			multiplier = plantGrowthModifier.getMultiplier(event.getState().getBlock(), level, event.getPos());
			if (multiplier != -1d)
				break;
		}
		if (multiplier == 0d) {
			event.setResult(Event.Result.DENY);
			return;
		}
		if (multiplier == 1d || multiplier == -1d)
			return;
		double chance = 1d / multiplier;
		if (level.getRandom().nextDouble() > chance)
			event.setResult(Event.Result.DENY);
	}

	public enum CropsRequireWater {
		NO,
		BONEMEAL_ONLY,
		ANY_CASE
	}
}
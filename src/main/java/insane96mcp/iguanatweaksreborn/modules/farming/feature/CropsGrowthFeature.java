package insane96mcp.iguanatweaksreborn.modules.farming.feature;

import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.modules.farming.FarmingModule;
import insane96mcp.iguanatweaksreborn.modules.farming.classutils.CropsRequireWater;
import insane96mcp.iguanatweaksreborn.setup.Config;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Label(name = "Crops Growth", description = "Slower Crops growing based off various factors")
public class CropsGrowthFeature extends ITFeature {

	private final ForgeConfigSpec.ConfigValue<CropsRequireWater> cropsRequireWaterConfig;
	private final ForgeConfigSpec.ConfigValue<Double> cropsGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Double> noSunlightGrowthMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<Integer> minSunlightConfig;

	public CropsRequireWater cropsRequireWater = CropsRequireWater.ANY_CASE;
	public double cropsGrowthMultiplier = 2.5d;
	public double noSunLightGrowthMultiplier = 2.0d;
	public int minSunlight = 10;

	public CropsGrowthFeature(ITModule module) {
		super(module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		cropsRequireWaterConfig = Config.builder
				.comment("Set if crops require wet farmland to grow.\nValid Values:\nNO: Crops will not require water to grow\nBONEMEAL_ONLY: Crops will grow on dry farmland by only using bonemeal\nANY_CASE: Will make Crops not grow in any case when on dry farmland")
				.defineEnum("Crops Require Water", cropsRequireWater);
		cropsGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for a crop (stems included) to grow (e.g. at 2.0 the crop will take twice to grow).\nSetting this to 0 will prevent crops from growing naturally.\n1.0 will make crops grow like normal.")
				.defineInRange("Crops Growth Speed Multiplier", cropsGrowthMultiplier, 0.0d, 128d);
		noSunlightGrowthMultiplierConfig = Config.builder
				.comment("Increases the time required for a crop to grow when it's sky light level is below \"Min Sunlight\", (e.g. at 2.0 when the crop has a skylight below \"Min Sunlight\" will take twice to grow).\nSetting this to 0 will prevent crops from growing when sky light level is below \"Min Sunlight\".\n1.0 will make crops growth not affected by skylight.")
				.defineInRange("No Sunlight Growth Multiplier", noSunLightGrowthMultiplier, 0.0d, 128d);
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
		this.minSunlight = this.minSunlightConfig.get();
	}

	@SubscribeEvent
	public void cropsRequireWater(BlockEvent.CropGrowEvent.Pre event) {
		if (!this.isEnabled())
			return;
		if (this.cropsRequireWater.equals(CropsRequireWater.NO))
			return;
		if (!FarmingModule.isAffectedByFarmlandState(event.getWorld(), event.getPos()))
			return;
		if (!FarmingModule.isCropOnWetFarmland(event.getWorld(), event.getPos()))
			event.setResult(Event.Result.DENY);
	}

	/**
	 * Handles Crop Growth Speed Multiplier and NoSunlight Growth multiplier
	 */
	@SubscribeEvent
	public void cropsGrowthSpeedMultiplier(BlockEvent.CropGrowEvent.Post event) {
		if (!this.isEnabled())
			return;
		if (this.cropsGrowthMultiplier == 1.0d && this.noSunLightGrowthMultiplier == 1.0d)
			return;
		IWorld world = event.getWorld();
		BlockState state = event.getOriginalState();
		if (!(state.getBlock() instanceof CropsBlock))
			return;
		double chance;
		if (this.cropsGrowthMultiplier == 0.0d)
			chance = -1d;
		else
			chance = 1d / this.cropsGrowthMultiplier;
		int skyLight = world.getLightFor(LightType.SKY, event.getPos());
		if (skyLight < this.minSunlight)
			if (this.noSunLightGrowthMultiplier == 0.0d)
				chance = -1d;
			else
				chance *= 1d / this.noSunLightGrowthMultiplier;
		if (event.getWorld().getRandom().nextDouble() > chance)
			world.setBlockState(event.getPos(), state, 2);
	}
}

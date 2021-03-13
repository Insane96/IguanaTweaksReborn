package insane96mcp.iguanatweaksreborn.modules.farming;

import insane96mcp.iguanatweaksreborn.base.ITModule;
import insane96mcp.iguanatweaksreborn.base.Label;
import insane96mcp.iguanatweaksreborn.modules.farming.feature.*;
import insane96mcp.iguanatweaksreborn.setup.Config;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

@Label(name = "Farming")
public class FarmingModule extends ITModule {

	public LivestockSlowdownFeature liveStockSlowdownFeature;
	public NerfedBonemealFeature nerfedBonemealFeature;
	public CropsGrowthFeature cropsGrowthFeature;
	public PlantsGrowthFeature plantsGrowthFeature;
	public HoesNerfsFeature hoesNerfsFeature;
	public HarderCrops harderCrops;

	public FarmingModule() {
		pushConfig();
		liveStockSlowdownFeature = new LivestockSlowdownFeature(this);
		nerfedBonemealFeature = new NerfedBonemealFeature(this);
		cropsGrowthFeature = new CropsGrowthFeature(this);
		plantsGrowthFeature = new PlantsGrowthFeature(this);
		hoesNerfsFeature = new HoesNerfsFeature(this);
		harderCrops = new HarderCrops(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		liveStockSlowdownFeature.loadConfig();
		nerfedBonemealFeature.loadConfig();
		cropsGrowthFeature.loadConfig();
		plantsGrowthFeature.loadConfig();
		hoesNerfsFeature.loadConfig();
		harderCrops.loadConfig();
	}

	/**
	 * @param world
	 * @param cropPos
	 * @return true if the block is affected by the block below
	 */
	public static boolean isAffectedByFarmlandState(IWorld world, BlockPos cropPos) {
		BlockState state = world.getBlockState(cropPos);
		Block block = state.getBlock();
		return block instanceof CropsBlock || block instanceof StemBlock;
	}


	/**
	 * @param world
	 * @param cropPos
	 * @return true if the block is on wet farmland
	 */
	public static boolean isCropOnWetFarmland(IWorld world, BlockPos cropPos) {
		BlockState sustainState = world.getBlockState(cropPos.down());
		if (!(sustainState.getBlock() instanceof FarmlandBlock))
			return false;
		int moisture = sustainState.get(FarmlandBlock.MOISTURE);
		return moisture >= 7;
	}
}

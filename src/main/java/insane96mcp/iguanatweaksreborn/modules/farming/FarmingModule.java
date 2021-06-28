package insane96mcp.iguanatweaksreborn.modules.farming;

import insane96mcp.iguanatweaksreborn.modules.farming.feature.*;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

@Label(name = "Farming")
public class FarmingModule extends Module {

	public LivestockSlowdownFeature liveStockSlowdown;
	public NerfedBonemealFeature nerfedBonemeal;
	public CropsGrowthFeature cropsGrowth;
	public PlantsGrowthFeature plantsGrowth;
	public HoesNerfsFeature hoesNerfs;
	public HarderCropsFeature harderCrops;

	public FarmingModule() {
		super(Config.builder);
		pushConfig(Config.builder);
		liveStockSlowdown = new LivestockSlowdownFeature(this);
		nerfedBonemeal = new NerfedBonemealFeature(this);
		cropsGrowth = new CropsGrowthFeature(this);
		plantsGrowth = new PlantsGrowthFeature(this);
		hoesNerfs = new HoesNerfsFeature(this);
		harderCrops = new HarderCropsFeature(this);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		liveStockSlowdown.loadConfig();
		nerfedBonemeal.loadConfig();
		cropsGrowth.loadConfig();
		plantsGrowth.loadConfig();
		hoesNerfs.loadConfig();
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

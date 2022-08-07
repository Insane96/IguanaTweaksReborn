package insane96mcp.iguanatweaksreborn.module.farming;

import insane96mcp.iguanatweaksreborn.module.farming.feature.*;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;

@Label(name = "Farming")
public class Farming extends Module {

	public LivestockSlowdown liveStockSlowdown;
	public NerfedBonemeal nerfedBonemeal;
	public CropsGrowth cropsGrowth;
	public PlantsGrowth plantsGrowth;
	public HoesNerfs hoesNerfs;
	public HarderCrops harderCrops;

	public Farming() {
		super(Config.builder);
		pushConfig(Config.builder);
		liveStockSlowdown = new LivestockSlowdown(this);
		nerfedBonemeal = new NerfedBonemeal(this);
		cropsGrowth = new CropsGrowth(this);
		plantsGrowth = new PlantsGrowth(this);
		hoesNerfs = new HoesNerfs(this);
		harderCrops = new HarderCrops(this);
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
	 * @return true if the block is affected by the block below
	 */
	public static boolean isAffectedByFarmland(LevelAccessor levelAccessor, BlockPos cropPos) {
		BlockState state = levelAccessor.getBlockState(cropPos);
		Block block = state.getBlock();
		return block instanceof CropBlock || block instanceof StemBlock;
	}

	/**
	 * @return true if the block is on wet farmland
	 */
	public static boolean isCropOnWetFarmland(LevelAccessor levelAccessor, BlockPos cropPos) {
		BlockState sustainState = levelAccessor.getBlockState(cropPos.below());
		if (!(sustainState.getBlock() instanceof FarmBlock))
			return false;
		int moisture = sustainState.getValue(FarmBlock.MOISTURE);
		return moisture >= 7;
	}


	/**
	 * @return true if the block is on farmland
	 */
	public static boolean isCropOnFarmland(LevelAccessor levelAccessor, BlockPos cropPos) {
		BlockState sustainState = levelAccessor.getBlockState(cropPos.below());
		return sustainState.getBlock() instanceof FarmBlock;
	}
}
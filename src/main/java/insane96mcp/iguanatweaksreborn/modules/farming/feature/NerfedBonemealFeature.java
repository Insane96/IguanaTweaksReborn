package insane96mcp.iguanatweaksreborn.modules.farming.feature;

import insane96mcp.iguanatweaksreborn.base.Modules;
import insane96mcp.iguanatweaksreborn.modules.farming.FarmingModule;
import insane96mcp.iguanatweaksreborn.modules.farming.classutils.CropsRequireWater;
import insane96mcp.iguanatweaksreborn.modules.farming.classutils.NerfedBonemeal;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.utils.RandomHelper;
import net.minecraft.block.BeetrootBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.StemBlock;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collections;

@Label(name = "Nerfed Bonemeal", description = "Bonemeal is no longer so OP")
public class NerfedBonemealFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<NerfedBonemeal> nerfedBonemealConfig;
	private final ForgeConfigSpec.ConfigValue<Double> bonemealFailChanceConfig;

	public NerfedBonemeal nerfedBonemeal = NerfedBonemeal.NERFED;
	public double bonemealFailChance = 0d;

	public NerfedBonemealFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		nerfedBonemealConfig = Config.builder
				.comment("Makes more Bone Meal required for Crops. Valid Values are\nDISABLED: No Bone Meal changes\nSLIGHT: Makes Bone Meal grow 1-2 crop stages\nNERFED: Makes Bone Meal grow only 1 Stage")
				.defineEnum("Nerfed Bonemeal", nerfedBonemeal);
		bonemealFailChanceConfig = Config.builder
				.comment("Makes Bone Meal have a chance to fail to grow crops. 0 to disable, 100 to disable bonemeal.")
				.defineInRange("Bonemeal Fail Chance", bonemealFailChance, 0.0d, 100d);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.nerfedBonemeal = this.nerfedBonemealConfig.get();
		this.bonemealFailChance = this.bonemealFailChanceConfig.get();
	}

	/**
	 * Handles part of crops require water too
	 */
	@SubscribeEvent
	public void nerfBonemeal(BonemealEvent event) {
		if (event.isCanceled() || event.getResult() == Event.Result.DENY)
			return;
		if (!this.isEnabled())
			return;
		if (event.getWorld().isRemote)
			return;
		//If farmland is dry and cropsRequireWater is set to ANY_CASE then cancel the event
		if (FarmingModule.isAffectedByFarmlandState(event.getWorld(), event.getPos()) && !FarmingModule.isCropOnWetFarmland(event.getWorld(), event.getPos()) && Modules.farmingModule.cropsGrowthFeature.cropsRequireWater.equals(CropsRequireWater.ANY_CASE)) {
			event.setCanceled(true);
			return;
		}
		if (this.nerfedBonemeal.equals(NerfedBonemeal.DISABLED))
			return;
		BlockState state = event.getWorld().getBlockState(event.getPos());
		if (state.getBlock() instanceof CropsBlock) {
			boolean isBeetroot = state.getBlock() instanceof BeetrootBlock;
			int age = 0;
			int maxAge = Collections.max(CropsBlock.AGE.getAllowedValues());
			if (isBeetroot) {
				age = state.get(BeetrootBlock.BEETROOT_AGE);
				maxAge = Collections.max(BeetrootBlock.BEETROOT_AGE.getAllowedValues());
			}
			else
				age = state.get(CropsBlock.AGE);
			if (age == maxAge)
				return;

			if (RandomHelper.getDouble(event.getWorld().getRandom(), 0d, 100d) < this.bonemealFailChance) {
				event.setResult(Event.Result.ALLOW);
				return;
			}
			else if (this.nerfedBonemeal.equals(NerfedBonemeal.SLIGHT))
				age += RandomHelper.getInt(event.getWorld().getRandom(), 1, 2);
			else if (this.nerfedBonemeal.equals(NerfedBonemeal.NERFED))
				age++;
			if (age > maxAge)
				age = maxAge;
			if (isBeetroot) {
				state = state.with(BeetrootBlock.BEETROOT_AGE, age);
			}
			else
				state = state.with(CropsBlock.AGE, age);
		}
		else if (state.getBlock() instanceof StemBlock) {
			int age = state.get(StemBlock.AGE);
			int maxAge = Collections.max(StemBlock.AGE.getAllowedValues());
			if (age == maxAge)
				return;
			if (RandomHelper.getDouble(event.getWorld().getRandom(), 0d, 100d) < this.bonemealFailChance) {
				event.setResult(Event.Result.ALLOW);
				return;
			}
			else if (this.nerfedBonemeal.equals(NerfedBonemeal.SLIGHT))
				age += RandomHelper.getInt(event.getWorld().getRandom(), 1, 2);
			else if (this.nerfedBonemeal.equals(NerfedBonemeal.NERFED))
				age++;
			if (age > maxAge)
				age = maxAge;
			state = state.with(StemBlock.AGE, age);
		}
		else
			return;
		event.getWorld().setBlockState(event.getPos(), state, 3);
		event.setResult(Event.Result.ALLOW);
	}
}

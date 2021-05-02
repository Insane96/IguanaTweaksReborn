package insane96mcp.iguanatweaksreborn.modules.farming.feature;

import insane96mcp.iguanatweaksreborn.base.Modules;
import insane96mcp.iguanatweaksreborn.common.classutils.IdTagMatcher;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Label(name = "Nerfed Bonemeal", description = "Bonemeal is no longer so OP")
public class NerfedBonemealFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<NerfedBonemeal> nerfedBonemealConfig;
	private final ForgeConfigSpec.ConfigValue<Double> bonemealFailChanceConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> itemBlacklistConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> blockBlacklsitConfig;

	private static final List<String> blockBlacklsitDefault = Arrays.asList("#iguanatweaksreborn:bonemeal_unaffected_crops");

	public NerfedBonemeal nerfedBonemeal = NerfedBonemeal.NERFED;
	public double bonemealFailChance = 0d;
	public List<IdTagMatcher> itemBlacklist;
	public List<IdTagMatcher> blockBlacklist;

	public NerfedBonemealFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		nerfedBonemealConfig = Config.builder
				.comment("Makes more Bone Meal required for Crops. Valid Values are\nDISABLED: No Bone Meal changes\nSLIGHT: Makes Bone Meal grow 1-2 crop stages\nNERFED: Makes Bone Meal grow only 1 Stage")
				.defineEnum("Nerfed Bonemeal", nerfedBonemeal);
		bonemealFailChanceConfig = Config.builder
				.comment("Makes Bone Meal have a chance to fail to grow crops. 0 to disable, 100 to disable bonemeal.")
				.defineInRange("Bonemeal Fail Chance", bonemealFailChance, 0.0d, 100d);
		itemBlacklistConfig = Config.builder
				.comment("Items or item tags that will ignore the feature. Can be used with any item that inherits the properties of vanilla bonemeal (and it's properly implemented).\n" +
						"Each entry has an item or tag. The format is modid:item_id or #modid:item_tag.")
				.defineList("Item Blacklist", new ArrayList<>(), o -> o instanceof String);
		blockBlacklsitConfig = Config.builder
				.comment("Blocks or block tags that will not be affected by the bonemeal nerf. Can be used with any item that inherits the properties of vanilla bonemeal (and it's properly implemented).\n" +
						"Each entry has an item or tag. The format is modid:item_id or #modid:item_tag.")
				.defineList("Block Blacklist", new ArrayList<>(), o -> o instanceof String);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.nerfedBonemeal = this.nerfedBonemealConfig.get();
		this.bonemealFailChance = this.bonemealFailChanceConfig.get();
		this.itemBlacklist = IdTagMatcher.parseStringList(this.itemBlacklistConfig.get());
		this.blockBlacklist = IdTagMatcher.parseStringList(this.itemBlacklistConfig.get());
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
		for (IdTagMatcher idTagMatcher : itemBlacklist) {
			if (idTagMatcher.matchesItem(event.getStack().getItem()))
				return;
		}
		for (IdTagMatcher idTagMatcher : blockBlacklist) {
			if (idTagMatcher.matchesBlock(event.getBlock().getBlock()))
				return;
		}
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

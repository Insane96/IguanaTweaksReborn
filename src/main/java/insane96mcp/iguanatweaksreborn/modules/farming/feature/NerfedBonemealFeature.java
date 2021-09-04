package insane96mcp.iguanatweaksreborn.modules.farming.feature;

import insane96mcp.iguanatweaksreborn.modules.Modules;
import insane96mcp.iguanatweaksreborn.modules.farming.FarmingModule;
import insane96mcp.iguanatweaksreborn.modules.farming.classutils.CropsRequireWater;
import insane96mcp.iguanatweaksreborn.modules.farming.classutils.NerfedBonemeal;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.config.BlacklistConfig;
import insane96mcp.insanelib.utils.IdTagMatcher;
import insane96mcp.insanelib.utils.RandomHelper;
import net.minecraft.block.BeetrootBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Label(name = "Nerfed Bonemeal", description = "Bonemeal is no longer so OP")
public class NerfedBonemealFeature extends Feature {

	private final ForgeConfigSpec.ConfigValue<NerfedBonemeal> nerfedBonemealConfig;
	private final ForgeConfigSpec.ConfigValue<Double> bonemealFailChanceConfig;
	private final BlacklistConfig itemBlacklistConfig;
	private final BlacklistConfig blockBlacklistConfig;

	private static final List<String> blockBlacklistDefault = Arrays.asList("supplementaries:flax");

	public NerfedBonemeal nerfedBonemeal = NerfedBonemeal.NERFED;
	public double bonemealFailChance = 0d;
	public List<IdTagMatcher> itemBlacklist;
	public boolean itemBlacklistAsWhitelist = false;
	public List<IdTagMatcher> blockBlacklist;
	public boolean blockBlacklistAsWhitelist = false;

	public NerfedBonemealFeature(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		nerfedBonemealConfig = Config.builder
				.comment("Makes more Bone Meal required for Crops. Valid Values are\nDISABLED: No Bone Meal changes\nSLIGHT: Makes Bone Meal grow 1-2 crop stages\nNERFED: Makes Bone Meal grow only 1 Stage")
				.defineEnum("Nerfed Bonemeal", nerfedBonemeal);
		bonemealFailChanceConfig = Config.builder
				.comment("Makes Bone Meal have a chance to fail to grow crops. 0 to disable, 1 to disable bonemeal.")
				.defineInRange("Bonemeal Fail Chance", bonemealFailChance, 0d, 1d);
		itemBlacklistConfig = new BlacklistConfig(Config.builder, "Item Blacklist",
				"Items or item tags that will ignore the feature. Can be used with any item that inherits the properties of vanilla bonemeal (and it's properly implemented).\n" +
				"Each entry has an item or tag. The format is modid:item_id or #modid:item_tag.", Collections.emptyList(), this.itemBlacklistAsWhitelist);
		blockBlacklistConfig = new BlacklistConfig(Config.builder, "Block Blacklist",
				"Blocks or block tags that will not be affected by the bonemeal nerf.\n" +
				"Each entry has a block or a block tag. The format is modid:block_id or #modid:block_tag.", blockBlacklistDefault, this.blockBlacklistAsWhitelist);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		this.nerfedBonemeal = this.nerfedBonemealConfig.get();
		this.bonemealFailChance = this.bonemealFailChanceConfig.get();
		this.itemBlacklist = IdTagMatcher.parseStringList(this.itemBlacklistConfig.listConfig.get());
		this.itemBlacklistAsWhitelist = this.itemBlacklistConfig.listAsWhitelistConfig.get();
		this.blockBlacklist = IdTagMatcher.parseStringList(this.blockBlacklistConfig.listConfig.get());
		this.blockBlacklistAsWhitelist = this.blockBlacklistConfig.listAsWhitelistConfig.get();
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
		BonemealResult result = applyBonemeal(event.getWorld(), event.getStack(), event.getBlock(), event.getPos());
		if (result == BonemealResult.ALLOW)
			event.setResult(Event.Result.ALLOW);
		else if (result == BonemealResult.CANCEL)
			event.setCanceled(true);
	}

	public enum BonemealResult {
		NONE,
		CANCEL,
		ALLOW
	}

	public BonemealResult applyBonemeal(World world, ItemStack stack, BlockState state, BlockPos pos) {
		//Check for item black/whitelist
		boolean isInWhitelist = false;
		boolean isInBlacklist = false;
		for (IdTagMatcher blacklistEntry : this.itemBlacklist) {
			if (blacklistEntry.matchesItem(stack.getItem())) {
				if (!this.itemBlacklistAsWhitelist)
					isInBlacklist = true;
				else
					isInWhitelist = true;
				break;
			}
		}
		if (isInBlacklist || (!isInWhitelist && this.itemBlacklistAsWhitelist))
			return BonemealResult.NONE;

		//Check for block black/whitelist
		isInWhitelist = false;
		isInBlacklist = false;
		for (IdTagMatcher blacklistEntry : this.blockBlacklist) {
			if (blacklistEntry.matchesBlock(state.getBlock())) {
				if (!this.itemBlacklistAsWhitelist)
					isInBlacklist = true;
				else
					isInWhitelist = true;
				break;
			}
		}
		if (isInBlacklist || (!isInWhitelist && this.blockBlacklistAsWhitelist))
			return BonemealResult.NONE;

		//If farmland is dry and cropsRequireWater is set to ANY_CASE then cancel the event
		if (FarmingModule.isAffectedByFarmlandState(world, pos) && !FarmingModule.isCropOnWetFarmland(world, pos) && Modules.farming.cropsGrowth.cropsRequireWater.equals(CropsRequireWater.ANY_CASE)) {
			return BonemealResult.CANCEL;
		}
		if (this.nerfedBonemeal.equals(NerfedBonemeal.DISABLED))
			return BonemealResult.NONE;
		if (state.getBlock() instanceof CropsBlock) {
			boolean isBeetroot = state.getBlock() instanceof BeetrootBlock;
			int age;
			int maxAge = Collections.max(CropsBlock.AGE.getAllowedValues());
			if (isBeetroot) {
				age = state.get(BeetrootBlock.BEETROOT_AGE);
				maxAge = Collections.max(BeetrootBlock.BEETROOT_AGE.getAllowedValues());
			}
			else
				age = state.get(CropsBlock.AGE);
			if (age == maxAge)
				return BonemealResult.NONE;
			if (world.getRandom().nextDouble() < this.bonemealFailChance) {
				return BonemealResult.ALLOW;
			}
			else if (this.nerfedBonemeal.equals(NerfedBonemeal.SLIGHT))
				age += RandomHelper.getInt(world.getRandom(), 1, 2);
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
				return BonemealResult.NONE;
			if (RandomHelper.getDouble(world.getRandom(), 0d, 100d) < this.bonemealFailChance) {
				return BonemealResult.ALLOW;
			}
			else if (this.nerfedBonemeal.equals(NerfedBonemeal.SLIGHT))
				age += RandomHelper.getInt(world.getRandom(), 1, 2);
			else if (this.nerfedBonemeal.equals(NerfedBonemeal.NERFED))
				age++;
			if (age > maxAge)
				age = maxAge;
			state = state.with(StemBlock.AGE, age);
		}
		else
			return BonemealResult.NONE;
		world.setBlockState(pos, state, 3);
		return BonemealResult.ALLOW;
	}
}

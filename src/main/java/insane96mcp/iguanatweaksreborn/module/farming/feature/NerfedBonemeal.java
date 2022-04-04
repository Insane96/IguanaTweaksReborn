package insane96mcp.iguanatweaksreborn.module.farming.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.farming.Farming;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.config.BlacklistConfig;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.insanelib.util.RandomHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Label(name = "Nerfed Bonemeal", description = "Bonemeal is no longer so OP")
public class NerfedBonemeal extends Feature {

	private final ForgeConfigSpec.ConfigValue<BonemealNerf> nerfedBonemealConfig;
	private final ForgeConfigSpec.ConfigValue<Double> bonemealFailChanceConfig;
	private final BlacklistConfig itemBlacklistConfig;
	private final BlacklistConfig blockBlacklistConfig;

	private static final List<String> blockBlacklistDefault = Arrays.asList("supplementaries:flax");

	public BonemealNerf nerfedBonemeal = BonemealNerf.NERFED;
	public double bonemealFailChance = 0d;
	public List<IdTagMatcher> itemBlacklist;
	public boolean itemBlacklistAsWhitelist = false;
	public List<IdTagMatcher> blockBlacklist;
	public boolean blockBlacklistAsWhitelist = false;

	public NerfedBonemeal(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		nerfedBonemealConfig = Config.builder
				.comment("Makes more Bone Meal required for Crops. Valid Values are\nDISABLED: No Bone Meal changes\nSLIGHT: Makes Bone Meal grow 1-2 crop stages\nNERFED: Makes Bone Meal grow only 1 Stage")
				.defineEnum("Nerfed Bonemeal", this.nerfedBonemeal);
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
		this.itemBlacklist = (List<IdTagMatcher>) IdTagMatcher.parseStringList(this.itemBlacklistConfig.listConfig.get());
		this.itemBlacklistAsWhitelist = this.itemBlacklistConfig.listAsWhitelistConfig.get();
		this.blockBlacklist = (List<IdTagMatcher>) IdTagMatcher.parseStringList(this.blockBlacklistConfig.listConfig.get());
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
		if (event.getWorld().isClientSide)
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

	public BonemealResult applyBonemeal(Level level, ItemStack stack, BlockState state, BlockPos pos) {
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
		if (Modules.farming.cropsGrowth.isEnabled() && Modules.farming.cropsGrowth.cropsRequireWater.equals(CropsGrowth.CropsRequireWater.ANY_CASE) && Farming.isAffectedByFarmlandState(level, pos) && !Farming.isCropOnWetFarmland(level, pos)) {
			return BonemealResult.CANCEL;
		}
		if (this.nerfedBonemeal.equals(BonemealNerf.DISABLED))
			return BonemealResult.NONE;
		if (state.getBlock() instanceof CropBlock) {
			boolean isBeetroot = state.getBlock() instanceof BeetrootBlock;
			int age;
			int maxAge = Collections.max(CropBlock.AGE.getPossibleValues());
			if (isBeetroot) {
				age = state.getValue(BeetrootBlock.AGE);
				maxAge = Collections.max(BeetrootBlock.AGE.getPossibleValues());
			}
			else
				age = state.getValue(CropBlock.AGE);
			if (age == maxAge)
				return BonemealResult.NONE;
			if (level.getRandom().nextDouble() < this.bonemealFailChance) {
				return BonemealResult.ALLOW;
			}
			else if (this.nerfedBonemeal.equals(BonemealNerf.SLIGHT))
				age += RandomHelper.getInt(level.getRandom(), 1, 2);
			else if (this.nerfedBonemeal.equals(BonemealNerf.NERFED))
				age++;
			if (age > maxAge)
				age = maxAge;
			if (isBeetroot) {
				state = state.setValue(BeetrootBlock.AGE, age);
			}
			else
				state = state.setValue(CropBlock.AGE, age);
		}
		else if (state.getBlock() instanceof StemBlock) {
			int age = state.getValue(StemBlock.AGE);
			int maxAge = Collections.max(StemBlock.AGE.getPossibleValues());
			if (age == maxAge)
				return BonemealResult.NONE;
			if (RandomHelper.getDouble(level.getRandom(), 0d, 100d) < this.bonemealFailChance) {
				return BonemealResult.ALLOW;
			}
			else if (this.nerfedBonemeal.equals(BonemealNerf.SLIGHT))
				age += RandomHelper.getInt(level.getRandom(), 1, 2);
			else if (this.nerfedBonemeal.equals(BonemealNerf.NERFED))
				age++;
			if (age > maxAge)
				age = maxAge;
			state = state.setValue(StemBlock.AGE, age);
		}
		else
			return BonemealResult.NONE;
		level.setBlockAndUpdate(pos, state);
		return BonemealResult.ALLOW;
	}

	public enum BonemealNerf {
		DISABLED,
		SLIGHT,
		NERFED
	}
}

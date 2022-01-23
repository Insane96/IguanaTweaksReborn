package insane96mcp.iguanatweaksreborn.module.mining.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.mining.utils.BlockHardness;
import insane96mcp.iguanatweaksreborn.module.mining.utils.DepthHardnessDimension;
import insane96mcp.iguanatweaksreborn.module.mining.utils.DimensionHardnessMultiplier;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.config.BlacklistConfig;
import insane96mcp.insanelib.utils.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Global Hardness", description = "Change all the blocks hardness")
public class GlobalHardness extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> hardnessMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionHardnessMultiplierConfig;
	private final BlacklistConfig hardnessBlacklistConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> depthMultiplierDimensionConfig;
	private final BlacklistConfig depthMultiplierBlacklistConfig;

	private static final List<String> hardnessBlacklistDefault = Arrays.asList("#iguanatweaksreborn:obsidians");
	private static final List<String> dimensionHardnessMultiplierDefault = Arrays.asList("minecraft:the_nether,4", "minecraft:the_end,4");
	private static final List<String> depthMultiplierDimensionDefault = Arrays.asList("minecraft:overworld,0.025,63,-64");
	private static final List<String> depthMultiplierBlacklistDefault = Arrays.asList("#iguanatweaksreborn:obsidians");

	public double hardnessMultiplier = 2.5d;
	public ArrayList<DimensionHardnessMultiplier> dimensionHardnessMultiplier;
	public ArrayList<IdTagMatcher> hardnessBlacklist;
	public Boolean hardnessBlacklistAsWhitelist = false;
	public ArrayList<DepthHardnessDimension> depthMultiplierDimension;
	public ArrayList<IdTagMatcher> depthMultiplierBlacklist;
	public Boolean depthMultiplierBlacklistAsWhitelist = false;

	public GlobalHardness(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		hardnessMultiplierConfig = Config.builder
				.comment("Multiplier applied to the hardness of blocks. E.g. with this set to 3.0 blocks will take 3x more time to break.")
				.defineInRange("Hardness Multiplier", this.hardnessMultiplier, 0.0d, 128d);
		dimensionHardnessMultiplierConfig = Config.builder
				.comment("A list of dimensions and their relative block hardness multiplier. Each entry has a a dimension and hardness. This overrides the global multiplier.\nE.g. [\"minecraft:overworld,2\", \"minecraft:the_nether,4\"]")
				.defineList("Dimension Hardness Multiplier", dimensionHardnessMultiplierDefault, o -> o instanceof String);
		hardnessBlacklistConfig = new BlacklistConfig(Config.builder, "Block Hardness Blacklist", "Block ids or tags that will ignore the global and dimensional multipliers. This can be inverted via 'Blacklist as Whitelist'. Each entry has a block or tag and optionally a dimension. E.g. [\"minecraft:stone\", \"minecraft:diamond_block,minecraft:the_nether\"]", hardnessBlacklistDefault, this.hardnessBlacklistAsWhitelist);
		depthMultiplierDimensionConfig = Config.builder
				.comment("A list of dimensions and their relative block hardness multiplier per blocks below the set Y level. Each entry has a a dimension, a multiplier, a Y Level (where the increased hardness starts applying) and a Y Level cap (where the increase should stop).\nE.g. with the default configurations increases the overworld hardness multiplier by 0.03 for each block below the sea level (63); so at Y = 32 you'll get a multiplier of 2.0 (global multiplier) + 0.03 * (63 - 32) = 2.93 hardness multiplier.\nNOTE: This multiplier increase applies to blocks in Custom Hardness too.")
				.defineList("Depth Multiplier Dimension", depthMultiplierDimensionDefault, o -> o instanceof String);
		depthMultiplierBlacklistConfig = new BlacklistConfig(Config.builder, "Depth Multiplier Blacklist", "Block ids or tags that will ignore the depth multiplier. This can be inverted via 'Blacklist as Whitelist'. Each entry has a block or tag and optionally a dimension. E.g. [\"minecraft:stone\", \"minecraft:diamond_block,minecraft:the_nether\"]", depthMultiplierBlacklistDefault, this.depthMultiplierBlacklistAsWhitelist);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();

		this.hardnessMultiplier = this.hardnessMultiplierConfig.get();
		this.dimensionHardnessMultiplier = (ArrayList<DimensionHardnessMultiplier>) DimensionHardnessMultiplier.parseStringList(this.dimensionHardnessMultiplierConfig.get());
		this.hardnessBlacklist = (ArrayList<IdTagMatcher>) IdTagMatcher.parseStringList(this.hardnessBlacklistConfig.listConfig.get());
		this.hardnessBlacklistAsWhitelist = this.hardnessBlacklistConfig.listAsWhitelistConfig.get();
		this.depthMultiplierDimension = DepthHardnessDimension.parseStringList(this.depthMultiplierDimensionConfig.get());
		this.depthMultiplierBlacklist = (ArrayList<IdTagMatcher>) IdTagMatcher.parseStringList(this.depthMultiplierBlacklistConfig.listConfig.get());
		this.depthMultiplierBlacklistAsWhitelist = this.depthMultiplierBlacklistConfig.listAsWhitelistConfig.get();
	}

	public static ArrayList<DimensionHardnessMultiplier> parseDimensionHardnessMultipliers(List<? extends String> list) {
		ArrayList<DimensionHardnessMultiplier> dimensionHardnessMultipliers = new ArrayList<>();
		for (String line : list) {
			DimensionHardnessMultiplier dimensionHardnessMultiplier = DimensionHardnessMultiplier.parseLine(line);
			if (dimensionHardnessMultiplier != null)
				dimensionHardnessMultipliers.add(dimensionHardnessMultiplier);
		}

		return dimensionHardnessMultipliers;
	}

	public static ArrayList<DepthHardnessDimension> parseDepthMultiplierDimension(List<? extends String> list) {
		ArrayList<DepthHardnessDimension> depthHardnessDimensions = new ArrayList<>();
		for (String line : list) {
			DepthHardnessDimension depthHardnessDimension = DepthHardnessDimension.parseLine(line);
			if (depthHardnessDimension != null)
				depthHardnessDimensions.add(depthHardnessDimension);
		}
		return depthHardnessDimensions;
	}

	@SubscribeEvent
	public void processGlobalHardness(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;

		Level level = event.getPlayer().level;
		ResourceLocation dimensionId = level.dimension().location();
		BlockState blockState = level.getBlockState(event.getPos());
		Block block = blockState.getBlock();
		double blockGlobalHardness = getBlockGlobalHardness(block, dimensionId);
		blockGlobalHardness += getDepthHardnessMultiplier(block, dimensionId, event.getPos(), false);
		if (blockGlobalHardness == 1d)
			return;
		double multiplier = 1d / blockGlobalHardness;
		event.setNewSpeed((float) (event.getNewSpeed() * multiplier));
	}

	/**
	 * Returns 1d when no changes must be made, else will return a multiplier for block hardness
	 */
	public double getBlockGlobalHardness(Block block, ResourceLocation dimensionId) {
		//Check for block black/whitelist
		boolean isInWhitelist = false;
		boolean isInBlacklist = false;
		for (IdTagMatcher blacklistEntry : this.hardnessBlacklist) {
			if (blacklistEntry.matchesBlock(block, dimensionId)) {
				if (!this.hardnessBlacklistAsWhitelist)
					isInBlacklist = true;
				else
					isInWhitelist = true;
				break;
			}
		}
		if (isInBlacklist || (!isInWhitelist && this.hardnessBlacklistAsWhitelist))
			return 1d;

		//If there's a dimension multipler present return that
		for (DimensionHardnessMultiplier dimensionHardnessMultiplier : this.dimensionHardnessMultiplier)
			if (dimensionId.equals(dimensionHardnessMultiplier.dimension))
				return dimensionHardnessMultiplier.multiplier;

		//Otherwise return the global multiplier
		return this.hardnessMultiplier;
	}

	/**
	 * Returns an additive multiplier based off the depth of the block broken
	 */
	public double getDepthHardnessMultiplier(Block block, ResourceLocation dimensionId, BlockPos pos, boolean processCustomHardness) {
		if (!processCustomHardness)
			for (BlockHardness blockHardness : Modules.mining.customHardness.customHardness)
				if (blockHardness.matchesBlock(block, dimensionId))
					return 0d;


		//Check for block black/whitelist
		boolean isInWhitelist = false;
		boolean isInBlacklist = false;
		for (IdTagMatcher blacklistEntry : this.depthMultiplierBlacklist) {
			if (blacklistEntry.matchesBlock(block, dimensionId)) {
				if (!this.depthMultiplierBlacklistAsWhitelist)
					isInBlacklist = true;
				else
					isInWhitelist = true;
				break;
			}
		}
		if (isInBlacklist || (!isInWhitelist && this.depthMultiplierBlacklistAsWhitelist))
			return 0d;

		for (DepthHardnessDimension depthHardnessDimension : this.depthMultiplierDimension) {
			if (dimensionId.equals(depthHardnessDimension.dimension)) {
				return depthHardnessDimension.multiplier * Math.max(depthHardnessDimension.applyBelowY - Math.max(pos.getY(), depthHardnessDimension.capY), 0);
			}
		}
		return 0d;
	}
}
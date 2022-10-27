package insane96mcp.iguanatweaksreborn.module.mining.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.mining.utils.BlockHardness;
import insane96mcp.iguanatweaksreborn.module.mining.utils.DepthHardnessDimension;
import insane96mcp.iguanatweaksreborn.module.mining.utils.DimensionHardnessMultiplier;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.config.Blacklist;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Global Hardness", description = "Change all the blocks hardness")
public class GlobalHardness extends Feature {

	private final ForgeConfigSpec.ConfigValue<Double> hardnessMultiplierConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionHardnessMultiplierConfig;
	private final Blacklist.Config hardnessBlacklistConfig;
	private final ForgeConfigSpec.ConfigValue<List<? extends String>> depthMultiplierDimensionConfig;
	private final Blacklist.Config depthMultiplierBlacklistConfig;

	private static final List<String> hardnessBlacklistDefault = List.of("#iguanatweaksreborn:obsidians");
	private static final List<String> dimensionHardnessMultiplierDefault = Arrays.asList("minecraft:the_nether,4", "minecraft:the_end,4");
	private static final List<String> depthMultiplierDimensionDefault = Arrays.asList("minecraft:overworld,0.01,63,-64", "minecraft:overworld,-0.64,4,3");
	private static final List<String> depthMultiplierBlacklistDefault = List.of("#iguanatweaksreborn:obsidians");

	public double hardnessMultiplier = 2.5d;
	public ArrayList<DimensionHardnessMultiplier> dimensionHardnessMultiplier;
	public Blacklist hardnessBlacklist;
	public ArrayList<DepthHardnessDimension> depthMultiplierDimension;
	public Blacklist depthMultiplierBlacklist;

	public GlobalHardness(Module module) {
		super(ITCommonConfig.builder, module);
		ITCommonConfig.builder.comment(this.getDescription()).push(this.getName());
		hardnessMultiplierConfig = ITCommonConfig.builder
				.comment("Multiplier applied to the hardness of blocks. E.g. with this set to 3.0 blocks will take 3x more time to break.")
				.defineInRange("Hardness Multiplier", this.hardnessMultiplier, 0.0d, 128d);
		dimensionHardnessMultiplierConfig = ITCommonConfig.builder
				.comment("A list of dimensions and their relative block hardness multiplier. Each entry has a a dimension and hardness. This overrides the global multiplier.")
				.defineList("Dimension Hardness Multiplier", dimensionHardnessMultiplierDefault, o -> o instanceof String);
		hardnessBlacklistConfig = new Blacklist.Config(ITCommonConfig.builder, "Block Hardness Blacklist", "Block ids or tags that will ignore the global and dimensional multipliers. This can be inverted via 'Blacklist as Whitelist'. Each entry has a block or tag and optionally a dimension. E.g. [\"minecraft:stone\", \"minecraft:diamond_block,minecraft:the_nether\"]")
				.setDefaultList(hardnessBlacklistDefault)
				.setIsDefaultWhitelist(false)
				.build();
		depthMultiplierDimensionConfig = ITCommonConfig.builder
				.comment("A list of dimensions and their relative block hardness multiplier per blocks below the set Y level. Each entry has a a dimension, a multiplier, a Y Level (where the increased hardness starts applying) and a Y Level cap (where the increase should stop).\nE.g. with the default configurations increases the overworld hardness multiplier by 0.025 for each block below the sea level (63); so at Y = 32 you'll get a multiplier of 2.5 (global multiplier) + 0.025 * (63 - 32) = 3.3 hardness multiplier.\nNOTE: This multiplier increase applies to blocks in Custom Hardness too.")
				.defineList("Depth Multiplier Dimension", depthMultiplierDimensionDefault, o -> o instanceof String);
		depthMultiplierBlacklistConfig = new Blacklist.Config(ITCommonConfig.builder, "Depth Multiplier Blacklist", "Block ids or tags that will ignore the depth multiplier. This can be inverted via 'Blacklist as Whitelist'. Each entry has a block or tag and optionally a dimension. E.g. [\"minecraft:stone\", \"minecraft:diamond_block,minecraft:the_nether\"]")
				.setDefaultList(depthMultiplierBlacklistDefault)
				.setIsDefaultWhitelist(false)
				.build();
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();

		this.hardnessMultiplier = this.hardnessMultiplierConfig.get();
		this.dimensionHardnessMultiplier = (ArrayList<DimensionHardnessMultiplier>) DimensionHardnessMultiplier.parseStringList(this.dimensionHardnessMultiplierConfig.get());
		this.hardnessBlacklist = this.hardnessBlacklistConfig.get();
		this.depthMultiplierDimension = DepthHardnessDimension.parseStringList(this.depthMultiplierDimensionConfig.get());
		this.depthMultiplierBlacklist = this.depthMultiplierBlacklistConfig.get();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void processGlobalHardness(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;

		Level level = event.getPlayer().level;
		ResourceLocation dimensionId = level.dimension().location();
		BlockState blockState = level.getBlockState(event.getPos());
		Block block = blockState.getBlock();
		double blockGlobalHardness = getBlockGlobalHardnessMultiplier(block, dimensionId);
		blockGlobalHardness += getDepthHardnessMultiplier(block, dimensionId, event.getPos(), false);
		if (blockGlobalHardness == 1d)
			return;
		double multiplier = 1d / blockGlobalHardness;
		event.setNewSpeed((float) (event.getNewSpeed() * multiplier));
	}

	/**
	 * Returns 1d when no changes must be made, else will return a multiplier for block hardness
	 */
	public double getBlockGlobalHardnessMultiplier(Block block, ResourceLocation dimensionId) {
		if (this.hardnessBlacklist.isBlockBlackOrNotWhiteListed(block))
			return 1d;

		//If there's a dimension multiplier present return that
		for (DimensionHardnessMultiplier dimensionHardnessMultiplier : this.dimensionHardnessMultiplier)
			if (dimensionId.equals(dimensionHardnessMultiplier.dimension))
				return dimensionHardnessMultiplier.multiplier;

		//Otherwise, return the global multiplier
		return this.hardnessMultiplier;
	}

	/**
	 * Returns an additive multiplier based off the depth of the block broken
	 */
	public double getDepthHardnessMultiplier(Block block, ResourceLocation dimensionId, BlockPos pos, boolean processCustomHardness) {
		if (!this.isEnabled())
			return 0d;

		if (!processCustomHardness)
			for (BlockHardness blockHardness : Modules.mining.customHardness.customHardness)
				if (blockHardness.matchesBlock(block, dimensionId))
					return 0d;

		if (this.depthMultiplierBlacklist.isBlockBlackOrNotWhiteListed(block))
			return 0d;

		double hardness = 0d;
		for (DepthHardnessDimension depthHardnessDimension : this.depthMultiplierDimension) {
			if (dimensionId.equals(depthHardnessDimension.dimension)) {
				hardness += depthHardnessDimension.multiplier * Math.max(depthHardnessDimension.applyBelowY - Math.max(pos.getY(), depthHardnessDimension.capY), 0);
			}
		}
		return hardness;
	}
}
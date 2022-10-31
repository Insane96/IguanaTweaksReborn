package insane96mcp.iguanatweaksreborn.module.mining.feature;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.mining.utils.BlockHardness;
import insane96mcp.iguanatweaksreborn.module.mining.utils.DepthHardnessDimension;
import insane96mcp.iguanatweaksreborn.module.mining.utils.DimensionHardnessMultiplier;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Blacklist;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Global Hardness", description = "Change all the blocks hardness")
@LoadFeature(module = Modules.Ids.MINING)
public class GlobalHardness extends Feature {

	private static ForgeConfigSpec.ConfigValue<List<? extends String>> dimensionHardnessMultiplierConfig;
	private static final List<String> dimensionHardnessMultiplierDefault = List.of("minecraft:the_nether,4", "minecraft:the_end,4");
	public static ArrayList<DimensionHardnessMultiplier> dimensionHardnessMultiplier;

	private static ForgeConfigSpec.ConfigValue<List<? extends String>> depthMultiplierDimensionConfig;
	private static final List<String> depthMultiplierDimensionDefault = List.of("minecraft:overworld,0.01,63,-64", "minecraft:overworld,-0.64,4,3");
	public static ArrayList<DepthHardnessDimension> depthMultiplierDimension;

	@Config(min = 0d, max = 128d)
	@Label(name = "Hardness Multiplier", description = "Multiplier applied to the hardness of blocks. E.g. with this set to 3.0 blocks will take 3x more time to break.")
	public static Double hardnessMultiplier = 2.5d;
	@Config
	@Label(name = "Block Hardness Blacklist", description = "Block ids or tags that will ignore the global and dimensional multipliers. This can be inverted via 'Blacklist as Whitelist'. Each entry has a block or tag and optionally a dimension. E.g. [\"minecraft:stone\", \"minecraft:diamond_block,minecraft:the_nether\"]")
	public static Blacklist hardnessBlacklist = new Blacklist(List.of(
			new IdTagMatcher(IdTagMatcher.Type.TAG, "iguanatweaksreborn:obsidians")
	));
	@Config
	@Label(name = "Depth Multiplier Blacklist", description = "Block ids or tags that will ignore the depth multiplier. This can be inverted via 'Blacklist as Whitelist'. Each entry has a block or tag and optionally a dimension. E.g. [\"minecraft:stone\", \"minecraft:diamond_block,minecraft:the_nether\"]")
	public static Blacklist depthMultiplierBlacklist = new Blacklist(List.of(
			new IdTagMatcher(IdTagMatcher.Type.TAG, "iguanatweaksreborn:obsidians")
	));

	public GlobalHardness(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadConfigOptions() {
		super.loadConfigOptions();
		dimensionHardnessMultiplierConfig = this.getBuilder()
				.comment("A list of dimensions and their relative block hardness multiplier. Each entry has a a dimension and hardness. This overrides the global multiplier.")
				.defineList("Dimension Hardness Multiplier", dimensionHardnessMultiplierDefault, o -> o instanceof String);
		depthMultiplierDimensionConfig = this.getBuilder()
				.comment("A list of dimensions and their relative block hardness multiplier per blocks below the set Y level. Each entry has a a dimension, a multiplier, a Y Level (where the increased hardness starts applying) and a Y Level cap (where the increase should stop).\nE.g. with the default configurations increases the overworld hardness multiplier by 0.025 for each block below the sea level (63); so at Y = 32 you'll get a multiplier of 2.5 (global multiplier) + 0.025 * (63 - 32) = 3.3 hardness multiplier.\nNOTE: This multiplier increase applies to blocks in Custom Hardness too.")
				.defineList("Depth Multiplier Dimension", depthMultiplierDimensionDefault, o -> o instanceof String);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);

		dimensionHardnessMultiplier = (ArrayList<DimensionHardnessMultiplier>) DimensionHardnessMultiplier.parseStringList(dimensionHardnessMultiplierConfig.get());
		depthMultiplierDimension = DepthHardnessDimension.parseStringList(depthMultiplierDimensionConfig.get());
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void processGlobalHardness(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled()
				|| event.getPosition().isEmpty())
			return;

		BlockPos pos = event.getPosition().get();
		Level level = event.getEntity().level;
		ResourceLocation dimensionId = level.dimension().location();
		BlockState blockState = level.getBlockState(pos);
		Block block = blockState.getBlock();
		double blockGlobalHardness = getBlockGlobalHardnessMultiplier(block, dimensionId);
		blockGlobalHardness += getDepthHardnessMultiplier(block, dimensionId, pos, false);
		if (blockGlobalHardness == 1d)
			return;
		double multiplier = 1d / blockGlobalHardness;
		event.setNewSpeed((float) (event.getNewSpeed() * multiplier));
	}

	/**
	 * Returns 1d when no changes must be made, else will return a multiplier for block hardness
	 */
	public double getBlockGlobalHardnessMultiplier(Block block, ResourceLocation dimensionId) {
		if (hardnessBlacklist.isBlockBlackOrNotWhiteListed(block))
			return 1d;

		//If there's a dimension multiplier present return that
		for (DimensionHardnessMultiplier dimensionHardnessMultiplier : dimensionHardnessMultiplier)
			if (dimensionId.equals(dimensionHardnessMultiplier.dimension))
				return dimensionHardnessMultiplier.multiplier;

		//Otherwise, return the global multiplier
		return hardnessMultiplier;
	}

	/**
	 * Returns an additive multiplier based off the depth of the block broken
	 */
	public double getDepthHardnessMultiplier(Block block, ResourceLocation dimensionId, BlockPos pos, boolean processCustomHardness) {
		if (!this.isEnabled())
			return 0d;

		if (!processCustomHardness)
			for (BlockHardness blockHardness : CustomHardness.customHardness)
				if (blockHardness.matchesBlock(block, dimensionId))
					return 0d;

		if (depthMultiplierBlacklist.isBlockBlackOrNotWhiteListed(block))
			return 0d;

		double hardness = 0d;
		for (DepthHardnessDimension depthHardnessDimension : depthMultiplierDimension) {
			if (dimensionId.equals(depthHardnessDimension.dimension)) {
				hardness += depthHardnessDimension.multiplier * Math.max(depthHardnessDimension.applyBelowY - Math.max(pos.getY(), depthHardnessDimension.capY), 0);
			}
		}
		return hardness;
	}
}
package insane96mcp.iguanatweaksreborn.module.mining.feature;

import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.mining.utils.BlockHardness;
import insane96mcp.iguanatweaksreborn.module.mining.utils.DepthHardnessDimension;
import insane96mcp.iguanatweaksreborn.module.mining.utils.DimensionHardnessMultiplier;
import insane96mcp.iguanatweaksreborn.utils.Utils;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

@Label(name = "Global Hardness", description = "Change all the blocks hardness. Dimension Hardness and Depth Hardness are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.MINING)
public class GlobalHardness extends ITFeature {
	public static final ResourceLocation HARDNESS_BLACKLIST = new ResourceLocation(IguanaTweaksReborn.RESOURCE_PREFIX + "hardness_blacklist");
	public static final ResourceLocation DEPTH_MULTIPLIER_BLACKLIST = new ResourceLocation(IguanaTweaksReborn.RESOURCE_PREFIX + "depth_multiplier_blacklist");

	public static final ArrayList<DimensionHardnessMultiplier> DIMENSION_HARDNESS_MULTIPLIERS_DEFAULT = new ArrayList<>(Arrays.asList(
			new DimensionHardnessMultiplier("minecraft:the_nether", 4d),
			new DimensionHardnessMultiplier("minecraft:the_end", 4d)
	));
	public static final ArrayList<DimensionHardnessMultiplier> dimensionHardnessMultiplier = new ArrayList<>();

	public static final ArrayList<DepthHardnessDimension> DEPTH_HARDNESS_DIMENSIONS_DEFAULT = new ArrayList<>(Arrays.asList(
			new DepthHardnessDimension("minecraft:overworld", 0.01d, 63, -64),
			new DepthHardnessDimension("minecraft:overworld", -0.64d, 4, 3)
	));
	public static final ArrayList<DepthHardnessDimension> depthMultiplierDimension = new ArrayList<>();

	@Config(min = 0d, max = 128d)
	@Label(name = "Hardness Multiplier", description = "Multiplier applied to the hardness of blocks. E.g. with this set to 3.0 blocks will take 3x more time to break.")
	public static Double hardnessMultiplier = 2.5d;

	public GlobalHardness(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	static final Type dimensionHardnessMultiplierListType = new TypeToken<ArrayList<DimensionHardnessMultiplier>>(){}.getType();
	static final Type depthHardnessDimensionListType = new TypeToken<ArrayList<DepthHardnessDimension>>(){}.getType();

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
		this.loadAndReadFile("dimension_hardness.json", dimensionHardnessMultiplier, DIMENSION_HARDNESS_MULTIPLIERS_DEFAULT, dimensionHardnessMultiplierListType);
		this.loadAndReadFile("depth_multipliers.json", depthMultiplierDimension, DEPTH_HARDNESS_DIMENSIONS_DEFAULT, depthHardnessDimensionListType);
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
		if (Utils.isBlockInTag(block, HARDNESS_BLACKLIST))
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
			for (BlockHardness blockHardness : CustomHardness.customHardnesses)
				if (blockHardness.matchesBlock(block, dimensionId))
					return 0d;

		if (Utils.isBlockInTag(block, DEPTH_MULTIPLIER_BLACKLIST))
			return 0d;

		double hardness = 0d;
		for (DepthHardnessDimension depthHardnessDimension : depthMultiplierDimension) {
			if (depthHardnessDimension.matchesDimension(dimensionId)) {
				hardness += depthHardnessDimension.multiplier * Math.max(depthHardnessDimension.applyBelowY - Math.max(pos.getY(), depthHardnessDimension.stopAt), 0);
			}
		}
		return hardness;
	}
}
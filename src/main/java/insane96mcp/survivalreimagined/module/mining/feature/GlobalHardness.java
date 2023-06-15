package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.IdTagValue;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.mining.data.DepthHardnessDimension;
import insane96mcp.survivalreimagined.module.mining.data.DimensionHardnessMultiplier;
import insane96mcp.survivalreimagined.network.message.GlobalHardnessSyncMessage;
import insane96mcp.survivalreimagined.network.message.JsonConfigSyncMessage;
import insane96mcp.survivalreimagined.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Global Hardness", description = "Change all the blocks hardness. Dimension Hardness and Depth Hardness are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.MINING)
public class GlobalHardness extends SRFeature {
	public static final ResourceLocation HARDNESS_BLACKLIST = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "hardness_blacklist");
	public static final ResourceLocation DEPTH_MULTIPLIER_BLACKLIST = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "depth_multiplier_blacklist");

	public static final ArrayList<DimensionHardnessMultiplier> DIMENSION_HARDNESS_MULTIPLIERS_DEFAULT = new ArrayList<>(List.of(
			new DimensionHardnessMultiplier("minecraft:the_nether", 4.0d)
	));
	public static final ArrayList<DimensionHardnessMultiplier> dimensionHardnessMultiplier = new ArrayList<>();

	public static final ArrayList<DepthHardnessDimension> DEPTH_MULTIPLIER_DIMENSION_DEFAULT = new ArrayList<>(List.of(
			new DepthHardnessDimension("minecraft:overworld", 0.01d, 63, 0)
	));
	public static final ArrayList<DepthHardnessDimension> depthMultiplierDimension = new ArrayList<>();

	@Config(min = 0d, max = 128d)
	@Label(name = "Hardness Multiplier", description = "Multiplier applied to the hardness of blocks. E.g. with this set to 2.0 blocks will take 2 times longer to break.")
	public static Double hardnessMultiplier = 1.5d;

	public GlobalHardness(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("dimension_hardness.json", dimensionHardnessMultiplier, DIMENSION_HARDNESS_MULTIPLIERS_DEFAULT, DimensionHardnessMultiplier.LIST_TYPE, true, JsonConfigSyncMessage.ConfigType.DIMENSION_HARDNESS));
		JSON_CONFIGS.add(new JsonConfig<>("depth_multipliers.json", depthMultiplierDimension, DEPTH_MULTIPLIER_DIMENSION_DEFAULT, DepthHardnessDimension.LIST_TYPE, true, JsonConfigSyncMessage.ConfigType.DEPTH_HARDNESS));
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
	}

	@SubscribeEvent
	public void syncHardness(OnDatapackSyncEvent event) {
		if (event.getPlayer() == null)
			event.getPlayerList().getPlayers().forEach(player -> GlobalHardnessSyncMessage.sync(player, hardnessMultiplier.floatValue()));
		else
			GlobalHardnessSyncMessage.sync(event.getPlayer(), hardnessMultiplier.floatValue());
	}

	public static void handleDimensionHardnessPacket(String json) {
		loadAndReadJson(json, dimensionHardnessMultiplier, DIMENSION_HARDNESS_MULTIPLIERS_DEFAULT, DimensionHardnessMultiplier.LIST_TYPE);
	}

	public static void handleDepthHardnessPacket(String json) {
		loadAndReadJson(json, depthMultiplierDimension, DEPTH_MULTIPLIER_DIMENSION_DEFAULT, DepthHardnessDimension.LIST_TYPE);
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
		double blockHardnessMultiplier = getBlockHardnessMultiplier(block, dimensionId, pos);
		if (blockHardnessMultiplier == 1d)
			return;
		double multiplier = 1d / blockHardnessMultiplier;
		event.setNewSpeed((float) (event.getNewSpeed() * multiplier));
	}

	public static double getBlockHardnessMultiplier(Block block, ResourceLocation dimensionId, BlockPos pos) {
		double blockHardness = getBlockGlobalHardnessMultiplier(block, dimensionId);
		blockHardness += getDepthHardnessMultiplier(block, dimensionId, pos, true);
		return blockHardness;
	}

	/**
	 * Returns 1d when no changes must be made, else will return a multiplier for block hardness
	 */
	public static double getBlockGlobalHardnessMultiplier(Block block, ResourceLocation dimensionId) {
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
	public static double getDepthHardnessMultiplier(Block block, ResourceLocation dimensionId, BlockPos pos, boolean processCustomHardness) {
		if (!Feature.isEnabled(GlobalHardness.class))
			return 0d;

		if (!processCustomHardness)
			for (IdTagValue blockHardness : CustomHardness.customHardnesses)
				if (blockHardness.matchesBlock(block, dimensionId))
					return 0d;

		if (Utils.isBlockInTag(block, DEPTH_MULTIPLIER_BLACKLIST))
			return 0d;

		double hardness = 0d;
		for (DepthHardnessDimension depthHardnessDimension : depthMultiplierDimension) {
			if (dimensionId.equals(depthHardnessDimension.dimension)) {
				hardness += depthHardnessDimension.multiplier * Math.max(depthHardnessDimension.applyBelowY - Math.max(pos.getY(), depthHardnessDimension.stopAt), 0);
			}
		}
		return hardness;
	}
}
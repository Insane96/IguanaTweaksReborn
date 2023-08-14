package insane96mcp.survivalreimagined.module.mining.blockhardness;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.SurvivalReimagined;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.IdTagValue;
import insane96mcp.survivalreimagined.module.Modules;
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

@Label(name = "Block Hardness", description = "Change blocks hardness. Dimension Hardness, Depth Hardness and Custom Hardness are controlled via json in this feature's folder")
@LoadFeature(module = Modules.Ids.MINING)
public class BlockHardness extends SRFeature {
	public static final ResourceLocation HARDNESS_BLACKLIST = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "hardness_blacklist");
	public static final ResourceLocation DEPTH_MULTIPLIER_BLACKLIST = new ResourceLocation(SurvivalReimagined.RESOURCE_PREFIX + "depth_multiplier_blacklist");

	public static final ArrayList<DimensionHardnessMultiplier> DIMENSION_HARDNESS_MULTIPLIERS_DEFAULT = new ArrayList<>(List.of(
			new DimensionHardnessMultiplier("minecraft:the_nether", 2.0d)
	));
	public static final ArrayList<DimensionHardnessMultiplier> dimensionHardnessMultiplier = new ArrayList<>();

	public static final ArrayList<DepthHardnessDimension> DEPTH_MULTIPLIER_DIMENSION_DEFAULT = new ArrayList<>(List.of(
			new DepthHardnessDimension("minecraft:overworld", 0.01d, 100, 0)
	));
	public static final ArrayList<DepthHardnessDimension> depthMultiplierDimension = new ArrayList<>();

	public static final ArrayList<IdTagValue> CUSTOM_HARDNESSES_DEFAULT = new ArrayList<>(List.of(
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:coal_ore", 2.5d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:copper_ore", 2.5d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:iron_ore", 3d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:gold_ore", 3.5d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:diamond_ore", 4d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:redstone_ore", 3d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:lapis_ore", 3d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:emerald_ore", 4d),

			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:poor_copper_ore", 1.667d),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:poor_iron_ore", 2d),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:poor_gold_ore", 2.333d),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:rich_copper_ore", 3.333d),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:rich_iron_ore", 4d),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:rich_gold_ore", 4.6666d),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:deepslate_coal_ore", 4d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:deepslate_copper_ore", 4d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:deepslate_iron_ore", 4.5d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:deepslate_gold_ore", 5d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:deepslate_diamond_ore", 6d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:deepslate_redstone_ore", 4.5d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:deepslate_lapis_ore", 4.5d),
			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:deepslate_emerald_ore", 6d),

			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:poor_deepslate_copper_ore", 2.667d),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:poor_deepslate_iron_ore", 3d),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:poor_deepslate_gold_ore", 3.333d),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:rich_deepslate_copper_ore", 5.333d),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:rich_deepslate_iron_ore", 6d),
			new IdTagValue(IdTagMatcher.Type.ID, "survivalreimagined:rich_deepslate_gold_ore", 6.666d),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:ancient_debris", 12d),
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:obsidians", 33d),

			new IdTagValue(IdTagMatcher.Type.ID, "minecraft:powder_snow", 2.5d)
	));
	public static final ArrayList<IdTagValue> customHardnesses = new ArrayList<>();

	@Config(min = 0d, max = 128d)
	@Label(name = "Hardness Multiplier", description = "Multiplier applied to the hardness of blocks. E.g. with this set to 2.0 blocks will take 2 times longer to break.")
	public static Double hardnessMultiplier = 1d;

	public BlockHardness(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("dimension_hardness.json", dimensionHardnessMultiplier, DIMENSION_HARDNESS_MULTIPLIERS_DEFAULT, DimensionHardnessMultiplier.LIST_TYPE, true, JsonConfigSyncMessage.ConfigType.DIMENSION_HARDNESS));
		JSON_CONFIGS.add(new JsonConfig<>("depth_multipliers.json", depthMultiplierDimension, DEPTH_MULTIPLIER_DIMENSION_DEFAULT, DepthHardnessDimension.LIST_TYPE, true, JsonConfigSyncMessage.ConfigType.DEPTH_HARDNESS));
		JSON_CONFIGS.add(new JsonConfig<>("custom_hardnesses.json", customHardnesses, CUSTOM_HARDNESSES_DEFAULT, IdTagValue.LIST_TYPE, BlockHardness::processBlockHardness, true, JsonConfigSyncMessage.ConfigType.DURABILITIES));
	}

	public static void handleCustomBlockHardnessPacket(String json) {
		loadAndReadJson(json, customHardnesses, CUSTOM_HARDNESSES_DEFAULT, IdTagValue.LIST_TYPE);
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
	}

	private static final Object mutex = new Object();

	public static void processBlockHardness(List<IdTagValue> list, boolean isClientSide) {
		if (list.isEmpty())
			return;

		synchronized (mutex) {
			for (IdTagValue blockHardness : list) {
				//If the block's hardness is 0 I replace the hardness
				List<Block> blocksToProcess = blockHardness.getAllBlocks();
				for (Block block : blocksToProcess) {
					block.getStateDefinition().getPossibleStates().forEach(blockState -> blockState.destroySpeed = (float) blockHardness.value);
				}
			}
		}
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
		Level level = event.getEntity().level();
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
		if (!Feature.isEnabled(BlockHardness.class))
			return 0d;

		if (!processCustomHardness)
			for (IdTagValue blockHardness : customHardnesses)
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
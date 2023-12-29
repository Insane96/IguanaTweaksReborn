package insane96mcp.iguanatweaksreborn.module.mining.blockhardness;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.data.generator.ITRBlockTagsProvider;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.network.message.GlobalHardnessSync;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.JsonFeature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.data.IdTagValue;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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
public class BlockHardness extends JsonFeature {
	public static final TagKey<Block> HARDNESS_BLACKLIST = ITRBlockTagsProvider.create("hardness_blacklist");
	public static final TagKey<Block> DEPTH_MULTIPLIER_BLACKLIST = ITRBlockTagsProvider.create("depth_multiplier_blacklist");

	public static final ArrayList<DimensionHardnessMultiplier> DIMENSION_HARDNESS_MULTIPLIERS_DEFAULT = new ArrayList<>(List.of(
			new DimensionHardnessMultiplier("minecraft:the_nether", 3d)
	));
	public static final ArrayList<DimensionHardnessMultiplier> dimensionHardnessMultiplier = new ArrayList<>();

	public static final ArrayList<DepthHardnessDimension> DEPTH_MULTIPLIER_DIMENSION_DEFAULT = new ArrayList<>(List.of(
			new DepthHardnessDimension("minecraft:overworld", 0.01d, 64, 0)
	));
	public static final ArrayList<DepthHardnessDimension> depthMultiplierDimension = new ArrayList<>();

	public static final ArrayList<IdTagValue> CUSTOM_HARDNESSES_DEFAULT = new ArrayList<>(List.of(
			IdTagValue.newId("minecraft:coal_ore", 2.5d),
			IdTagValue.newId("minecraft:copper_ore", 2.5d),
			IdTagValue.newId("minecraft:iron_ore", 3d),
			IdTagValue.newId("minecraft:gold_ore", 3.5d),
			IdTagValue.newId("minecraft:diamond_ore", 4d),
			IdTagValue.newId("minecraft:redstone_ore", 3d),
			IdTagValue.newId("minecraft:lapis_ore", 3d),
			IdTagValue.newId("minecraft:emerald_ore", 4d),

			/*IdTagValue.newId("survivalreimagined:poor_copper_ore", 1.667d),
			IdTagValue.newId("survivalreimagined:poor_iron_ore", 2d),
			IdTagValue.newId("survivalreimagined:poor_gold_ore", 2.333d),
			IdTagValue.newId("survivalreimagined:rich_copper_ore", 3.333d),
			IdTagValue.newId("survivalreimagined:rich_iron_ore", 4d),
			IdTagValue.newId("survivalreimagined:rich_gold_ore", 4.6666d),*/

			IdTagValue.newId("minecraft:deepslate_coal_ore", 4d),
			IdTagValue.newId("minecraft:deepslate_copper_ore", 4d),
			IdTagValue.newId("minecraft:deepslate_iron_ore", 4.5d),
			IdTagValue.newId("minecraft:deepslate_gold_ore", 5d),
			IdTagValue.newId("minecraft:deepslate_diamond_ore", 6d),
			IdTagValue.newId("minecraft:deepslate_redstone_ore", 4.5d),
			IdTagValue.newId("minecraft:deepslate_lapis_ore", 4.5d),
			IdTagValue.newId("minecraft:deepslate_emerald_ore", 6d),

			IdTagValue.newId("survivalreimagined:poor_deepslate_copper_ore", 2.667d),
			IdTagValue.newId("survivalreimagined:poor_deepslate_iron_ore", 3d),
			IdTagValue.newId("survivalreimagined:poor_deepslate_gold_ore", 3.333d),
			IdTagValue.newId("survivalreimagined:rich_deepslate_copper_ore", 5.333d),
			IdTagValue.newId("survivalreimagined:rich_deepslate_iron_ore", 6d),
			IdTagValue.newId("survivalreimagined:rich_deepslate_gold_ore", 6.666d),

			IdTagValue.newId("minecraft:ancient_debris", 12d),
			IdTagValue.newTag("survivalreimagined:obsidians", 33d),

			IdTagValue.newId("minecraft:powder_snow", 2.5d)
	));
	public static final ArrayList<IdTagValue> customHardnesses = new ArrayList<>();

	@Config(min = 0d, max = 128d)
	@Label(name = "Hardness Multiplier", description = "Multiplier applied to the hardness of blocks. E.g. with this set to 2.0 blocks will take 2 times longer to break.")
	public static Double hardnessMultiplier = 1d;

	public BlockHardness(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		addSyncType(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "dimension_hardness"), new SyncType(json -> loadAndReadJson(json, dimensionHardnessMultiplier, DIMENSION_HARDNESS_MULTIPLIERS_DEFAULT, DimensionHardnessMultiplier.LIST_TYPE)));
		JSON_CONFIGS.add(new JsonConfig<>("dimension_hardness.json", dimensionHardnessMultiplier, DIMENSION_HARDNESS_MULTIPLIERS_DEFAULT, DimensionHardnessMultiplier.LIST_TYPE, true, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "dimension_hardness")));
		addSyncType(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "depth_multipliers"), new SyncType(json -> loadAndReadJson(json, depthMultiplierDimension, DEPTH_MULTIPLIER_DIMENSION_DEFAULT, DepthHardnessDimension.LIST_TYPE)));
		JSON_CONFIGS.add(new JsonConfig<>("depth_multipliers.json", depthMultiplierDimension, DEPTH_MULTIPLIER_DIMENSION_DEFAULT, DepthHardnessDimension.LIST_TYPE, true, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "depth_multipliers")));
		addSyncType(new ResourceLocation(IguanaTweaksReborn.MOD_ID, "custom_hardnesses"), new SyncType(json -> loadAndReadJson(json, customHardnesses, CUSTOM_HARDNESSES_DEFAULT, IdTagValue.LIST_TYPE)));
		JSON_CONFIGS.add(new JsonConfig<>("custom_hardnesses.json", customHardnesses, CUSTOM_HARDNESSES_DEFAULT, IdTagValue.LIST_TYPE, BlockHardness::processBlockHardness, true, new ResourceLocation(IguanaTweaksReborn.MOD_ID, "custom_hardnesses")));
	}

	@Override
	public String getModConfigFolder() {
		return IguanaTweaksReborn.CONFIG_FOLDER;
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
				List<Block> blocksToProcess = blockHardness.id.getAllBlocks();
				for (Block block : blocksToProcess) {
					block.getStateDefinition().getPossibleStates().forEach(blockState -> blockState.destroySpeed = (float) blockHardness.value);
				}
			}
		}
	}

	@SubscribeEvent
	public void syncHardness(OnDatapackSyncEvent event) {
		if (event.getPlayer() == null)
			event.getPlayerList().getPlayers().forEach(player -> GlobalHardnessSync.sync(player, hardnessMultiplier.floatValue()));
		else
			GlobalHardnessSync.sync(event.getPlayer(), hardnessMultiplier.floatValue());
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
		double blockHardnessMultiplier = getBlockHardnessMultiplier(blockState, dimensionId, pos);
		if (blockHardnessMultiplier == 1d)
			return;
		double multiplier = 1d / blockHardnessMultiplier;
		event.setNewSpeed((float) (event.getNewSpeed() * multiplier));
	}

	public static double getBlockHardnessMultiplier(BlockState state, ResourceLocation dimensionId, BlockPos pos) {
		double blockHardness = getBlockGlobalHardnessMultiplier(state, dimensionId);
		blockHardness += getDepthHardnessMultiplier(state, dimensionId, pos, true);
		return blockHardness;
	}

	/**
	 * Returns 1d when no changes must be made, else will return a multiplier for block hardness
	 */
	public static double getBlockGlobalHardnessMultiplier(BlockState state, ResourceLocation dimensionId) {
		if (state.is(HARDNESS_BLACKLIST))
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
	public static double getDepthHardnessMultiplier(BlockState state, ResourceLocation dimensionId, BlockPos pos, boolean processCustomHardness) {
		if (!Feature.isEnabled(BlockHardness.class))
			return 0d;

		if (!processCustomHardness)
			for (IdTagValue blockHardness : customHardnesses)
				if (blockHardness.id.matchesBlock(state, dimensionId))
					return 0d;

		if (state.is(DEPTH_MULTIPLIER_BLACKLIST))
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
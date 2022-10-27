package insane96mcp.iguanatweaksreborn.module.mining.feature;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.module.mining.utils.BlockHardness;
import insane96mcp.iguanatweaksreborn.setup.ITCommonConfig;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Custom Hardness", description = "Change specific blocks hardness as well as black and whitelist. Requires a Minecraft restart if you remove a block from the list.")
public class CustomHardness extends Feature {

	private final ForgeConfigSpec.ConfigValue<List<? extends String>> customHardnessConfig;

	private static final ArrayList<String> customHardnessDefault = Lists.newArrayList(
			"minecraft:coal_ore,2.5", "minecraft:iron_ore,3", "minecraft:gold_ore,3.5", "minecraft:diamond_ore,4", "minecraft:redstone_ore,3", "minecraft:lapis_ore,3", "minecraft:emerald_ore,4",
			"minecraft:deepslate_coal_ore,4.5", "minecraft:deepslate_iron_ore,5", "minecraft:deepslate_gold_ore,5.5", "minecraft:deepslate_diamond_ore,6", "minecraft:deepslate_redstone_ore,5", "minecraft:deepslate_lapis_ore,5", "minecraft:deepslate_emerald_ore,6",
			"minecraft:ancient_debris,10", "#iguanatweaksreborn:obsidians,33");

	public ArrayList<BlockHardness> customHardness;

	public CustomHardness(Module module) {
		super(ITCommonConfig.builder, module);
		ITCommonConfig.builder.comment(this.getDescription()).push(this.getName());
		customHardnessConfig = ITCommonConfig.builder
				.comment("""
						Define custom blocks hardness, one string = one block/tag. Those blocks ARE AFFECTED by the global block hardness multiplier, unless put in the blacklist.
						The format is modid:blockid,hardness,dimensionid or #modid:tagid,hardness,dimensionid
						E.g. 'minecraft:stone,5.0' will make stone have 5 hardness in every dimension (multiplied by Global Hardness).
						E.g. '#forge:stone,5.0,minecraft:overworld' will make all the stone types have 5 hardness but only in the overworld.""")
				.defineList("Custom Hardness", customHardnessDefault, o -> o instanceof String);
		ITCommonConfig.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		customHardness = BlockHardness.parseStringList(this.customHardnessConfig.get());
		processBlockHardness();
	}
	private final Object mutex = new Object();

	public void processBlockHardness() {
		if (!this.isEnabled()
				|| this.customHardness.isEmpty())
			return;

		synchronized (mutex) {
			for (BlockHardness blockHardness : this.customHardness) {
				//If the block's hardness is 0 I replace the hardness
				List<Block> blocksToProcess = blockHardness.getAllBlocks();
				for (Block block : blocksToProcess) {
					block.getStateDefinition().getPossibleStates().forEach(blockState -> blockState.destroySpeed = (float) blockHardness.hardness);
				}
			}
		}
	}

	/*@SubscribeEvent(priority = EventPriority.LOW)
	public void processSingleHardness(PlayerEvent.BreakSpeed event) {
		if (!this.isEnabled())
			return;

		if (this.customHardness.size() == 0)
			return;
		Level level = event.getPlayer().level;

		ResourceLocation dimensionId = level.dimension().location();

		BlockPos pos = event.getPos();
		BlockState blockState = level.getBlockState(pos);

		Block block = blockState.getBlock();
		double customHardness = getBlockSingleHardness(block, dimensionId);
		if (customHardness == -1d)
			return;
		double ratio = getRatio(customHardness, blockState, level, pos);
		if (ratio == -1d)
			event.setNewSpeed(Float.MAX_VALUE);
		else
			event.setNewSpeed(event.getNewSpeed() * (float) ratio);
	}

	//Returns -1 if it's hardness 0 (so instabreak)
	private static double getRatio(double newHardness, BlockState state, Level level, BlockPos pos) {
		//Add depth dimension multiplier
		double depthMultiplier = Modules.mining.globalHardness.getDepthHardnessMultiplier(state.getBlock(), level.dimension().location(), pos, true);
		double ratio = state.getDestroySpeed(level, pos) / newHardness;
		double multiplier = (1d / ratio) + depthMultiplier;
		if (multiplier == 0d)
			return -1d;
		return 1d / multiplier;
	}

	/**
	 * Returns -1 when the block has no custom hardness, the hardness otherwise
	 */
	/*public double getBlockSingleHardness(Block block, ResourceLocation dimensionId) {
		for (BlockHardness blockHardness : this.customHardness) {
			if (blockHardness.matchesBlock(block, dimensionId)) {
				return blockHardness.hardness;
			}
		}
		return -1d;
	}*/
}
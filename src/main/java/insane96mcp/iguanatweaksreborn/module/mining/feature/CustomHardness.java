package insane96mcp.iguanatweaksreborn.module.mining.feature;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.mining.utils.BlockHardness;
import insane96mcp.iguanatweaksreborn.setup.Config;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Custom Hardness", description = "Change specific blocks hardness as well as black and whitelist. Zero hardness blocks changes require a Minecraft restart.")
public class CustomHardness extends Feature {

	private final ForgeConfigSpec.ConfigValue<List<? extends String>> customHardnessConfig;

	private static final ArrayList<String> customHardnessDefault = Lists.newArrayList("minecraft:coal_ore,3", "minecraft:iron_ore,3.5", "minecraft:gold_ore,4.0", "minecraft:diamond_ore,4.5", "minecraft:ancient_debris,10", "minecraft:redstone_ore,3.5", "minecraft:lapis_ore,3.5", "minecraft:emerald_ore,4.5","minecraft:deepslate_coal_ore,4.5", "minecraft:deepslate_iron_ore,5.25", "minecraft:deepslate_gold_ore,6.0", "minecraft:deepslate_diamond_ore,6.75", "minecraft:deepslate_redstone_ore,5.25", "minecraft:deepslate_lapis_ore,5.25", "minecraft:deepslate_emerald_ore,6.75","minecraft:nether_quartz_ore,3", "minecraft:nether_gold_ore,3", "#iguanatweaksreborn:obsidians,40");

	public ArrayList<BlockHardness> customHardness;

	public CustomHardness(Module module) {
		super(Config.builder, module);
		Config.builder.comment(this.getDescription()).push(this.getName());
		customHardnessConfig = Config.builder
				.comment("Define custom blocks hardness, one string = one block/tag. Those blocks ARE AFFECTED by the global block hardness multiplier, unless put in the blacklisst.\n" +
						"The format is modid:blockid,hardness,dimensionid or #modid:tagid,hardness,dimensionid\n" +
						"E.g. 'minecraft:stone,5.0' will make stone have 5 hardness in every dimension (multiplied by Global Hardness).\n" +
						"E.g. '#forge:stone,5.0,minecraft:overworld' will make all the stone types have 5 hardness but only in the overworld.\n" +
						"As of 2.4.0 this now works with blocks that instantly break too (e.g. Torches)")
				.defineList("Custom Hardness", customHardnessDefault, o -> o instanceof String);
		Config.builder.pop();
	}

	@Override
	public void loadConfig() {
		super.loadConfig();
		customHardness = BlockHardness.parseStringList(this.customHardnessConfig.get());
		processZeroHardness();
	}

	private boolean processedZeroHardness = false;

	public void processZeroHardness() {
		if (!this.isEnabled())
			return;
		if (processedZeroHardness)
			return;
		for (BlockHardness blockHardness : this.customHardness) {
			//If the block's hardness is 0 I replace the hardness
			List<Block> blocksToProcess = blockHardness.getAllBlocks();
			for (Block block : blocksToProcess) {
				block.getStateDefinition().getPossibleStates().forEach(blockState -> {
					if (blockState.destroySpeed == 0f || blockHardness.has0Hardness) {
						blockState.destroySpeed = (float) blockHardness.hardness;
						blockHardness.has0Hardness = true;
					}
				});
			}
		}
		processedZeroHardness = true;
	}

	@SubscribeEvent
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
		event.setNewSpeed(event.getNewSpeed() * (float) ratio);
	}

	private static double getRatio(double newHardness, BlockState state, Level level, BlockPos pos) {
		//Add depth dimension multiplier
		double depthMultiplier = Modules.mining.globalHardness.getDepthHardnessMultiplier(state.getBlock(), level.dimension().location(), pos, true);
		double ratio = state.getDestroySpeed(level, pos) / newHardness;
		double multiplier = (1d / ratio) + depthMultiplier;
		return 1d / multiplier;
	}

	/**
	 * Returns -1 when the block has no custom hardness, the hardness otherwise
	 */
	public double getBlockSingleHardness(Block block, ResourceLocation dimensionId) {
		for (BlockHardness blockHardness : this.customHardness) {
			if (blockHardness.matchesBlock(block, dimensionId)) {
				return blockHardness.hardness;
			}
		}
		return -1d;
	}
}
package insane96mcp.iguanatweaksreborn.module.mining.feature;

import com.google.common.collect.Lists;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.mining.utils.BlockHardness;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;

@Label(name = "Custom Hardness", description = "Change specific blocks hardness as well as black and whitelist. Requires a Minecraft restart if you remove a block from the list.")
@LoadFeature(module = Modules.Ids.MINING)
public class CustomHardness extends Feature {

	private static ForgeConfigSpec.ConfigValue<List<? extends String>> customHardnessConfig;

	private static final ArrayList<String> customHardnessDefault = Lists.newArrayList(
			"minecraft:coal_ore,2.5", "minecraft:iron_ore,3", "minecraft:gold_ore,3.5", "minecraft:diamond_ore,4", "minecraft:redstone_ore,3", "minecraft:lapis_ore,3", "minecraft:emerald_ore,4",
			"minecraft:deepslate_coal_ore,4.5", "minecraft:deepslate_iron_ore,5", "minecraft:deepslate_gold_ore,5.5", "minecraft:deepslate_diamond_ore,6", "minecraft:deepslate_redstone_ore,5", "minecraft:deepslate_lapis_ore,5", "minecraft:deepslate_emerald_ore,6",
			"minecraft:ancient_debris,10", "#iguanatweaksreborn:obsidians,33");

	public static ArrayList<BlockHardness> customHardness;

	public CustomHardness(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void loadConfigOptions() {
		super.loadConfigOptions();
		customHardnessConfig = this.getBuilder()
				.comment("""
						Define custom blocks hardness, one string = one block/tag. Those blocks ARE AFFECTED by the global block hardness multiplier, unless put in the blacklist.
						The format is modid:blockid,hardness,dimensionid or #modid:tagid,hardness,dimensionid
						E.g. 'minecraft:stone,5.0' will make stone have 5 hardness in every dimension (multiplied by Global Hardness).
						E.g. '#forge:stone,5.0,minecraft:overworld' will make all the stone types have 5 hardness but only in the overworld.""")
				.defineList("Custom Hardness", customHardnessDefault, o -> o instanceof String);
	}

	@Override
	public void readConfig(final ModConfigEvent event) {
		super.readConfig(event);
		customHardness = BlockHardness.parseStringList(customHardnessConfig.get());
		processBlockHardness();
	}
	private final Object mutex = new Object();

	public void processBlockHardness() {
		if (!this.isEnabled()
				|| customHardness.isEmpty())
			return;

		synchronized (mutex) {
			for (BlockHardness blockHardness : customHardness) {
				//If the block's hardness is 0 I replace the hardness
				List<Block> blocksToProcess = blockHardness.getAllBlocks();
				for (Block block : blocksToProcess) {
					block.getStateDefinition().getPossibleStates().forEach(blockState -> blockState.destroySpeed = (float) blockHardness.hardness);
				}
			}
		}
	}
}
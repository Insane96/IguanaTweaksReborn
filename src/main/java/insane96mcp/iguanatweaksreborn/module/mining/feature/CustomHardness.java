package insane96mcp.iguanatweaksreborn.module.mining.feature;

import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.base.ITFeature;
import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.mining.utils.BlockHardness;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Custom Hardness", description = "Change specific blocks hardness as well as black and whitelist. Custom Hardness are controlled via json in this feature's folder. Requires a Minecraft restart if you remove a block from the list.")
@LoadFeature(module = Modules.Ids.MINING)
public class CustomHardness extends ITFeature {
	public static final ArrayList<BlockHardness> CUSTOM_HARDNESSES_DEFAULT = new ArrayList<>(Arrays.asList(
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:coal_ore", 2.5d),
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:iron_ore", 3d),
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:gold_ore", 3.5d),
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:diamond_ore", 4d),
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:redstone_ore", 3d),
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:lapis_ore", 3d),
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:emerald_ore", 4d),

			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:deepslate_coal_ore", 4.5d),
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:deepslate_iron_ore", 5d),
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:deepslate_gold_ore", 5.5d),
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:deepslate_diamond_ore", 6d),
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:deepslate_redstone_ore", 5d),
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:deepslate_lapis_ore", 5d),
			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:deepslate_emerald_ore", 6d),

			new BlockHardness(IdTagMatcher.Type.ID, "minecraft:ancient_debris", 10d),
			new BlockHardness(IdTagMatcher.Type.TAG, "iguanatweaksreborn:obsidians", 33d)
	));
	public static final ArrayList<BlockHardness> customHardnesses = new ArrayList<>();

	public CustomHardness(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	static final Type blockHardnessListType = new TypeToken<ArrayList<BlockHardness>>(){}.getType();
	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
		this.loadAndReadFile("custom_hardnesses.json", customHardnesses, CUSTOM_HARDNESSES_DEFAULT, blockHardnessListType);

		processBlockHardness();
	}

	private final Object mutex = new Object();

	public void processBlockHardness() {
		if (!this.isEnabled()
				|| customHardnesses.isEmpty())
			return;

		synchronized (mutex) {
			for (BlockHardness blockHardness : customHardnesses) {
				//If the block's hardness is 0 I replace the hardness
				List<Block> blocksToProcess = blockHardness.getAllBlocks();
				for (Block block : blocksToProcess) {
					block.getStateDefinition().getPossibleStates().forEach(blockState -> blockState.destroySpeed = (float) blockHardness.hardness);
				}
			}
		}
	}
}
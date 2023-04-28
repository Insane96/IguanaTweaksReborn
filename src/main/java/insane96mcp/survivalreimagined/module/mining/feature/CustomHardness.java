package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.insanelib.util.IdTagMatcher;
import insane96mcp.survivalreimagined.base.SRFeature;
import insane96mcp.survivalreimagined.data.IdTagValue;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.network.message.JsonConfigSyncMessage;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Label(name = "Custom Hardness", description = "Change specific blocks hardness as well as black and whitelist. Custom Hardness are controlled via json in this feature's folder. Requires a Minecraft restart if you remove a block from the list.")
@LoadFeature(module = Modules.Ids.MINING)
public class CustomHardness extends SRFeature {
	public static final ArrayList<IdTagValue> CUSTOM_HARDNESSES_DEFAULT = new ArrayList<>(Arrays.asList(
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
			new IdTagValue(IdTagMatcher.Type.TAG, "survivalreimagined:obsidians", 33d)
	));
	public static final ArrayList<IdTagValue> customHardnesses = new ArrayList<>();

	public CustomHardness(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		JSON_CONFIGS.add(new JsonConfig<>("custom_hardnesses.json", customHardnesses, CUSTOM_HARDNESSES_DEFAULT, IdTagValue.LIST_TYPE, CustomHardness::processBlockHardness, true, JsonConfigSyncMessage.ConfigType.DURABILITIES));
	}

	@Override
	public void loadJsonConfigs() {
		if (!this.isEnabled())
			return;
		super.loadJsonConfigs();
	}

	public static void handleCustomBlockHardnessPacket(String json) {
		loadAndReadJson(json, customHardnesses, CUSTOM_HARDNESSES_DEFAULT, IdTagValue.LIST_TYPE);
		//processBlockHardness(customHardnesses, true);
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
}
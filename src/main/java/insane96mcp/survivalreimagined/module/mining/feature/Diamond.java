package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.IntegratedDataPacks;
import net.minecraft.server.packs.PackType;

@Label(name = "Diamond", description = "Various changes for diamonds")
@LoadFeature(module = Modules.Ids.MINING)
public class Diamond extends Feature {
	@Config
	@Label(name = "Equipment Crafting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Diamond Armor requires Gold armor to be crafted in an anvil
			* Diamond Armor require Gold tools to be crafted in an anvil""")
	public static Boolean equipmentCraftingDataPack = true;

	public Diamond(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "diamond_equipment_crafting", net.minecraft.network.chat.Component.literal("Survival Reimagined Diamond Equipment Crafting"), () -> this.isEnabled() && equipmentCraftingDataPack));
	}
}

package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;

@Label(name = "Misc Materials", description = "Various changes for different materials.")
@LoadFeature(module = Modules.Ids.MINING)
public class MiscMaterials extends Feature {
	@Config
	@Label(name = "Copper Smelting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Smelting copper in a furnace takes 4x time""")
	public static Boolean copperSmeltingDataPack = true;
	@Config
	@Label(name = "Alloying Netherite Data Pack", description = "Enables a data pack that adds a recipe to alloy netherite in a Blast or Soul furnace, requiring less materials")
	public static Boolean alloyingNetheriteDataPack = true;

	public MiscMaterials(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "copper_smelting", Component.literal("Survival Reimagined Copper Smelting"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && copperSmeltingDataPack));
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "alloying_netherite", Component.literal("Survival Reimagined Netherite Alloy"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && alloyingNetheriteDataPack));
	}
}

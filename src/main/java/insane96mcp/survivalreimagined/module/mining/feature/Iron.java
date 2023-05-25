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

@Label(name = "Iron", description = "Various changes for iron")
@LoadFeature(module = Modules.Ids.MINING)
public class Iron extends Feature {

	@Config
	@Label(name = "Farmable Iron data pack", description = """
			Enables the following changes to vanilla data pack:
			* Stone (Broken with a non Silk-Touch tool) can drop Iron Nuggets
			* Silverfish can drop Iron Nuggets""")
	public static Boolean farmableIronDataPack = true;

	@Config
	@Label(name = "Iron Smelting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Smelting iron in a furnace takes 4x time""")
	public static Boolean ironSmeltingDataPack = true;

	public Iron(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "farmable_iron", Component.literal("Survival Reimagined Farmable Iron"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && farmableIronDataPack));
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "iron_smelting", Component.literal("Survival Reimagined Iron Smelting"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && ironSmeltingDataPack));
	}
}

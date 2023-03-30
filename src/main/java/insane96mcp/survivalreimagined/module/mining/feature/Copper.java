package insane96mcp.survivalreimagined.module.mining.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.server.packs.PackType;

@Label(name = "Copper", description = "Various changes for copper")
@LoadFeature(module = Modules.Ids.MINING)
public class Copper extends Feature {
	@Config
	@Label(name = "Copper Smelting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Smelting copper in a furnace takes 4x time""")
	public static Boolean copperSmeltingDataPack = true;

	public Copper(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "copper_smelting", net.minecraft.network.chat.Component.literal("Survival Reimagined Copper Smelting"), () -> this.isEnabled() && copperSmeltingDataPack));
	}
}

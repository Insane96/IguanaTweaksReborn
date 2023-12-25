package insane96mcp.iguanatweaksreborn.module.mining;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.iguanatweaksreborn.module.misc.DataPacks;
import insane96mcp.iguanatweaksreborn.setup.IntegratedDataPack;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;

@Label(name = "Misc Materials", description = "Various changes for different materials.")
@LoadFeature(module = Modules.Ids.MINING)
public class MiscMaterials extends Feature {
	@Config
	@Label(name = "Copper Smelting Data Pack", description = """
			Enables the following changes to vanilla data pack:
			* Smelting copper in a furnace takes 2x time""")
	public static Boolean copperSmeltingDataPack = true;

	public MiscMaterials(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "copper_smelting", Component.literal("Survival Reimagined Copper Smelting"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && copperSmeltingDataPack));
	}
}

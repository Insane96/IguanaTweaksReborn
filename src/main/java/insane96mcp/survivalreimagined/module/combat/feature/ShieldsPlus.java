package insane96mcp.survivalreimagined.module.combat.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.server.packs.PackType;

@Label(name = "Shields+", description = "Changes to Shields+.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class ShieldsPlus extends Feature {
	@Config
	@Label(name = "Shields+ Compat DataPack", description = "Removes wooden shields and changes the crafting recipes of metal shields.")
	public static Boolean shieldsPlusCompatDataPack = true;

	public ShieldsPlus(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "shields", net.minecraft.network.chat.Component.literal("Survival Reimagined Shields"), () -> super.isEnabled() && shieldsPlusCompatDataPack));
	}
}
package insane96mcp.survivalreimagined.module.combat.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.shieldsplus.module.base.feature.BaseFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Label(name = "Shields+", description = "Changes to Shields+.")
@LoadFeature(module = Modules.Ids.COMBAT)
public class ShieldsPlus extends Feature {
	@Config
	@Label(name = "Shields+ Compat DataPack", description = "Removes wooden shields and changes the crafting recipes of metal shields.")
	public static Boolean shieldsPlusCompatDataPack = true;

	public ShieldsPlus(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "shields", Component.literal("Survival Reimagined Shields"), () -> super.isEnabled() && !DataPacks.disableAllDataPacks && shieldsPlusCompatDataPack));
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);

		Module.getFeature(BaseFeature.class).setConfigOption("Min Shield Hurt Damage", 1.5d);
		Module.getFeature(BaseFeature.class).readConfig(event);
	}
}
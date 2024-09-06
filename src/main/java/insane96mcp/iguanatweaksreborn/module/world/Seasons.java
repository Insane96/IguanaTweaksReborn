package insane96mcp.iguanatweaksreborn.module.world;

import insane96mcp.iguanatweaksreborn.module.Modules;
import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import sereneseasons.config.FertilityConfig;

@Label(name = "Seasons", description = "Change a few things relative to Serene Seasons")
@LoadFeature(module = Modules.Ids.WORLD)
public class Seasons extends Feature {
	@Config
	@Label(name = "Serene Seasons changes", description = """
			Makes the following changes to Serene Seasons config:
			* seasonal_crops is set to false, as it's controlled by Plants Growth
			""")
	public static Boolean changeSereneSeasonsConfig = true;

	public Seasons(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);

		if (ModList.get().isLoaded("sereneseasons") && changeSereneSeasonsConfig)
			FertilityConfig.seasonalCrops.set(false);
	}
}
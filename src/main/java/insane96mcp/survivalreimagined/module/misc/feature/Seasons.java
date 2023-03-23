package insane96mcp.survivalreimagined.module.misc.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.setup.IntegratedDataPacks;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import sereneseasons.config.FertilityConfig;

@Label(name = "Seasons", description = "Change a few things relative to Serene Seasons")
@LoadFeature(module = Modules.Ids.MISC)
public class Seasons extends Feature {

	@Config
	@Label(name = "Serene Seasons changes", description = "Normal glass no longer counts as greenhouse glass and saplings no longer grow in Winter.")
	public static Boolean sereneSeasonsChanges = true;

	@Config
	@Label(name = "No Saplings in Winter", description = "Saplings no longer drop in Winter")
	public static Boolean noSaplingsInWinter = true;

	@Config
	@Label(name = "Serene Seasons changes", description = """
			Makes the following changes to Serene Seasons config:
			* out_of_season_crop_behavior is set to 2 so out of season crops break when trying to grow
			* underground_fertility_level is set to -1 so out of season crops can never grow, even underground
			""")
	public static Boolean changeSereneSeasonsConfig = true;

	public Seasons(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "serene_seasons_changes", net.minecraft.network.chat.Component.literal("Survival Reimagined Serene Seasons Changes"), () -> this.isEnabled() && sereneSeasonsChanges));
		IntegratedDataPacks.INTEGRATED_DATA_PACKS.add(new IntegratedDataPacks.IntegratedDataPack(PackType.SERVER_DATA, "no_saplings_in_winter", net.minecraft.network.chat.Component.literal("Survival Reimagined No Saplings in Winter"), () -> this.isEnabled() && noSaplingsInWinter));
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);

		if (changeSereneSeasonsConfig) {
			FertilityConfig.outOfSeasonCropBehavior.set(2);
			FertilityConfig.undergroundFertilityLevel.set(-1);
		}
	}
}
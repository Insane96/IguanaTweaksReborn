package insane96mcp.survivalreimagined.module.world.feature;

import insane96mcp.insanelib.base.Feature;
import insane96mcp.insanelib.base.Label;
import insane96mcp.insanelib.base.Module;
import insane96mcp.insanelib.base.config.Config;
import insane96mcp.insanelib.base.config.LoadFeature;
import insane96mcp.survivalreimagined.module.Modules;
import insane96mcp.survivalreimagined.module.misc.feature.DataPacks;
import insane96mcp.survivalreimagined.setup.IntegratedDataPack;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.FertilityConfig;
import sereneseasons.config.ServerConfig;
import sereneseasons.handler.season.SeasonHandler;
import sereneseasons.season.SeasonSavedData;
import sereneseasons.season.SeasonTime;

@Label(name = "Seasons", description = "Change a few things relative to Serene Seasons")
@LoadFeature(module = Modules.Ids.WORLD)
public class Seasons extends Feature {

	@Config
	@Label(name = "Serene Seasons changes", description = "Normal glass no longer counts as greenhouse glass, saplings no longer grow in Winter and starting season is mid summer.")
	public static Boolean sereneSeasonsChanges = true;

	@Config
	@Label(name = "No Saplings in Winter", description = "Saplings no longer drop in Winter")
	public static Boolean noSaplingsInWinter = true;

	@Config
	@Label(name = "Serene Seasons changes", description = """
			Makes the following changes to Serene Seasons config:
			Makes the following changes to Serene Seasons config:
			* seasonal_crops is set to false, as it's controlled by PlantsGrowth
			* Sets the starting season to mid summer
			""")
	public static Boolean changeSereneSeasonsConfig = true;

	@Config
	@Label(name = "Season based fishing time")
	public static Boolean seasonBasedFishingTime = true;

	public Seasons(Module module, boolean enabledByDefault, boolean canBeDisabled) {
		super(module, enabledByDefault, canBeDisabled);
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "serene_seasons_changes", net.minecraft.network.chat.Component.literal("Survival Reimagined Serene Seasons Changes"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && sereneSeasonsChanges));
		IntegratedDataPack.INTEGRATED_DATA_PACKS.add(new IntegratedDataPack(PackType.SERVER_DATA, "no_saplings_in_winter", net.minecraft.network.chat.Component.literal("Survival Reimagined No Saplings in Winter"), () -> this.isEnabled() && !DataPacks.disableAllDataPacks && noSaplingsInWinter));
	}

	@Override
	public void readConfig(ModConfigEvent event) {
		super.readConfig(event);

		if (changeSereneSeasonsConfig) {
			FertilityConfig.seasonalCrops.set(false);
		}
	}

	@SubscribeEvent
	public void onServerStart(ServerStartedEvent event) {
		if (changeSereneSeasonsConfig) {
			ServerConfig.startingSubSeason.set(5);
			ServerConfig.progressSeasonWhileOffline.set(false);
		}
	}

	@SubscribeEvent
	public void onPreLevelTick(TickEvent.LevelTickEvent event) {
		if (!event.level.isClientSide && event.level.getGameTime() == 0 && changeSereneSeasonsConfig) {
			SeasonSavedData seasonData = SeasonHandler.getSeasonSavedData(event.level);
			seasonData.seasonCycleTicks = SeasonTime.ZERO.getSubSeasonDuration() * Season.SubSeason.MID_SUMMER.ordinal();
			//seasonData.seasonCycleTicks = event.level.random.nextInt(12) * SeasonTime.ZERO.getSubSeasonDuration();
			seasonData.setDirty();
			SeasonHandler.sendSeasonUpdate(event.level);
		}
	}

	public static boolean shouldSlowdownFishing(Level level) {
		if (!Feature.isEnabled(Seasons.class)
				|| !seasonBasedFishingTime)
			return false;

		Season season = SeasonHelper.getSeasonState(level).getSeason();
		//Chance to slowdown fishing
		float rng = switch (season) {
			case SPRING -> 0.1F;
			case SUMMER -> 0.0F;
			case AUTUMN -> 0.2F;
			case WINTER -> 0.5F;
		};
		return level.getRandom().nextFloat() < rng;
	}
}
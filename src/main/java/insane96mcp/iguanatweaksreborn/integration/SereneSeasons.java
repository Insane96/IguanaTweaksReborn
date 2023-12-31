package insane96mcp.iguanatweaksreborn.integration;

import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

public class SereneSeasons {
	public static boolean doesSeasonMatch(Object season, Level level) {
		return (SeasonHelper.getSeasonState(level).getSeason().equals(season));
	}

	public static Object deserializeSeason(JsonObject jObject, String memberName) {
		Season season = null;
		if (jObject.has(memberName))
			season = Season.valueOf(GsonHelper.getAsString(jObject, "season"));
		return season;
	}

	public static String serializeSeason(Object object) {
		return String.valueOf(object);
	}
}

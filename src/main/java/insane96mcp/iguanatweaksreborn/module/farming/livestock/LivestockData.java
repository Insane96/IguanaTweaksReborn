package insane96mcp.iguanatweaksreborn.module.farming.livestock;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.data.IdTagMatcher;
import insane96mcp.insanelib.data.IdTagValue;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.ArrayList;

@JsonAdapter(LivestockData.Serializer.class)
public class LivestockData extends IdTagValue {
	@Nullable
	private final Season season;

	public LivestockData(IdTagMatcher idTagMatcher, double value) {
		this(idTagMatcher, value, null);
	}

	public LivestockData(IdTagMatcher idTagMatcher, double value, @Nullable Season season) {
		super(idTagMatcher, value);
		this.season = season;
	}

	public boolean matches(Entity entity) {
		if (season != null && !season.equals(SeasonHelper.getSeasonState(entity.level()).getSeason()))
			return false;
		return this.id.matchesEntity(entity);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<LivestockData>>(){}.getType();

	public static class Serializer implements JsonDeserializer<LivestockData>, JsonSerializer<LivestockData> {
		@Override
		public LivestockData deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			IdTagMatcher idTagMatcher = context.deserialize(json.getAsJsonObject().get("entity"), IdTagMatcher.class);

			Season season = null;
			if (json.getAsJsonObject().has("season"))
				season = Season.valueOf(GsonHelper.getAsString(json.getAsJsonObject(), "season"));

			return new LivestockData(idTagMatcher, GsonHelper.getAsDouble(json.getAsJsonObject(), "value"), season);
		}

		@Override
		public JsonElement serialize(LivestockData src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", context.serialize(src.id));
			jsonObject.addProperty("value", src.value);
			if (src.season != null)
				jsonObject.addProperty("season", String.valueOf(src.season));
			return jsonObject;
		}
	}
}
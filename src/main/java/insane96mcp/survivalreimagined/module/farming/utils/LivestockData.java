package insane96mcp.survivalreimagined.module.farming.utils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

import java.util.ArrayList;

@JsonAdapter(LivestockData.Serializer.class)
public class LivestockData extends IdTagMatcher {
	private double value;
	@Nullable
	private Season season;

	public LivestockData(Type type, String location) {
		super(type, location);
	}

	public LivestockData(Type type, String location, double value, Season season) {
		super(type, location);
		this.value = value;
		this.season = season;
	}

	public double getValue() {
		return value;
	}

	public boolean matches(Entity entity) {
		if (season != null && !season.equals(SeasonHelper.getSeasonState(entity.level).getSeason()))
			return false;
		return this.matchesEntity(entity);
	}

	public static final java.lang.reflect.Type livestockDataListType = new TypeToken<ArrayList<LivestockData>>(){}.getType();

	public static class Serializer implements JsonDeserializer<LivestockData>, JsonSerializer<LivestockData> {
		@Override
		public LivestockData deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");
			String tag = GsonHelper.getAsString(json.getAsJsonObject(), "tag", "");

			if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id: %s".formatted(id));
			}
			if (!tag.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid tag: %s".formatted(tag));
			}

			LivestockData livestockData;
			if (!id.equals("") && !tag.equals("")){
				throw new JsonParseException("Invalid object containing both tag (%s) and id (%s)".formatted(tag, id));
			}
			else if (!id.equals("")) {
				livestockData = new LivestockData(Type.ID, id);
			}
			else if (!tag.equals("")){
				livestockData = new LivestockData(Type.TAG, tag);
			}
			else {
				throw new JsonParseException("Invalid object missing either tag and id");
			}

			String dimension = GsonHelper.getAsString(json.getAsJsonObject(), "dimension", "");
			if (!dimension.equals("")) {
				if (!ResourceLocation.isValidResourceLocation(dimension)) {
					throw new JsonParseException("Invalid dimension: %s".formatted(dimension));
				}
				else {
					livestockData.dimension = ResourceLocation.tryParse(dimension);
				}
			}

			livestockData.value = GsonHelper.getAsDouble(json.getAsJsonObject(), "value");
			if (json.getAsJsonObject().has("season"))
				livestockData.season = Season.valueOf(GsonHelper.getAsString(json.getAsJsonObject(), "season"));

			return livestockData;
		}

		@Override
		public JsonElement serialize(LivestockData src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			if (src.type == Type.ID) {
				jsonObject.addProperty("id", src.location.toString());
			}
			else if (src.type == Type.TAG) {
				jsonObject.addProperty("tag", src.location.toString());
			}
			if (src.dimension != null) {
				jsonObject.addProperty("dimension", src.dimension.toString());
			}
			jsonObject.addProperty("value", src.value);
			if (src.season != null)
				jsonObject.addProperty("season", String.valueOf(src.season));
			return jsonObject;
		}
	}
}
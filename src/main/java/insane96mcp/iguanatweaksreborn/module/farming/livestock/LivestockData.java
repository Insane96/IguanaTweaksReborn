package insane96mcp.iguanatweaksreborn.module.farming.livestock;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.iguanatweaksreborn.integration.SereneSeasons;
import insane96mcp.insanelib.data.IdTagMatcher;
import insane96mcp.insanelib.data.IdTagValue;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@JsonAdapter(LivestockData.Serializer.class)
public class LivestockData extends IdTagValue {
	@Nullable
	private final Object season;

	public LivestockData(IdTagMatcher idTagMatcher, double value) {
		this(idTagMatcher, value, null);
	}

	public LivestockData(IdTagMatcher idTagMatcher, double value, @Nullable Object season) {
		super(idTagMatcher, value);
		this.season = season;
	}

	public boolean matches(Entity entity) {
		if (this.season != null && !SereneSeasons.doesSeasonMatch(this.season, entity.level()))
			return false;
		return this.id.matchesEntity(entity);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<LivestockData>>(){}.getType();

	public static class Serializer implements JsonDeserializer<LivestockData>, JsonSerializer<LivestockData> {
		@Override
		public LivestockData deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jObject = json.getAsJsonObject();
			IdTagMatcher idTagMatcher = context.deserialize(jObject.get("entity"), IdTagMatcher.class);
			Object season = null;
			if (jObject.has("season")) {
				if (!ModList.get().isLoaded("sereneseasons"))
					throw new JsonParseException("Tried deserializing Livestock Data `season` but Serene Season is not installed");
				season = SereneSeasons.deserializeSeason(jObject, "season");
			}
			return new LivestockData(idTagMatcher, GsonHelper.getAsDouble(jObject, "value"), season);
		}

		@Override
		public JsonElement serialize(LivestockData src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", context.serialize(src.id));
			jsonObject.addProperty("value", src.value);
			if (src.season != null) {
				if (!ModList.get().isLoaded("sereneseasons"))
					throw new JsonParseException("Tried serializing Livestock Data `season` but Serene Season is not installed");
				jsonObject.addProperty("season", SereneSeasons.serializeSeason(src.season));
			}
			return jsonObject;
		}
	}
}
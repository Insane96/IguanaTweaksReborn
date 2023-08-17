package insane96mcp.survivalreimagined.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;

@JsonAdapter(IdTagRange.Serializer.class)
public class IdTagRange extends IdTagMatcher {
	public float min;
	public float max;

	public IdTagRange(Type type, String id) {
		super(type, id);
	}

	public IdTagRange(Type type, String id, float min, float max) {
		super(type, id);
		this.min = min;
		this.max = max;
	}

	public IdTagRange(Type type, String id, float min) {
		this(type, id, min, min);
	}

	public float getRandomBetween(RandomSource random) {
		return Mth.nextFloat(random, this.min, this.max);
	}

	public int getRandomIntBetween(RandomSource random) {
		return Mth.nextInt(random, (int) this.min, (int) this.max);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<IdTagRange>>(){}.getType();

	public static class Serializer implements JsonDeserializer<IdTagRange>, JsonSerializer<IdTagRange> {
		@Override
		public IdTagRange deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");
			String tag = GsonHelper.getAsString(json.getAsJsonObject(), "tag", "");

			if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id: %s".formatted(id));
			}
			if (!tag.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid tag: %s".formatted(tag));
			}

			IdTagRange idTagValue;
			if (!id.equals("") && !tag.equals("")){
				throw new JsonParseException("Invalid object containing both tag (%s) and id (%s)".formatted(tag, id));
			} else if (!id.equals("")) {
				idTagValue = new IdTagRange(Type.ID, id);
			} else if (!tag.equals("")){
				idTagValue = new IdTagRange(Type.TAG, tag);
			} else {
				throw new JsonParseException("Invalid object missing either tag and id");
			}

			float min = GsonHelper.getAsFloat(json.getAsJsonObject(), "min");
			float max;
			if (json.getAsJsonObject().has("max"))
				max = GsonHelper.getAsFloat(json.getAsJsonObject(), "max");
			else
				max = min;
			if (min > max)
				throw new JsonParseException("min (%f) can't be bigger than max (%f) in %s".formatted(min, max, idTagValue));
			idTagValue.min = min;
			idTagValue.max = max;

			return idTagValue;
		}

		@Override
		public JsonElement serialize(IdTagRange src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			if (src.type == Type.ID) {
				jsonObject.addProperty("id", src.location.toString());
			} else if (src.type == Type.TAG) {
				jsonObject.addProperty("tag", src.location.toString());
			}
			jsonObject.addProperty("min", src.min);
			if (src.min != src.max)
				jsonObject.addProperty("max", src.max);

			return jsonObject;
		}
	}
}
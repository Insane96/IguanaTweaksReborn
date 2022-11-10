package insane96mcp.iguanatweaksreborn.module.misc.utils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

@JsonAdapter(IdTagValue.Serializer.class)
public class IdTagValue extends IdTagMatcher {
	public double value;

	public IdTagValue(Type type, String id) {
		super(type, id);
	}

	public IdTagValue(Type type, String id, double value) {
		super(type, id);
		this.value = value;
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<IdTagValue>>(){}.getType();

	public static class Serializer implements JsonDeserializer<IdTagValue>, JsonSerializer<IdTagValue> {
		@Override
		public IdTagValue deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");
			String tag = GsonHelper.getAsString(json.getAsJsonObject(), "tag", "");

			if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id: %s".formatted(id));
			}
			if (!tag.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid tag: %s".formatted(tag));
			}

			IdTagValue idTagValue;
			if (!id.equals("") && !tag.equals("")){
				throw new JsonParseException("Invalid object containing both tag (%s) and id (%s)".formatted(tag, id));
			}
			else if (!id.equals("")) {
				idTagValue = new IdTagValue(Type.ID, id);
			}
			else if (!tag.equals("")){
				idTagValue = new IdTagValue(Type.TAG, tag);
			}
			else {
				throw new JsonParseException("Invalid object missing either tag and id");
			}

			idTagValue.value = GsonHelper.getAsDouble(json.getAsJsonObject(), "value");

			return idTagValue;
		}

		@Override
		public JsonElement serialize(IdTagValue src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			if (src.type == Type.ID) {
				jsonObject.addProperty("id", src.location.toString());
			}
			else if (src.type == Type.TAG) {
				jsonObject.addProperty("tag", src.location.toString());
			}
			jsonObject.addProperty("value", src.value);

			return jsonObject;
		}
	}
}
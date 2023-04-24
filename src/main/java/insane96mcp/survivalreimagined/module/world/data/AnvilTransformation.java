package insane96mcp.survivalreimagined.module.world.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

@JsonAdapter(AnvilTransformation.Serializer.class)
public class AnvilTransformation extends IdTagMatcher {

	public ResourceLocation to;

	public AnvilTransformation(Type type, String location, String to) {
		super(type, location);
		this.to = ResourceLocation.tryParse(to);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<AnvilTransformation>>(){}.getType();

	public static class Serializer implements JsonDeserializer<AnvilTransformation>, JsonSerializer<AnvilTransformation> {
		@Override
		public AnvilTransformation deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String from = GsonHelper.getAsString(json.getAsJsonObject(), "from", "");
			Type type = Type.ID;
			if (from.startsWith("#")) {
				type = Type.TAG;
				from = from.substring(1);
			}
			if (!from.equals("") && !ResourceLocation.isValidResourceLocation(from)) {
				throw new JsonParseException("Invalid from: %s".formatted(from));
			}

			String to = GsonHelper.getAsString(json.getAsJsonObject(), "to", "");
			if (!to.equals("") && !ResourceLocation.isValidResourceLocation(to)) {
				throw new JsonParseException("Invalid from: %s".formatted(to));
			}

			return new AnvilTransformation(type, from, to);
		}

		@Override
		public JsonElement serialize(AnvilTransformation src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			if (src.type == Type.ID) {
				jsonObject.addProperty("from", src.location.toString());
			} else if (src.type == Type.TAG) {
				jsonObject.addProperty("from", "#" + src.location.toString());
			}
			jsonObject.addProperty("to", src.to.toString());
			return jsonObject;
		}
	}
}
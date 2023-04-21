package insane96mcp.survivalreimagined.module.movement.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

@JsonAdapter(ArmorMaterialWeight.Serializer.class)
public class ArmorMaterialWeight {
	public String id;
	public double totalWeight;

	public ArmorMaterialWeight(String id) {
		this.id = id;
	}

	public ArmorMaterialWeight(String id, double totalWeight) {
		this.id = id;
		this.totalWeight = totalWeight;
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<ArmorMaterialWeight>>(){}.getType();
	public static class Serializer implements JsonDeserializer<ArmorMaterialWeight>, JsonSerializer<ArmorMaterialWeight> {
		@Override
		public ArmorMaterialWeight deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id");
			ArmorMaterialWeight armorMaterialWeight = new ArmorMaterialWeight(id);
			armorMaterialWeight.totalWeight = GsonHelper.getAsDouble(json.getAsJsonObject(), "total_weight");

			return armorMaterialWeight;
		}

		@Override
		public JsonElement serialize(ArmorMaterialWeight src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", src.id);
			jsonObject.addProperty("total_weight", src.totalWeight);

			return jsonObject;
		}
	}
}

package insane96mcp.survivalreimagined.module.movement.weightedequipment;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

@JsonAdapter(ArmorEnchantmentWeight.Serializer.class)
public class ArmorEnchantmentWeight extends IdTagMatcher {
	public double percentageSlownessPerLevel = 0d;
	public double flatSlownessPerLevel = 0d;
	public double percentageSlowness = 0d;
	public double flatSlowness = 0d;

	public ArmorEnchantmentWeight(String id) {
		super(Type.ID, id);
	}

	public ArmorEnchantmentWeight(String id, double percentageSlownessPerLevel) {
		super(Type.ID, id);
		this.percentageSlownessPerLevel = percentageSlownessPerLevel;
	}

	public ArmorEnchantmentWeight(String id, double percentageSlownessPerLevel, double flatSlowness) {
		super(Type.ID, id);
		this.percentageSlownessPerLevel = percentageSlownessPerLevel;
		this.flatSlowness = flatSlowness;
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<ArmorEnchantmentWeight>>(){}.getType();

	public static class Serializer implements JsonDeserializer<ArmorEnchantmentWeight>, JsonSerializer<ArmorEnchantmentWeight> {
		@Override
		public ArmorEnchantmentWeight deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id");

			if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id: %s".formatted(id));
			}

			ArmorEnchantmentWeight armorEnchantmentWeight = new ArmorEnchantmentWeight(id);

			armorEnchantmentWeight.percentageSlownessPerLevel = GsonHelper.getAsDouble(json.getAsJsonObject(), "percentage_slowness_per_level", 0d);
			armorEnchantmentWeight.flatSlownessPerLevel = GsonHelper.getAsDouble(json.getAsJsonObject(), "flat_slowness_per_level", 0d);
			armorEnchantmentWeight.percentageSlowness = GsonHelper.getAsDouble(json.getAsJsonObject(), "percentage_slowness", 0d);
			armorEnchantmentWeight.flatSlowness = GsonHelper.getAsDouble(json.getAsJsonObject(), "flat_slowness", 0d);

			return armorEnchantmentWeight;
		}

		@Override
		public JsonElement serialize(ArmorEnchantmentWeight src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", src.location.toString());
			if (src.percentageSlownessPerLevel != 0d)
				jsonObject.addProperty("percentage_slowness_per_level", src.percentageSlownessPerLevel);
			if (src.flatSlownessPerLevel != 0d)
				jsonObject.addProperty("flat_slowness_per_level", src.flatSlownessPerLevel);
			if (src.percentageSlowness != 0d)
				jsonObject.addProperty("percentage_slowness", src.percentageSlowness);
			if (src.flatSlowness != 0d)
				jsonObject.addProperty("flat_slowness", src.flatSlowness);

			return jsonObject;
		}
	}
}
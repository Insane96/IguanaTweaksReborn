package insane96mcp.survivalreimagined.module.mining.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.survivalreimagined.data.IdTagValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

/**
 * In this case the {@link IdTagValue#value} field is used per block below the {@link DepthHardnessDimension#applyBelowY} level
 */
@JsonAdapter(DepthHardnessDimension.Serializer.class)
public class DepthHardnessDimension extends IdTagValue {

	public int applyBelowY;
	public int stopAt;

	public DepthHardnessDimension() {
		super(Type.ID, "minecraft:air");
	}

	public DepthHardnessDimension(String dimension, double multiplier, int applyBelowY, int stopAt) {
		super(dimension, multiplier);
		this.applyBelowY = applyBelowY;
		this.stopAt = stopAt;
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<DepthHardnessDimension>>(){}.getType();

	public static class Serializer implements JsonDeserializer<DepthHardnessDimension>, JsonSerializer<DepthHardnessDimension> {
		@Override
		public DepthHardnessDimension deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			DepthHardnessDimension dimensionHardnessMultiplier = new DepthHardnessDimension();
			String dimension = GsonHelper.getAsString(json.getAsJsonObject(), "dimension", "");
			if (!dimension.equals("")) {
				if (!ResourceLocation.isValidResourceLocation(dimension)) {
					throw new JsonParseException("Invalid dimension: %s".formatted(dimension));
				}
				else {
					dimensionHardnessMultiplier.dimension = ResourceLocation.tryParse(dimension);
				}
			}

			dimensionHardnessMultiplier.value = GsonHelper.getAsDouble(json.getAsJsonObject(), "multiplier");
			dimensionHardnessMultiplier.applyBelowY = GsonHelper.getAsInt(json.getAsJsonObject(), "apply_below_y");
			dimensionHardnessMultiplier.stopAt = GsonHelper.getAsInt(json.getAsJsonObject(), "stop_at");

			return dimensionHardnessMultiplier;
		}

		@Override
		public JsonElement serialize(DepthHardnessDimension src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			if (src.dimension != null) {
				jsonObject.addProperty("dimension", src.dimension.toString());
			}
			jsonObject.addProperty("multiplier", src.value);
			jsonObject.addProperty("apply_below_y", src.applyBelowY);
			jsonObject.addProperty("stop_at", src.stopAt);

			return jsonObject;
		}
	}
}
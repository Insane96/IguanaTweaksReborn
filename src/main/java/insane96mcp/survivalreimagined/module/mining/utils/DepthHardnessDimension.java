package insane96mcp.survivalreimagined.module.mining.utils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

/**
 * In this case the {@link DepthHardnessDimension#multiplier} field is used per block below the {@link DepthHardnessDimension#applyBelowY} level
 */
@JsonAdapter(DepthHardnessDimension.Serializer.class)
public class DepthHardnessDimension extends DimensionHardnessMultiplier {

	public int applyBelowY;
	public int stopAt;

	public DepthHardnessDimension() {
		super();
	}

	public DepthHardnessDimension(String dimension, double multiplier, int applyBelowY, int stopAt) {
		super(dimension, multiplier);
		this.applyBelowY = applyBelowY;
		this.stopAt = stopAt;
	}

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

			dimensionHardnessMultiplier.multiplier = GsonHelper.getAsDouble(json.getAsJsonObject(), "multiplier");
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
			jsonObject.addProperty("multiplier", src.multiplier);
			jsonObject.addProperty("apply_below_y", src.applyBelowY);
			jsonObject.addProperty("stop_at", src.stopAt);

			return jsonObject;
		}
	}
}
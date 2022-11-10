package insane96mcp.iguanatweaksreborn.module.mining.utils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

@JsonAdapter(DimensionHardnessMultiplier.Serializer.class)
public class DimensionHardnessMultiplier extends IdTagMatcher {
	public double multiplier;

	public DimensionHardnessMultiplier() {
		super(Type.ID, "minecraft:air");
	}

	public DimensionHardnessMultiplier(String dimension, double multiplier) {
		super(Type.ID, "minecraft:air", dimension);
		this.multiplier = multiplier;
	}

	public static class Serializer implements JsonDeserializer<DimensionHardnessMultiplier>, JsonSerializer<DimensionHardnessMultiplier> {
		@Override
		public DimensionHardnessMultiplier deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			DimensionHardnessMultiplier dimensionHardnessMultiplier = new DimensionHardnessMultiplier();
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

			return dimensionHardnessMultiplier;
		}

		@Override
		public JsonElement serialize(DimensionHardnessMultiplier src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			if (src.dimension != null) {
				jsonObject.addProperty("dimension", src.dimension.toString());
			}
			jsonObject.addProperty("multiplier", src.multiplier);

			return jsonObject;
		}
	}
}
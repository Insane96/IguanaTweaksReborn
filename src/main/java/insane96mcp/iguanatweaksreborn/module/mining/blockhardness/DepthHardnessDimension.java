package insane96mcp.iguanatweaksreborn.module.mining.blockhardness;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import insane96mcp.insanelib.data.IdTagValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;

/**
 * In this case the {@link IdTagValue#value} field is used per block below the {@link DepthHardnessDimension#applyBelowY} level
 */
@JsonAdapter(DepthHardnessDimension.Serializer.class)
public class DepthHardnessDimension extends DimensionHardnessMultiplier {

	public final int applyBelowY;
	public final int stopAt;

	public DepthHardnessDimension(String dimension, double multiplier, int applyBelowY, int stopAt) {
		super(dimension, multiplier);
		this.applyBelowY = applyBelowY;
		this.stopAt = stopAt;
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<DepthHardnessDimension>>(){}.getType();

	public static class Serializer implements JsonDeserializer<DepthHardnessDimension>, JsonSerializer<DepthHardnessDimension> {
		@Override
		public DepthHardnessDimension deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String dimension = GsonHelper.getAsString(json.getAsJsonObject(), "dimension");
			if (!ResourceLocation.isValidResourceLocation(dimension))
				throw new JsonParseException("Invalid dimension: %s".formatted(dimension));
			return new DepthHardnessDimension(dimension, GsonHelper.getAsDouble(json.getAsJsonObject(), "multiplier"), GsonHelper.getAsInt(json.getAsJsonObject(), "apply_below_y", Integer.MAX_VALUE), GsonHelper.getAsInt(json.getAsJsonObject(), "stop_at", Integer.MIN_VALUE));
		}

		@Override
		public JsonElement serialize(DepthHardnessDimension src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("dimension", src.dimension.toString());
			jsonObject.addProperty("multiplier", src.multiplier);
			if (src.applyBelowY != Integer.MAX_VALUE)
				jsonObject.addProperty("apply_below_y", src.applyBelowY);
			if (src.stopAt != Integer.MIN_VALUE)
				jsonObject.addProperty("stop_at", src.stopAt);

			return jsonObject;
		}
	}
}
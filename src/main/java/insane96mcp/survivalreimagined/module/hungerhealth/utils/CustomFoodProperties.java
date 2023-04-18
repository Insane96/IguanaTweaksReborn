package insane96mcp.survivalreimagined.module.hungerhealth.utils;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

@JsonAdapter(CustomFoodProperties.Serializer.class)
public class CustomFoodProperties extends IdTagMatcher {
	public int nutrition = -1;
	public float saturationModifier = -1f;
	public int eatingTime = -1;
	public Boolean fastEating = null;

	public CustomFoodProperties(Type type, String id) {
		super(type, id);
	}

	public static class Builder {
		CustomFoodProperties customFoodProperties;

		public Builder(Type type, String id) {
			this.customFoodProperties = new CustomFoodProperties(type, id);
		}

		public CustomFoodProperties build() {
			return this.customFoodProperties;
		}

		public Builder setNutrition(int nutrition) {
			this.customFoodProperties.nutrition = nutrition;
			return this;
		}

		public Builder setSaturationModifier(float saturationModifier) {
			this.customFoodProperties.saturationModifier = saturationModifier;
			return this;
		}

		public Builder setEatingTime(int eatingTime) {
			this.customFoodProperties.eatingTime = eatingTime;
			return this;
		}

		public Builder fastEating() {
			this.customFoodProperties.fastEating = true;
			return this;
		}
	}

	@Override
	public String toString() {
		return "FoodValue{type: %s, id: %s, nutrition: %s, saturation_modifier: %s, eating_time: %s, fast_eating: %s}".formatted(this.type, this.location, this.nutrition, this.saturationModifier, this.eatingTime, this.fastEating);
	}

	public static class Serializer implements JsonDeserializer<CustomFoodProperties>, JsonSerializer<CustomFoodProperties> {
		@Override
		public CustomFoodProperties deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");
			String tag = GsonHelper.getAsString(json.getAsJsonObject(), "tag", "");

			if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id: %s".formatted(id));
			}
			if (!tag.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid tag: %s".formatted(tag));
			}

			CustomFoodProperties customFoodProperties;
			if (!id.equals("") && !tag.equals("")){
				throw new JsonParseException("Invalid object containing both tag (%s) and id (%s)".formatted(tag, id));
			}
			else if (!id.equals("")) {
				customFoodProperties = new CustomFoodProperties(Type.ID, id);
			}
			else if (!tag.equals("")){
				customFoodProperties = new CustomFoodProperties(Type.TAG, tag);
			}
			else {
				throw new JsonParseException("Invalid object missing either tag and id");
			}

			customFoodProperties.nutrition = GsonHelper.getAsInt(json.getAsJsonObject(), "nutrition", -1);
			customFoodProperties.saturationModifier = GsonHelper.getAsFloat(json.getAsJsonObject(), "saturation_modifier", -1f);
			customFoodProperties.eatingTime = GsonHelper.getAsInt(json.getAsJsonObject(), "eating_time", -1);
			if (json.getAsJsonObject().has("fast_eating")) {
				customFoodProperties.fastEating = GsonHelper.getAsBoolean(json.getAsJsonObject(), "fast_eating");
			}

			return customFoodProperties;
		}

		@Override
		public JsonElement serialize(CustomFoodProperties src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			if (src.type == Type.ID) {
				jsonObject.addProperty("id", src.location.toString());
			}
			else if (src.type == Type.TAG) {
				jsonObject.addProperty("tag", src.location.toString());
			}
			if (src.nutrition >= 0) {
				jsonObject.addProperty("nutrition", src.nutrition);
			}
			if (src.saturationModifier >= 0f) {
				jsonObject.addProperty("saturation_modifier", src.saturationModifier);
			}
			if (src.eatingTime >= 0) {
				jsonObject.addProperty("eating_time", src.eatingTime);
			}
			if (src.fastEating != null) {
				jsonObject.addProperty("fast_eating", src.fastEating);
			}

			return jsonObject;
		}
	}
}

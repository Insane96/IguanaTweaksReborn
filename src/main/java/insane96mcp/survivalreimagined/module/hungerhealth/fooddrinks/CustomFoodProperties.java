package insane96mcp.survivalreimagined.module.hungerhealth.fooddrinks;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import com.mojang.datafixers.util.Pair;
import insane96mcp.insanelib.util.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@JsonAdapter(CustomFoodProperties.Serializer.class)
public class CustomFoodProperties extends IdTagMatcher {
	public int nutrition = -1;
	public float saturationModifier = -1f;
	public int eatingTime = -1;
	public boolean fastEating = false;
	//If null, don't modify existing effects
	public List<Pair<Supplier<MobEffectInstance>, Float>> effects = null;

	public CustomFoodProperties(Type type, String id) {
		super(type, id);
	}

	public List<Pair<MobEffectInstance, Float>> getEffects() {
		return this.effects.stream().map(pair -> Pair.of(pair.getFirst() != null ? pair.getFirst().get() : null, pair.getSecond())).collect(java.util.stream.Collectors.toList());
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

		public Builder noEffects() {
			this.customFoodProperties.effects = new ArrayList<>();
			return this;
		}

		public Builder addEffect(MobEffectInstance mobEffectInstance, float chance) {
			if (this.customFoodProperties.effects == null)
				this.customFoodProperties.effects = new ArrayList<>();
			this.customFoodProperties.effects.add(Pair.of(() -> mobEffectInstance, chance));
			return this;
		}
	}

	@Override
	public String toString() {
		return "FoodValue{type: %s, id: %s, nutrition: %s, saturation_modifier: %s, eating_time: %s, fast_eating: %s, effects: %s}".formatted(this.type, this.location, this.nutrition, this.saturationModifier, this.eatingTime, this.fastEating, this.effects);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<CustomFoodProperties>>(){}.getType();

	public static class Serializer implements JsonDeserializer<CustomFoodProperties>, JsonSerializer<CustomFoodProperties> {
		@Override
		public CustomFoodProperties deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");
			String tag = GsonHelper.getAsString(json.getAsJsonObject(), "tag", "");

			if (!id.isEmpty() && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id: %s".formatted(id));
			}
			if (!tag.isEmpty() && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid tag: %s".formatted(tag));
			}

			CustomFoodProperties customFoodProperties;
			if (!id.isEmpty() && !tag.isEmpty()){
				throw new JsonParseException("Invalid object containing both tag (%s) and id (%s)".formatted(tag, id));
			}
			else if (!id.isEmpty()) {
				customFoodProperties = new CustomFoodProperties(Type.ID, id);
			}
			else if (!tag.isEmpty()){
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

			if (json.getAsJsonObject().has("effects")) {
				JsonArray effects = json.getAsJsonObject().getAsJsonArray("effects");
				if (customFoodProperties.effects == null)
					customFoodProperties.effects = new ArrayList<>();
				effects.forEach(element -> {
					String stringId = GsonHelper.getAsString(element.getAsJsonObject(), "id");
					ResourceLocation effectId = ResourceLocation.tryParse(stringId);
					MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);
					if (mobEffect == null)
						throw new JsonParseException("Mob effect %s not found".formatted(stringId));
					int amplifier = GsonHelper.getAsInt(element.getAsJsonObject(), "amplifier", 0);
					int duration = GsonHelper.getAsInt(element.getAsJsonObject(), "duration");
					MobEffectInstance effectInstance = new MobEffectInstance(mobEffect, duration, amplifier);
					float chance = GsonHelper.getAsFloat(element.getAsJsonObject(), "chance", 1f);
					customFoodProperties.effects.add(Pair.of(() -> effectInstance, chance));
				});
			}

			return customFoodProperties;
		}

		@Override
		public JsonElement serialize(CustomFoodProperties src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			if (src.type == Type.ID)
				jsonObject.addProperty("id", src.location.toString());
			else if (src.type == Type.TAG)
				jsonObject.addProperty("tag", src.location.toString());

			if (src.nutrition >= 0)
				jsonObject.addProperty("nutrition", src.nutrition);
			if (src.saturationModifier >= 0f)
				jsonObject.addProperty("saturation_modifier", src.saturationModifier);
			if (src.eatingTime >= 0)
				jsonObject.addProperty("eating_time", src.eatingTime);
			if (src.fastEating)
				jsonObject.addProperty("fast_eating", true);
			if (src.effects != null) {
				JsonArray effects = new JsonArray();
				src.getEffects().forEach(pair -> {
					JsonObject effect = new JsonObject();
					effect.addProperty("id", ForgeRegistries.MOB_EFFECTS.getKey(pair.getFirst().getEffect()).toString());
					effect.addProperty("amplifier", pair.getFirst().getAmplifier());
					effect.addProperty("duration", pair.getFirst().getDuration());
					if (pair.getSecond() < 1f)
						effect.addProperty("chance", pair.getSecond());
					effects.add(effect);
				});
				jsonObject.add("effects", effects);
			}

			return jsonObject;
		}
	}
}

package insane96mcp.survivalreimagined.module.hungerhealth.fooddrinks;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import com.mojang.datafixers.util.Pair;
import insane96mcp.insanelib.data.IdTagMatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@JsonAdapter(CustomFoodProperties.Serializer.class)
public class CustomFoodProperties {
	IdTagMatcher food;
	public int nutrition = -1;
	public float saturationModifier = -1f;
	public int eatingTime = -1;
	public boolean fastEating = false;
	//If null, don't modify existing effects
	public List<Pair<Supplier<MobEffectInstance>, Float>> effects = null;

	public CustomFoodProperties(IdTagMatcher food) {
		this.food = food;
	}

	public List<Pair<MobEffectInstance, Float>> getEffects() {
		return this.effects.stream().map(pair -> Pair.of(pair.getFirst() != null ? pair.getFirst().get() : null, pair.getSecond())).collect(java.util.stream.Collectors.toList());
	}

	public static class Builder {
		CustomFoodProperties customFoodProperties;

		public Builder(IdTagMatcher food) {
			this.customFoodProperties = new CustomFoodProperties(food);
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

		public Builder setFastEating(boolean fastEating) {
			this.customFoodProperties.fastEating = fastEating;
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
		return "FoodValue{food: %s, nutrition: %s, saturation_modifier: %s, eating_time: %s, fast_eating: %s, effects: %s}".formatted(this.food, this.nutrition, this.saturationModifier, this.eatingTime, this.fastEating, this.effects);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<CustomFoodProperties>>(){}.getType();

	public static class Serializer implements JsonDeserializer<CustomFoodProperties>, JsonSerializer<CustomFoodProperties> {
		@Override
		public CustomFoodProperties deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			IdTagMatcher food = context.deserialize(json.getAsJsonObject().get("food"), IdTagMatcher.class);

			CustomFoodProperties.Builder builder = new CustomFoodProperties.Builder(food)
					.setNutrition(GsonHelper.getAsInt(json.getAsJsonObject(), "nutrition", -1))
					.setSaturationModifier(GsonHelper.getAsFloat(json.getAsJsonObject(), "saturation_modifier", -1f))
					.setEatingTime(GsonHelper.getAsInt(json.getAsJsonObject(), "eating_time", -1))
					.setFastEating(GsonHelper.getAsBoolean(json.getAsJsonObject(), "fast_eating", false));
			if (json.getAsJsonObject().has("effects")) {
				JsonArray jArray = json.getAsJsonObject().getAsJsonArray("effects");
				if (jArray.isEmpty())
					builder.noEffects();
				else {
					jArray.forEach(element -> {
						String stringId = GsonHelper.getAsString(element.getAsJsonObject(), "id");
						ResourceLocation effectId = ResourceLocation.tryParse(stringId);
						MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(effectId);
						if (mobEffect == null)
							throw new JsonParseException("Mob effect %s not found".formatted(stringId));
						int amplifier = GsonHelper.getAsInt(element.getAsJsonObject(), "amplifier", 0);
						int duration = GsonHelper.getAsInt(element.getAsJsonObject(), "duration");
						MobEffectInstance effectInstance = new MobEffectInstance(mobEffect, duration, amplifier);
						float chance = GsonHelper.getAsFloat(element.getAsJsonObject(), "chance", 1f);
						builder.addEffect(effectInstance, chance);
					});
				}
			}

			return builder.build();
		}

		@Override
		public JsonElement serialize(CustomFoodProperties src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("food", context.serialize(src.food));
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

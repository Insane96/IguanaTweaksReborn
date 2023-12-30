package insane96mcp.iguanatweaksreborn.data;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;

/**
 * Serializable version of MobEffectInstance
 */
@JsonAdapter(ITRMobEffectInstance.Serializer.class)
public class ITRMobEffectInstance extends MobEffectInstance {
	public ITRMobEffectInstance(MobEffect mobEffect, int duration, int amplifier) {
		super(mobEffect, duration, amplifier);
	}

	public static final java.lang.reflect.Type LIST_TYPE = new TypeToken<ArrayList<ITRMobEffectInstance>>(){}.getType();
	public static class Serializer implements JsonDeserializer<ITRMobEffectInstance>, JsonSerializer<ITRMobEffectInstance> {
		@Override
		public ITRMobEffectInstance deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String id = GsonHelper.getAsString(json.getAsJsonObject(), "id", "");

			if (!id.equals("") && !ResourceLocation.isValidResourceLocation(id)) {
				throw new JsonParseException("Invalid id: %s".formatted(id));
			}
			MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(id));
			if (mobEffect == null) {
				throw new JsonParseException("%d is not a known mob_effect".formatted(id));
			}
			int duration = GsonHelper.getAsInt(json.getAsJsonObject(), "duration");
			int amplifier = GsonHelper.getAsInt(json.getAsJsonObject(), "amplifier", 0);

			return new ITRMobEffectInstance(mobEffect, duration, amplifier);
		}

		@Override
		public JsonElement serialize(ITRMobEffectInstance src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("id", ForgeRegistries.MOB_EFFECTS.getKey(src.getEffect()).toString());
			jsonObject.addProperty("duration", src.getDuration());
			if (src.getAmplifier() > 0)
				jsonObject.addProperty("amplifier", src.getAmplifier());

			return jsonObject;
		}
	}
}
